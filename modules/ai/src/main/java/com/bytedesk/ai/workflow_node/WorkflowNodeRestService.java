/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-04 11:31:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.workflow_node;

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
import com.bytedesk.ai.workflow.WorkflowEntity;
import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class WorkflowNodeRestService extends BaseRestServiceWithExport<WorkflowNodeEntity, WorkflowNodeRequest, WorkflowNodeResponse, WorkflowNodeExcel> {

    private final WorkflowNodeRepository workflowNodeRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<WorkflowNodeEntity> createSpecification(WorkflowNodeRequest request) {
        return WorkflowNodeSpecification.search(request, authService);
    }

    @Override
    protected Page<WorkflowNodeEntity> executePageQuery(Specification<WorkflowNodeEntity> spec, Pageable pageable) {
        return workflowNodeRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "workflow_node", key = "#uid", unless="#result==null")
    @Override
    public Optional<WorkflowNodeEntity> findByUid(String uid) {
        return workflowNodeRepository.findByUid(uid);
    }

    @Cacheable(value = "workflow_node", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<WorkflowNodeEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return workflowNodeRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return workflowNodeRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public WorkflowNodeResponse create(WorkflowNodeRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+nodeType是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getNodeType())) {
            Optional<WorkflowNodeEntity> workflow_node = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getNodeType());
            if (workflow_node.isPresent()) {
                return convertToResponse(workflow_node.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        WorkflowNodeEntity entity = WorkflowNodeConvert.toEntity(request, null);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        WorkflowNodeEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create workflow_node failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public WorkflowNodeResponse update(WorkflowNodeRequest request) {
        Optional<WorkflowNodeEntity> optional = workflowNodeRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            WorkflowNodeEntity entity = optional.get();
            WorkflowNodeConvert.updateEntity(entity, request);
            //
            WorkflowNodeEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update workflow_node failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("WorkflowNode not found");
        }
    }

    @Override
    protected WorkflowNodeEntity doSave(WorkflowNodeEntity entity) {
        return workflowNodeRepository.save(entity);
    }

    @Override
    public WorkflowNodeEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, WorkflowNodeEntity entity) {
        try {
            Optional<WorkflowNodeEntity> latest = workflowNodeRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                WorkflowNodeEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return workflowNodeRepository.save(latestEntity);
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
        Optional<WorkflowNodeEntity> optional = workflowNodeRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // workflow_nodeRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("WorkflowNode not found");
        }
    }

    @Override
    public void delete(WorkflowNodeRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public WorkflowNodeResponse convertToResponse(WorkflowNodeEntity entity) {
        return WorkflowNodeConvert.toResponse(entity);
    }

    @Override
    public WorkflowNodeExcel convertToExcel(WorkflowNodeEntity entity) {
        return modelMapper.map(entity, WorkflowNodeExcel.class);
    }

    public void initWorkflowNodes(String orgUid) {
        // log.info("initThreadWorkflowNode");
        // for (String workflow_node : WorkflowNodeInitData.getAllWorkflowNodes()) {
        //     WorkflowNodeRequest workflow_nodeRequest = WorkflowNodeRequest.builder()
        //             .uid(Utils.formatUid(orgUid, workflow_node))
        //             .name(workflow_node)
        //             .order(0)
        //             .type(WorkflowNodeTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     create(workflow_nodeRequest);
        // }
    }
    
    // Service层需要的业务方法
    @Transactional
    public WorkflowNodeEntity createNode(WorkflowNodeEntity node) {
        return save(node);
    }
    
    @Transactional
    public WorkflowNodeEntity updateNode(WorkflowNodeEntity node) {
        return save(node);
    }
    
    @Transactional
    public void deleteNode(String uid) {
        deleteByUid(uid);
    }
    
    public WorkflowNodeEntity findNodeByUid(String uid) {
        return findByUid(uid).orElse(null);
    }
    
    public boolean nodeExistsByUid(String uid) {
        return existsByUid(uid);
    }
    
    // 支持WorkflowNodeService的复杂查询方法
    public boolean existsByUidAndDeletedFalse(String uid) {
        return workflowNodeRepository.existsByUidAndDeletedFalse(uid);
    }
    
    public WorkflowNodeEntity findByUidAndDeletedFalse(String uid) {
        return workflowNodeRepository.findByUidAndDeletedFalse(uid).orElse(null);
    }
    
    public List<WorkflowNodeEntity> findByWorkflowOrderBySortOrderAsc(WorkflowEntity workflow) {
        return workflowNodeRepository.findByWorkflowOrderBySortOrderAsc(workflow);
    }
    
    public List<WorkflowNodeEntity> findByWorkflowAndType(WorkflowEntity workflow, String type) {
        return workflowNodeRepository.findByWorkflowAndType(workflow, type);
    }
    
    public List<WorkflowNodeEntity> findByWorkflowAndEnabledTrueOrderBySortOrderAsc(WorkflowEntity workflow) {
        return workflowNodeRepository.findByWorkflowAndEnabledTrueOrderBySortOrderAsc(workflow);
    }
    
    public List<WorkflowNodeEntity> findByWorkflowAndStatus(WorkflowEntity workflow, String status) {
        return workflowNodeRepository.findByWorkflowAndStatus(workflow, status);
    }
    
    @Transactional
    public void deleteNodeEntity(WorkflowNodeEntity entity) {
        workflowNodeRepository.delete(entity);
    }
    
    @Transactional
    public void deleteAllNodes(List<WorkflowNodeEntity> nodes) {
        workflowNodeRepository.deleteAll(nodes);
    }
    
    @Transactional
    public void saveAllNodes(List<WorkflowNodeEntity> nodes) {
        workflowNodeRepository.saveAll(nodes);
    }
    
}