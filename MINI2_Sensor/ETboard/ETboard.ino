#include <WiFi.h>
#include <PubSubClient.h>
#include <DHT.h>
#include "config.h"

// MQTT 브로커
const char* mqtt_server = "broker.hivemq.com";
WiFiClient espClient;
PubSubClient client(espClient);

// DHT11 설정
#define DHTPIN 27
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);

// 센서 핀 설정
#define TRIG_PIN        15
#define ECHO_PIN        13
#define FLAME_PIN       17
#define SOUND_PIN       33
#define GAS_DIGITAL_PIN 16
#define GAS_ANALOG_PIN  35

// millis 타이머용 변수
unsigned long lastReconnectAttempt = 0;
unsigned long lastSensorReadTime = 0;
const unsigned long sensorInterval = 3000; // 센서 주기: 3초

// WiFi 연결
void setup_wifi() {
  WiFi.begin(ssid, password);
  int attempts = 0;
  while (WiFi.status() != WL_CONNECTED && attempts < 20) {
    delay(500);
    Serial.print(".");
    attempts++;
  }
  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("\nWiFi 연결 성공");
  } else {
    Serial.println("\nWiFi 연결 실패");
  }
}

// MQTT 연결 시도
bool reconnect() {
  if (client.connect("ESP32Client")) {
    return true;
  }
  return false;
}

void setup() {
  Serial.begin(115200);

  pinMode(TRIG_PIN, OUTPUT);
  pinMode(ECHO_PIN, INPUT);
  pinMode(FLAME_PIN, INPUT);
  pinMode(SOUND_PIN, INPUT);
  pinMode(GAS_DIGITAL_PIN, INPUT);
  pinMode(GAS_ANALOG_PIN, INPUT);

  dht.begin();
  setup_wifi();
  client.setServer(mqtt_server, 1883);
  lastReconnectAttempt = millis();
}

void loop() {
  // MQTT 연결 상태 확인 및 재시도 (5초 간격)
  if (!client.connected()) {
    unsigned long now = millis();
    if (now - lastReconnectAttempt > 5000) {
      lastReconnectAttempt = now;
      if (reconnect()) {
        lastReconnectAttempt = 0;
      }
    }
    return; // 연결 안 되면 측정 스킵
  }

  client.loop(); // MQTT 유지를 위한 루프

  // 센서 측정 주기 체크 (3초 간격)
  unsigned long now = millis();
  if (now - lastSensorReadTime >= sensorInterval) {
    lastSensorReadTime = now;

    // 초음파 거리 측정
    digitalWrite(TRIG_PIN, LOW);
    delayMicroseconds(2);
    digitalWrite(TRIG_PIN, HIGH);
    delayMicroseconds(10);
    digitalWrite(TRIG_PIN, LOW);
    long duration = pulseIn(ECHO_PIN, HIGH, 30000);
    float distance = (duration != 0) ? (duration * 0.034 / 2) : -1;

    // 온도 및 습도 측정
    float temperature = dht.readTemperature();
    float humidity = dht.readHumidity();

    // 불꽃 감지
    int flameValue = digitalRead(FLAME_PIN);

    // 소리 감지
    int soundValue = analogRead(SOUND_PIN);
    delayMicroseconds(100);

    // 가스 감지
    int gasDigitalValue = digitalRead(GAS_DIGITAL_PIN);
    int gasAnalogValue = analogRead(GAS_ANALOG_PIN);
    delayMicroseconds(100);

    // 시리얼 출력
    Serial.printf("온도: %.2f°C | 습도: %.2f%% | 거리: %.2fcm | 화재: %d | 소리: %d | 가스: D:%d A:%d\n",
                  temperature, humidity, distance, flameValue, soundValue, gasDigitalValue, gasAnalogValue);

    // JSON 문자열 구성
    char jsonPayload[300];
    snprintf(jsonPayload, sizeof(jsonPayload),
             "{\"temperature\":%.2f,\"humidity\":%.2f,\"distance\":%.2f,\"flame\":%d,\"sound\":%d,\"gasDigital\":%d,\"gasAnalog\":%d}",
             temperature, humidity, distance, flameValue, soundValue, gasDigitalValue, gasAnalogValue);

    // MQTT 전송
    client.publish("sensor/data/moon", jsonPayload);
  }
}
