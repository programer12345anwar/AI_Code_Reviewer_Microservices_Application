package com.aicodereviewer.worker.service;

import com.aicodereviewer.common.dto.CodeIssue;
import com.aicodereviewer.common.dto.LlmReviewResult;
import com.aicodereviewer.common.enums.Severity;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class QualityScoreCalculator {

    public int calculate(LlmReviewResult result) {
        int penalty = penalty(result.getBugs(), 5)
            + penalty(result.getPerformanceIssues(), 4)
            + penalty(result.getSecurityIssues(), 8)
            + penalty(result.getSuggestions(), 2);
        return Math.max(0, 100 - penalty);
    }

    private int penalty(List<CodeIssue> issues, int baseWeight) {
        if (issues == null || issues.isEmpty()) {
            return 0;
        }
        return issues.stream().mapToInt(issue -> baseWeight * weight(issue.getSeverity())).sum();
    }

    private int weight(Severity severity) {
        if (severity == null) {
            return 1;
        }
        return switch (severity) {
            case LOW -> 1;
            case MEDIUM -> 2;
            case HIGH -> 3;
            case CRITICAL -> 5;
        };
    }
}
