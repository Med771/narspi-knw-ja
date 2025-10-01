package ru.sin.narspiknwja.tools;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.sin.narspiknwja.body.*;
import ru.sin.narspiknwja.config.RabbitConfig;
import ru.sin.narspiknwja.model.History;
import ru.sin.narspiknwja.model.Site;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListenerTools {
    private final YandexTools yandexTools;
    private final DataTools dataTools;

    private final Logger logger = LoggerFactory.getLogger(ListenerTools.class);

    @RabbitListener(queues = RabbitConfig.queryReqQueue, concurrency = "3-5")
    public QueryRes handleQuery(QueryReq req) {
        logger.info("[UUID: {}] Handle query", req.uuid());

        if (req.query() == null) {
            return new QueryRes(req.uuid(), null);
        }

        List<String> knowledge = dataTools.getKnowledge(req.query());

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

        List<Site> sites = dataTools.saveSources(req);

        return new UrlsRes(req.uuid(), sites);
    }

    @RabbitListener(queues = RabbitConfig.pageReqQueue, concurrency = "1-2")
    public void handlePage(PageReq req) {
        logger.info("[UUID: {}] Handle page", req.uuid());

        dataTools.saveParsePosts(req);
    }
}
