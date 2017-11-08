package restServerPackage;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/*
 * This Smart Home Resource class implements the REST resources. It is
 * called every time a client makes a request on the REST server.
 */

//Root URI
@Path("/resources")
public class SmartHomeResource {
	
	SmartHomeService shService = new SmartHomeService();	//The Smart Home Service instance
	
	/*
	 * Method that is called when the client sends an HTTP GET request to the URI /resources.
	 * Returns a text/plain String to the caller containing all the current service resources:
	 * Device names
	 * Device states
	 * Enable/disable device time
	 * Enable/disable device based on sensor
	 * Sensor names
	 * Sensor states
	 * (For debugging purposes)
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getInfo(){
		
		String returnMessage = "";
		
		for(int i=1;i<7;i++){
			returnMessage += shService.getDevice(i).getName();
			returnMessage += ": ";
			returnMessage += shService.getDevice(i).getState();
			returnMessage += System.lineSeparator();
			returnMessage += "		TimeEnable: ";
			returnMessage += shService.getDevice(i).getEnableTime();
			returnMessage += System.lineSeparator();
			returnMessage += "		TimeDisable: ";
			returnMessage += shService.getDevice(i).getDisableTime();
			returnMessage += System.lineSeparator();
			returnMessage += "		SensorEnable: ";
			returnMessage += shService.getDevice(i).getEnableSensorValue();
			returnMessage += System.lineSeparator();
			returnMessage += "		SensorDisable: ";
			returnMessage += shService.getDevice(i).getDisableSensorValue();
			returnMessage += System.lineSeparator();
		}
		
		returnMessage += System.lineSeparator();
		
		for(int i=1;i<5;i++){
			returnMessage += shService.getSensor(i).getName();
			returnMessage += ": ";
			returnMessage += shService.getSensor(i).getValue();
			returnMessage += System.lineSeparator();
		}
		
		return returnMessage;
	}
	
	/*
	 * Method that is called when the client sends an HTTP GET request to the URI /resources/device/{deviceId}
	 * Returns a text/plain String to the caller containing the state of the device "deviceId"
	 */
	@GET
	@Path("device/{deviceId}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getDeviceState(@PathParam("deviceId") int id) {
		System.out.println("GET device/" + id);
		Device tempDevice = shService.getDevice(id);
		return Boolean.toString(tempDevice.getState());
	}
	
	/*
	 * Method that is called when the client sends an HTTP PUT request to the URI /resources/device/{deviceId}
	 * It places the string "state" as the state of the device "deviceId"
	 * Returns "Success"
	 */
	@PUT
	@Path("/device/{deviceId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String setDeviceState(@PathParam("deviceId") int id, String state) {
		System.out.println("PUT device/" + id + " State: " + state);
		shService.updateDevice(id, Boolean.valueOf(state));
		return "Success";
	}
	
	/*
	 * Method that is called when the client sends an HTTP GET request to the URI /resources/sensor/{sensorId}
	 * Returns a text/plain String to the caller containing the value of the sensor "sensorId"
	 */
	@GET
	@Path("/sensor/{sensorId}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getSensorState(@PathParam("sensorId") int id) {
		System.out.println("GET sensor/" + id);
		Sensor tempSensor = shService.getSensor(id);
		return Integer.toString(tempSensor.getValue());
		
	}

	/*
	 * Method that is called when the client sends an HTTP PUT request to the URI /resources/sensor/{sensorId}
	 * It places the string "value" as the value of the sensor "sensorId"
	 * Returns "Success"
	 */
	@PUT
	@Path("sensor/{sensorId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String setSensorValue(@PathParam("sensorId") int id, String value) {
		System.out.println("PUT sensor/" + id + " Value: " + value);
		shService.updateSensor(id, Integer.parseInt(value));
		return "Success";
	}
	
	/*
	 * Method that is called when the client sends an HTTP GET request to the URI /resources/device/{deviceId}/enable/time
	 * Returns a text/plain String to the caller containing the time when the device "deviceId" is to be enabled
	 */
	@GET
	@Path("device/{deviceId}/enable/time")
	@Produces(MediaType.TEXT_PLAIN)
	public String getDeviceEnableTimeValue(@PathParam("deviceId") int id) {
		System.out.println("GET device/" + id + "/enable/time");
		Device tempDevice = shService.getDevice(id);
		return tempDevice.getEnableTime();
	}
	
