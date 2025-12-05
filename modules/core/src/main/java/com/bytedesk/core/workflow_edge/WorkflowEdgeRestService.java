/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-04 11:31:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow_edge;

import java.util.List;
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
import com.bytedesk.core.workflow.WorkflowEntity;
import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class WorkflowEdgeRestService extends BaseRestServiceWithExport<WorkflowEdgeEntity, WorkflowEdgeRequest, WorkflowEdgeResponse, WorkflowEdgeExcel> {

    private final WorkflowEdgeRepository workflowEdgeRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<WorkflowEdgeEntity> createSpecification(WorkflowEdgeRequest request) {
        return WorkflowEdgeSpecification.search(request, authService);
    }

    @Override
    protected Page<WorkflowEdgeEntity> executePageQuery(Specification<WorkflowEdgeEntity> spec, Pageable pageable) {
        return workflowEdgeRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "workflow_edge", key = "#uid", unless="#result==null")
    @Override
    public Optional<WorkflowEdgeEntity> findByUid(String uid) {
        return workflowEdgeRepository.findByUid(uid);
    }

    @Cacheable(value = "workflow_edge", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<WorkflowEdgeEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return workflowEdgeRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return workflowEdgeRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public WorkflowEdgeResponse create(WorkflowEdgeRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<WorkflowEdgeEntity> workflow_edge = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (workflow_edge.isPresent()) {
                return convertToResponse(workflow_edge.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        WorkflowEdgeEntity entity = modelMapper.map(request, WorkflowEdgeEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        WorkflowEdgeEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create workflow_edge failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public WorkflowEdgeResponse update(WorkflowEdgeRequest request) {
        Optional<WorkflowEdgeEntity> optional = workflowEdgeRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            WorkflowEdgeEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            WorkflowEdgeEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update workflow_edge failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("WorkflowEdge not found");
        }
    }

    @Override
    protected WorkflowEdgeEntity doSave(WorkflowEdgeEntity entity) {
        return workflowEdgeRepository.save(entity);
    }

    @Override
    public WorkflowEdgeEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, WorkflowEdgeEntity entity) {
        try {
            Optional<WorkflowEdgeEntity> latest = workflowEdgeRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                WorkflowEdgeEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return workflowEdgeRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Transactional
    @Override
    public void deleteByUid(String uid) {
        Optional<WorkflowEdgeEntity> optional = workflowEdgeRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // workflow_edgeRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("WorkflowEdge not found");
        }
    }

    @Override
    public void delete(WorkflowEdgeRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public WorkflowEdgeResponse convertToResponse(WorkflowEdgeEntity entity) {
        return modelMapper.map(entity, WorkflowEdgeResponse.class);
    }

    @Override
    public WorkflowEdgeExcel convertToExcel(WorkflowEdgeEntity entity) {
        return modelMapper.map(entity, WorkflowEdgeExcel.class);
    }

    public void initWorkflowEdges(String orgUid) {
        // log.info("initThreadWorkflowEdge");
        // for (String workflow_edge : WorkflowEdgeInitData.getAllWorkflowEdges()) {
        //     WorkflowEdgeRequest workflow_edgeRequest = WorkflowEdgeRequest.builder()
        //             .uid(Utils.formatUid(orgUid, workflow_edge))
        //             .name(workflow_edge)
        //             .order(0)
        //             .type(WorkflowEdgeTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     create(workflow_edgeRequest);
        // }
    }
    
    // Service层需要的业务方法
    @Transactional
    public WorkflowEdgeEntity createEdge(WorkflowEdgeEntity edge) {
        return save(edge);
    }
    
    @Transactional
    public WorkflowEdgeEntity updateEdge(WorkflowEdgeEntity edge) {
        return save(edge);
    }
    
    @Transactional
    public void deleteEdge(String uid) {
        deleteByUid(uid);
    }
    
    public WorkflowEdgeEntity findEdgeByUid(String uid) {
        return findByUid(uid).orElse(null);
    }
    
    public boolean edgeExistsByUid(String uid) {
        return existsByUid(uid);
    }
    
    // 支持WorkflowEdgeService的复杂查询方法
    public boolean existsByUidAndDeletedFalse(String uid) {
        return workflowEdgeRepository.existsByUidAndDeletedFalse(uid);
    }
    
    public List<WorkflowEdgeEntity> findByWorkflow(WorkflowEntity workflow) {
        return workflowEdgeRepository.findByWorkflowAndDeletedFalse(workflow);
    }
    
    public List<WorkflowEdgeEntity> findByWorkflowAndType(WorkflowEntity workflow, String type) {
        return workflowEdgeRepository.findByWorkflowAndTypeAndDeletedFalse(workflow, type);
    }
    
    public List<WorkflowEdgeEntity> findEnabledEdgesByWorkflow(WorkflowEntity workflow) {
        return workflowEdgeRepository.findByWorkflowAndEnabledTrueAndDeletedFalse(workflow);
    }
    
    public List<WorkflowEdgeEntity> findEdgesConnectingNode(WorkflowEntity workflow, String nodeId) {
        return workflowEdgeRepository.findEdgesConnectingNode(workflow, nodeId);
    }
    
    public List<WorkflowEdgeEntity> findEdgesFromNode(WorkflowEntity workflow, String sourceNodeId) {
        return workflowEdgeRepository.findByWorkflowAndSourceNodeIdAndDeletedFalse(workflow, sourceNodeId);
    }
    
    public List<WorkflowEdgeEntity> findEdgesToNode(WorkflowEntity workflow, String targetNodeId) {
        return workflowEdgeRepository.findByWorkflowAndTargetNodeIdAndDeletedFalse(workflow, targetNodeId);
    }
    
    public List<WorkflowEdgeEntity> findEdgesBetweenNodes(WorkflowEntity workflow, String sourceNodeId, String targetNodeId) {
        return workflowEdgeRepository.findByWorkflowAndSourceNodeIdAndTargetNodeIdAndDeletedFalse(
                workflow, sourceNodeId, targetNodeId);
    }
    
    @Transactional
    public void deleteEdgeEntity(WorkflowEdgeEntity entity) {
        workflowEdgeRepository.delete(entity);
    }
    
    @Transactional
    public void deleteAllEdges(List<WorkflowEdgeEntity> edges) {
        workflowEdgeRepository.deleteAll(edges);
    }
    
}