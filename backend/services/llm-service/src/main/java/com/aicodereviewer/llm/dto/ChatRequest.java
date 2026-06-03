package com.aicodereviewer.llm.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(
    @NotBlank String question,
    @NotBlank String context
) {
}
