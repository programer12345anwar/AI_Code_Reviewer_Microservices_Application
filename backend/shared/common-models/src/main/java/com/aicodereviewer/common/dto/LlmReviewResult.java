package com.aicodereviewer.common.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LlmReviewResult {
    private List<CodeIssue> bugs;
    private List<CodeIssue> performanceIssues;
    private List<CodeIssue> securityIssues;
    private List<CodeIssue> suggestions;
    private String modelUsed;
    private String summary;
}
