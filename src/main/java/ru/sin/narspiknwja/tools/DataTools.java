package ru.sin.narspiknwja.tools;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.sin.narspiknwja.body.PageReq;
import ru.sin.narspiknwja.body.UrlsReq;
import ru.sin.narspiknwja.entity.EmbeddingEntity;
import ru.sin.narspiknwja.entity.ParsePostEntity;
import ru.sin.narspiknwja.model.EmbedWithSimilarity;
import ru.sin.narspiknwja.model.Page;
import ru.sin.narspiknwja.model.Site;
import ru.sin.narspiknwja.model.Url;
import ru.sin.narspiknwja.repository.EmbeddingRepository;
import ru.sin.narspiknwja.repository.ParsePostRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DataTools {
    private static final Logger logger = LoggerFactory.getLogger(DataTools.class);

    private final ParsePostRepository parsePostRepository;
    private final EmbeddingRepository embeddingRepository;

    private final ConsumerTools consumerTools;

    public List<Site> saveSources(UrlsReq req) {
        String type = req.type();

        List<String> allUrls = req.data()
                .stream()
                .map(Url::link)
                .toList();

        Set<String> existingUrlsSet = parsePostRepository.findAllBySourceIn(allUrls)
                .stream()
                .map(ParsePostEntity::getSource)
                .collect(Collectors.toSet());

        List<ParsePostEntity> newParsePostEntities = req.data().stream()
                .filter(source -> !existingUrlsSet.contains(source.link()))
                .map(source -> new ParsePostEntity(
                        type,
                        source.link(),
                        source.date()
                ))
                .toList();

        try {

            return parsePostRepository.saveAll(newParsePostEntities)
                    .stream()
                    .map(entity -> new Site(entity.getId(), entity.getSource()))
                    .toList();
        }
        catch (DataIntegrityViolationException e) {
            logger.warn("Data integrity violation in save Sources: {}", e.getMessage());
        }
        catch (Exception e) {
            logger.warn("Unexpected error in save Sources: {}", e.getMessage());
        }

        return List.of();
    }

    public void saveParsePosts(PageReq pageReq) {
        List<Long> postIds = pageReq.pages().stream()
                .map(Page::postId)
                .toList();
        Map<Long, Page> pagesById = pageReq.pages().stream()
                .collect(Collectors.toMap(Page::postId, p -> p));

        List<ParsePostEntity> posts = parsePostRepository.findAllById(postIds);
        List<ParsePostEntity> newPosts = new ArrayList<>();

        for (ParsePostEntity parsePostEntity : posts) {
            Page page = pagesById.getOrDefault(parsePostEntity.getId(), null);

            if (page == null) {
                continue;
            }

            parsePostEntity.setParsed(true);
            parsePostEntity.setText(page.text());

            boolean isEmbed = createEmbeds(parsePostEntity, page.text());

            if (isEmbed) {
                newPosts.add(parsePostEntity);
            }
        }

        try {
            parsePostRepository.saveAll(newPosts);
        } catch (DataIntegrityViolationException e) {
            logger.warn("Data integrity violation in save Parse Posts: {}", e.getMessage());
        } catch (Exception e) {
            logger.warn("Unexpected error in save Embeds in save Parse Posts: {}", e.getMessage());
        }
    }

    public List<String> getKnowledge(String query) {
        List<Float> vector = consumerTools.embedSendAndReceive(query);

        float[] embed = new float[vector.size()];

        for (int i = 0; i < embed.length; i++) {
            embed[i] = vector.get(i);
        }

        List<EmbedWithSimilarity> embeds = embeddingRepository.findNearestEmbeddings(embed, 50);

        return embeds.stream()
                .map(EmbedWithSimilarity::chunk)
                .toList();
    }

    private boolean createEmbeds(ParsePostEntity parsePostEntity, String text) {
        List<String> chunks = Stream.of(text.split("\n\n"))
                .filter(String::isBlank)
                .collect(Collectors.toList());

        List<EmbeddingEntity> embeds = new ArrayList<>();
        List<List<Float>> embeddings = consumerTools.docsSendAndReceive(chunks);

        for (int i = 0; i < chunks.size(); i++) {
            if (i < embeddings.size() && !embeddings.get(i).isEmpty()) {
                embeds.add(
                        new EmbeddingEntity(
                                parsePostEntity,
                                chunks.get(i),
                                (long) i,
                                embeddings.get(i),
                                true
                        )
                );
            }
        }

        try {
            embeddingRepository.saveAll(embeds);

            return true;
        } catch (DataIntegrityViolationException e) {
            logger.warn("Data integrity violation in create Embeds: {}", e.getMessage());
        } catch (Exception e) {
            logger.warn("Unexpected error in save Embeds in create Embeds: {}", e.getMessage());
        }

        return false;
    }
}
