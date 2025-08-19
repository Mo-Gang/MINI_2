package com.kseb.mini_2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class Mini2Application {

    public static void main(String[] args) {
        SpringApplication.run(Mini2Application.class, args);
    }
}

@Component
class MqttStarter implements CommandLineRunner {

    private final MqttSubscriber mqttSubscriber;

    public MqttStarter(MqttSubscriber mqttSubscriber) {
        this.mqttSubscriber = mqttSubscriber;
    }

    @Override
    public void run(String... args) {
        mqttSubscriber.start(); // ✅ MqttSubscriber에서 @PostConstruct는 제거
    }
}

