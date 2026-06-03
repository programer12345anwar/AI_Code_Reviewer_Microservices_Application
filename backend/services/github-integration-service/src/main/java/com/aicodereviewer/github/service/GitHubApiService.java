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
        JsonNode response = webClientBuilder.baseUrl(gitHubApiBaseUrl)
            .build()
            .get()
            .uri("/repos/{owner}/{repo}/pulls/{number}/files", owner, repo, prNumber)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + gitHubToken)
            .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
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

    public void postPrComment(GitHubCommentRequest request) {
        String[] parts = request.repository().split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Repository must be in owner/repo format");
        }

        webClientBuilder.baseUrl(gitHubApiBaseUrl)
            .build()
            .post()
            .uri("/repos/{owner}/{repo}/issues/{number}/comments", parts[0], parts[1], request.prNumber())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + gitHubToken)
            .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(java.util.Map.of("body", request.body()))
            .retrieve()
            .toBodilessEntity()
            .block();
    }
}
