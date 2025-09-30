package ru.sin.narspiknwja.body;

import java.util.List;
import java.util.UUID;

public record EmbedRes(
        UUID uuid,
        List<Float> embedding
) {
}
