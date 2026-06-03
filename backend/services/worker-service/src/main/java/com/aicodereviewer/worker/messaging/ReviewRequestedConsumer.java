package com.aicodereviewer.worker.messaging;

import com.aicodereviewer.common.KafkaTopics;
import com.aicodereviewer.common.dto.LlmReviewRequest;
import com.aicodereviewer.common.dto.LlmReviewResult;
import com.aicodereviewer.common.events.ReviewCompletedEvent;
import com.aicodereviewer.common.events.ReviewRequestedEvent;
import com.aicodereviewer.worker.service.LlmClientService;
import com.aicodereviewer.worker.service.QualityScoreCalculator;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewRequestedConsumer {

    private final StringRedisTemplate redisTemplate;
    private final LlmClientService llmClientService;
    private final QualityScoreCalculator qualityScoreCalculator;
    private final KafkaTemplate<String, ReviewCompletedEvent> kafkaTemplate;

    @org.springframework.kafka.annotation.KafkaListener(
        topics = KafkaTopics.REVIEW_REQUESTED,
        groupId = "worker-service",
        containerFactory = "reviewRequestedKafkaListenerContainerFactory"
    )
    public void consume(ReviewRequestedEvent event) {
        String key = "worker:dedupe:" + event.getDedupeKey();
        Boolean fresh = redisTemplate.opsForValue().setIfAbsent(key, "1");
        if (Boolean.FALSE.equals(fresh)) {
            log.info("Skipping duplicate requestId={} dedupeKey={}", event.getRequestId(), event.getDedupeKey());
            return;
        }

        LlmReviewRequest request = LlmReviewRequest.builder()
            .requestId(event.getRequestId())
            .metadata(event.getMetadata())
            .normalizedDiff(event.getNormalizedDiff())
            .build();

        LlmReviewResult result = llmClientService.review(request);
        int score = qualityScoreCalculator.calculate(result);

        ReviewCompletedEvent completedEvent = ReviewCompletedEvent.builder()
            .requestId(event.getRequestId())
            .completedAt(Instant.now())
            .qualityScore(score)
            .dedupeKey(event.getDedupeKey())
            .normalizedDiff(event.getNormalizedDiff())
            .metadata(event.getMetadata())
            .result(result)
            .build();

        kafkaTemplate.send(KafkaTopics.REVIEW_COMPLETED, event.getRequestId(), completedEvent);
    }
}
