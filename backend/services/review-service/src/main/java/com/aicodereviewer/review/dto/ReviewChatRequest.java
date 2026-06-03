package com.aicodereviewer.review.dto;

import jakarta.validation.constraints.NotBlank;

public record ReviewChatRequest(@NotBlank String question) {
}
