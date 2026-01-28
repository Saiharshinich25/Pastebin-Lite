package com.assessment.Project.Controller;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    private final RedisTemplate<String, Object> redis;

    public HealthController(RedisTemplate<String, Object> redis) {
        this.redis = redis;
    }

    @GetMapping("/healthz")
    public ResponseEntity<Map<String, Boolean>> health() {
        try {
            redis.hasKey("health-check");
            return ResponseEntity.ok(Map.of("ok", true));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("ok", false));
        }
    }
}
