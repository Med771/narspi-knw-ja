package ru.sin.narspiknwja.body;

import java.util.UUID;

public record HistoryReq(
        UUID uuid,
        int page,
        int size
) {
}
