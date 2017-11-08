package com.smarthome.pachoumis.homeautomation.activityPackage;

import android.graphics.Color;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.smarthome.pachoumis.homeautomation.R;
import com.smarthome.pachoumis.homeautomation.utilityPackage.Constant;
import com.smarthome.pachoumis.homeautomation.utilityPackage.GlobalMethods;
import com.smarthome.pachoumis.homeautomation.utilityPackage.HttpRequests;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
The LightsScreen class implements the activity that is responsible for the management
of the smart home room lights. Has the ability to enable and disable the lights, set it
to be enabled or disabled at a certain time of the day and/or when the light level
percentage reaches a certain value. Displays information about the light level.
 */
public class LightsScreen extends AppCompatActivity {

    private TextView lightLevelText;

    /*
    Method called when the activity is created
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lights_screen);

        //Permit all policy for the threads
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //The view objects and their bindings to the layout xml counterpart of the activity
        Switch light1Switch = (Switch) findViewById(R.id.lsLight1Switch);
        Switch light2Switch = (Switch) findViewById(R.id.lsLight2Switch);
        CheckBox turnOnTimeBox = (CheckBox) findViewById(R.id.lsTurnOnTimeBox);
        EditText turnOnTimeField = (EditText) findViewById(R.id.lsTurnOnTimeField);
        CheckBox turnOffTimeBox = (CheckBox) findViewById(R.id.lsTurnOffTimeBox);
        EditText turnOffTimeField = (EditText) findViewById(R.id.lsTurnOffTimeField);
        CheckBox turnOnSensorBox = (CheckBox) findViewById(R.id.lsTurnOnSensorBox);
        EditText turnOnSensorField = (EditText) findViewById(R.id.lsTurnOnSensorField);
        CheckBox turnOffSensorBox = (CheckBox) findViewById(R.id.lsTurnOffSensorBox);
        EditText turnOffSensorField = (EditText) findViewById(R.id.lsTurnOffSensorField);
        lightLevelText = (TextView) findViewById(R.id.lsLightLevelText);

        //initializing the switches for the lights and the checkboxes and fields for time and sensor value scheduling
        GlobalMethods.initializeSwitch(Constant.DEVICE_LIGHT_1_URI, light1Switch);
        GlobalMethods.initializeSwitch(Constant.DEVICE_LIGHT_2_URI, light2Switch);
        GlobalMethods.initializeCheckBoxAndField(Constant.DEVICE_LIGHT_1_URI,Constant.DEVICE_TIME_ENABLE_URI, turnOnTimeBox, turnOnTimeField);
        GlobalMethods.initializeCheckBoxAndField(Constant.DEVICE_LIGHT_2_URI,Constant.DEVICE_TIME_ENABLE_URI, turnOnTimeBox, turnOnTimeField);
        GlobalMethods.initializeCheckBoxAndField(Constant.DEVICE_LIGHT_1_URI,Constant.DEVICE_TIME_DISABLE_URI, turnOffTimeBox, turnOffTimeField);
        GlobalMethods.initializeCheckBoxAndField(Constant.DEVICE_LIGHT_2_URI,Constant.DEVICE_TIME_DISABLE_URI, turnOffTimeBox, turnOffTimeField);
        GlobalMethods.initializeCheckBoxAndField(Constant.DEVICE_LIGHT_1_URI,Constant.DEVICE_SENSOR_ENABLE_URI, turnOnSensorBox, turnOnSensorField);
        GlobalMethods.initializeCheckBoxAndField(Constant.DEVICE_LIGHT_2_URI,Constant.DEVICE_SENSOR_ENABLE_URI, turnOnSensorBox, turnOnSensorField);
        GlobalMethods.initializeCheckBoxAndField(Constant.DEVICE_LIGHT_1_URI,Constant.DEVICE_SENSOR_DISABLE_URI, turnOffSensorBox, turnOffSensorField);
        GlobalMethods.initializeCheckBoxAndField(Constant.DEVICE_LIGHT_2_URI,Constant.DEVICE_SENSOR_DISABLE_URI, turnOffSensorBox, turnOffSensorField);

        //methods that add functionality to the switches, the time schedule checkboxes and fields and the sensor value checkboxes and fields
        GlobalMethods.switchFunctionality(light1Switch,Constant.DEVICE_LIGHT_1_URI);
        GlobalMethods.switchFunctionality(light2Switch,Constant.DEVICE_LIGHT_2_URI);
        checkBoxDualFunctionality(turnOnTimeBox, turnOnTimeField,Constant.DEVICE_TIME_ENABLE_URI);
        GlobalMethods.editTextFunctionality(turnOnTimeField, turnOnTimeBox,Constant.TIME_PATTERN);
        checkBoxDualFunctionality(turnOffTimeBox, turnOffTimeField,Constant.DEVICE_TIME_DISABLE_URI);
        GlobalMethods.editTextFunctionality(turnOffTimeField, turnOffTimeBox,Constant.TIME_PATTERN);
        checkBoxDualFunctionality(turnOnSensorBox, turnOnSensorField,Constant.DEVICE_SENSOR_ENABLE_URI);
        GlobalMethods.editTextFunctionality(turnOnSensorField, turnOnSensorBox,Constant.PERCENTAGE_PATTERN);
        checkBoxDualFunctionality(turnOffSensorBox, turnOffSensorField,Constant.DEVICE_SENSOR_DISABLE_URI);
        GlobalMethods.editTextFunctionality(turnOffSensorField, turnOffSensorBox,Constant.PERCENTAGE_PATTERN);
    }

    /*
    Method called when the activity is resumed. Also runs after onCreate.
    */
    @Override
    protected void onResume() {
        super.onResume();

        //Scheduling a new Thread to run every 10 seconds
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable(){

            @Override
            public void run() {
                LightsScreen.this.runOnUiThread(new Runnable(){         //This runs on User Interface level

                    @Override
                    public void run() {

                        //Get light level information from the server and display it
                        String lightSensor = HttpRequests.getRequest(Constant.SENSOR_LIGHT_LEVEL_URI);
                        lightLevelText.setText(lightSensor + "  %");

                    }
                });
            }
        },10,10, TimeUnit.SECONDS);
    }

    //same functionality as the GlobalMethods.checkBoxFunctionality but modified to accept two lights instead of a device
    private void checkBoxDualFunctionality(final CheckBox checkbox, final EditText edittext, final String functionURI){
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    HttpRequests.putRequest(Constant.DEVICE_LIGHT_1_URI + functionURI,edittext.getText().toString());
                    HttpRequests.putRequest(Constant.DEVICE_LIGHT_2_URI + functionURI,edittext.getText().toString());
                }else{
                    if(functionURI.equals(Constant.DEVICE_TIME_ENABLE_URI) || functionURI.equals(Constant.DEVICE_TIME_DISABLE_URI)){
                        HttpRequests.putRequest(Constant.DEVICE_LIGHT_1_URI + functionURI,Constant.NO_TIME_VALUE);
                        HttpRequests.putRequest(Constant.DEVICE_LIGHT_2_URI,Constant.NO_TIME_VALUE);
                    }else if(functionURI.equals(Constant.DEVICE_SENSOR_ENABLE_URI) || functionURI.equals(Constant.DEVICE_SENSOR_DISABLE_URI)){
                        HttpRequests.putRequest(Constant.DEVICE_LIGHT_1_URI + functionURI,Constant.NO_SENSOR_VALUE);
                        HttpRequests.putRequest(Constant.DEVICE_LIGHT_2_URI + functionURI,Constant.NO_SENSOR_VALUE);
                    }
                    checkbox.setEnabled(false);
                    checkbox.setClickable(false);
                    checkbox.setTextColor(Color.rgb(117,118,120));
                }
            }
        });
    }

}
