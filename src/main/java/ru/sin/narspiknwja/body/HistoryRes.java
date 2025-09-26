package ru.sin.narspiknwja.body;

import ru.sin.narspiknwja.model.History;

import java.util.List;
import java.util.UUID;

public record HistoryRes(
        UUID uuid,
        List<History> messages
) {
}
