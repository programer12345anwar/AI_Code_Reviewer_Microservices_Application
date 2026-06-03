package com.aicodereviewer.review.service;

import com.aicodereviewer.common.events.ReviewCompletedEvent;
import com.aicodereviewer.review.entity.ReviewRecord;
import com.aicodereviewer.review.repository.ReviewRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewPersistenceService {

    private final ReviewRepository repository;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;

    public ReviewRecord persistIfNotDuplicate(ReviewCompletedEvent event) {
        String dedupeKey = "review:dedupe:" + event.getDedupeKey();
        Boolean fresh = redisTemplate.opsForValue().setIfAbsent(dedupeKey, "1");
        if (Boolean.FALSE.equals(fresh)) {
            return null;
        }

        ReviewRecord review = new ReviewRecord();
        review.setRequestId(event.getRequestId());
        review.setRepository(event.getMetadata().getRepository());
        review.setPrNumber(event.getMetadata().getPrNumber());
        review.setDedupeKey(event.getDedupeKey());
        review.setQualityScore(event.getQualityScore());
        review.setModelUsed(event.getResult().getModelUsed());
        review.setSummary(event.getResult().getSummary());
        review.setNormalizedDiff(event.getNormalizedDiff());
        review.setResultJson(toJson(event.getResult()));
        return repository.save(review);
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Could not serialize review result", ex);
        }
    }
}
