package com.aicodereviewer.github.controller;

import com.aicodereviewer.github.dto.GitHubCommentRequest;
import com.aicodereviewer.github.service.GitHubApiService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/github")
@RequiredArgsConstructor
public class GitHubCommentController {

    private final GitHubApiService gitHubApiService;

    @Value("${clients.internal.api-key}")
    private String internalApiKey;

    @PostMapping("/comments")
    public ResponseEntity<Map<String, String>> createComment(
        @RequestHeader("X-Internal-Api-Key") String apiKey,
        @RequestBody @Valid GitHubCommentRequest request
    ) {
        if (!internalApiKey.equals(apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", "unauthorized"));
        }

        boolean posted = gitHubApiService.postPrComment(request);
        return ResponseEntity.ok(Map.of("status", posted ? "posted" : "skipped-missing-github-token"));
    }
}
