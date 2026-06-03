package com.aicodereviewer.llm.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class GeminiProvider implements LlmProvider {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${providers.gemini.enabled:true}")
    private boolean enabled;

    @Value("${providers.gemini.api-key:}")
    private String apiKey;

    @Value("${providers.gemini.model:gemini-1.5-pro}")
    private String model;

    @Override
    public String name() {
        return "gemini";
    }

    @Override
    public Mono<String> generate(String prompt, boolean strictJson) {
        if (!enabled || apiKey == null || apiKey.isBlank()) {
            return Mono.error(new IllegalStateException("Gemini provider disabled or API key missing"));
        }

        String url = "/v1beta/models/" + model + ":generateContent?key=" + apiKey;
        return webClientBuilder.baseUrl("https://generativelanguage.googleapis.com")
            .build()
            .post()
            .uri(url)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(Map.of(
                "contents", new Object[]{Map.of("parts", new Object[]{Map.of("text", prompt)})},
                "generationConfig", Map.of("temperature", 0.1)
            ))
            .retrieve()
            .bodyToMono(String.class)
            .map(this::extract);
    }

    private String extract(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            return root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to parse Gemini response", ex);
        }
    }
}
