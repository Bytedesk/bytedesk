/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-08-10
 * @Description: RAG Query 增强记录查询服务。
 *   仅支持查询/删除（记录由 RagQueryRewriteHelper 异步生成，不支持前端创建/更新）。
 */
package com.bytedesk.ai.rag_rewrite;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class RagRewriteRestService extends BaseRestService<RagRewriteEntity, RagRewriteRequest, RagRewriteResponse> {

    private final RagRewriteRepository RagRewriteRepository;
    private final ModelMapper modelMapper;
    private final AuthService authService;

    @Override
    public Optional<RagRewriteEntity> findByUid(String uid) {
        return RagRewriteRepository.findByUid(uid);
    }

    @Override
    protected Specification<RagRewriteEntity> createSpecification(RagRewriteRequest request) {
        return RagRewriteSpecification.search(request, authService);
    }

    @Override
    protected Page<RagRewriteEntity> executePageQuery(Specification<RagRewriteEntity> spec,
            Pageable pageable) {
        return RagRewriteRepository.findAll(spec, pageable);
    }

    // 记录由系统异步生成，不支持前端创建
    @Override
    public RagRewriteResponse create(RagRewriteRequest request) {
        throw new UnsupportedOperationException("RagRewrite is system-generated");
    }

    // 记录为只读，不支持更新
    @Override
    public RagRewriteResponse update(RagRewriteRequest request) {
        throw new UnsupportedOperationException("RagRewrite is read-only");
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<RagRewriteEntity> optional = RagRewriteRepository.findByUid(uid);
        if (optional.isPresent()) {
            RagRewriteEntity entity = optional.get();
            entity.setDeleted(true);
            save(entity);
            return;
        }
        throw new RuntimeException("RagRewrite not found");
    }

    @Override
    public void delete(RagRewriteRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    protected RagRewriteEntity doSave(RagRewriteEntity entity) {
        return RagRewriteRepository.save(entity);
    }

    @Override
    public RagRewriteEntity handleOptimisticLockingFailureException(
            ObjectOptimisticLockingFailureException e, RagRewriteEntity entity) {
        try {
            Optional<RagRewriteEntity> latest = RagRewriteRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                return RagRewriteRepository.save(latest.get());
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public RagRewriteResponse convertToResponse(RagRewriteEntity entity) {
        return modelMapper.map(entity, RagRewriteResponse.class);
    }
}
