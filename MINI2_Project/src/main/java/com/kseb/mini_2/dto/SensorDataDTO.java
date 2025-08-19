package com.kseb.mini_2.dto;

import java.time.LocalDateTime;

public class SensorDataDTO {
    private LocalDateTime ts;
    private Double temperature; // °C
    private Double humidity;    // %
    private Double distance;    // cm
    private Double gas;         // 가스 (analog)
    private Boolean flame;      // 불꽃 감지 (true/false)
    private Boolean smoke;      // 연기 감지 (true/false)
    private Double sound;       // 소리 (옵션)

    public SensorDataDTO() {}

    // --- getters / setters ---
    public LocalDateTime getTs() { return ts; }
    public void setTs(LocalDateTime ts) { this.ts = ts; }
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    public Double getHumidity() { return humidity; }
    public void setHumidity(Double humidity) { this.humidity = humidity; }
    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }
    public Double getGas() { return gas; }
    public void setGas(Double gas) { this.gas = gas; }
    public Boolean getFlame() { return flame; }
    public void setFlame(Boolean flame) { this.flame = flame; }
    public Boolean getSmoke() { return smoke; }
    public void setSmoke(Boolean smoke) { this.smoke = smoke; }
    public Double getSound() { return sound; }
    public void setSound(Double sound) { this.sound = sound; }


}
