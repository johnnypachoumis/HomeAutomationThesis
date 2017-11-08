package restServerPackage;

import java.util.HashMap;
import java.util.Map;

/*
 * This Database class contains the information about all devices and sensors
 * of a smart home. The devices and sensors are initialized here. Comes with 
 * necessary getters and setters. Creates 6 devices and 4 sensors and maps them
 * with IDs.
 */

public class DatabaseClass {
	
	private static Map<Integer, Device> devices = new HashMap<>();
	private static Map<Integer, Sensor> sensors = new HashMap<>();
	
	static{
		devices.put(Constant.DEVICE_LIGHT_1_ID, new Device("Light1"));
		devices.put(Constant.DEVICE_LIGHT_2_ID, new Device("Light2"));
		devices.put(Constant.DEVICE_BOILER_ID, new Device("Boiler"));
		devices.put(Constant.DEVICE_AIR_CONDITION_ID, new Device("AirCondition"));
		devices.put(Constant.DEVICE_BLINDS_ID, new Device("Blinds"));
		devices.put(Constant.DEVICE_ALARM_ID, new Device("Alarm"));
		
		sensors.put(Constant.SENSOR_TEMPERATURE_ID, new Sensor("Temperature"));
		sensors.put(Constant.SENSOR_HOT_WATER_ID, new Sensor("Hot Water"));
		sensors.put(Constant.SENSOR_LIGHT_LEVEL_ID, new Sensor("Light Level"));
		sensors.put(Constant.SENSOR_BREAK_IN_ID, new Sensor("Break In"));
	}
	
	public static Map<Integer, Device> getDevices(){
		return devices;
	}
	
	public static Map<Integer, Sensor> getSensors(){
		return sensors;
	}

}
