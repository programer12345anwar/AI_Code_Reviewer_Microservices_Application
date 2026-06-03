package com.aicodereviewer.common.events;

import com.aicodereviewer.common.dto.LlmReviewResult;
import com.aicodereviewer.common.dto.PrMetadata;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCompletedEvent {
    private String requestId;
    private Instant completedAt;
    private Integer qualityScore;
    private String dedupeKey;
    private String normalizedDiff;
    private PrMetadata metadata;
    private LlmReviewResult result;
}
