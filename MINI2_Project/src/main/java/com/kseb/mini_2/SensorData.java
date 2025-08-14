package com.kseb.mini_2;

public class SensorData {
    private float temperature;
    private float distance;
    private int flame;
    private int sound;
    private int gasDigital;
    private int gasAnalog;
    private float humidity;

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public int getFlame() {
        return flame;
    }

    public void setFlame(int flame) {
        this.flame = flame;
    }

    public int getSound() {
        return sound;
    }

    public void setSound(int sound) {
        this.sound = sound;
    }

    public int getGasDigital() {
        return gasDigital;
    }

    public void setGasDigital(int gasDigital) {
        this.gasDigital = gasDigital;
    }

    public int getGasAnalog() {
        return gasAnalog;
    }

    public void setGasAnalog(int gasAnalog) {
        this.gasAnalog = gasAnalog;
    }

    //로그로 받기용
    public String toString() {
        return "SensorData{" +
                "temperature=" + temperature +
                ", humidity=" + humidity +
                ", distance=" + distance +
                ", flame=" + flame +
                ", sound=" + sound +
                ", gasDigital=" + gasDigital +
                ", gasAnalog=" + gasAnalog +
                '}';
    }
}
