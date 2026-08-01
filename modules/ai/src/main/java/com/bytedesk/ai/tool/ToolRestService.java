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
package com.bytedesk.ai.tool;

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
public class ToolRestService extends BaseRestServiceWithExport<ToolEntity, ToolRequest, ToolResponse, ToolExcel> {

    private final ToolRepository toolRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<ToolEntity> queryByOrgEntity(ToolRequest request) {
        Pageable pageable = request.getPageable();
        Specification<ToolEntity> specs = ToolSpecification.search(request, authService);
        return toolRepository.findAll(specs, pageable);
    }

    @Override
    public Page<ToolResponse> queryByOrg(ToolRequest request) {
        Page<ToolEntity> toolPage = queryByOrgEntity(request);
        return toolPage.map(this::convertToResponse);
    }

    @Override
    public Page<ToolResponse> queryByUser(ToolRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "tool", key = "#uid", unless="#result==null")
    @Override
    public Optional<ToolEntity> findByUid(String uid) {
        return toolRepository.findByUid(uid);
    }

    @Cacheable(value = "tool", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<ToolEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return toolRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return toolRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public ToolResponse create(ToolRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public ToolResponse createSystemTool(ToolRequest request) {
        return createInternal(request, true);
    }

    private ToolResponse createInternal(ToolRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<ToolEntity> tool = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (tool.isPresent()) {
                return convertToResponse(tool.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(ToolPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        ToolEntity entity = modelMapper.map(request, ToolEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        ToolEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create tool failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public ToolResponse update(ToolRequest request) {
        Optional<ToolEntity> optional = toolRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ToolEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(ToolPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            //
            ToolEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update tool failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Tool not found");
        }
    }

    @Override
    protected ToolEntity doSave(ToolEntity entity) {
        return toolRepository.save(entity);
    }

    @Override
    public ToolEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ToolEntity entity) {
        try {
            Optional<ToolEntity> latest = toolRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ToolEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return toolRepository.save(latestEntity);
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
        Optional<ToolEntity> optional = toolRepository.findByUid(uid);
        if (optional.isPresent()) {
            ToolEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(ToolPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // toolRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Tool not found");
        }
    }

    @Override
    public void delete(ToolRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public ToolResponse convertToResponse(ToolEntity entity) {
        return modelMapper.map(entity, ToolResponse.class);
    }

    @Override
    public ToolExcel convertToExcel(ToolEntity entity) {
        return modelMapper.map(entity, ToolExcel.class);
    }

    @Override
    protected Specification<ToolEntity> createSpecification(ToolRequest request) {
        return ToolSpecification.search(request, authService);
    }

    @Override
    protected Page<ToolEntity> executePageQuery(Specification<ToolEntity> spec, Pageable pageable) {
        return toolRepository.findAll(spec, pageable);
    }
    
    public void initTools(String orgUid) {
        // log.info("initToolTool");
        // for (String tool : ToolInitData.getAllTools()) {
        //     ToolRequest toolRequest = ToolRequest.builder()
        //             .uid(Utils.formatUid(orgUid, tool))
        //             .name(tool)
        //             .order(0)
        //             .type(ToolTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemTool(toolRequest);
        // }
    }

    
    
}
