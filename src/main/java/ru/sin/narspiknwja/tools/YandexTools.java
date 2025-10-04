package ru.sin.narspiknwja.tools;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import ru.sin.narspiknwja.config.YandexConfig;
import ru.sin.narspiknwja.model.GptRequest;
import ru.sin.narspiknwja.model.GptResponse;
import ru.sin.narspiknwja.model.HistoryMessage;
import ru.sin.narspiknwja.property.YandexProperty;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class YandexTools {
    private final YandexConfig config;

    private final YandexProperty yandexProperty;

    private static final Logger logger = LoggerFactory.getLogger(YandexTools.class);

    private String getAnswer(GptRequest request) {
        GptResponse response =  config.contextWebClient().post()
                .uri("/completion")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp ->
                        resp.bodyToMono(String.class).map(body ->
                                new RuntimeException("Yandex API error: " + body)
                        )
                )
                .bodyToMono(GptResponse.class)
                .block();


        if (response == null) {
            throw new RuntimeException("Response is null");
        }

        if (response.result() == null) {
            throw new RuntimeException("Result is null. Response is " + response);
        }

        if (response.result().alternatives().isEmpty()) {
            throw new RuntimeException("Alternatives is empty");
        }

        logger.info("Yandex API response tokens: {}", response.result().usage().totalTokens());

        return response.result().alternatives().getFirst().message().text();
    }

    private GptRequest getGptRequest(List<HistoryMessage> history, String context, String query) {
        List<HistoryMessage> messages = new ArrayList<>();

        messages.add(new HistoryMessage("system", context));

//        for (int i = 0; i < Integer.min(history.size(), 10); i++) {
//            messages.add(history.get(i));
//        }

        messages.add(new HistoryMessage("user", query));

        return new GptRequest(
                config.getContextUri(),
                new GptRequest.CompletionOptions(
                        yandexProperty.getContext().getConfiguration().getStream(),
                        yandexProperty.getContext().getConfiguration().getTemperature(),
                        yandexProperty.getContext().getConfiguration().getMaxTokens(),
                        new GptRequest.CompletionOptions.ReasoningOptions(yandexProperty.getContext().getConfiguration().getMode())
                ),
                messages
        );
    }

    public String getQuery(List<HistoryMessage> history, List<String> knowledge, String query) {
        String context = config.getContextText().formatted(LocalDate.now(), String.join("\n", knowledge));

        GptRequest request = getGptRequest(history, context, query);

        logger.info("[DateTine: {}] [Context length: {}] Yandex query",LocalDate.now(), context.length());

        try {
            return getAnswer(request);
        }
        catch (Exception e) {
            logger.error("Failed to get answer: {}", e.getMessage());

            return null;
        }
    }
}
