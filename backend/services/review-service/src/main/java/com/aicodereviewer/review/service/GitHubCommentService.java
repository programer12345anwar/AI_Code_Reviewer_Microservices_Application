package com.aicodereviewer.review.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class GitHubCommentService {

    private final WebClient.Builder webClientBuilder;

    @Value("${clients.github-integration.base-url}")
    private String githubIntegrationBaseUrl;

    @Value("${clients.internal.api-key}")
    private String internalApiKey;

    public void publishComment(String repository, Integer prNumber, String body) {
        webClientBuilder.baseUrl(githubIntegrationBaseUrl)
            .build()
            .post()
            .uri("/api/v1/github/comments")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header("X-Internal-Api-Key", internalApiKey)
            .bodyValue(Map.of(
                "repository", repository,
                "prNumber", prNumber,
                "body", body
            ))
            .retrieve()
            .toBodilessEntity()
            .block();
    }
}
