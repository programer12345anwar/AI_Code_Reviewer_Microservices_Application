package com.aicodereviewer.review.dto;

import java.time.Instant;

public record ReviewSummaryResponse(
    Long id,
    String requestId,
    String repository,
    Integer prNumber,
    Integer qualityScore,
    String modelUsed,
    Instant createdAt
) {
}
