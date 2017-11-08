package com.smarthome.pachoumis.homeautomation.activityPackage;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
The BlindsScreen class implements the activity that is responsible for the management
of the smart home window blinds device. Has the ability to enable and disable the
blinds and/or set them to be enabled or disabled at a certain time of the day or
when the light level reaches a certain value. Displays information about the light level
 */
public class BlindsScreen extends AppCompatActivity {

    private TextView lightLevelText;

    /*
    Method called when the activity is created
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blinds_screen);

        //Permit all policy for the threads
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //The view objects and their bindings to the layout xml counterpart of the activity
        Switch blindsSwitch = (Switch) findViewById(R.id.blsBlindsSwitch);
        CheckBox turnOnTimeBox = (CheckBox) findViewById(R.id.blsTurnOnTimeBox);
        EditText turnOnTimeField = (EditText) findViewById(R.id.blsTurnOnTimeField);
        CheckBox turnOffTimeBox = (CheckBox) findViewById(R.id.blsTurnOffTimeBox);
        EditText turnOffTimeField = (EditText) findViewById(R.id.blsTurnOffTimeField);
        CheckBox turnOnSensorBox = (CheckBox) findViewById(R.id.blsTurnOnSensorBox);
        EditText turnOnSensorField = (EditText) findViewById(R.id.blsTurnOnSensorField);
        CheckBox turnOffSensorBox = (CheckBox) findViewById(R.id.blsTurnOffSensorBox);
        EditText turnOffSensorField = (EditText) findViewById(R.id.blsTurnOffSensorField);
        lightLevelText = (TextView) findViewById(R.id.blsLightLevelText);

        //initializing the switch for the window blinds and the checkboxes and fields for time and sensor value scheduling
        GlobalMethods.initializeSwitch(Constant.DEVICE_BLINDS_URI, blindsSwitch);
        GlobalMethods.initializeCheckBoxAndField(Constant.DEVICE_BLINDS_URI,Constant.DEVICE_TIME_ENABLE_URI, turnOnTimeBox, turnOnTimeField);
        GlobalMethods.initializeCheckBoxAndField(Constant.DEVICE_BLINDS_URI,Constant.DEVICE_TIME_DISABLE_URI, turnOffTimeBox, turnOffTimeField);
        GlobalMethods.initializeCheckBoxAndField(Constant.DEVICE_BLINDS_URI,Constant.DEVICE_SENSOR_ENABLE_URI, turnOnSensorBox, turnOnSensorField);
        GlobalMethods.initializeCheckBoxAndField(Constant.DEVICE_BLINDS_URI,Constant.DEVICE_SENSOR_DISABLE_URI, turnOffSensorBox, turnOffSensorField);

        //methods that add functionality to the switch, the time schedule checkboxes and fields and the sensor value checkboxes and fields
        GlobalMethods.switchFunctionality(blindsSwitch,Constant.DEVICE_BLINDS_URI);
        GlobalMethods.checkBoxFunctionality(turnOnTimeBox, turnOnTimeField,Constant.DEVICE_BLINDS_URI,Constant.DEVICE_TIME_ENABLE_URI);
        GlobalMethods.editTextFunctionality(turnOnTimeField, turnOnTimeBox,Constant.TIME_PATTERN);
        GlobalMethods.checkBoxFunctionality(turnOffTimeBox, turnOffTimeField,Constant.DEVICE_BLINDS_URI,Constant.DEVICE_TIME_DISABLE_URI);
        GlobalMethods.editTextFunctionality(turnOffTimeField, turnOffTimeBox,Constant.TIME_PATTERN);
        GlobalMethods.checkBoxFunctionality(turnOnSensorBox, turnOnSensorField,Constant.DEVICE_BLINDS_URI,Constant.DEVICE_SENSOR_ENABLE_URI);
        GlobalMethods.editTextFunctionality(turnOnSensorField, turnOnSensorBox,Constant.PERCENTAGE_PATTERN);
        GlobalMethods.checkBoxFunctionality(turnOffSensorBox, turnOffSensorField,Constant.DEVICE_BLINDS_URI,Constant.DEVICE_SENSOR_DISABLE_URI);
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
               BlindsScreen.this.runOnUiThread(new Runnable(){      //This runs on User Interface level

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
}
