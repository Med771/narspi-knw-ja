package ru.sin.narspiknwja.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sin.narspiknwja.entity.EmbeddingEntity;
import ru.sin.narspiknwja.model.EmbedWithSimilarity;

import java.util.List;

@Repository
public interface EmbeddingRepository extends JpaRepository<EmbeddingEntity, Long> {
    
    List<EmbeddingEntity> findByPostId(Long postId);

    @Query(nativeQuery = true, value = """
        SELECT 
            id, 
            chunk,  
            1 - (embed <=> CAST(?1 AS vector)) AS similarity
        FROM embeddings
        WHERE active = true
        ORDER BY embed <=> CAST(?1 AS vector)
        LIMIT ?2
        """)
    List<EmbedWithSimilarity> findNearestEmbeddings(float[] queryEmbedding, int limit);
}