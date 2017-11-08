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
The BoilerScreen class implements the activity that is responsible for the management
of the smart home boiler device. Has the ability to enable and disable the boiler and/or
set it to be enabled or disabled when the hot water percentage reaches a certain value. \
Displays information about the hot water inside the boiler.
 */
public class BoilerScreen extends AppCompatActivity {

    private TextView hotWaterText;

    /*
    Method called when the activity is created
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boiler_screen);

        //Permit all policy for the threads
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //The view objects and their bindings to the layout xml counterpart of the activity
        Switch boilerSwitch = (Switch) findViewById(R.id.bosBoilerSwitch);
        CheckBox turnOnTimeBox = (CheckBox) findViewById(R.id.bosTurnOnTimeBox);
        EditText turnOnTimeField = (EditText) findViewById(R.id.bosTurnOnTimeField);
        CheckBox turnOffTimeBox = (CheckBox) findViewById(R.id.bosTurnOffTimeBox);
        EditText turnOffTimeField = (EditText) findViewById(R.id.bosTurnOffTimeField);
        CheckBox turnOnSensorBox = (CheckBox) findViewById(R.id.bosTurnOnSensorBox);
        EditText turnOnSensorField = (EditText) findViewById(R.id.bosTurnOnSensorField);
        CheckBox turnOffSensorBox = (CheckBox) findViewById(R.id.bosTurnOffSensorBox);
        EditText turnOffSensorField = (EditText) findViewById(R.id.bosTurnOffSensorField);
        hotWaterText = (TextView) findViewById(R.id.bosHotWaterText);

        //initializing the switch for the boiler and the checkboxes and fields for time and sensor value scheduling
        GlobalMethods.initializeSwitch(Constant.DEVICE_BOILER_URI, boilerSwitch);
        GlobalMethods.initializeCheckBoxAndField(Constant.DEVICE_BOILER_URI,Constant.DEVICE_TIME_ENABLE_URI, turnOnTimeBox, turnOnTimeField);
        GlobalMethods.initializeCheckBoxAndField(Constant.DEVICE_BOILER_URI,Constant.DEVICE_TIME_DISABLE_URI, turnOffTimeBox, turnOffTimeField);
        GlobalMethods.initializeCheckBoxAndField(Constant.DEVICE_BOILER_URI,Constant.DEVICE_SENSOR_ENABLE_URI, turnOnSensorBox, turnOnSensorField);
        GlobalMethods.initializeCheckBoxAndField(Constant.DEVICE_BOILER_URI,Constant.DEVICE_SENSOR_DISABLE_URI, turnOffSensorBox, turnOffSensorField);

        //methods that add functionality to the switch, the time schedule checkboxes and fields and the sensor value checkboxes and fields
        GlobalMethods.switchFunctionality(boilerSwitch,Constant.DEVICE_BOILER_URI);
        GlobalMethods.checkBoxFunctionality(turnOnTimeBox, turnOnTimeField,Constant.DEVICE_BOILER_URI,Constant.DEVICE_TIME_ENABLE_URI);
        GlobalMethods.editTextFunctionality(turnOnTimeField, turnOnTimeBox,Constant.TIME_PATTERN);
        GlobalMethods.checkBoxFunctionality(turnOffTimeBox, turnOffTimeField,Constant.DEVICE_BOILER_URI,Constant.DEVICE_TIME_DISABLE_URI);
        GlobalMethods.editTextFunctionality(turnOffTimeField, turnOffTimeBox,Constant.TIME_PATTERN);
        GlobalMethods.checkBoxFunctionality(turnOnSensorBox, turnOnSensorField,Constant.DEVICE_BOILER_URI,Constant.DEVICE_SENSOR_ENABLE_URI);
        GlobalMethods.editTextFunctionality(turnOnSensorField, turnOnSensorBox,Constant.PERCENTAGE_PATTERN);
        GlobalMethods.checkBoxFunctionality(turnOffSensorBox, turnOffSensorField,Constant.DEVICE_BOILER_URI,Constant.DEVICE_SENSOR_DISABLE_URI);
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
                BoilerScreen.this.runOnUiThread(new Runnable(){         //This runs on User Interface level

                    @Override
                    public void run() {

                        //Get hot water information from the server and display it
                        String hotWaterSensor = HttpRequests.getRequest(Constant.SENSOR_HOT_WATER_URI);
                        hotWaterText.setText(hotWaterSensor + "  %");

                    }
                });
            }
        },10,10, TimeUnit.SECONDS);
    }
}





