package com.smarthome.pachoumis.homeautomation.utilityPackage;

import android.graphics.Color;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
The GlobalMethods class implements the methods that are used through the app. Instead of implementing the methods
under each class, i implement them here because the only difference are the local variable names. Has methods about
initializing View objects and their functionality.
 */
public class GlobalMethods {

    /*
    The initializeSwitch method initializes a switch in the onCreate method of each activity. Checks with the server
    and sets the state of the switch depending on the server values. Accepts the device URI and the switch object.
     */
    public static void initializeSwitch(String deviceURI, Switch aSwitch){
        if(HttpRequests.getRequest(deviceURI).equals(Constant.DEVICE_ON))
            aSwitch.setChecked(true);
        else
            aSwitch.setChecked(false);
    }

    /*
    The initializeCheckBoxAndField method sets up a checkbox and its corresponding editText field inside the onCreate
    method of each activity. Accepts the device URI, the function URI extension of the request, a checkbox and an
    editText field. It initializes the state of the checkbox and the text of the editText field depending on the
    server values for these fields.
     */
    public static void initializeCheckBoxAndField(String deviceURI, String functionURI, CheckBox checkbox, EditText edittext){
        String request = HttpRequests.getRequest(deviceURI + functionURI);
        String nullValue;
        if(functionURI.equals(Constant.DEVICE_TIME_ENABLE_URI) || functionURI.equals(Constant.DEVICE_TIME_DISABLE_URI))
            nullValue = Constant.NO_TIME_VALUE;     //If the extension is about time, set the null value to blank text
        else
            nullValue = Constant.NO_SENSOR_VALUE;   //If the extension is about a sensor, set the null value to -1

        if(request.equals(nullValue)){
            checkbox.setChecked(false);
            checkbox.setEnabled(false);                     //Disable checkbox
            checkbox.setClickable(false);
            checkbox.setTextColor(Color.rgb(117,118,120));  //Color Gray
            edittext.setText(Constant.NO_DISPLAY_VALUE);    //Leave the editText blank
        }else{
            checkbox.setChecked(true);
            checkbox.setEnabled(true);                      //Enable checkbox
            checkbox.setClickable(true);
            edittext.setText(request);                      //Display the value in the editText
            checkbox.setTextColor(Color.rgb(67,78,121));    //Color Blue
        }
    }

    /*
    The switchFunctionality method implements the rules of the switch, how the switch must behave in the app. It
    accepts the switch and its corresponding device URI and listens for an onCheckedChanged action that occurs
    when the switch changes state. Then it sends the REST server information depending on the state of the switch
     */
    public static void switchFunctionality(Switch aSwitch, final String deviceURI){
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    HttpRequests.putRequest(deviceURI,Constant.DEVICE_ON);
                }else{
                    HttpRequests.putRequest(deviceURI,Constant.DEVICE_OFF);
                }
            }
        });
    }

    /*
    The checkBoxFunctionality method implements the rules of the checkbox and its corresponding editText field. It
    accepts a checkbox, an editText field, the device the checkbox is bound to and the function URI extension. It
    usually precedes the editTextFunctionality method. Implements a listener that listens for an onCheckedChanged
    action that occurs when the checkbox changes state. Then it sends the REST server information depending on the
    state of the checkbox.
     */
    public static void checkBoxFunctionality(final CheckBox checkbox, final EditText edittext, final String deviceURI, final String functionURI){
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    HttpRequests.putRequest(deviceURI + functionURI,edittext.getText().toString());
                }else{
                    if(functionURI.equals(Constant.DEVICE_TIME_ENABLE_URI) || functionURI.equals(Constant.DEVICE_TIME_DISABLE_URI)){
                        HttpRequests.putRequest(deviceURI + functionURI,Constant.NO_TIME_VALUE);
                    }else if(functionURI.equals(Constant.DEVICE_SENSOR_ENABLE_URI) || functionURI.equals(Constant.DEVICE_SENSOR_DISABLE_URI)){
                        HttpRequests.putRequest(deviceURI + functionURI,Constant.NO_SENSOR_VALUE);
                    }
                    checkbox.setEnabled(false);
                    checkbox.setClickable(false);
                    checkbox.setTextColor(Color.rgb(117,118,120));
                }
            }
        });
    }

    /*
    The editTextFunctionality method implements the rules for the editText field corresponding to a checkbox. It
    accepts the editText field, the checkbox and a regular expression. When the user presses the "Done" button
    on the device soft keyboard, the method checks if the text in the editText field matches the regular expression
    given. If so it enables the checkbox changes its color to Blue. If not it disables the checkbox and sets its
    color to grey.
     */
    public static void editTextFunctionality(final EditText edittext, final CheckBox checkbox, final String aPattern){
        edittext.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Boolean matches;
                    try {
                        Pattern pattern = Pattern.compile(aPattern);
                        Matcher matcher = pattern.matcher(edittext.getText().toString());
                        matches = matcher.matches();
                    } catch (RuntimeException e) {
                        matches = false;
                    }
                    if(matches){
                        checkbox.setEnabled(true);
                        checkbox.setClickable(true);
                        checkbox.setTextColor(Color.rgb(67,78,121));
                    }else{
                        checkbox.setEnabled(false);
                        checkbox.setClickable(false);
                        checkbox.setTextColor(Color.rgb(117,118,120));
                    }
                }
                return false;
            }
        });
    }
}