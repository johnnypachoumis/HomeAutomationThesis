package com.smarthome.pachoumis.homeautomation.activityPackage;

import android.graphics.Color;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
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
The AlarmScreen class implements the activity that is responsible for the management
of the smart home alarm device. Has the ability to enable and disable the home alarm
after the password is given and set the alarm to be enabled or disabled at a certain
time of the day. Displays information about the status of the alarm and if someone
has broken in. Contains a button for disabling the alarm siren.
 */
public class AlarmScreen extends AppCompatActivity {

    private EditText passwordField;
    private Switch alarmSwitch;
    private TextView alarmLabel;
    private TextView passwordLabel;
    private TextView homeStatusText;

/*
Method called when the activity is created
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_screen);

        //Permit all policy for the threads
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //The view objects and their bindings to the layout xml counterpart of the activity
        passwordField = (EditText) findViewById(R.id.asPasswordField);
        alarmSwitch = (Switch) findViewById(R.id.asAlarmSwitch);
        CheckBox enableTimeBox = (CheckBox) findViewById((R.id.asEnableTimeBox));
        EditText enableTimeField = (EditText) findViewById(R.id.asEnableTimeField);
        CheckBox disableTimeBox = (CheckBox) findViewById(R.id.asDisableTimeBox);
        EditText disableTimeField = (EditText) findViewById(R.id.asDisableTimeField);
        Button alarmOffButton = (Button) findViewById(R.id.asAlarmOffButton);
        alarmLabel = (TextView) findViewById(R.id.asAlarmLabel);
        passwordLabel = (TextView) findViewById(R.id.asPasswordLabel);
        homeStatusText = (TextView) findViewById(R.id.asHomeStatusText);

        //initializing the switch for the alarm and the checkboxes and fields for time scheduling
        GlobalMethods.initializeSwitch(Constant.DEVICE_ALARM_URI,alarmSwitch);
        GlobalMethods.initializeCheckBoxAndField(Constant.DEVICE_ALARM_URI,Constant.DEVICE_TIME_ENABLE_URI, enableTimeBox, enableTimeField);
        GlobalMethods.initializeCheckBoxAndField(Constant.DEVICE_ALARM_URI,Constant.DEVICE_TIME_DISABLE_URI, disableTimeBox, disableTimeField);

        /*
        A listener for the password field. If the password matches the constant password then make
        the alarm Label green to notify the user that it is enabled. If the password doesn't match
        the constant password then make the alarm label red to notify the user of the incorrect password
         */
        passwordField.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if((passwordField.getText().toString()).equals(Constant.USER_PASSWORD)){
                        alarmSwitch.setClickable(true);
                        alarmLabel.setTextColor(Color.rgb(67,78,121));
                        passwordLabel.setTextColor(Color.rgb(102,153,50));
                    }
                    else{
                        alarmSwitch.setClickable(false);
                        alarmLabel.setTextColor(Color.rgb(117,118,120));
                        passwordLabel.setTextColor(Color.rgb(204,0,0));
                    }
                }
                return false;
            }
        });

        //methods that add functionality to the switch and the time schedule checkboxes and fields
        GlobalMethods.switchFunctionality(alarmSwitch,Constant.DEVICE_ALARM_URI);
        GlobalMethods.checkBoxFunctionality(enableTimeBox, enableTimeField,Constant.DEVICE_ALARM_URI,Constant.DEVICE_TIME_ENABLE_URI);
        GlobalMethods.editTextFunctionality(enableTimeField, enableTimeBox,Constant.TIME_PATTERN);
        GlobalMethods.checkBoxFunctionality(disableTimeBox, disableTimeField,Constant.DEVICE_ALARM_URI,Constant.DEVICE_TIME_DISABLE_URI);
        GlobalMethods.editTextFunctionality(disableTimeField, disableTimeBox,Constant.TIME_PATTERN);

        //If the user clicks this button the alarm siren is turned off
        assert alarmOffButton != null;
        alarmOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpRequests.putRequest(Constant.SENSOR_BREAK_IN_URI,Constant.SENSOR_OFF);
            }
        });

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
                AlarmScreen.this.runOnUiThread(new Runnable(){      //This runs on User Interface level

                    /*
                    This Runnable displays information about the break in sensor. If the alarm is enabled and
                    the break in sensor displays no "motion" the text becomes green and displays the appropriate
                    text. If the alarm is enabled and the break in sensor displays "motion", the text becomes
                    red and displays again the appropriate text. If the alarm is disabled then there is no
                    motion data to be displayed and the appropriate text is being displayed
                     */
                    @Override
                    public void run() {
                        String homeStatus = HttpRequests.getRequest(Constant.SENSOR_BREAK_IN_URI);
                        if(!alarmSwitch.isChecked()) {
                            homeStatusText.setText(R.string.disabledAlarmMessage);
                            homeStatusText.setTextColor(Color.rgb(255,136,0));
                        }else if(alarmSwitch.isChecked()){
                            if (homeStatus.equals(Constant.SENSOR_ON)) {
                                homeStatusText.setText(R.string.alarmMotionDetectionMessage);
                                homeStatusText.setTextColor(Color.rgb(204, 0, 0));
                            } else {
                                homeStatusText.setText(R.string.alarmNoMotionDetectionMessage);
                                homeStatusText.setTextColor(Color.rgb(102, 153, 50));
                            }
                        }
                        else{
                            homeStatusText.setText(R.string.alarmNoServiceMessage);
                            homeStatusText.setTextColor(Color.rgb(117,118,120));
                        }

                    }
                });
            }
        },10,10, TimeUnit.SECONDS);
    }
}
