#include <Arduino.h>
#include <ESP8266WiFi.h>
#include <ESP8266WiFiMulti.h>
#include <ESP8266HTTPClient.h>
#include <WiFiClient.h>
#include <DHTesp.h>
ESP8266WiFiMulti WiFiMulti;
DHTesp dht;
String login, token;

#define PIR 16
#define v 13
void setup() {
  
  Serial.begin(115200);
  dht.setup(16,DHTesp::DHT11);
  for (uint8_t t = 4; t > 0; t--) {
    Serial.printf("[SETUP] WAIT %d...\n", t);
    Serial.flush();
    delay(1000);
  }

  WiFi.mode(WIFI_STA);
  WiFiMulti.addAP("ARRIS-9B42", "0E4AF2333C5A9E5D");

  pinMode(PIR,INPUT);
  pinMode(v,OUTPUT);
}

void loop() {
  
  
   if ((WiFiMulti.run() == WL_CONNECTED)) {
    inicioSesion();
    if(login == "y") post();
////////// prender/apagar
}
String prende ="prender";
  String apaga ="apagar";
int temperature = dht.getTemperature();
 if(temperature>25){
      Serial.print(temperature);
      Serial.print("\n");
      Serial.print(prende+"\n");
      // hacer incert en la base de datos
      digitalWrite(v, HIGH);
    }else{
      Serial.print(temperature);
      Serial.print("\n");
      Serial.print(apaga+"\n");
      // hacer incert en la base de datos
      digitalWrite(v, LOW);
    }
  delay(20000);
}

void inicioSesion(){
    WiFiClient client;
    HTTPClient http;
    int temperature = dht.getTemperature();
    Serial.print("[HTTP] GET begin...\n");
    if (http.begin(client, "http://pw4.kyared.com/S17030189/enfriamiento/login.php?user=admin&pass=123")) {  // HTTP
      Serial.print("[HTTP] GET...\n");
      int httpCode = http.GET();

      if (httpCode > 0) {
        Serial.printf("[HTTP] GET... code: %d\n", httpCode);            //aqui esta lo raro

        if (httpCode == HTTP_CODE_OK || httpCode == HTTP_CODE_MOVED_PERMANENTLY) {
          String payload = http.getString();
          Serial.println(payload);
          int inicio, fin;
          inicio = payload.indexOf("login");
          fin = payload.indexOf(",");
          login = payload.substring(inicio + 8, fin - 1);
          Serial.println("login: " + login);

          inicio = payload.indexOf("token");
          fin = payload.indexOf("}");
          token = payload.substring(inicio + 8, fin - 1);
          Serial.println("token: " + token);
          
            
        }
      } else {
        Serial.printf("[HTTP] GET... failed, error: %s\n", http.errorToString(httpCode).c_str());
      }

      http.end();
    } else {
      Serial.printf("[HTTP} Unable to connect\n");
    }
}

void post(){
    WiFiClient client;
    HTTPClient http;
    int temperature = dht.getTemperature();
    //Serial.print("La temperatura es: "+temperature);
    Serial.print("[HTTP] begin...\n");
    http.begin(client, "http://pw4.kyared.com/S17030189/enfriamiento/sensor.php");
    http.addHeader("Content-Type", "application/x-www-form-urlencoded");
    http.addHeader("Authorization", token);

    Serial.print("[HTTP] POST...\n");
    //int x = digitalRead(PIR);
    int httpCode = http.POST("tipo=T&valor=" + (String)temperature);

    if (httpCode > 0) {
      Serial.printf("[HTTP] POST... code: %d\n", httpCode);
      if (httpCode == HTTP_CODE_OK) {
        const String& payload = http.getString();
        Serial.println("received payload:\n<<");
        Serial.println(payload);
        Serial.println(">>");
      }
    } else {
      Serial.printf("[HTTP] POST... failed, error: %s\n", http.errorToString(httpCode).c_str());
    }

    http.end();
}
