package com.aicodereviewer.llm.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LlmFallbackService {

    private final List<LlmProvider> providers;

    public ProviderResponse generate(String prompt, boolean strictJson) {
        RuntimeException lastException = null;
        for (LlmProvider provider : providers) {
            try {
                String body = provider.generate(prompt, strictJson).block();
                if (body != null && !body.isBlank()) {
                    return new ProviderResponse(provider.name(), body);
                }
            } catch (RuntimeException ex) {
                lastException = ex;
                log.warn("Provider {} failed: {}", provider.name(), ex.getMessage());
            }
        }

        throw new IllegalStateException("All LLM providers failed", lastException);
    }

    public record ProviderResponse(String provider, String body) {
    }
}
