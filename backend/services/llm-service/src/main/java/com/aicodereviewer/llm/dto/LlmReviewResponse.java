package com.aicodereviewer.llm.dto;

import com.aicodereviewer.common.dto.LlmReviewResult;

public record LlmReviewResponse(
    String requestId,
    LlmReviewResult result
) {
}
