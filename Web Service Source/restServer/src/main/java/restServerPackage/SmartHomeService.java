package restServerPackage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

/*
 * This Smart Home Service class implements the Web Service. Inside the constructor 
 * is an internal time calculator that gets the current time of the system and 
 * compares it with the enable/disable properties of all devices. Also checks 
 * and enables/disables the devices based on Sensor data.
 */

public class SmartHomeService {
	
	private Map<Integer,Device> devices = DatabaseClass.getDevices();
	private Map<Integer,Sensor> sensors = DatabaseClass.getSensors();
	
	Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	
	public SmartHomeService(){
		
//		long startTime = System.currentTimeMillis();

        String currentTime = sdf.format(cal.getTime());
        for(int i=1;i<7;i++){
        	Device tempDevice = devices.get(i);
        	if(tempDevice.getEnableTime().equals(currentTime)){			//Compares system time with device enable time
        		tempDevice.setState(Constant.DEVICE_ON);
        		tempDevice.setEnableTime(Constant.NO_TIME_VALUE);
        	}
        	if(tempDevice.getDisableTime().equals(currentTime)){		//Compares system time with device disable time
        		tempDevice.setState(Constant.DEVICE_OFF);
        		tempDevice.setDisableTime(Constant.NO_TIME_VALUE);
        	}
        	if(tempDevice.getEnableSensorValue() != Constant.NO_SENSOR_VALUE){		//Checks for Sensor enable data
        		switch (i){
        			case Constant.DEVICE_LIGHT_1_ID: 
        			case Constant.DEVICE_LIGHT_2_ID:	//In case the device is a light, check based on light level enable sensor data
        				if((Math.abs(sensors.get(Constant.SENSOR_LIGHT_LEVEL_ID).getValue() - tempDevice.getEnableSensorValue())) < Constant.SENSOR_OFFSET){
        					tempDevice.setState(Constant.DEVICE_ON);
        					tempDevice.setEnableSensorValue(Constant.NO_SENSOR_VALUE);
        				}		
        				break;
        			case Constant.DEVICE_BOILER_ID:		//In case the device is a boiler, check based on hot water enable sensor data
        				if((Math.abs(sensors.get(Constant.SENSOR_HOT_WATER_ID).getValue() - tempDevice.getEnableSensorValue())) < Constant.SENSOR_OFFSET){
        					tempDevice.setState(Constant.DEVICE_ON);
        					tempDevice.setEnableSensorValue(Constant.NO_SENSOR_VALUE);
        				}		
        				break;
        			case Constant.DEVICE_AIR_CONDITION_ID:	//In case the device is an air condition, check based on temperature enable sensor data
        				if((sensors.get(Constant.SENSOR_TEMPERATURE_ID).getValue() > tempDevice.getEnableSensorValue())){
        					tempDevice.setState(Constant.DEVICE_ON);
        					tempDevice.setEnableSensorValue(Constant.NO_SENSOR_VALUE);
        				}		
        				break;
        			case Constant.DEVICE_BLINDS_ID:			//In case the device is window blinds, check based on light level enable sensor data
        				if((Math.abs(sensors.get(Constant.SENSOR_LIGHT_LEVEL_ID).getValue() - tempDevice.getEnableSensorValue())) < Constant.SENSOR_OFFSET){
        					tempDevice.setState(Constant.DEVICE_ON);
        					tempDevice.setEnableSensorValue(Constant.NO_SENSOR_VALUE);
        				}		
        				break;		
        		}
        	}
        	
        	if(tempDevice.getDisableSensorValue() != Constant.NO_SENSOR_VALUE){		//Checks for Sensor disable data
        		switch (i){
        			case Constant.DEVICE_LIGHT_1_ID: 
        			case Constant.DEVICE_LIGHT_2_ID:	//In case the device is a light, check based on light level disable sensor data
        				if((Math.abs(sensors.get(Constant.SENSOR_LIGHT_LEVEL_ID).getValue() - tempDevice.getDisableSensorValue())) < Constant.SENSOR_OFFSET){
        					tempDevice.setState(Constant.DEVICE_OFF);
        					tempDevice.setDisableSensorValue(Constant.NO_SENSOR_VALUE);
        				}		
        				break;
    				case Constant.DEVICE_BOILER_ID:		//In case the device is a boiler, check based on hot water disable sensor data

        				if((Math.abs(sensors.get(Constant.SENSOR_HOT_WATER_ID).getValue() - tempDevice.getDisableSensorValue())) < Constant.SENSOR_OFFSET){
        					tempDevice.setState(Constant.DEVICE_OFF);
        					tempDevice.setDisableSensorValue(Constant.NO_SENSOR_VALUE);
        				}		
        				break;
        			case Constant.DEVICE_AIR_CONDITION_ID:	//In case the device is an air condition, check based on temperature disable sensor data

        				if((sensors.get(Constant.SENSOR_TEMPERATURE_ID).getValue() < tempDevice.getDisableSensorValue())){
        					tempDevice.setState(Constant.DEVICE_OFF);
        					tempDevice.setDisableSensorValue(Constant.NO_SENSOR_VALUE);
        				}		
        				break;
        			case Constant.DEVICE_BLINDS_ID:			//In case the device is window blinds, check based on light level disable sensor data
        				if((Math.abs(sensors.get(Constant.SENSOR_LIGHT_LEVEL_ID).getValue() - tempDevice.getDisableSensorValue())) < Constant.SENSOR_OFFSET){
        					tempDevice.setState(Constant.DEVICE_OFF);
        					tempDevice.setDisableSensorValue(Constant.NO_SENSOR_VALUE);
        				}		
        				break;		
        		}
        	}
        	
        devices.put(i, tempDevice);  //Place the new device with the correct parameters in the place of the initial device
        }
        
/*		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		double totalTime2 =  totalTime/1000.0;
		System.out.println("Total Cycle: " + totalTime);
*/
	}
	

