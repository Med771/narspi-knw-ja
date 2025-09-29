package ru.sin.narspiknwja.tools;

import lombok.RequiredArgsConstructor;
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

    private String getAnswer(GptRequest request) {
        GptResponse response =  config.contextWebClient().post()
                .uri("/completion")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GptResponse.class)
                .block();


        if (response == null || response.result() == null || response.result().alternatives().isEmpty()) {
            throw new RuntimeException("No result found");
        }

        return response.result().alternatives().getFirst().message().message();
    }

    private GptRequest getGptRequest(List<HistoryMessage> history, String context, String query) {
        List<HistoryMessage> messages = new ArrayList<>();

        messages.add(new HistoryMessage("system", context));

        for (int i = 0; i < Integer.min(history.size(), 10); i++) {
            messages.add(history.get(i));
        }

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
        String context = config.getContextText().formatted(LocalDate.now(), knowledge);

        GptRequest request = getGptRequest(history, context, query);

        try {
            // TODO: change Yandex queries
            return "OK"; // getAnswer(request);
        }
        catch (Exception e) {
            return null;
        }
    }
}
