package ru.sin.narspiknwja.tools;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import ru.sin.narspiknwja.body.DocsReq;
import ru.sin.narspiknwja.body.DocsRes;
import ru.sin.narspiknwja.body.EmbedReq;
import ru.sin.narspiknwja.body.EmbedRes;
import ru.sin.narspiknwja.config.RabbitConfig;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsumerTools {
    private final RabbitTemplate rabbitTemplate;
    private final RabbitConfig rabbitConfig;

    private final Logger logger = LoggerFactory.getLogger(ConsumerTools.class);


    public List<List<Float>> docsSendAndReceive(List<String> docs) throws RuntimeException {
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

    public List<Float> embedSendAndReceive(String query) throws RuntimeException {
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
