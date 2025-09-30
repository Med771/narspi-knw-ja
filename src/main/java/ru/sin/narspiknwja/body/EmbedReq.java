package ru.sin.narspiknwja.body;

import java.util.UUID;

public record EmbedReq(
        UUID uuid,
        String query
) {
}