	/*
	 * Method that is called when the client sends an HTTP PUT request to the URI /resources/device/{deviceId}/enable/time
	 * It places the string "time" as the time when the device "deviceId" is to be enabled
	 * Returns "Success"
	 */
	@PUT
	@Path("/device/{deviceId}/enable/time")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String setDeviceEnableTime(@PathParam("deviceId") int id,  String time) {
		System.out.println("PUT device/" + id + " Enable: Time: " + time);
		shService.setDeviceEnableTime(id, time);
		return "Success";
	}
	
	/*
	 * Method that is called when the client sends an HTTP GET request to the URI /resources/device/{deviceId}/disable/time
	 * Returns a text/plain String to the caller containing the time when the device "deviceId" is to be disabled
	 */
	@GET
	@Path("device/{deviceId}/disable/time")
	@Produces(MediaType.TEXT_PLAIN)
	public String getDeviceDisableTime(@PathParam("deviceId") int id) {
		System.out.println("GET device/" + id + "/disable/time");
		Device tempDevice = shService.getDevice(id);
		return tempDevice.getDisableTime();
	}
	
	/*
	 * Method that is called when the client sends an HTTP PUT request to the URI /resources/device/{deviceId}/disable/time
	 * It places the string "time" as the time when the device "deviceId" is to be disabled
	 * Returns "Success"
	 */
	@PUT
	@Path("/device/{deviceId}/disable/time")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String setDeviceDisableTime(@PathParam("deviceId") int id,  String time) {
		System.out.println("PUT device/" + id + " Disable: Time: " + time);
		shService.setDeviceDisableTime(id, time);
		return "Success";
	}
	
	/*
	 * Method that is called when the client sends an HTTP GET request to the URI /resources/device/{deviceId}/enable/sensorValue
	 * Returns a text/plain String to the caller containing the value that the sensor must be for the device "deviceId" to be enabled
	 */
	@GET
	@Path("device/{deviceId}/enable/sensorValue")
	@Produces(MediaType.TEXT_PLAIN)
	public String getDeviceEnableSensorValue(@PathParam("deviceId") int id) {
		System.out.println("GET device/" + id + "/enable/sensorValue");
		Device tempDevice = shService.getDevice(id);
		return Integer.toString(tempDevice.getEnableSensorValue());
	}
	
	/*
	 * Method that is called when the client sends an HTTP PUT request to the URI /resources/device/{deviceId}/enable/sensorValue
	 * It places the string "value" as the value that the sensor must be for the device "deviceId" to be enabled
	 * Returns "Success"
	 */
	@PUT
	@Path("/device/{deviceId}/enable/sensorValue")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String setDeviceEnableSensor(@PathParam("deviceId") int id,  String value) {
		System.out.println("PUT device/" + id + " Enable: Sensor Value: " + value);
		shService.setDeviceEnableSensorValue(id, Integer.parseInt(value));
		return "Success";
	}
	
	/*
	 * Method that is called when the client sends an HTTP GET request to the URI /resources/device/{deviceId}/disable/sensorValue
	 * Returns a text/plain String to the caller containing the value that the sensor must be for the device "deviceId" to be disabled
	 */
	@GET
	@Path("device/{deviceId}/disable/sensorValue")
	@Produces(MediaType.TEXT_PLAIN)
	public String getDeviceDisableSensorValue(@PathParam("deviceId") int id) {
		System.out.println("GET device/" + id + "/disable/sensorValue");
		Device tempDevice = shService.getDevice(id);
		return Integer.toString(tempDevice.getDisableSensorValue());
	}
	
	/*
	 * Method that is called when the client sends an HTTP PUT request to the URI /resources/device/{deviceId}/disable/sensorValue
	 * It places the string "value" as the value that the sensor must be for the device "deviceId" to be disabled
	 * Returns "Success"
	 */
	@PUT
	@Path("/device/{deviceId}/disable/sensorValue")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String setDeviceDisableSensor(@PathParam("deviceId") int id,  String value) {
		System.out.println("PUT device/" + id + " Disable: Sensor Value: " + value);
		shService.setDeviceDisableSensorValue(id, Integer.parseInt(value));
		return "Success";
	}

}
