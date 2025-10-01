package ru.sin.narspiknwja.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "parse_posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParsePostEntity {
    public ParsePostEntity(
            String type,
            String source,
            LocalDateTime publishedAt
    ) {
        this.type = type;
        this.source = source;
        this.publishedAt = publishedAt;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "type", nullable = false, length = 50)
    private String type;
    
    @Column(name = "source", nullable = false, unique = true, length = 511)
    private String source;
    
    @Column(name = "active", nullable = false)
    private Boolean active = false;
    
    @Column(name = "parsed", nullable = false)
    private Boolean parsed = false;
    
    @Column(name = "title", columnDefinition = "TEXT")
    private String title;
    
    @Column(name = "text", columnDefinition = "TEXT")
    private String text;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST)
    private List<EmbeddingEntity> embeddings;
}