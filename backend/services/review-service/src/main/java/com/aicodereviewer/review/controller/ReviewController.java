package com.aicodereviewer.review.controller;

import com.aicodereviewer.review.dto.ReviewAnalyticsResponse;
import com.aicodereviewer.review.dto.ReviewChatRequest;
import com.aicodereviewer.review.dto.ReviewChatResponse;
import com.aicodereviewer.review.dto.ReviewDetailResponse;
import com.aicodereviewer.review.dto.ReviewSummaryResponse;
import com.aicodereviewer.review.service.ReviewChatService;
import com.aicodereviewer.review.service.ReviewQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewQueryService queryService;
    private final ReviewChatService reviewChatService;

    @GetMapping
    public Page<ReviewSummaryResponse> list(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "20") int size,
        @RequestParam(name = "search", required = false) String search
    ) {
        return queryService.list(page, size, search);
    }

    @GetMapping("/{id}")
    public ReviewDetailResponse getById(@PathVariable("id") Long id) {
        return queryService.get(id);
    }

    @GetMapping("/analytics")
    public ReviewAnalyticsResponse analytics() {
        return queryService.analytics();
    }

    @PostMapping("/{id}/chat")
    public ReviewChatResponse chat(@PathVariable("id") Long id, @RequestBody @Valid ReviewChatRequest request) {
        return reviewChatService.ask(id, request.question());
    }
}
