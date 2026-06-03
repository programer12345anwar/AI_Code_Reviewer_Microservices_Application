package com.aicodereviewer.review.dto;

public record ReviewAnalyticsResponse(
    long totalReviews,
    double avgScore,
    long highRiskReviews,
    long lowRiskReviews
) {
}
