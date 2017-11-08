package com.smarthome.pachoumis.homeautomation.activityPackage;

import android.content.Intent;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.smarthome.pachoumis.homeautomation.R;
import com.smarthome.pachoumis.homeautomation.utilityPackage.Constant;
import com.smarthome.pachoumis.homeautomation.utilityPackage.HttpRequests;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
The HomeScreen class implements the activity that gives the user access to all the devices
by calling instances of the other activities. Is the activity that launches when the user
first runs the app. Displays information about the alarm status, the break in sensor status,
the light level, the temperature and the hot water level.
 */
public class HomeScreen extends AppCompatActivity {


    private ScheduledExecutorService scheduledExecutorService;
    private TextView alarmStatusText;
    private TextView homeStatusText;
    private TextView lightLevelText;
    private TextView hotWaterText;
    private TextView temperatureText;

    /*
    Method called when the activity is created
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        //Permit all policy for the threads
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //The view objects and their bindings to the layout xml counterpart of the activity
        Button alarmButton = (Button) findViewById(R.id.hsAlarmButton);
        Button lightsButton = (Button) findViewById(R.id.hsLightsButton);
        Button boilerButton = (Button) findViewById(R.id.hsBoilerButton);
        Button airConditionButton = (Button) findViewById(R.id.hsAirConditionButton);
        Button blindsButton = (Button) findViewById(R.id.hsBlindsButton);

        alarmStatusText = (TextView) findViewById(R.id.hsAlarmStatusText);
        homeStatusText = (TextView) findViewById(R.id.hsHomeStatusText);
        lightLevelText = (TextView) findViewById(R.id.hsLightLevelText);
        hotWaterText = (TextView) findViewById(R.id.hsHotWaterText);
        temperatureText = (TextView) findViewById(R.id.hsTemperatureText);

        //The buttons for navigating to the other activities
        runActivityButton(alarmButton,AlarmScreen.class);
        runActivityButton(lightsButton,LightsScreen.class);
        runActivityButton(boilerButton,BoilerScreen.class);
        runActivityButton(airConditionButton,AirConditionScreen.class);
        runActivityButton(blindsButton,BlindsScreen.class);
    }

    /*
    Method called when the activity is resumed. Also runs after onCreate.
    */
    @Override
    protected void onResume() {
        super.onResume();

        //Scheduling a new Thread to run every 10 seconds
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable(){

            @Override
            public void run() {
                HomeScreen.this.runOnUiThread(new Runnable(){       //This runs on User Interface level

                    @Override
                    public void run() {

                        //Get temperature information from the server and display it
                        String tempSensor = HttpRequests.getRequest(Constant.SENSOR_TEMPERATURE_URI);
                        temperatureText.setText(tempSensor + "° C");

                        //Get hot water sensor information from the server and display it
                        String hotWSensor = HttpRequests.getRequest(Constant.SENSOR_HOT_WATER_URI);
                        hotWaterText.setText(hotWSensor + "  %");

                        //Get light level information from the server and display it
                        String lightSensor = HttpRequests.getRequest(Constant.SENSOR_LIGHT_LEVEL_URI);
                        lightLevelText.setText(lightSensor + "  %");

                        //Get alarm status and display color and text accordingly
                        String alarmStatus = HttpRequests.getRequest(Constant.DEVICE_ALARM_URI);
                        switch (alarmStatus) {
                            case Constant.DEVICE_ON:
                                alarmStatusText.setText(R.string.alarmEnabledMessage);
                                alarmStatusText.setTextColor(Color.rgb(102, 153, 50));  //Green Color
                                break;
                            case Constant.DEVICE_OFF:
                                alarmStatusText.setText(R.string.alarmDisabledMessage); //Red Color
                                alarmStatusText.setTextColor(Color.rgb(204, 0, 0));
                                break;
                            default:
                                alarmStatusText.setText(R.string.alarmNoServiceMessage);
                                alarmStatusText.setTextColor(Color.rgb(117, 118, 120)); //Grey Color
                                break;
                        }

                        /*
                        Displays information about the break in sensor. If the alarm is enabled and the break in
                        sensor displays no "motion" the text becomes green and displays the appropriate text. If
                        the alarm is enabled and the break in sensor displays "motion", the text becomes red and
                        displays again the appropriate text. If the alarm is disabled then there is no motion data
                        to be displayed and the appropriate text is being displayed
                        */
                        String homeStatus = HttpRequests.getRequest(Constant.SENSOR_BREAK_IN_URI);
                        switch (alarmStatus) {
                            case Constant.DEVICE_OFF:
                                homeStatusText.setText(R.string.disabledAlarmMessage);
                                homeStatusText.setTextColor(Color.rgb(255, 136, 0));
                                break;
                            case Constant.DEVICE_ON:
                                if (homeStatus.equals(Constant.SENSOR_ON)) {
                                    homeStatusText.setText(R.string.alarmMotionDetectionMessage);
                                    homeStatusText.setTextColor(Color.rgb(204, 0, 0));
                                } else {
                                    homeStatusText.setText(R.string.alarmMotionDetectionMessage);
                                    homeStatusText.setTextColor(Color.rgb(102, 153, 50));
                                }
                                break;
                            default:
                                homeStatusText.setText(R.string.alarmNoServiceMessage);
                                homeStatusText.setTextColor(Color.rgb(117, 118, 120));
                                break;
                        }
                    }
                });
            }
        },10,10, TimeUnit.SECONDS);
    }

    /*
    Method called when the activity is paused. Kills the scheduled thread
    */
    @Override
    protected void onPause() {
        super.onPause();

        scheduledExecutorService.shutdownNow();

    }

    //If the user presses the button, navigate to the corresponding activity
    private void runActivityButton(Button aButton, final Class aClass){
        assert aButton != null;
        aButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent anIntent = new Intent(HomeScreen.this, aClass);
                HomeScreen.this.startActivity(anIntent);
            }
        });
    }
}