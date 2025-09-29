package ru.sin.narspiknwja.model;

import java.util.List;

public record GptRequest(
        String modelUri,
        CompletionOptions completionOptions,
        List<HistoryMessage> messages
) {
    public record CompletionOptions(
            boolean stream,
            float temperature,
            String maxTokens,
            ReasoningOptions reasoningOptions
    ) {
        public record ReasoningOptions(String mode) {}
    }
}