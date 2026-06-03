package com.aicodereviewer.review.service;

import com.aicodereviewer.review.dto.ReviewChatResponse;
import com.aicodereviewer.review.entity.ReviewRecord;
import com.aicodereviewer.review.repository.ReviewRepository;
import java.util.Map;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class ReviewChatService {

    private final ReviewRepository reviewRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${clients.llm.base-url}")
    private String llmBaseUrl;

    @Value("${clients.internal.api-key}")
    private String internalApiKey;

    public ReviewChatResponse ask(Long reviewId, String question) {
        ReviewRecord review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new NoSuchElementException("Review not found"));

        String context = "Summary: " + review.getSummary()
            + "\nScore: " + review.getQualityScore()
            + "\nDiff:\n" + review.getNormalizedDiff()
            + "\nResult:\n" + review.getResultJson();

        @SuppressWarnings("rawtypes")
        Map response = webClientBuilder.baseUrl(llmBaseUrl)
            .build()
            .post()
            .uri("/api/v1/llm/chat")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header("X-Internal-Api-Key", internalApiKey)
            .bodyValue(Map.of("question", question, "context", context))
            .retrieve()
            .bodyToMono(Map.class)
            .block();

        Object answerValue = response != null ? response.get("answer") : null;
        String answer = answerValue != null ? String.valueOf(answerValue) : "No answer available.";
        return new ReviewChatResponse(answer);
    }
}
