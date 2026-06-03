package com.aicodereviewer.llm.service;

import com.aicodereviewer.common.dto.CodeIssue;
import com.aicodereviewer.common.dto.LlmReviewResult;
import com.aicodereviewer.common.enums.Severity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StructuredReviewMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public LlmReviewResult fromJson(String rawJson, String modelUsed) {
        try {
            JsonNode root = objectMapper.readTree(rawJson);
            List<CodeIssue> bugs = parse(root.path("bugs"));
            List<CodeIssue> perf = parse(root.path("performance_issues"));
            List<CodeIssue> security = parse(root.path("security_issues"));
            List<CodeIssue> suggestions = parse(root.path("suggestions"));

            String summary = "Detected "
                + (bugs.size() + perf.size() + security.size())
                + " actionable issues and "
                + suggestions.size()
                + " improvements.";

            return LlmReviewResult.builder()
                .bugs(bugs)
                .performanceIssues(perf)
                .securityIssues(security)
                .suggestions(suggestions)
                .modelUsed(modelUsed)
                .summary(summary)
                .build();
        } catch (Exception ex) {
            throw new IllegalStateException("LLM response is not valid strict JSON", ex);
        }
    }

    private List<CodeIssue> parse(JsonNode nodes) {
        List<CodeIssue> list = new ArrayList<>();
        if (!nodes.isArray()) {
            return list;
        }

        for (JsonNode node : nodes) {
            list.add(CodeIssue.builder()
                .line(node.path("line").isInt() ? node.path("line").asInt() : null)
                .severity(parseSeverity(node.path("severity").asText("MEDIUM")))
                .message(node.path("message").asText(""))
                .recommendation(node.path("recommendation").asText(""))
                .build());
        }
        return list;
    }

    private Severity parseSeverity(String severity) {
        try {
            return Severity.valueOf(severity.toUpperCase());
        } catch (Exception ignored) {
            return Severity.MEDIUM;
        }
    }
}
