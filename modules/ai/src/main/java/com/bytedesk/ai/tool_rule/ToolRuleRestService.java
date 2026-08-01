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
package com.bytedesk.ai.tool_rule;

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
public class ToolRuleRestService extends BaseRestServiceWithExport<ToolRuleEntity, ToolRuleRequest, ToolRuleResponse, ToolRuleExcel> {

    private final ToolRuleRepository tool_ruleRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<ToolRuleEntity> queryByOrgEntity(ToolRuleRequest request) {
        Pageable pageable = request.getPageable();
        Specification<ToolRuleEntity> specs = ToolRuleSpecification.search(request, authService);
        return tool_ruleRepository.findAll(specs, pageable);
    }

    @Override
    public Page<ToolRuleResponse> queryByOrg(ToolRuleRequest request) {
        Page<ToolRuleEntity> tool_rulePage = queryByOrgEntity(request);
        return tool_rulePage.map(this::convertToResponse);
    }

    @Override
    public Page<ToolRuleResponse> queryByUser(ToolRuleRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "tool_rule", key = "#uid", unless="#result==null")
    @Override
    public Optional<ToolRuleEntity> findByUid(String uid) {
        return tool_ruleRepository.findByUid(uid);
    }

    @Cacheable(value = "tool_rule", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<ToolRuleEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return tool_ruleRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return tool_ruleRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public ToolRuleResponse create(ToolRuleRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public ToolRuleResponse createSystemToolRule(ToolRuleRequest request) {
        return createInternal(request, true);
    }

    private ToolRuleResponse createInternal(ToolRuleRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<ToolRuleEntity> tool_rule = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (tool_rule.isPresent()) {
                return convertToResponse(tool_rule.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(ToolRulePermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        ToolRuleEntity entity = modelMapper.map(request, ToolRuleEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        ToolRuleEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create tool_rule failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public ToolRuleResponse update(ToolRuleRequest request) {
        Optional<ToolRuleEntity> optional = tool_ruleRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ToolRuleEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(ToolRulePermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            //
            ToolRuleEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update tool_rule failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("ToolRule not found");
        }
    }

    @Override
    protected ToolRuleEntity doSave(ToolRuleEntity entity) {
        return tool_ruleRepository.save(entity);
    }

    @Override
    public ToolRuleEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ToolRuleEntity entity) {
        try {
            Optional<ToolRuleEntity> latest = tool_ruleRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ToolRuleEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return tool_ruleRepository.save(latestEntity);
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
        Optional<ToolRuleEntity> optional = tool_ruleRepository.findByUid(uid);
        if (optional.isPresent()) {
            ToolRuleEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(ToolRulePermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // tool_ruleRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("ToolRule not found");
        }
    }

    @Override
    public void delete(ToolRuleRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public ToolRuleResponse convertToResponse(ToolRuleEntity entity) {
        return modelMapper.map(entity, ToolRuleResponse.class);
    }

    @Override
    public ToolRuleExcel convertToExcel(ToolRuleEntity entity) {
        return modelMapper.map(entity, ToolRuleExcel.class);
    }

    @Override
    protected Specification<ToolRuleEntity> createSpecification(ToolRuleRequest request) {
        return ToolRuleSpecification.search(request, authService);
    }

    @Override
    protected Page<ToolRuleEntity> executePageQuery(Specification<ToolRuleEntity> spec, Pageable pageable) {
        return tool_ruleRepository.findAll(spec, pageable);
    }
    
    public void initToolRules(String orgUid) {
        // log.info("initToolRuleToolRule");
        // for (String tool_rule : ToolRuleInitData.getAllToolRules()) {
        //     ToolRuleRequest tool_ruleRequest = ToolRuleRequest.builder()
        //             .uid(Utils.formatUid(orgUid, tool_rule))
        //             .name(tool_rule)
        //             .order(0)
        //             .type(ToolRuleTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemToolRule(tool_ruleRequest);
        // }
    }

    
    
}
