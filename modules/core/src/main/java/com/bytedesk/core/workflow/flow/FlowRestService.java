/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-10 11:35:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-11 11:39:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  技术/商务联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow.flow;

import lombok.AllArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.utils.Utils;

import java.util.Optional;

@Service
@AllArgsConstructor
public class FlowRestService extends BaseRestService<FlowEntity, FlowRequest, FlowResponse> {

    private final FlowRepository flowRepository;

    private final ModelMapper modelMapper;

    @Override
    public Page<FlowResponse> queryByOrg(FlowRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.ASC,
                "updatedAt");
        Specification<FlowEntity> spec = FlowSpecification.search(request);
        Page<FlowEntity> page = flowRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<FlowResponse> queryByUser(FlowRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.ASC,
                "updatedAt");
        Specification<FlowEntity> spec = FlowSpecification.search(request);
        Page<FlowEntity> page = flowRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Optional<FlowEntity> findByUid(String uid) {
        return flowRepository.findByUid(uid);
    }

    @Override
    public FlowResponse create(FlowRequest request) {
        
        FlowEntity flow = modelMapper.map(request, FlowEntity.class);
        flow.setUid(Utils.getUid());

        FlowEntity savedFlow = save(flow);
        if (savedFlow == null) {
            throw new RuntimeException("Failed to create flow");
        }
        
        return convertToResponse(flowRepository.save(flow));
    }

    @Override
    public FlowResponse update(FlowRequest request) {
        
        Optional<FlowEntity> flow = flowRepository.findByUid(request.getUid());
        if (flow.isPresent()) {
            FlowEntity flowEntity = flow.get();
            modelMapper.map(request, flowEntity);
            return convertToResponse(flowRepository.save(flowEntity));
        }
        return null;
    }

    @Override
    public FlowEntity save(FlowEntity entity) {
        try {
            return flowRepository.save(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<FlowEntity> flow = flowRepository.findByUid(uid);
        if (flow.isPresent()) {
            flow.get().setDeleted(true);
            save(flow.get());
        }
    }

    @Override
    public void delete(FlowRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, FlowEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public FlowResponse convertToResponse(FlowEntity entity) {
        return modelMapper.map(entity, FlowResponse.class);
    }

    // public FlowEntity createFlow(FlowEntity flow) {
    // flow.setCreatedAt(LocalDateTime.now());
    // flow.setUpdatedAt(LocalDateTime.now());
    // return flowRepository.save(flow);
    // }

    // public FlowEntity updateFlow(String id, FlowEntity flow) {
    // FlowEntity existingFlow = flowRepository.findById(id)
    // .orElseThrow(() -> new RuntimeException("Flow not found"));

    // existingFlow.setName(flow.getName());
    // existingFlow.setDescription(flow.getDescription());
    // // existingFlow.setGroups(flow.getGroups());
    // // existingFlow.setEdges(flow.getEdges());
    // // existingFlow.setVariables(flow.getVariables());
    // existingFlow.setSettings(flow.getSettings());
    // existingFlow.setUpdatedAt(LocalDateTime.now());

    // return flowRepository.save(existingFlow);
    // }

    // public void deleteFlow(String id) {
    // flowRepository.deleteById(id);
    // }

    // // public List<Flow> getWorkspaceFlows(String workspaceId) {
    // // return flowRepository.findByWorkspaceId(workspaceId);
    // // }

    // public FlowEntity getFlow(String id) {
    // return flowRepository.findById(id)
    // .orElseThrow(() -> new RuntimeException("Flow not found"));
    // }

    // public FlowEntity publishFlow(String id, String publishedTypeflowId) {
    // FlowEntity flow = getFlow(id);
    // // flow.setPublishedTypeflowId(publishedTypeflowId);
    // flow.setUpdatedAt(LocalDateTime.now());
    // return flowRepository.save(flow);
    // }

    // public void validateFlowAccess(String flowId, Map<String, Object> context) {
    // // TODO: 实现访问权限验证逻辑
    // }

}
