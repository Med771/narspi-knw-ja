package ru.sin.narspiknwja.model;

import java.util.List;

public record GptResponse(
        Result result) {
    public record Result(
            List<Alternative> alternatives,
            Usage usage,
            String modelVersion
    ) {
        public record Alternative(
                HistoryMessage message,
                String status
        ) {}

        public record Usage(int inputTextTokens, int completionTokens, int totalTokens) {}
    }
}