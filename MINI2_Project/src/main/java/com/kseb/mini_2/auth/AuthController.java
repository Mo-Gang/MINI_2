package com.example.mini_2.auth;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @GetMapping("/ping")
    public String ping() { return "auth-ok"; }

    @PostMapping("/login")
    public ResponseEntity<Map<String,Object>> login(@RequestBody Map<String,String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if ("admin".equals(username) && "1234".equals(password)) {
            return ResponseEntity.ok(Map.of(
                    "token", "demo-jwt-like-token",
                    "user",  Map.of("name","관리자","role","ADMIN")
            ));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message","아이디 또는 비밀번호가 틀립니다."));
    }
}
