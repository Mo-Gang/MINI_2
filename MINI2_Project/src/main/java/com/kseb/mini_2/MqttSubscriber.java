package com.kseb.mini_2;

import com.kseb.mini_2.entity.SensorDataEntity;
import com.kseb.mini_2.repository.SensorDataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class MqttSubscriber {

    private final SensorDataRepository repository;

    private final String broker = "tcp://broker.hivemq.com:1883";
    private final String clientId = "springboot-subscriber-" + System.currentTimeMillis();
    private final String topic = "sensor/data/moon";
    private MqttClient client;

    // 이동평균 계산용 (최근 5개 값 저장)
    private final Map<String, LinkedList<Double>> sensorHistory = new HashMap<>();

    public MqttSubscriber(SensorDataRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void start() {
        try {
            client = new MqttClient(broker, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            client.connect(options);

            client.subscribe(topic, (t, msg) -> {
                String payload = new String(msg.getPayload());
                System.out.println("수신된 메시지: " + payload);

                try {
                    ObjectMapper mapper = new ObjectMapper();
                    SensorDataEntity data = mapper.readValue(payload, SensorDataEntity.class);

                    // 필터링 적용
                    SensorDataEntity filteredData = applyFiltering(data);

                    filteredData.setTimestamp(LocalDateTime.now());
                    repository.save(filteredData);
                    System.out.println("(필터링 후) 저장된 데이터: " + filteredData);
                    System.out.println("\n--------------------------------------\n");

                } catch (Exception e) {
                    System.out.println("JSON 파싱 or 저장 실패: " + e.getMessage());
                    e.printStackTrace();
                }
            });

            System.out.println("MQTT 구독 시작");

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private SensorDataEntity applyFiltering(SensorDataEntity data) {
        // 거리 센서
        Double distance = filterValue("distance", data.getDistance(), 0, 100);
        if (distance != null && distance < 3) {
            System.out.println("[위험] 거리 3cm 미만");
        }
        if (distance == null) {
            System.out.println("거리 센서 이상치 감지, null 처리");
        }
        data.setDistance(distance);

        // 온도 센서
        Double temperature = filterValue("temperature", data.getTemperature(), 0, 50);
        if (temperature != null) {
            if (temperature >= 35) {
                System.out.println("[위험] 온도 35도 이상");
            } else if (temperature >= 30) {
                System.out.println("[경고] 온도 30~35도");
            }
        } else {
            System.out.println("온도 센서 이상치 감지, null 처리");
        }
        data.setTemperature(temperature);

        // 습도 센서
        Double humidity = filterValue("humidity", data.getHumidity(), 0, 100);
        if (humidity != null) {
            if (humidity >= 75) {
                System.out.println("[위험] 습도 75% 이상");
            } else if (humidity >= 65) {
                System.out.println("[경고] 습도 65~75%");
            }
        } else {
            System.out.println("습도 센서 이상치 감지, null 처리");
        }
        data.setHumidity(humidity);

        // 소리 센서
        Double sound = filterValue("sound", data.getSound(), 0, 4000);
        if (sound != null && sound >= 1000) {
            System.out.println("[경고] 소리 1000(ADC) 이상");
        }
        if (sound == null) {
            System.out.println("소리 센서 이상치 감지, null 처리");
        }
        data.setSound(sound);

        // 가스 센서 (analog)
        Double gas = filterValue("gas", data.getGasAnalog(), 0, 4000);
        if (gas != null && gas >= 80) {
            System.out.println("[경고] 가스 80(ADC) 이상");
        }
        if (gas == null) {
            System.out.println("가스 센서 이상치 감지, null 처리");
        }
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
