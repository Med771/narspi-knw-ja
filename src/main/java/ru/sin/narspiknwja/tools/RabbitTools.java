package ru.sin.narspiknwja.tools;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import ru.sin.narspiknwja.body.*;
import ru.sin.narspiknwja.config.RabbitConfig;
import ru.sin.narspiknwja.model.History;
import ru.sin.narspiknwja.model.Site;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RabbitTools {
    private final YandexTools yandexTools;

    private final RabbitTemplate rabbitTemplate;
    private final RabbitConfig rabbitConfig;

    private final Logger logger = LoggerFactory.getLogger(RabbitTools.class);

    @RabbitListener(queues = RabbitConfig.queryReqQueue, concurrency = "3-5")
    public QueryRes handleQuery(QueryReq req) {
        logger.info("[UUID: {}] Handle query", req.uuid());

        if (req.query() == null) {
            return new QueryRes(req.uuid(), null);
        }

        List<Float> vector = embedSendAndReceive(req.query());

        // TODO: find Embeddings for knowledge
        List<String> knowledge = List.of();

        String answer = yandexTools.getQuery(req.history(), knowledge, req.query());

        return new QueryRes(req.uuid(), answer);
    }

    @RabbitListener(queues = RabbitConfig.historyReqQueue, concurrency = "1-2")
    public HistoryRes handleHistory(HistoryReq req) {
        logger.info("[UUID: {}] Handle history", req.uuid());

        List<History> history = List.of(
                new History(LocalDateTime.now(), "query", LocalDateTime.now(), "answer"));

        return new HistoryRes(req.uuid(), history);
    }

    @RabbitListener(queues = RabbitConfig.urlsReqQueue, concurrency = "1-2")
    public UrlsRes handleUrls(UrlsReq req) {
        logger.info("[UUID: {}] Handle urls", req.uuid());
        List<Site> sites = new ArrayList<>();

        for (int i = 1; i <= req.data().size(); i++) {
            System.out.println(req.data().get(i - 1).link());
            sites.add(new Site((long) i, req.data().get(i - 1).link()));
        }

        return new UrlsRes(req.uuid(), sites);
    }

    @RabbitListener(queues = RabbitConfig.pageReqQueue, concurrency = "1-2")
    public void handlePage(PageReq req) {
        logger.info("[UUID: {}] Handle page", req.uuid());

        for (int i = 1; i <= req.pages().size(); i++) {
            System.out.println(req.pages().get(i - 1).text());
        }
    }

    public List<List<Float>> docsSendAndRecieve(List<String> docs) {
        DocsReq req = new DocsReq(
                UUID.randomUUID(),
                docs
        );

        logger.info("[UUID: {}] Send chunks size: {}", req.uuid(), req.chunks().size());

        try {
            DocsRes res = rabbitTemplate.convertSendAndReceiveAsType(
                    rabbitConfig.getGtwExc(),
                    rabbitConfig.getDocsReqRoutingKey(),
                    req,
                    new ParameterizedTypeReference<>() {
                    }
            );

            if (res == null) {
                throw new RuntimeException("Response is null");
            }

            if (!req.uuid().equals(res.uuid())) {
                throw new RuntimeException("Response does not match uuid");
            }

            if (res.embeddings() == null || res.embeddings().isEmpty()) {
                throw new RuntimeException("Response answer is empty");
            }

            return res.embeddings();
        }
        catch (Exception e) {
            logger.error("[UUID: {}] Docs response exception: {}", req.uuid(), e.getMessage());

            throw new RuntimeException("Docs response exception: " + e);
        }
    }

    public List<Float> embedSendAndReceive(String query) {
        EmbedReq req = new EmbedReq(
                UUID.randomUUID(),
                query
        );

        logger.info("[UUID: {}] Send query size: {}", req.uuid(), req.query());

        try {
            EmbedRes res = rabbitTemplate.convertSendAndReceiveAsType(
                    rabbitConfig.getGtwExc(),
                    rabbitConfig.getEmbedReqRoutingKey(),
                    req,
                    new ParameterizedTypeReference<>() {
                    }
            );

            if (res == null) {
                throw new RuntimeException("Response is null");
            }

            if (!req.uuid().equals(res.uuid())) {
                throw new RuntimeException("Response does not match uuid");
            }

            if (res.embedding() == null || res.embedding().isEmpty()) {
                throw new RuntimeException("Response answer is empty");
            }

            return res.embedding();
        }
        catch (Exception e) {
            logger.error("[UUID: {}] Embed response exception: {}", req.uuid(), e.getMessage());

            throw new RuntimeException("Embed response exception: " + e);
        }
    }
}
