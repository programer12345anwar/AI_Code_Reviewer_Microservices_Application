package com.aicodereviewer.review.dto;

import java.time.Instant;

public record ReviewDetailResponse(
    Long id,
    String requestId,
    String repository,
    Integer prNumber,
    Integer qualityScore,
    String modelUsed,
    String summary,
    String normalizedDiff,
    String resultJson,
    Instant createdAt
) {
}
