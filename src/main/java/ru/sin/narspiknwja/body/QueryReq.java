package ru.sin.narspiknwja.body;

import java.util.UUID;

public record QueryReq(
        UUID uuid,
        String query
) {
}
