package com.kseb.mini_2;

import com.kseb.mini_2.dto.SensorDataDTO;
import com.kseb.mini_2.entity.SensorDataEntity;
import com.kseb.mini_2.repository.SensorDataRepository;
import com.kseb.mini_2.stream.SensorHub;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class MqttSubscriber {

    private final SensorDataRepository repository;
    private final SensorHub hub;
    private final ObjectMapper mapper = new ObjectMapper();

    private final String broker   = "tcp://broker.hivemq.com:1883";
    private final String clientId = "springboot-subscriber-" + System.currentTimeMillis();
    private final String topic    = "sensor/data/moon";

    private MqttClient client;
    private final Map<String, LinkedList<Double>> sensorHistory = new HashMap<>();

    public MqttSubscriber(SensorDataRepository repository, SensorHub hub) {
        this.repository = repository;
        this.hub = hub;
    }

    @PostConstruct
    public void start() {
        try {
            client = new MqttClient(broker, clientId, new MemoryPersistence());

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);
            options.setKeepAliveInterval(60);
            options.setConnectionTimeout(20);

            client.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    System.out.println("[MQTT] connectComplete(reconnect=" + reconnect + ")");
                    new Thread(() -> {
                        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
                        try {
                            if (client != null && client.isConnected()) {
                                client.subscribe(topic, MqttSubscriber.this::handleMessage);
                                System.out.println("[MQTT] re-subscribed topic=" + topic);
                            }
                        } catch (MqttException ignored) { }
                    }).start();
                }

                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("[MQTT] connection lost");
                }

                @Override public void messageArrived(String t, MqttMessage m) { }
                @Override public void deliveryComplete(IMqttDeliveryToken token) { }
            });

            System.out.println("[MQTT] connecting " + broker + " ...");
            client.connect(options);

            client.subscribe(topic, this::handleMessage);
            System.out.println("✅ MQTT 구독 시작됨 (topic=" + topic + ")");

        } catch (MqttException e) {
            System.err.println("[MQTT] start failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleMessage(String topic, MqttMessage msg) {
        String payload = new String(msg.getPayload(), StandardCharsets.UTF_8);
        System.out.println("📥 수신된 메시지: " + payload);

        try {
            SensorDataEntity data = mapper.readValue(payload, SensorDataEntity.class);

            // 필터링
            SensorDataEntity filtered = applyFiltering(data);
            filtered.setTimestamp(LocalDateTime.now());

            // 저장
            repository.save(filtered);
            System.out.println("✅ (필터링 후) 저장: " + filtered);

            // 프론트로 push
            SensorDataDTO dto = toDTO(filtered);
            hub.publish(dto);

            System.out.println("--------------------------------------");
        } catch (Exception e) {
            System.out.println("❌ JSON 파싱/저장 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private SensorDataDTO toDTO(SensorDataEntity e) {
        SensorDataDTO d = new SensorDataDTO();
        d.setTs(e.getTimestamp() != null ? e.getTimestamp() : LocalDateTime.now());
        d.setTemperature(e.getTemperature());
        d.setHumidity(e.getHumidity());
        d.setDistance(e.getDistance());
        d.setGas(e.getGasAnalog());
        d.setSound(e.getSound());

        Boolean flame =
                e.getFlame() != null ? e.getFlame()
                        : (e.getGasAnalog() != null && e.getGasAnalog() >= 3000);
        Boolean smoke =
                e.getSmoke() != null ? e.getSmoke()
                        : (e.getSound() != null && e.getSound() >= 3000);

        d.setFlame(flame);
        d.setSmoke(smoke);

        return d;
    }

    private SensorDataEntity applyFiltering(SensorDataEntity data) {
        Double distance = filterValue("distance", data.getDistance(), 0, 100);
        if (distance != null && distance < 3) System.out.println("🚨 [위험] 거리 3cm 미만");
        if (distance == null) System.out.println("⚠️ 거리 센서 이상치 감지, null 처리");
        data.setDistance(distance);

        Double temperature = filterValue("temperature", data.getTemperature(), 0, 50);
        if (temperature != null) {
            if (temperature >= 35) System.out.println("🚨 [위험] 온도 35도 이상");
            else if (temperature >= 30) System.out.println("⚠️ [경고] 온도 30~35도");
        } else System.out.println("⚠️ 온도 센서 이상치 감지, null 처리");
        data.setTemperature(temperature);

        Double humidity = filterValue("humidity", data.getHumidity(), 0, 100);
        if (humidity != null) {
            if (humidity >= 75) System.out.println("🚨 [위험] 습도 75% 이상");
            else if (humidity >= 65) System.out.println("⚠️ [경고] 습도 65~75%");
        } else System.out.println("⚠️ 습도 센서 이상치 감지, null 처리");
        data.setHumidity(humidity);

        Double sound = filterValue("sound", data.getSound(), 0, 4000);
        if (sound != null && sound >= 3000) System.out.println("⚠️ [경고] 소리 3000 이상");
        if (sound == null) System.out.println("⚠️ 소리 센서 이상치 감지, null 처리");
        data.setSound(sound);

        Double gas = filterValue("gas", data.getGasAnalog(), 0, Double.MAX_VALUE);
        if (gas != null && gas >= 3000) System.out.println("⚠️ [경고] 가스 3000 이상");
        if (gas == null) System.out.println("⚠️ 가스 센서 이상치 감지, null 처리");
        data.setGasAnalog(gas);

        return data;
    }

    private Double filterValue(String key, Double value, double min, double max) {
        if (value == null) return null;
        if (value < min || value > max) return null;

        sensorHistory.putIfAbsent(key, new LinkedList<>());
        LinkedList<Double> history = sensorHistory.get(key);

        history.add(value);
        if (history.size() > 5) history.removeFirst();

        double sum = 0;
        for (double v : history) sum += v;
        return sum / history.size();
    }
}
