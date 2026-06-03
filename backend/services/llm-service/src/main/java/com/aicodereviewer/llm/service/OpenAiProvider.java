package com.aicodereviewer.llm.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class OpenAiProvider implements LlmProvider {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${providers.openai.enabled:true}")
    private boolean enabled;

    @Value("${providers.openai.api-key:}")
    private String apiKey;

    @Value("${providers.openai.model:gpt-4.1-mini}")
    private String model;

    @Override
    public String name() {
        return "openai";
    }

    @Override
    public Mono<String> generate(String prompt, boolean strictJson) {
        if (!enabled || apiKey == null || apiKey.isBlank()) {
            return Mono.error(new IllegalStateException("OpenAI provider disabled or API key missing"));
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("model", model);
        payload.put("temperature", 0.1);
        payload.put("messages", new Object[] {
            Map.of("role", "system", "content", strictJson
                ? "You are a senior code reviewer. Return only valid JSON."
                : "You are a senior code reviewer assistant. Give concise and practical answers."),
            Map.of("role", "user", "content", prompt)
        });
        if (strictJson) {
            payload.put("response_format", Map.of("type", "json_object"));
        }

        return webClientBuilder.baseUrl("https://api.openai.com")
            .build()
            .post()
            .uri("/v1/chat/completions")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(payload)
            .retrieve()
            .bodyToMono(String.class)
            .map(this::extract);
    }

    private String extract(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to parse OpenAI response", ex);
        }
    }
}
