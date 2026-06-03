package com.aicodereviewer.common.events;

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
public class ReviewRequestedEvent {
    private String requestId;
    private Instant requestedAt;
    private PrMetadata metadata;
    private String normalizedDiff;
    private String dedupeKey;
    private String triggeredBy;
}
