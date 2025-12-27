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
package com.bytedesk.service.agent_next;

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
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.permission.PermissionService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.Utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class AgentNextRestService extends BaseRestServiceWithExport<AgentNextEntity, AgentNextRequest, AgentNextResponse, AgentNextExcel> {

    private final AgentNextRepository agent_nextRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;

    // 模块名称，用于权限检查
    private static final String MODULE_NAME = "TAG";
    
    @Override
    public Page<AgentNextEntity> queryByOrgEntity(AgentNextRequest request) {
        Pageable pageable = request.getPageable();
        Specification<AgentNextEntity> specs = AgentNextSpecification.search(request, authService);
        return agent_nextRepository.findAll(specs, pageable);
    }

    @Override
    public Page<AgentNextResponse> queryByOrg(AgentNextRequest request) {
        Page<AgentNextEntity> agent_nextPage = queryByOrgEntity(request);
        return agent_nextPage.map(this::convertToResponse);
    }

    @Override
    public Page<AgentNextResponse> queryByUser(AgentNextRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "agent_next", key = "#uid", unless="#result==null")
    @Override
    public Optional<AgentNextEntity> findByUid(String uid) {
        return agent_nextRepository.findByUid(uid);
    }

    @Cacheable(value = "agent_next", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<AgentNextEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return agent_nextRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return agent_nextRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public AgentNextResponse create(AgentNextRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public AgentNextResponse createSystemAgentNext(AgentNextRequest request) {
        return createInternal(request, true);
    }

    private AgentNextResponse createInternal(AgentNextRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<AgentNextEntity> agent_next = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (agent_next.isPresent()) {
                return convertToResponse(agent_next.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        AgentNextEntity entity = modelMapper.map(request, AgentNextEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        AgentNextEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create agent_next failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public AgentNextResponse update(AgentNextRequest request) {
        Optional<AgentNextEntity> optional = agent_nextRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            AgentNextEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            //
            AgentNextEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update agent_next failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("AgentNext not found");
        }
    }

    @Override
    protected AgentNextEntity doSave(AgentNextEntity entity) {
        return agent_nextRepository.save(entity);
    }

    @Override
    public AgentNextEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, AgentNextEntity entity) {
        try {
            Optional<AgentNextEntity> latest = agent_nextRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                AgentNextEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return agent_nextRepository.save(latestEntity);
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
        Optional<AgentNextEntity> optional = agent_nextRepository.findByUid(uid);
        if (optional.isPresent()) {
            AgentNextEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // agent_nextRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("AgentNext not found");
        }
    }

    @Override
    public void delete(AgentNextRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public AgentNextResponse convertToResponse(AgentNextEntity entity) {
        return modelMapper.map(entity, AgentNextResponse.class);
    }

    @Override
    public AgentNextExcel convertToExcel(AgentNextEntity entity) {
        return modelMapper.map(entity, AgentNextExcel.class);
    }

    @Override
    protected Specification<AgentNextEntity> createSpecification(AgentNextRequest request) {
        return AgentNextSpecification.search(request, authService);
    }

    @Override
    protected Page<AgentNextEntity> executePageQuery(Specification<AgentNextEntity> spec, Pageable pageable) {
        return agent_nextRepository.findAll(spec, pageable);
    }
    
    public void initAgentNexts(String orgUid) {
        // log.info("initAgentNextAgentNext");
        for (String agent_next : AgentNextInitData.getAllAgentNexts()) {
            AgentNextRequest agent_nextRequest = AgentNextRequest.builder()
                    .uid(Utils.formatUid(orgUid, agent_next))
                    .name(agent_next)
                    .order(0)
                    .type(AgentNextTypeEnum.THREAD.name())
                    .level(LevelEnum.ORGANIZATION.name())
                    .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                    .orgUid(orgUid)
                    .build();
            createSystemAgentNext(agent_nextRequest);
        }
    }

    
    
}
