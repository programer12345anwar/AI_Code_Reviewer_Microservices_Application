package com.aicodereviewer.llm.service;

import reactor.core.publisher.Mono;

public interface LlmProvider {
    String name();
    Mono<String> generate(String prompt, boolean strictJson);
}
