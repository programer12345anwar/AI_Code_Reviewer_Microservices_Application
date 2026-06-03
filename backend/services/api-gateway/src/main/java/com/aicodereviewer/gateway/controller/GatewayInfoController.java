package com.aicodereviewer.gateway.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/gateway")
public class GatewayInfoController {

    @GetMapping("/status")
    public Map<String, String> status() {
        return Map.of("service", "api-gateway", "status", "UP");
    }
}
