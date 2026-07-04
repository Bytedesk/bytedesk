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
package com.bytedesk.ai.tool_audit;

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
public class ToolAuditRestService extends BaseRestServiceWithExport<ToolAuditEntity, ToolAuditRequest, ToolAuditResponse, ToolAuditExcel> {

    private final ToolAuditRepository tool_auditRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<ToolAuditEntity> queryByOrgEntity(ToolAuditRequest request) {
        Pageable pageable = request.getPageable();
        Specification<ToolAuditEntity> specs = ToolAuditSpecification.search(request, authService);
        return tool_auditRepository.findAll(specs, pageable);
    }

    @Override
    public Page<ToolAuditResponse> queryByOrg(ToolAuditRequest request) {
        Page<ToolAuditEntity> tool_auditPage = queryByOrgEntity(request);
        return tool_auditPage.map(this::convertToResponse);
    }

    @Override
    public Page<ToolAuditResponse> queryByUser(ToolAuditRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "tool_audit", key = "#uid", unless="#result==null")
    @Override
    public Optional<ToolAuditEntity> findByUid(String uid) {
        return tool_auditRepository.findByUid(uid);
    }

    @Cacheable(value = "tool_audit", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<ToolAuditEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return tool_auditRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return tool_auditRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public ToolAuditResponse create(ToolAuditRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public ToolAuditResponse createSystemToolAudit(ToolAuditRequest request) {
        return createInternal(request, true);
    }

    private ToolAuditResponse createInternal(ToolAuditRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<ToolAuditEntity> tool_audit = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (tool_audit.isPresent()) {
                return convertToResponse(tool_audit.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(ToolAuditPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        ToolAuditEntity entity = modelMapper.map(request, ToolAuditEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        ToolAuditEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create tool_audit failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public ToolAuditResponse update(ToolAuditRequest request) {
        Optional<ToolAuditEntity> optional = tool_auditRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ToolAuditEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(ToolAuditPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            //
            ToolAuditEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update tool_audit failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("ToolAudit not found");
        }
    }

    @Override
    protected ToolAuditEntity doSave(ToolAuditEntity entity) {
        return tool_auditRepository.save(entity);
    }

    @Override
    public ToolAuditEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ToolAuditEntity entity) {
        try {
            Optional<ToolAuditEntity> latest = tool_auditRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ToolAuditEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return tool_auditRepository.save(latestEntity);
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
        Optional<ToolAuditEntity> optional = tool_auditRepository.findByUid(uid);
        if (optional.isPresent()) {
            ToolAuditEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(ToolAuditPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // tool_auditRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("ToolAudit not found");
        }
    }

    @Override
    public void delete(ToolAuditRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public ToolAuditResponse convertToResponse(ToolAuditEntity entity) {
        return modelMapper.map(entity, ToolAuditResponse.class);
    }

    @Override
    public ToolAuditExcel convertToExcel(ToolAuditEntity entity) {
        return modelMapper.map(entity, ToolAuditExcel.class);
    }

    @Override
    protected Specification<ToolAuditEntity> createSpecification(ToolAuditRequest request) {
        return ToolAuditSpecification.search(request, authService);
    }

    @Override
    protected Page<ToolAuditEntity> executePageQuery(Specification<ToolAuditEntity> spec, Pageable pageable) {
        return tool_auditRepository.findAll(spec, pageable);
    }
    
    public void initToolAudits(String orgUid) {
        // log.info("initToolAuditToolAudit");
        // for (String tool_audit : ToolAuditInitData.getAllToolAudits()) {
        //     ToolAuditRequest tool_auditRequest = ToolAuditRequest.builder()
        //             .uid(Utils.formatUid(orgUid, tool_audit))
        //             .name(tool_audit)
        //             .order(0)
        //             .type(ToolAuditTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemToolAudit(tool_auditRequest);
        // }
    }

    
    
}
