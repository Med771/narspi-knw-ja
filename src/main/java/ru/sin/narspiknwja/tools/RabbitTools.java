package ru.sin.narspiknwja.tools;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.sin.narspiknwja.body.HistoryReq;
import ru.sin.narspiknwja.body.HistoryRes;
import ru.sin.narspiknwja.body.QueryReq;
import ru.sin.narspiknwja.body.QueryRes;
import ru.sin.narspiknwja.config.RabbitConfig;
import ru.sin.narspiknwja.model.History;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RabbitTools {
    private final YandexTools yandexTools;

    private final Logger logger = LoggerFactory.getLogger(RabbitTools.class);

    @RabbitListener(queues = RabbitConfig.queryReqQueue, concurrency = "3-5")
    public QueryRes handleQuery(QueryReq req) {
        logger.info("[UUID: {}] Handle query", req.uuid());

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
}
