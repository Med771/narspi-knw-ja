package ru.sin.narspiknwja.body;

import ru.sin.narspiknwja.model.Url;

import java.util.List;
import java.util.UUID;

public record UrlsReq(
        UUID uuid,
        String type,
        List<Url> data
) {
}
