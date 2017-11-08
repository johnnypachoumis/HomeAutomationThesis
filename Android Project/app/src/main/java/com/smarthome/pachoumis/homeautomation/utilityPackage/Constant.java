package com.smarthome.pachoumis.homeautomation.utilityPackage;

/*
The Constant class is used to store the global constants of the app. Contains information
about the device and sensor valid states, the possible URIs of the REST server and their
extensions, the null device and sensor states, the user password, the server IP and the
regular expression patterns that are used throughout the app
 */
public final class Constant {

    public static final String DEVICE_ON = "true";
    public static final String DEVICE_OFF = "false";
    public static final String SENSOR_OFF = "0";
    public static final String SENSOR_ON = "1";

    public static final String DEVICE_LIGHT_1_URI = "device/1";
    public static final String DEVICE_LIGHT_2_URI = "device/2";
    public static final String DEVICE_BOILER_URI = "device/3";
    public static final String DEVICE_AIR_CONDITION_URI = "device/4";
    public static final String DEVICE_BLINDS_URI = "device/5";
    public static final String DEVICE_ALARM_URI = "device/6";
    public static final String SENSOR_TEMPERATURE_URI = "sensor/1";
    public static final String SENSOR_HOT_WATER_URI = "sensor/2";
    public static final String SENSOR_LIGHT_LEVEL_URI = "sensor/3";
    public static final String SENSOR_BREAK_IN_URI = "sensor/4";

    public static final String DEVICE_TIME_ENABLE_URI = "/enable/time";
    public static final String DEVICE_TIME_DISABLE_URI = "/disable/time";

    public static final String DEVICE_SENSOR_ENABLE_URI = "/enable/sensorValue";
    public static final String DEVICE_SENSOR_DISABLE_URI = "/disable/sensorValue";

    public static final String NO_TIME_VALUE = "";
    public static final String NO_SENSOR_VALUE = "-1";
    public static final String NO_DISPLAY_VALUE = "";

    public static final String TIME_PATTERN = "(0[0-9]|1[1-9]|2[0-3]):[0-5][0-9]";
    public static final String PERCENTAGE_PATTERN = "[0-9]|[1-9][0-9]|100";

    public static final String USER_PASSWORD = "1234567";
    public static final String SERVER_IP = "83.212.118.164";

}
