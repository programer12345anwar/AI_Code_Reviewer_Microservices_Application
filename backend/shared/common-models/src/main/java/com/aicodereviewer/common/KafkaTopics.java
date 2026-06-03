package com.aicodereviewer.common;

public final class KafkaTopics {
    private KafkaTopics() {
    }

    public static final String REVIEW_REQUESTED = "review.requested";
    public static final String REVIEW_COMPLETED = "review.completed";
    public static final String REVIEW_DLT = "review.requested.dlt";
}
