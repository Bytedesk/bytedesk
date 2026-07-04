/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.tool_approval;

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
import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.permission.PermissionService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ToolApprovalRestService extends BaseRestServiceWithExport<ToolApprovalEntity, ToolApprovalRequest, ToolApprovalResponse, ToolApprovalExcel> {

    private final ToolApprovalRepository tool_approvalRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<ToolApprovalEntity> queryByOrgEntity(ToolApprovalRequest request) {
        Pageable pageable = request.getPageable();
        Specification<ToolApprovalEntity> specs = ToolApprovalSpecification.search(request, authService);
        return tool_approvalRepository.findAll(specs, pageable);
    }

    @Override
    public Page<ToolApprovalResponse> queryByOrg(ToolApprovalRequest request) {
        Page<ToolApprovalEntity> tool_approvalPage = queryByOrgEntity(request);
        return tool_approvalPage.map(this::convertToResponse);
    }

    @Override
    public Page<ToolApprovalResponse> queryByUser(ToolApprovalRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "tool_approval", key = "#uid", unless="#result==null")
    @Override
    public Optional<ToolApprovalEntity> findByUid(String uid) {
        return tool_approvalRepository.findByUid(uid);
    }

    @Cacheable(value = "tool_approval", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<ToolApprovalEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return tool_approvalRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return tool_approvalRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public ToolApprovalResponse create(ToolApprovalRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public ToolApprovalResponse createSystemToolApproval(ToolApprovalRequest request) {
        return createInternal(request, true);
    }

    private ToolApprovalResponse createInternal(ToolApprovalRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<ToolApprovalEntity> tool_approval = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (tool_approval.isPresent()) {
                return convertToResponse(tool_approval.get());
            }
        }
        
        // 获取用户信息
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        
        // 确定数据层级
        String level = request.getLevel();
        if (!StringUtils.hasText(level)) {
            level = LevelEnum.ORGANIZATION.name();
            request.setLevel(level);
        }
        
        // 检查用户是否有权限创建该层级的数据
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(ToolApprovalPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        ToolApprovalEntity entity = modelMapper.map(request, ToolApprovalEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        ToolApprovalEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create tool_approval failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public ToolApprovalResponse update(ToolApprovalRequest request) {
        Optional<ToolApprovalEntity> optional = tool_approvalRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ToolApprovalEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(ToolApprovalPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            //
            ToolApprovalEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update tool_approval failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("ToolApproval not found");
        }
    }

    @Override
    protected ToolApprovalEntity doSave(ToolApprovalEntity entity) {
        return tool_approvalRepository.save(entity);
    }

    @Override
    public ToolApprovalEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ToolApprovalEntity entity) {
        try {
            Optional<ToolApprovalEntity> latest = tool_approvalRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ToolApprovalEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return tool_approvalRepository.save(latestEntity);
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
        Optional<ToolApprovalEntity> optional = tool_approvalRepository.findByUid(uid);
        if (optional.isPresent()) {
            ToolApprovalEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(ToolApprovalPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // tool_approvalRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("ToolApproval not found");
        }
    }

    @Override
    public void delete(ToolApprovalRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public ToolApprovalResponse convertToResponse(ToolApprovalEntity entity) {
        return modelMapper.map(entity, ToolApprovalResponse.class);
    }

    @Override
    public ToolApprovalExcel convertToExcel(ToolApprovalEntity entity) {
        return modelMapper.map(entity, ToolApprovalExcel.class);
    }

    @Override
    protected Specification<ToolApprovalEntity> createSpecification(ToolApprovalRequest request) {
        return ToolApprovalSpecification.search(request, authService);
    }

    @Override
    protected Page<ToolApprovalEntity> executePageQuery(Specification<ToolApprovalEntity> spec, Pageable pageable) {
        return tool_approvalRepository.findAll(spec, pageable);
    }
    
    public void initToolApprovals(String orgUid) {
        // log.info("initToolApprovalToolApproval");
        // for (String tool_approval : ToolApprovalInitData.getAllToolApprovals()) {
        //     ToolApprovalRequest tool_approvalRequest = ToolApprovalRequest.builder()
        //             .uid(Utils.formatUid(orgUid, tool_approval))
        //             .name(tool_approval)
        //             .order(0)
        //             .type(ToolApprovalTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemToolApproval(tool_approvalRequest);
        // }
    }

    
    
}
