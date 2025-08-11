package com.example.mini_2;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;


@CrossOrigin(origins = "http://localhost:3000")


@RestController
@RequestMapping("/sensor")
public class SensorController {

    private final RiskEvaluator evaluator = new RiskEvaluator();

    @PostMapping(
            value = "/status",
            consumes = "application/json",
            produces = "application/json; charset=UTF-8"
    )
    public RiskResult getStatus(@RequestBody SensorDataDTO data) {
        RiskResult result = new RiskResult();
        result.setStatus("OK");
        result.setMessage("백엔드 연결 정상!");
        return result;
    }


    @GetMapping("/test")
    public String testAPI() {
        return "백엔드 연결 OK!";
    }
}
