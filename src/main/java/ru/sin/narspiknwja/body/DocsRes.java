package ru.sin.narspiknwja.body;

import java.util.List;
import java.util.UUID;

public record DocsRes(
        UUID uuid,
        List<List<Float>> embeddings
) {
}
