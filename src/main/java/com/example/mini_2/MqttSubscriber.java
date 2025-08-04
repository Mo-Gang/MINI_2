package com.example.mini_2;

import com.example.mini_2.entity.SensorDataEntity;
import com.example.mini_2.repository.SensorDataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;


@Component
public class MqttSubscriber {

    private final SensorDataRepository repository;

    private final String broker = "tcp://broker.hivemq.com:1883";
    private final String clientId = "springboot-subscriber-" + System.currentTimeMillis();
    private final String topic = "sensor/data/moon";

    private MqttClient client;

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
                System.out.println("📥 수신된 메시지: " + payload);

                try {
                    ObjectMapper mapper = new ObjectMapper();
                    SensorDataEntity data = mapper.readValue(payload, SensorDataEntity.class);
                    data.setTimestamp(java.time.LocalDateTime.now());
                    repository.save(data);
                    System.out.println("✅ 저장된 데이터: " + data);
                } catch (Exception e) {
                    System.out.println("❌ JSON 파싱 또는 저장 실패: " + e.getMessage());
                    e.printStackTrace();
                }
            });

            System.out.println("✅ MQTT 구독 시작됨");

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
