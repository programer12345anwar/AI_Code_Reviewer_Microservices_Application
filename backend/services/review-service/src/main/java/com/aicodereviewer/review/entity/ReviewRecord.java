package com.aicodereviewer.review.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "reviews")
public class ReviewRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String requestId;

    @Column(nullable = false)
    private String repository;

    @Column(nullable = false)
    private Integer prNumber;

    @Column(nullable = false)
    private String dedupeKey;

    @Column(nullable = false)
    private Integer qualityScore;

    @Column(nullable = false)
    private String modelUsed;

    @Column(length = 4000)
    private String summary;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String normalizedDiff;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String resultJson;

    @Column(nullable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
    }
}
