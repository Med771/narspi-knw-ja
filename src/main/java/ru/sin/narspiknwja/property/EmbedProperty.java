package ru.sin.narspiknwja.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("embed")
public class EmbedProperty {
    private int maxChunks;
}