	//Accepts an ID and returns the device with that ID
	public Device getDevice(int id){
		return devices.get(id);
	}
	
	//Accepts an ID and a Boolean value and updates the state of the device
	public void updateDevice(int id, Boolean state){
		Device tempDevice = devices.get(id);
		tempDevice.setState(state);
		devices.put(id, tempDevice);
	}
	
	//Accepts an ID and returns the sensor with that ID
	public Sensor getSensor(int id){
		return sensors.get(id);
	}
	
	//Accepts an ID and an integer value and updates the value of the sensor
	public void updateSensor(int id, int value){
		Sensor tempSensor = sensors.get(id);
		tempSensor.setValue(value);
		sensors.put(id, tempSensor);
	}
	
	//Accepts an ID and a String formatted to display time. Updates the time to enable the device with the given ID
	public void setDeviceEnableTime(int id, String time){
		Device tempDevice = devices.get(id);
		tempDevice.setEnableTime(time);
		devices.put(id, tempDevice);	
	}
	
	//Accepts an ID and a String formatted to display time. Updates the time to disable the device with the given ID
	public void setDeviceDisableTime(int id, String time){
		Device tempDevice = devices.get(id);
		tempDevice.setDisableTime(time);
		devices.put(id, tempDevice);
	}
	
	//Accepts an ID and an integer value and updates the device to turn on when a sensor reaches that value
	public void setDeviceEnableSensorValue(int id, int value){
		Device tempDevice = devices.get(id);
		tempDevice.setEnableSensorValue(value);
		devices.put(id, tempDevice);
	}
	
	//Accepts an ID and an integer value and updates the device to turn off when a sensor reaches that value
	public void setDeviceDisableSensorValue(int id, int value){
		Device tempDevice = devices.get(id);
		tempDevice.setDisableSensorValue(value);
		devices.put(id, tempDevice);
	}


}
