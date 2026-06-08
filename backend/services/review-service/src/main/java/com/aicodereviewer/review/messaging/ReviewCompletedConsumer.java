package com.aicodereviewer.review.messaging;

import com.aicodereviewer.common.KafkaTopics;
import com.aicodereviewer.common.events.ReviewCompletedEvent;
import com.aicodereviewer.review.entity.ReviewRecord;
import com.aicodereviewer.review.service.GitHubCommentService;
import com.aicodereviewer.review.service.ReviewPersistenceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewCompletedConsumer {

    private final ReviewPersistenceService persistenceService;
    private final SimpMessagingTemplate messagingTemplate;
    private final GitHubCommentService gitHubCommentService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaTopics.REVIEW_COMPLETED, groupId = "review-service", containerFactory = "reviewCompletedKafkaListenerContainerFactory")
    public void consume(ReviewCompletedEvent event) {
        ReviewRecord record = persistenceService.persistIfNotDuplicate(event);
        if (record == null) {
            log.info("Duplicate review ignored for dedupeKey={}", event.getDedupeKey());
            return;
        }

        messagingTemplate.convertAndSend("/topic/reviews", record.getId());
        try {
            gitHubCommentService.publishComment(
                event.getMetadata().getRepository(),
                event.getMetadata().getPrNumber(),
                formatComment(event)
            );
        } catch (Exception ex) {
            log.warn("Review persisted but GitHub comment could not be published for repository={} prNumber={}",
                event.getMetadata().getRepository(),
                event.getMetadata().getPrNumber(),
                ex
            );
        }
    }

    private String formatComment(ReviewCompletedEvent event) {
        StringBuilder comment = new StringBuilder();
        comment.append("## AI Code Review\n\n");
        comment.append("Quality Score: **").append(event.getQualityScore()).append("/100**\n\n");
        comment.append(event.getResult().getSummary()).append("\n\n");
        comment.append("```json\n");
        try {
            comment.append(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(event.getResult()));
        } catch (Exception ignored) {
            comment.append("{\"error\":\"Unable to format review payload\"}");
        }
        comment.append("\n```");
        return comment.toString();
    }
}
