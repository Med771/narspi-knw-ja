package ru.sin.narspiknwja.body;

import java.util.UUID;

public record QueryRes(
        UUID uuid,
        String answer
) {
}
