package com.example.mini_2;

// RiskResult.java
public class RiskResult {
    private String status;
    private String message;

    public RiskResult() {}                       // 기본 생성자
    public RiskResult(String status, String message) {
        this.status = status; this.message = message;
    }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
