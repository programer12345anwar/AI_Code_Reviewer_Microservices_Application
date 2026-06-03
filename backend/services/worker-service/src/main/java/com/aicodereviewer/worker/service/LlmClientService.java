package com.aicodereviewer.worker.service;

import com.aicodereviewer.common.dto.LlmReviewRequest;
import com.aicodereviewer.common.dto.LlmReviewResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class LlmClientService {

    private final WebClient.Builder webClientBuilder;

    @Value("${clients.llm.base-url}")
    private String llmBaseUrl;

    @Value("${clients.internal.api-key}")
    private String internalApiKey;

    public LlmReviewResult review(LlmReviewRequest request) {
        return webClientBuilder.baseUrl(llmBaseUrl)
            .build()
            .post()
            .uri("/api/v1/llm/review")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header("X-Internal-Api-Key", internalApiKey)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(LlmReviewResult.class)
            .block();
    }
}
