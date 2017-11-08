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
The AirConditionScreen class implements the activity that is responsible for the management
of the smart home air condition device. Has the ability to enable and disable the air
condition and set it to be enabled or disabled when the temperature reaches a certain
value. Displays information about the temperature of the room.
 */

public class AirConditionScreen extends AppCompatActivity {

    private TextView temperatureText;

    /*
    Method called when the activity is created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_condition_screen);

        //Permit all policy for the threads
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //The view objects and their bindings to the layout xml counterpart of the activity
        Switch airConditionSwitch = (Switch) findViewById(R.id.acsAirConditionSwitch);
        CheckBox turnOnTemperatureBox = (CheckBox) findViewById(R.id.acsTurnOnTemperatureBox);
        CheckBox turnOffTemperatureBox = (CheckBox) findViewById(R.id.acsTurnOffTemperatureBox);
        EditText turnOnTemperatureField = (EditText) findViewById(R.id.acsTurnOnTemperatureField);
        EditText turnOffTemperatureField = (EditText) findViewById(R.id.acsTurnOffTemperatureField);
        temperatureText = (TextView) findViewById(R.id.acsTemperatureText);

        //initializing the switch for the air condition and the checkboxes and fields for sensor value scheduling
        GlobalMethods.initializeSwitch(Constant.DEVICE_AIR_CONDITION_URI, airConditionSwitch);
        GlobalMethods.initializeCheckBoxAndField(Constant.DEVICE_AIR_CONDITION_URI,Constant.DEVICE_SENSOR_ENABLE_URI, turnOnTemperatureBox, turnOnTemperatureField);
        GlobalMethods.initializeCheckBoxAndField(Constant.DEVICE_AIR_CONDITION_URI,Constant.DEVICE_SENSOR_DISABLE_URI, turnOffTemperatureBox, turnOffTemperatureField);

        //methods that add functionality to the switch and the time schedule checkboxes and fields
        GlobalMethods.switchFunctionality(airConditionSwitch,Constant.DEVICE_AIR_CONDITION_URI);
        GlobalMethods.checkBoxFunctionality(turnOnTemperatureBox, turnOnTemperatureField,Constant.DEVICE_AIR_CONDITION_URI, Constant.DEVICE_SENSOR_ENABLE_URI);
        GlobalMethods.editTextFunctionality(turnOnTemperatureField, turnOnTemperatureBox,Constant.PERCENTAGE_PATTERN);

        //methods that add functionality to the sensor schedule checkboxes and fields
        GlobalMethods.checkBoxFunctionality(turnOffTemperatureBox, turnOffTemperatureField,Constant.DEVICE_AIR_CONDITION_URI, Constant.DEVICE_SENSOR_DISABLE_URI);
        GlobalMethods.editTextFunctionality(turnOffTemperatureField, turnOffTemperatureBox,Constant.PERCENTAGE_PATTERN);

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
                AirConditionScreen.this.runOnUiThread(new Runnable(){       //This runs on User Interface level

                    @Override
                    public void run() {
                        //Get temperature information from the server and display it
                        String tempSensor = HttpRequests.getRequest(Constant.SENSOR_TEMPERATURE_URI);
                        temperatureText.setText(tempSensor + "° C");

                    }
                });
            }
        },10,10, TimeUnit.SECONDS);
    }
}







