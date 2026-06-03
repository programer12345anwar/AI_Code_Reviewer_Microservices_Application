package com.aicodereviewer.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrMetadata {
    private String repository;
    private String owner;
    private String name;
    private Integer prNumber;
    private String headSha;
    private String installationId;
}
