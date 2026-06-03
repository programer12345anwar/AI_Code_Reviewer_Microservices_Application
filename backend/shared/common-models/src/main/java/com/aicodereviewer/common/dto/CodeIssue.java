package com.aicodereviewer.common.dto;

import com.aicodereviewer.common.enums.Severity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeIssue {
    private Integer line;
    private Severity severity;
    private String message;
    private String recommendation;
}
