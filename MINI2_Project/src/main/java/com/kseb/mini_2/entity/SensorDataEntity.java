<<<<<<< HEAD:MINI2_Project/src/main/java/com/kseb/mini_2/entity/SensorDataEntity.java
//package com.example.mini_2.entity;
//
//import jakarta.persistence.*;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "sensor_data")
//public class SensorDataEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "timestamp", nullable = false)
//    private LocalDateTime timestamp;
//
//    @Column(name = "temperature")
//    private Float temperature;
//
//    @Column(name = "humidity")
//    private Float humidity;
//
//    @Column(name = "distance")
//    private Float distance;
//
//    @Column(name = "flame")
//    private Boolean flame;
//
//    @Column(name = "sound")
//    private Integer sound;
//
//    @Column(name = "gas_digital")
//    private Boolean gasDigital;
//
//    @Column(name = "gas_analog")
//    private Integer gasAnalog;
//
//    // ----- Getters & Setters -----
//
//    public Long getId() {
//        return id;
//    }
//
//    public LocalDateTime getTimestamp() {
//        return timestamp;
//    }
//    public void setTimestamp(LocalDateTime timestamp) {
//        this.timestamp = timestamp;
//    }
//
//    public Float getTemperature() {
//        return temperature;
//    }
//    public void setTemperature(Float temperature) {
//        this.temperature = temperature;
//    }
//
//    public Float getHumidity() {
//        return humidity;
//    }
//    public void setHumidity(Float humidity) {
//        this.humidity = humidity;
//    }
//
//    public Float getDistance() {
//        return distance;
//    }
//    public void setDistance(Float distance) {
//        this.distance = distance;
//    }
//
//    public Boolean getFlame() {
//        return flame;
//    }
//    public void setFlame(Boolean flame) {
//        this.flame = flame;
//    }
//
//    public Integer getSound() {
//        return sound;
//    }
//    public void setSound(Integer sound) {
//        this.sound = sound;
//    }
//
//    public Boolean getGasDigital() {
//        return gasDigital;
//    }
//    public void setGasDigital(Boolean gasDigital) {
//        this.gasDigital = gasDigital;
//    }
//
//    public Integer getGasAnalog() {
//        return gasAnalog;
//    }
//    public void setGasAnalog(Integer gasAnalog) {
//        this.gasAnalog = gasAnalog;
//    }
//
//    //엔티티 내용 확인용
//    @Override
//    public String toString() {
//        return "SensorDataEntity{" +
//                "id=" + id +
//                ", timestamp=" + timestamp +
//                ", temperature=" + temperature +
//                ", humidity=" + humidity +
//                ", distance=" + distance +
//                ", flame=" + flame +
//                ", sound=" + sound +
//                ", gasDigital=" + gasDigital +
//                ", gasAnalog=" + gasAnalog +
//                '}';
//    }
//
//}

package com.kseb.mini_2.entity;
=======
package com.example.mini_2.entity;
>>>>>>> 23e379a (내 로컬 작업 저장):MINI2_Project/src/main/java/com/example/mini_2/entity/SensorDataEntity.java

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_data")
public class SensorDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 수신 시각 */
    private LocalDateTime timestamp;

    /** 센서 값들 */
    private Double temperature;   // °C
    private Double humidity;      // %
    private Double distance;      // cm
    private Double gasAnalog;     // 가스(아날로그)
    private Double sound;         // 소리 (옵션)

    /** 불꽃/연기 감지 */
    private Boolean flame;        // 불꽃 감지
    private Boolean smoke;        // 연기 감지  ← 새로 추가

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Double getHumidity() { return humidity; }
    public void setHumidity(Double humidity) { this.humidity = humidity; }

    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }

    public Double getGasAnalog() { return gasAnalog; }
    public void setGasAnalog(Double gasAnalog) { this.gasAnalog = gasAnalog; }

    public Double getSound() { return sound; }
    public void setSound(Double sound) { this.sound = sound; }

    public Boolean getFlame() { return flame; }
    public void setFlame(Boolean flame) { this.flame = flame; }

<<<<<<< HEAD:MINI2_Project/src/main/java/com/kseb/mini_2/entity/SensorDataEntity.java
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Boolean getFlame() {
        return flame;
    }

    public void setFlame(Boolean flame) {
        this.flame = flame;
    }

    public Double getSound() {
        return sound;
    }

    public void setSound(Double sound) {
        this.sound = sound;
    }

    public Boolean getGasDigital() {
        return gasDigital;
    }

    public void setGasDigital(Boolean gasDigital) {
        this.gasDigital = gasDigital;
    }

    public Double getGasAnalog() {
        return gasAnalog;
    }

    public void setGasAnalog(Double gasAnalog) {
        this.gasAnalog = gasAnalog;
    }


    @Override

    public String toString() {
        return String.format(
                "SensorDataEntity{id=%d, timestamp=%s}%n" +
                        "      temperature=%.2f, humidity=%.2f, distance=%.2f, flame=%b, sound=%.2f, gasDigital=%b, gasAnalog=%.2f",
                id, timestamp, temperature, humidity, distance, flame, sound, gasDigital, gasAnalog
        );
    }
=======
    public Boolean getSmoke() { return smoke; }
    public void setSmoke(Boolean smoke) { this.smoke = smoke; }
>>>>>>> 23e379a (내 로컬 작업 저장):MINI2_Project/src/main/java/com/example/mini_2/entity/SensorDataEntity.java
}
