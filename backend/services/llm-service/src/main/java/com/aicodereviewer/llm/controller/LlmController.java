package com.aicodereviewer.llm.controller;

import com.aicodereviewer.common.dto.LlmReviewRequest;
import com.aicodereviewer.common.dto.LlmReviewResult;
import com.aicodereviewer.llm.dto.ChatRequest;
import com.aicodereviewer.llm.dto.ChatResponse;
import com.aicodereviewer.llm.service.LlmFallbackService;
import com.aicodereviewer.llm.service.PromptBuilderService;
import com.aicodereviewer.llm.service.StructuredReviewMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/llm")
@RequiredArgsConstructor
public class LlmController {

    private final PromptBuilderService promptBuilderService;
    private final LlmFallbackService llmFallbackService;
    private final StructuredReviewMapper structuredReviewMapper;

    @Value("${clients.internal.api-key}")
    private String internalApiKey;

    @PostMapping("/review")
    public ResponseEntity<LlmReviewResult> review(
        @RequestHeader("X-Internal-Api-Key") String apiKey,
        @RequestBody @Valid LlmReviewRequest request
    ) {
        if (!internalApiKey.equals(apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String prompt = promptBuilderService.build(request);
        LlmFallbackService.ProviderResponse response = llmFallbackService.generate(prompt, true);
        LlmReviewResult result = structuredReviewMapper.fromJson(response.body(), response.provider());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(
        @RequestHeader("X-Internal-Api-Key") String apiKey,
        @RequestBody @Valid ChatRequest request
    ) {
        if (!internalApiKey.equals(apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String prompt = """
            You are an AI code review assistant.
            Answer the user's question using only the provided review context.
            Keep the answer concise, technical, and actionable.

            QUESTION:
            %s

            REVIEW CONTEXT:
            %s
            """.formatted(request.question(), request.context());

        LlmFallbackService.ProviderResponse response = llmFallbackService.generate(prompt, false);
        return ResponseEntity.ok(new ChatResponse(response.body(), response.provider()));
    }
}
