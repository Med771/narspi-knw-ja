package ru.sin.narspiknwja.body;

import ru.sin.narspiknwja.model.Page;

import java.util.List;
import java.util.UUID;

public record PageReq(
        UUID uuid,
        List<Page> pages
) {
}
