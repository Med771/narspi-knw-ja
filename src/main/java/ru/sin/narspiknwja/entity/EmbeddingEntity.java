package ru.sin.narspiknwja.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "embeddings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmbeddingEntity {
    public EmbeddingEntity(
            ParsePostEntity post,
            String chunk,
            Long chunkOrder,
            List<Float> embed,
            boolean active
    ) {
        this.post = post;
        this.chunk = chunk;
        this.chunkOrder = chunkOrder;
        this.active = active;

        float[] e = new float[embed.size()];

        for (int i = 0; i < embed.size(); i++) {
            e[i] = embed.get(i);
        }

        this.embed = e;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private ParsePostEntity post;
    
    @Column(name = "chunk", nullable = false, columnDefinition = "TEXT")
    private String chunk;
    
    @Column(name = "chunk_order", nullable = false)
    private Long chunkOrder;

    @Column(columnDefinition = "vector(512)")
    @JdbcTypeCode(SqlTypes.VECTOR)
    private float[] embed;
    
    @Column(name = "active", nullable = false)
    private Boolean active = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}