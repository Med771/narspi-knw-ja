package ru.sin.narspiknwja.body;

import ru.sin.narspiknwja.model.Site;

import java.util.List;
import java.util.UUID;

public record UrlsRes(
        UUID uuid,
        List<Site> sites
) {
}
