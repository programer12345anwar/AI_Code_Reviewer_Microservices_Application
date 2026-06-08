package com.aicodereviewer.github.service;

import com.aicodereviewer.github.dto.GitHubCommentRequest;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class GitHubApiService {

    private final WebClient.Builder webClientBuilder;

    @Value("${github.api.base-url:https://api.github.com}")
    private String gitHubApiBaseUrl;

    @Value("${github.token:}")
    private String gitHubToken;

    public List<String> fetchPullRequestPatches(String owner, String repo, int prNumber) {
        WebClient.RequestHeadersSpec<?> request = webClientBuilder.baseUrl(gitHubApiBaseUrl)
            .build()
            .get()
            .uri("/repos/{owner}/{repo}/pulls/{number}/files", owner, repo, prNumber)
            .header(HttpHeaders.ACCEPT, "application/vnd.github+json");

        if (StringUtils.hasText(gitHubToken)) {
            request = request.header(HttpHeaders.AUTHORIZATION, "Bearer " + gitHubToken);
        }

        JsonNode response = request
            .retrieve()
            .bodyToMono(JsonNode.class)
            .block();

        List<String> patches = new ArrayList<>();
        if (response != null && response.isArray()) {
            for (JsonNode fileNode : response) {
                String patch = fileNode.path("patch").asText("");
                if (!patch.isBlank()) {
                    patches.add(patch);
                }
            }
        }
        return patches;
    }

    public boolean postPrComment(GitHubCommentRequest commentRequest) {
        if (!StringUtils.hasText(gitHubToken)) {
            return false;
        }

        String[] parts = commentRequest.repository().split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Repository must be in owner/repo format");
        }

        WebClient.RequestBodySpec apiRequest = webClientBuilder.baseUrl(gitHubApiBaseUrl)
            .build()
            .post()
            .uri("/repos/{owner}/{repo}/issues/{number}/comments", parts[0], parts[1], commentRequest.prNumber())
            .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + gitHubToken);

        apiRequest
            .bodyValue(java.util.Map.of("body", commentRequest.body()))
            .retrieve()
            .toBodilessEntity()
            .block();
        return true;
    }
}
