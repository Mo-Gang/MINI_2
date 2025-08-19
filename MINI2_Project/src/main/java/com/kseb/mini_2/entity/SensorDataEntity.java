package com.kseb.mini_2.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_data")
public class SensorDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 수신 시각 */
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    /** 센서 값들 */
    private Double temperature;   // °C
    private Double humidity;      // %
    private Double distance;      // cm
    private Double gasAnalog;     // 가스 (아날로그)
    private Double sound;         // 소리

    /** 불꽃/연기/가스 감지 */
    private Boolean flame;        // 불꽃 감지
    private Boolean gasDigital;   // 가스 디지털 감지
    private Boolean smoke;        // 연기 감지

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

    public Boolean getGasDigital() { return gasDigital; }
    public void setGasDigital(Boolean gasDigital) { this.gasDigital = gasDigital; }

    public Boolean getSmoke() { return smoke; }
    public void setSmoke(Boolean smoke) { this.smoke = smoke; }

    // ===== toString =====
    @Override
    public String toString() {
        return String.format(
                "SensorDataEntity{" +
                        "id=%d, timestamp=%s, temperature=%.2f, humidity=%.2f, distance=%.2f, " +
                        "flame=%b, sound=%.2f, gasDigital=%b, gasAnalog=%.2f, smoke=%b}",
                id, timestamp, temperature, humidity, distance,
                flame, sound, gasDigital, gasAnalog, smoke
        );
    }
}
