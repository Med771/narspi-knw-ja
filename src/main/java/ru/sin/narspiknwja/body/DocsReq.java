package ru.sin.narspiknwja.body;

import java.util.List;
import java.util.UUID;

public record DocsReq(
      UUID uuid,
      List<String> chunks
) {
}
