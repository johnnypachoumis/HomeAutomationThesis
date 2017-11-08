#include <SPI.h>
#include <Ethernet.h>
#include <Servo.h>

//MAC and IP addresses initialization
byte mac[] = { 0x90,0xA2,0xDA, 0x0D, 0xD5,0x49 };
IPAddress  restServer(83,212,118,164 );
IPAddress ip(192,168,1,177);


//Variables taken from the Server
boolean serverLight1;
boolean serverLight2;
boolean serverBoiler;
boolean serverAirCondition;
boolean serverBlinds;
boolean serverAlarm;

//The client that communicates with the REST server
EthernetClient client; 

//Pin and Component matching
Servo boiler;
const int roomLight1 = A4;
const int roomLight2 = A2;
const int alarmGreenLight = A1;
const int alarmRedLight = A0;
const int boilerLight = 5;
const int blindsSimLight = 6;
const int alarmBeeper = 7;
const int alarmButton = 8;
const int airCondition = 9;
const int boilerServo = 3;
const int roomLightLevel = A5;
const int roomTempLevel = A3;

//Setting constants for devices, sensors and values
const String DEVICE_LIGHT1 = "device/1";
const String DEVICE_LIGHT2 = "device/2";
const String DEVICE_BOILER = "device/3";
const String DEVICE_AIRCONDITION = "device/4";
const String DEVICE_BLINDS = "device/5";
const String DEVICE_ALARM = "device/6";

const String SENSOR_TEMPERATURE = "sensor/1";
const String SENSOR_HOTWATER = "sensor/2";
const String SENSOR_LIGHTLEVEL = "sensor/3";
const String SENSOR_BREAKIN = "sensor/4";

const String DEVICE_ON = "true";
const String DEVICE_OFF = "false";

const String SENSOR_ON = "1";
const String SENSOR_OFF = "0";

//Sensor values
int boilerPercentage = 0;
int temperature;
int lightLevel;
int alarmTriggered = 0;

//Other variables
float tempTemp;
int boilerCounter = 0;
int blindsState = 0;
unsigned long time;


void setup() {
    
    //Setting I/O for PINS
    pinMode(roomLight1,OUTPUT);
    pinMode(roomLight2,OUTPUT);
    pinMode(alarmGreenLight,OUTPUT); 
    pinMode(alarmRedLight,OUTPUT);
    pinMode(boilerLight,OUTPUT); 
    pinMode(blindsSimLight,OUTPUT); 
    pinMode(alarmButton,INPUT);
    pinMode(airCondition,OUTPUT);
    
    //Attaching and initializing boiler
    boiler.attach(boilerServo);
    boiler.write(0);
  
    //Starting Serial port connection with computer
    Serial.begin(9600);
  
    //Obtaining and printing IP address
    Serial.println("Starting Ethernet Connection...      ");
    Ethernet.begin(mac,ip); 
    ip = Ethernet.localIP();
    printIPAddress();
}

