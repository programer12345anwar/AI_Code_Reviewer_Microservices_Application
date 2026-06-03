package com.aicodereviewer.github.controller;

import com.aicodereviewer.github.service.WebhookProcessorService;
import com.aicodereviewer.github.service.WebhookSignatureVerifier;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhooks/github")
@RequiredArgsConstructor
public class GitHubWebhookController {

    private final WebhookProcessorService webhookProcessorService;
    private final WebhookSignatureVerifier signatureVerifier;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<Map<String, String>> onWebhook(
        @RequestHeader(value = "X-GitHub-Event", required = false) String event,
        @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature,
        @RequestBody String body
    ) throws IOException {
        if (!"pull_request".equals(event)) {
            return ResponseEntity.accepted().body(Map.of("status", "ignored"));
        }
        if (!signatureVerifier.verify(body, signature)) {
            return ResponseEntity.status(401).body(Map.of("status", "invalid-signature"));
        }

        JsonNode payload = objectMapper.readTree(body);
        webhookProcessorService.handlePullRequestEvent(payload);
        return ResponseEntity.accepted().body(Map.of("status", "queued"));
    }
}
