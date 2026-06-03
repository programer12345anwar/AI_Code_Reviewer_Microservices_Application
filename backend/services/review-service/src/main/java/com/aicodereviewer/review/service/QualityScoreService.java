package com.aicodereviewer.review.service;

import com.aicodereviewer.common.dto.CodeIssue;
import com.aicodereviewer.common.dto.LlmReviewResult;
import com.aicodereviewer.common.enums.Severity;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class QualityScoreService {

    public int calculateScore(LlmReviewResult result) {
        int penalty = score(result.getBugs(), 5)
            + score(result.getPerformanceIssues(), 4)
            + score(result.getSecurityIssues(), 8)
            + score(result.getSuggestions(), 2);

        return Math.max(0, 100 - penalty);
    }

    private int score(List<CodeIssue> issues, int baseWeight) {
        if (issues == null || issues.isEmpty()) {
            return 0;
        }
        return issues.stream()
            .mapToInt(issue -> baseWeight * severityWeight(issue.getSeverity()))
            .sum();
    }

    private int severityWeight(Severity severity) {
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
