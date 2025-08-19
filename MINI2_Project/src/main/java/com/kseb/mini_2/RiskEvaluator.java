package com.example.mini_2;

import com.example.mini_2.dto.SensorDataDTO;

public class RiskEvaluator {

    public RiskResult evaluate(SensorDataDTO data) {
        if ((data.getGas() != null && data.getGas() > 70)
                || Boolean.TRUE.equals(data.getFlame())) {
            return new RiskResult("위험", "가스 농도 높음 또는 불꽃 감지");
        } else if ((data.getSound() != null && data.getSound() > 80)
                || (data.getTemperature() != null && data.getTemperature() > 35)) {
            return new RiskResult("경고", "소음 과다 또는 온도 높음");
        } else {
            return new RiskResult("정상", "안전한 상태입니다");
        }

    }
}

