package com.kseb.mini_2.stream;

import com.kseb.mini_2.dto.SensorDataDTO;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SensorHub {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private volatile SensorDataDTO latest;

    /** 프론트 구독(SSE) */
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(0L); // timeout 없음
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));

        // 현재 최신값 1회 즉시 전송(있으면)
        if (latest != null) {
            try {
                send(emitter, latest);
            } catch (IOException ignored) {
                emitters.remove(emitter);
            }
        }
        return emitter;
    }

    /** 새로운 센서값 브로드캐스트 */
    public void publish(SensorDataDTO dto) {
        latest = dto;
        for (SseEmitter emitter : emitters) {
            try {
                send(emitter, dto);
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }

    /** 최신값 단건 조회 */
    public SensorDataDTO latest() {
        return latest;
    }

    private void send(SseEmitter emitter, SensorDataDTO dto) throws IOException {
        emitter.send(SseEmitter.event()
                .name("data")
                .data(dto, MediaType.APPLICATION_JSON));
    }
}
