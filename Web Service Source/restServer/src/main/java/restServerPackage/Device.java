package restServerPackage;

/*
 * This Device class contains the information about a single component of
 * a smart home. Comes with necessary getters and setters. Provides function
 * for enabling/disabling the device at a certain time of the day as well as
 * enabling/disabling the device based on sensor values.
 */

public class Device {

	private String name;
	private Boolean state;
	private String enableTime;			//Turn on based on time
	private String disableTime;			//Turn off based on time
	private int enableSensorValue;		//Turn on based on the value of a sensor
	private int disableSensorValue;		//Turn off based on the value of a sensor
	
	public Device(String name){
		this.name = name;
		this.state = Constant.DEVICE_OFF;
		this.enableTime = Constant.NO_TIME_VALUE;
		this.disableTime = Constant.NO_TIME_VALUE;
		this.enableSensorValue = Constant.NO_SENSOR_VALUE;
		this.disableSensorValue = Constant.NO_SENSOR_VALUE;		
	}

	public String getName() {
		return name;
	}

	public Boolean getState() {
		return state;
	}

	public void setState(Boolean state) {
		this.state = state;
	}
		
	public String getEnableTime() {
		return enableTime;
	}

	public void setEnableTime(String enableTime) {
		this.enableTime = enableTime;
	}


	public String getDisableTime() {
		return disableTime;
	}

	public void setDisableTime(String disableTime) {
		this.disableTime = disableTime;
	}

	public int getEnableSensorValue() {
		return enableSensorValue;
	}

	public void setEnableSensorValue(int enableSensorValue) {
		this.enableSensorValue = enableSensorValue;
	}

	public int getDisableSensorValue() {
		return disableSensorValue;
	}

	public void setDisableSensorValue(int disableSensorValue) {
		this.disableSensorValue = disableSensorValue;
	}

}