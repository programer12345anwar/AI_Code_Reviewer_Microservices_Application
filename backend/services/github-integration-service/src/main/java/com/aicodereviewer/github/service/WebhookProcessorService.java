package com.aicodereviewer.github.service;

import com.aicodereviewer.common.KafkaTopics;
import com.aicodereviewer.common.dto.PrMetadata;
import com.aicodereviewer.common.events.ReviewRequestedEvent;
import com.fasterxml.jackson.databind.JsonNode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebhookProcessorService {

    private final GitHubApiService gitHubApiService;
    private final DiffExtractionService diffExtractionService;
    private final KafkaTemplate<String, ReviewRequestedEvent> kafkaTemplate;

    public void handlePullRequestEvent(JsonNode payload) {
        String action = payload.path("action").asText();
        if (!("opened".equals(action) || "synchronize".equals(action) || "reopened".equals(action))) {
            return;
        }

        JsonNode repositoryNode = payload.path("repository");
        JsonNode pullRequestNode = payload.path("pull_request");

        String owner = repositoryNode.path("owner").path("login").asText();
        String repo = repositoryNode.path("name").asText();
        String fullName = repositoryNode.path("full_name").asText(owner + "/" + repo);
        int prNumber = payload.path("number").asInt();
        String headSha = pullRequestNode.path("head").path("sha").asText();
        String installationId = payload.path("installation").path("id").asText();
        String sender = payload.path("sender").path("login").asText("unknown");

        List<String> patches = gitHubApiService.fetchPullRequestPatches(owner, repo, prNumber);
        String normalizedDiff = diffExtractionService.normalize(patches);
        String dedupeKey = sha256(fullName + ":" + prNumber + ":" + headSha + ":" + normalizedDiff);

        ReviewRequestedEvent event = ReviewRequestedEvent.builder()
            .requestId(UUID.randomUUID().toString())
            .requestedAt(Instant.now())
            .metadata(PrMetadata.builder()
                .owner(owner)
                .name(repo)
                .repository(fullName)
                .prNumber(prNumber)
                .headSha(headSha)
                .installationId(installationId)
                .build())
            .normalizedDiff(normalizedDiff)
            .dedupeKey(dedupeKey)
            .triggeredBy(sender)
            .build();

        kafkaTemplate.send(KafkaTopics.REVIEW_REQUESTED, event.getRequestId(), event);
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(input.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to create dedupe hash", ex);
        }
    }
}