void loop() {
  
    //Initiating Cycle Time Calculation
    time = millis();
  
    //Getting device status from REST server and converting them to boolean values
    serverLight1 = toBoolean(getRequest(DEVICE_LIGHT1));
    serverLight2 = toBoolean(getRequest(DEVICE_LIGHT2));
    serverBoiler = toBoolean(getRequest(DEVICE_BOILER));
    serverAirCondition = toBoolean(getRequest(DEVICE_AIRCONDITION));
    serverBlinds = toBoolean(getRequest(DEVICE_BLINDS));
    serverAlarm = toBoolean(getRequest(DEVICE_ALARM));
    
    //Reading the sensor temperature, modifying it (Voltage to Celcius) and uploading it to server
    temperature = analogRead(roomTempLevel);
    tempTemp = (temperature/1024.0) * 5.0;
    tempTemp = (tempTemp - .5) * 100;
    temperature = (int)(tempTemp + .5);
    putRequest(SENSOR_TEMPERATURE,String(temperature));
    
    //Reading the sensor light level, modifying it (Voltage to Percentage) and uploading it to server
    lightLevel = analogRead(roomLightLevel);
    lightLevel = map(lightLevel,0,1023,0,100);
    putRequest(SENSOR_LIGHTLEVEL,String(lightLevel));
    
    //Turning on/off the lights
    if(serverLight1)
        digitalWrite(roomLight1,HIGH);
    else
        digitalWrite(roomLight1,LOW);
        
    if(serverLight2)
        digitalWrite(roomLight2,HIGH);
    else
        digitalWrite(roomLight2,LOW);
        
    /* Turning on/off boiler
       When the boiler is turned on, the servo starts to rotate slowly
       just like the indicator needle on a real boiler. When the boiler
       reaches ~100% then it shuts down by itself. If the boiler has hot
       water and shuts down the water begins to cool and the needle to
       rotate backwards till there is no hot water left.               */
    if(serverBoiler){
        digitalWrite(boilerLight,HIGH);
        if(boilerCounter < 176){
            boilerCounter += 8;
            boiler.write(boilerCounter);
            boilerPercentage = map(boilerCounter,0,179,0,100);
            putRequest(SENSOR_HOTWATER,String(boilerPercentage));
        }
        else{
            putRequest(DEVICE_BOILER,DEVICE_OFF);   
        }
    }
    else{
        digitalWrite(boilerLight,LOW);
        if (boilerCounter > 0){
            boilerCounter-= 8;
            boiler.write(boilerCounter);
            boilerPercentage = map(boilerCounter,0,179,0,100);
            putRequest(SENSOR_HOTWATER,String(boilerPercentage));
        }    
    }
    
    /* Turning on/off the air condition
       The air condition system consists of a fan. It can only provide
       cold air so it's not technically an air condition but a smart fan */
    if(serverAirCondition)
        digitalWrite(airCondition,HIGH);
    else
        digitalWrite(airCondition,LOW);
        
    /* Raising and lowering the window blinds
       The window blinds are simulated by an LED. When the blinds are opening
       the LED turns on gradually till it reaches 100% brightness and the blinds
       are completely open. When the blinds are closing the LED shines gradually
       less till it is turned off.                                              */      
    if(serverBlinds && blindsState == 0){
        for(int i=0;i<256;i+=4){
            analogWrite(blindsSimLight,i);
            delay(20);
        }
        blindsState = 1;
    }
    else if (!serverBlinds && blindsState == 1){
        for(int i=252;i>=0;i-=4){
            analogWrite(blindsSimLight,i);
            delay(20);
        }
        blindsState = 0;
    }
    
    /* Turning on/off the alarm.   
       When the alarm is on the red LED is on. When the alarm is off the green LED is on.*/
    if(serverAlarm){
        digitalWrite(alarmRedLight,HIGH);
        digitalWrite(alarmGreenLight,LOW);
    }
    else{
        digitalWrite(alarmRedLight,LOW);
        digitalWrite(alarmGreenLight,HIGH);
    }
    
    /* Simulated break in button
       When this button is pressed for a couple of seconds, if the alarm is on, the beeper goes off 
       */
    if(digitalRead(alarmButton) == HIGH && serverAlarm){
        putRequest(SENSOR_BREAKIN,SENSOR_ON);
    }
    alarmTriggered = getRequest(SENSOR_BREAKIN).toInt();
    if (alarmTriggered == 1)
        tone(alarmBeeper,330);
    else
        noTone(alarmBeeper);
    
    //Displaying Cycle Time
    time = millis() - time;
    Serial.println();
    Serial.println("Cycle Time : " + String(time/1000.0));  
}

//Function that prints the IP Address 
void printIPAddress(){
  Serial.print("My IP address: "); 
    for (byte thisByte = 0; thisByte < 4; thisByte++) {  
        Serial.print(ip[thisByte], DEC); 
        Serial.print("."); 
    }
    Serial.println();
}

/*Function that sends HTTP GET requests to the REST server. 
  INPUT: The URI for the request.
  OUTPUT: The server response string. 
*/
String getRequest(String theRequest){
    Serial.print("Connecting : ");
    while (!client.connect(restServer, 8080)) {
        Serial.println("Connection failed");
        Serial.print("Reconnecting : ");
    }
    
    client.println("GET /restServer/resources/" + theRequest); 
    Serial.print("GET /restServer/resources/" + theRequest + " : ");
    delay(500);
    String response = "";
    while (client.available()) {
        char c = client.read();
        response.concat(c);
    } 
    
    Serial.println(response);
    client.flush();
    if (!client.connected()) 
        client.stop();
    return response;
}

/*Function that sends HTTP PUT requests to the REST server. 
  INPUT: The URI for the request.
         The message to be sent.  */
void putRequest(String theRequest, String message){
    String serverIP = "";
    Serial.print("Connecting : ");
    while (!client.connect(restServer, 8080)) {
        Serial.println("Connection failed");
        Serial.print("Reconnecting : ");
    }
    
    String tempRequest = "";
    for (byte thisByte = 0; thisByte < 4; thisByte++) {  
        serverIP += String(restServer[thisByte]);
        if(thisByte <3) 
            serverIP +=("."); 
    }
    
    tempRequest += "PUT /restServer/resources/" + theRequest + " HTTP/1.1\r\n";
    tempRequest += "Host: " + serverIP + "\r\n";
    tempRequest += "Content-Type:   text/plain; charset=UTF-8\r\n";
    tempRequest += "Content-Length: " + String(message.length(),DEC) + "\r\n";
    tempRequest += "\r\n";
    tempRequest += message;
    
    client.println(tempRequest); 
    Serial.println("PUT /restServer/resources/" + theRequest);
    delay(500);
    client.flush();
    client.stop();
}

//Function that returns the boolean value of a string
boolean toBoolean(String aString){
    if(aString.equals("true")){
        return true;
    }else{ 
        return false;
    }
}
