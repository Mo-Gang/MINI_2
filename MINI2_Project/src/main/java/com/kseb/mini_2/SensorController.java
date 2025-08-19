package com.kseb.mini_2;

import com.kseb.mini_2.dto.SensorDataDTO;               // DTO (네 패키지 경로에 맞춰 수정)
import com.kseb.mini_2.stream.SensorHub;               // SSE 허브 (네 패키지 경로에 맞춰 수정)
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@CrossOrigin(origins = "http://localhost:3000")           // CRA 프록시 쓰면 생략 가능
public class SensorController {

    // --- 기존 엔드포인트 호환 (원래의 /sensor 하위 경로) ---
    private final RiskEvaluator evaluator = new RiskEvaluator();

    @PostMapping(
            value = "/sensor/status",
            consumes = "application/json",
            produces = "application/json; charset=UTF-8"
    )
    public RiskResult getStatus(@RequestBody SensorDataDTO data) {
        RiskResult result = new RiskResult();
        result.setStatus("OK");
        result.setMessage("백엔드 연결 정상!");
        return result;
    }

    @GetMapping("/sensor/test")
    public String testAPI() {
        return "백엔드 연결 OK!";
    }

    // --- 실사용: 실시간 스트림 & 최신값 제공 (/api/sensors/*) ---
    private final SensorHub hub;

    public SensorController(SensorHub hub) {
        this.hub = hub;
    }

    /** 브라우저로 실시간 푸시 (SSE) */
    @GetMapping(value = "/api/sensors/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        return hub.subscribe(); // 새 구독자가 오면 최신값 1회 전송 + 이후 실시간 푸시
    }

    /** 최신 1건 조회 (SSE가 어려운 환경에서 폴링용) */
    @GetMapping("/api/sensors/latest")
    public SensorDataDTO latest() {
        return hub.latest();
    }
}
