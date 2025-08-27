/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 14:47:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.workflow;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class WorkflowRestService extends BaseRestService<WorkflowEntity, WorkflowRequest, WorkflowResponse> {

    private final WorkflowRepository workflowRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    protected Specification<WorkflowEntity> createSpecification(WorkflowRequest request) {
        return WorkflowSpecification.search(request, authService);
    }

    @Override
    protected Page<WorkflowEntity> executePageQuery(Specification<WorkflowEntity> spec, Pageable pageable) {
        return workflowRepository.findAll(spec, pageable);
    }

    
    @Cacheable(value = "workflow", key = "#uid", unless="#result==null")
    @Override
    public Optional<WorkflowEntity> findByUid(String uid) {
        return workflowRepository.findByUid(uid);
    }

    @Override
    public WorkflowResponse create(WorkflowRequest request) {
        WorkflowEntity entity = modelMapper.map(request, WorkflowEntity.class);
        entity.setUid(uidUtils.getUid());
        // 
        WorkflowEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create workflow failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    @Transactional
    public WorkflowResponse update(WorkflowRequest request) {
        // 参数验证
        if (!StringUtils.hasText(request.getUid())) {
            throw new IllegalArgumentException("Workflow UID cannot be null or empty");
        }
        
        log.debug("开始更新工作流，UID: {}", request.getUid());
        
        Optional<WorkflowEntity> optional = workflowRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            WorkflowEntity entity = optional.get();
            
            log.debug("找到工作流实体，ID: {}, 当前版本: {}", entity.getId(), entity.getVersion());
            
            // 使用ModelMapper将请求数据映射到实体，但需要保留一些重要字段
            String originalUid = entity.getUid();
            Long originalId = entity.getId();
            int originalVersion = entity.getVersion();
            
            // 映射请求数据到实体
            modelMapper.map(request, entity);
            
            // 手动处理字段名不一致的映射
            if (request.getCurrentNode() != null) {
                entity.setCurrentNodeId(request.getCurrentNode());
            }
            
            // 恢复关键字段，防止被覆盖
            entity.setUid(originalUid);
            entity.setId(originalId);
            entity.setVersion(originalVersion);
            
            // 设置默认头像
            if (entity.getAvatar() == null || entity.getAvatar().isEmpty()) {
                entity.setAvatar(AvatarConsts.getDefaultWorkflowAvatar());
            }
            
            WorkflowEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update workflow failed: saved entity is null");
            }
            log.debug("工作流更新成功，ID: {}, 新版本: {}", savedEntity.getId(), savedEntity.getVersion());
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("Workflow not found with UID: " + request.getUid());
        }
    }

    @Override
    protected WorkflowEntity doSave(WorkflowEntity entity) {
        return workflowRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<WorkflowEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        }
        else {
            throw new RuntimeException("Workflow not found");
        }
    }

    @Override
    public void delete(WorkflowRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public WorkflowEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, WorkflowEntity entity) {
        log.warn("处理工作流乐观锁冲突，实体ID: {}, UID: {}", entity.getId(), entity.getUid());
        
        try {
            // 重新获取最新的实体版本
            Optional<WorkflowEntity> latestOptional = workflowRepository.findByUid(entity.getUid());
            if (latestOptional.isPresent()) {
                WorkflowEntity latestEntity = latestOptional.get();
                
                // 将当前实体的修改应用到最新版本上
                if (entity.getNickname() != null) {
                    latestEntity.setNickname(entity.getNickname());
                }
                if (entity.getDescription() != null) {
                    latestEntity.setDescription(entity.getDescription());
                }
                if (entity.getAvatar() != null) {
                    latestEntity.setAvatar(entity.getAvatar());
                }
                if (entity.getSchema() != null) {
                    latestEntity.setSchema(entity.getSchema());
                }
                if (entity.getCurrentNodeId() != null) {
                    latestEntity.setCurrentNodeId(entity.getCurrentNodeId());
                }
                if (entity.getCategoryUid() != null) {
                    latestEntity.setCategoryUid(entity.getCategoryUid());
                }
                
                // 直接使用repository保存，避免递归调用save()方法
                WorkflowEntity savedEntity = workflowRepository.save(latestEntity);
                log.info("乐观锁冲突处理成功，实体ID: {}, 新版本: {}", savedEntity.getId(), savedEntity.getVersion());
                return savedEntity;
            } else {
                log.error("处理乐观锁冲突时未找到实体，UID: {}", entity.getUid());
                throw new RuntimeException("处理乐观锁冲突时未找到实体: " + entity.getUid());
            }
        } catch (ObjectOptimisticLockingFailureException retryException) {
            log.error("处理乐观锁冲突时再次发生冲突，实体UID: {}", entity.getUid());
            throw new RuntimeException("多次乐观锁冲突，请稍后重试", retryException);
        } catch (Exception ex) {
            log.error("处理乐观锁冲突时发生异常，实体UID: {}, 错误: {}", entity.getUid(), ex.getMessage(), ex);
            throw new RuntimeException("处理乐观锁冲突失败: " + ex.getMessage(), ex);
        }
    }

    @Override
    public WorkflowResponse convertToResponse(WorkflowEntity entity) {
        return modelMapper.map(entity, WorkflowResponse.class);
    }

    

}