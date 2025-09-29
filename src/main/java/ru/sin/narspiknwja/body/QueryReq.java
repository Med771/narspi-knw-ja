package ru.sin.narspiknwja.body;

import ru.sin.narspiknwja.model.HistoryMessage;

import java.util.List;
import java.util.UUID;

public record QueryReq(
        UUID uuid,
        String query,
        List<HistoryMessage> history
) {
}
