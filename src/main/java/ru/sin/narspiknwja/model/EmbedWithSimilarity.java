package ru.sin.narspiknwja.model;

public record EmbedWithSimilarity(
        Long id,
        String chunk,
        Double similarity) {
}