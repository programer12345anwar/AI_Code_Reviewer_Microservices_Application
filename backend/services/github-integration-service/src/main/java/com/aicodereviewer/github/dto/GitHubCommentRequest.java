package com.aicodereviewer.github.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GitHubCommentRequest(
    @NotBlank String repository,
    @NotNull Integer prNumber,
    @NotBlank String body
) {
}
