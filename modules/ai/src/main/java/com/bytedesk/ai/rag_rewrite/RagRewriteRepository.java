package com.bytedesk.ai.rag_rewrite;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RagRewriteRepository
        extends JpaRepository<RagRewriteEntity, Long>, JpaSpecificationExecutor<RagRewriteEntity> {

    Optional<RagRewriteEntity> findByUid(String uid);

    Boolean existsByUid(String uid);
}
