package com.aicodereviewer.llm.service;

import com.aicodereviewer.common.dto.LlmReviewRequest;
import org.springframework.stereotype.Service;

@Service
public class PromptBuilderService {

    public String build(LlmReviewRequest request) {
        return """
            Analyze this pull-request diff and return strict JSON only.

            RULES:
            1) Return JSON with exactly this schema:
            {
              "bugs": [],
              "performance_issues": [],
              "security_issues": [],
              "suggestions": []
            }
            2) Every issue object must include: line, severity, message, recommendation.
            3) Use only changed lines in the diff.
            4) Do not add generic comments; include only real defects or concrete suggestions.
            5) Keep messages concise and actionable.
            6) If no issues exist in a category, return an empty array.

            REPOSITORY: %s
            PR: #%d

            DIFF:
            %s
            """.formatted(
            request.getMetadata().getRepository(),
            request.getMetadata().getPrNumber(),
            request.getNormalizedDiff()
        );
    }
}
