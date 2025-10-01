package ru.sin.narspiknwja.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sin.narspiknwja.entity.ParsePostEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParsePostRepository extends JpaRepository<ParsePostEntity, Long> {
    
    Optional<ParsePostEntity> findBySource(String source);

    List<ParsePostEntity> findByType(String type);

    List<ParsePostEntity> findByParsed(Boolean parsed);
    
    List<ParsePostEntity> findByNeedsRefactor(Boolean needsRefactor);

    List<ParsePostEntity> findAllBySourceIn(List<String> sources);

    boolean existsBySource(String source);
}