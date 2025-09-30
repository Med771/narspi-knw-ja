package ru.sin.narspiknwja.model;

import java.time.LocalDateTime;

public record Url(
        String link,
        LocalDateTime date
) {
}
