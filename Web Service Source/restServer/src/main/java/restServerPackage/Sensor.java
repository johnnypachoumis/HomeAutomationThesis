package restServerPackage;

/*
 * This Sensor class contains the information about a single sensor or mock
 * sensor of a smart home. Comes with necessary getters and setters. Provides 
 * function for changing the value of the sensor.
 */

public class Sensor {

	private String name;
	private int value;
	
	public Sensor(){
	}
	
	public Sensor(String name){
		this.name = name;
		this.value = Constant.SENSOR_OFF;
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}