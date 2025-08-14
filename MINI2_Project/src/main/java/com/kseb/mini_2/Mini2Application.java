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

// CommandLineRunner를 구현해 스프링이 관리하는 빈에서 실행
@Component
class MqttStarter implements CommandLineRunner {

    private final MqttSubscriber mqttSubscriber;

    public MqttStarter(MqttSubscriber mqttSubscriber) {
        this.mqttSubscriber = mqttSubscriber;
    }

    @Override
    public void run(String... args) throws Exception {
        mqttSubscriber.start();
    }
}

