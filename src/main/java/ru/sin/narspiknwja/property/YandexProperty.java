package ru.sin.narspiknwja.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("yandex")
public class YandexProperty {
    private String key;
    private String folderId;
    private Header header;
    private Context context;

    @Data
    public static class Header {
        private String folderKey;
        private String apiKeyValue;
    }

    @Data
    public static class Context {
        private String url;
        private String uri;
        private Configuration configuration;

        @Data
        public static class Configuration {
            private Float temperature;
            private String maxTokens;
            private Boolean stream;
            private String mode;
        }

    }
}