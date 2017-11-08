package restServerPackage;

/*
 * This Constant class contains the information about the server constants
 */

public final class Constant {
	
	public static final int NO_SENSOR_VALUE = -1;
	public static final String NO_TIME_VALUE = "";
	public static final int SENSOR_OFF = 0;
	public static final Boolean DEVICE_OFF = false;
	public static final Boolean DEVICE_ON = true;
	public static final int SENSOR_OFFSET = 5;
	public static final int DEVICE_LIGHT_1_ID = 1;
	public static final int DEVICE_LIGHT_2_ID = 2;
	public static final int DEVICE_BOILER_ID = 3;
	public static final int DEVICE_AIR_CONDITION_ID = 4;
	public static final int DEVICE_BLINDS_ID = 5;
	public static final int DEVICE_ALARM_ID = 6;
	public static final int SENSOR_TEMPERATURE_ID = 1;
	public static final int SENSOR_HOT_WATER_ID = 2;
	public static final int SENSOR_LIGHT_LEVEL_ID = 3;
	public static final int SENSOR_BREAK_IN_ID = 4;
	
	private Constant(){
		throw new AssertionError();
	}

}
