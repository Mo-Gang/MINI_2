package com.example.mini_2;

public class RiskEvaluator {

    public RiskResult evaluate(SensorDataDTO data) {
        // 예시 기준치 (필요하면 수정)
        if (data.gas > 70 || data.flame == 1) {
            return new RiskResult("위험", "가스 농도 높음 또는 불꽃 감지!");
        } else if (data.sound > 80 || data.temp > 35) {
            return new RiskResult("경고", "소음 과다 또는 온도 높음");
        } else {
            return new RiskResult("정상", "안전한 상태입니다");
        }
    }
}
