package com.aicodereviewer.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LlmReviewRequest {

    @NotBlank
    private String requestId;

    @NotBlank
    private String normalizedDiff;

    @NotNull
    private PrMetadata metadata;
}
