#include <WiFiManager.h>
#include <ESP8266WiFi.h>
#include <PubSubClient.h>
// GPIO 5 D1
#define LED 5
// WiFi
const char *ssid = "iPhone 14 Pro max";
//const char *ssid = "iPhone 14 Pro max"; // Enter your WiFi name
const char *password = "12345678"; 
//const char *password = "sbxxwcnm";  // Enter WiFi password
// MQTT Broker
const char *mqtt_broker = "ip地址";
const char *topic = "mtopic/1";
const char *mqtt_username = "admin203";
const char *mqtt_password = "admin203";
const int mqtt_port = 1883;

bool ledState = false;
bool State = false;
WiFiClient espClient;
PubSubClient client(espClient);

void setup() {
    // Set software serial baud to 115200;
    Serial.begin(115200);
    delay(1000); // Delay for stability

    WiFiManager wm;
    // 擦除设置，它们被保存在芯片中
    //wm.resetSettings();
    //自动连接使用保存的凭证，
//如果连接失败，它将启动一个具有指定名称的接入点("AutoConnectAP")，
//如果为空将自动生成SSID，如果密码为空将是匿名AP (wm.autoConnect())
//然后进入阻塞循环等待配置并返回成功结果
    bool res;
    // res = wm.autoConnect(); //从芯片id自动生成AP名称
    // res = wm.autoConnect("AutoConnectAP"); // 匿名连接
    res = wm.autoConnect("测试","12345678"); // 指定密码的AP
    //根据返回结果判断连接是否生效
    if(!res) {
        Serial.println("Failed to connect");
        // ESP.restart();
    } 
    else {
        //WiFi连接成功    
        Serial.println("connected...yeey :)");
    }
    // Connecting to a WiFi network
 //   WiFi.begin(ssid, password);
    while (WiFi.status() != WL_CONNECTED) {
        delay(500);
        Serial.println("Connecting to WiFi...");
    }
    Serial.println("Connected to the WiFi network");
    // Setting LED pin as output
    pinMode(LED, OUTPUT);
    digitalWrite(LED, HIGH);  // Turn off the LED initially
    // Connecting to an MQTT broker
    client.setServer(mqtt_broker, mqtt_port);
    client.setCallback(callback);
    while (!client.connected()) {
        String client_id = "esp8266-client-";
        client_id += String(WiFi.macAddress());
        Serial.printf("The client %s connects to the public MQTT broker\n", client_id.c_str());
        if (client.connect(client_id.c_str(), mqtt_username, mqtt_password)) {
            Serial.println("Public EMQX MQTT broker connected");
        } else {
            Serial.print("Failed with state ");
            Serial.print(client.state());
            delay(2000);
        }
    }
    // Publish and subscribe
    client.publish(topic, "hello emqx");
    client.subscribe(topic);
}
void callback(char *topic, byte *payload, unsigned int length) {
    Serial.print("Message arrived in topic: ");
    Serial.println(topic);
    Serial.print("Message: ");
    String message;
    for (int i = 0; i < length; i++) {
        message += (char) payload[i];  // Convert *byte to string
    }
    Serial.print(message);
    if (message == "break" && !ledState) {
        digitalWrite(LED, LOW);  // Turn on the LED

        ledState = true;
        State=true;
    }
    if (message == "off" && ledState) {
        digitalWrite(LED, LOW); // Turn off the LED
        ledState = false;
        State=false;
    }
    Serial.println();
    Serial.println("-----------------------");
}
void loop_buzzer() {  
  digitalWrite(LED, LOW);  
  delay(500);  
  digitalWrite(LED, HIGH);  
  delay(500);  
}
void loop() {
    client.loop();
    delay(100); // Delay for a short period in each loop iteration
      while(State==true)
        {  client.loop();
           loop_buzzer();
        }
}
