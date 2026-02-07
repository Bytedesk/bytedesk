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
package com.bytedesk.service.routing_pool;

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
import com.bytedesk.core.thread.ThreadRequest;
import com.bytedesk.core.thread.ThreadResponseSimple;
import com.bytedesk.service.agent.AgentRestService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class RoutingPoolRestService extends BaseRestServiceWithExport<RoutingPoolEntity, RoutingPoolRequest, RoutingPoolResponse, RoutingPoolExcel> {

    private final RoutingPoolRepository routingPoolRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;

    private final AgentRestService agentRestService;
    
    @Override
    public Page<RoutingPoolEntity> queryByOrgEntity(RoutingPoolRequest request) {
        Pageable pageable = request.getPageable();
        Specification<RoutingPoolEntity> specs = RoutingPoolSpecification.search(request, authService);
        return routingPoolRepository.findAll(specs, pageable);
    }

    @Override
    public Page<RoutingPoolResponse> queryByOrg(RoutingPoolRequest request) {
        Page<RoutingPoolEntity> routing_poolPage = queryByOrgEntity(request);
        return routing_poolPage.map(this::convertToResponse);
    }

    @Override
    public Page<RoutingPoolResponse> queryByUser(RoutingPoolRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "routing_pool", key = "#uid", unless="#result==null")
    @Override
    public Optional<RoutingPoolEntity> findByUid(String uid) {
        return routingPoolRepository.findByUid(uid);
    }

    @Cacheable(value = "routing_pool", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<RoutingPoolEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return routingPoolRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return routingPoolRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public RoutingPoolResponse create(RoutingPoolRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public RoutingPoolResponse createSystemRoutingPool(RoutingPoolRequest request) {
        return createInternal(request, true);
    }

    private RoutingPoolResponse createInternal(RoutingPoolRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<RoutingPoolEntity> routing_pool = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (routing_pool.isPresent()) {
                return convertToResponse(routing_pool.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(RoutingPoolPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        RoutingPoolEntity entity = modelMapper.map(request, RoutingPoolEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        RoutingPoolEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create routing_pool failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public RoutingPoolResponse update(RoutingPoolRequest request) {
        Optional<RoutingPoolEntity> optional = routingPoolRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            RoutingPoolEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(RoutingPoolPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            //
            RoutingPoolEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update routing_pool failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("RoutingPool not found");
        }
    }

    @Override
    protected RoutingPoolEntity doSave(RoutingPoolEntity entity) {
        return routingPoolRepository.save(entity);
    }

    @Override
    public RoutingPoolEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, RoutingPoolEntity entity) {
        try {
            Optional<RoutingPoolEntity> latest = routingPoolRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                RoutingPoolEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return routingPoolRepository.save(latestEntity);
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
        Optional<RoutingPoolEntity> optional = routingPoolRepository.findByUid(uid);
        if (optional.isPresent()) {
            RoutingPoolEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(RoutingPoolPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // routing_poolRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("RoutingPool not found");
        }
    }

    /**
     * 手动接入：从路由池中认领一个等待会话并接入。
     */
    @Transactional
    public ThreadResponseSimple acceptManualThread(RoutingPoolRequest request) {
        if (request == null || !StringUtils.hasText(request.getUid())) {
            throw new IllegalArgumentException("routingPool uid is required");
        }

        RoutingPoolEntity entity = routingPoolRepository.findByUid(request.getUid())
                .orElseThrow(() -> new RuntimeException("RoutingPool not found"));

        if (!RoutingPoolTypeEnum.MANUAL_THREAD.name().equalsIgnoreCase(entity.getType())) {
            throw new IllegalStateException("RoutingPool type mismatch");
        }

        if (Boolean.TRUE.equals(entity.isDeleted())) {
            throw new IllegalStateException("RoutingPool already deleted");
        }

        if (!RoutingPoolStatusEnum.WAITING.name().equalsIgnoreCase(entity.getStatus())) {
            throw new IllegalStateException("RoutingPool already accepted: " + entity.getStatus());
        }

        if (!StringUtils.hasText(entity.getThreadUid())) {
            throw new IllegalStateException("RoutingPool threadUid missing");
        }

        // 先接入会话（会触发 ThreadAcceptEvent 等既有逻辑）
        ThreadRequest threadRequest = ThreadRequest.builder().uid(entity.getThreadUid()).build();
        try {
            ThreadResponseSimple accepted = agentRestService.acceptByAgent(threadRequest);

            // 再标记路由池条目为已处理（避免重复展示/重复认领）
            entity.setStatus(RoutingPoolStatusEnum.ACCEPTED.name());
            entity.setDeleted(true);
            routingPoolRepository.save(entity);

            return accepted;
        } catch (IllegalStateException e) {
            String message = e.getMessage();
            if (message != null && message.startsWith("thread already accepted:")) {
                entity.setStatus(RoutingPoolStatusEnum.ACCEPTED.name());
                entity.setDeleted(true);
                routingPoolRepository.save(entity);
                return null;
            }
            throw e;
        }
    }

    @Override
    public void delete(RoutingPoolRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public RoutingPoolResponse convertToResponse(RoutingPoolEntity entity) {
        return modelMapper.map(entity, RoutingPoolResponse.class);
    }

    @Override
    public RoutingPoolExcel convertToExcel(RoutingPoolEntity entity) {
        return modelMapper.map(entity, RoutingPoolExcel.class);
    }

    @Override
    protected Specification<RoutingPoolEntity> createSpecification(RoutingPoolRequest request) {
        return RoutingPoolSpecification.search(request, authService);
    }

    @Override
    protected Page<RoutingPoolEntity> executePageQuery(Specification<RoutingPoolEntity> spec, Pageable pageable) {
        return routingPoolRepository.findAll(spec, pageable);
    }
    
    public void initRoutingPools(String orgUid) {
        // log.info("initRoutingPoolRoutingPool");
        // for (String routing_pool : RoutingPoolInitData.getAllRoutingPools()) {
        //     RoutingPoolRequest routing_poolRequest = RoutingPoolRequest.builder()
        //             .uid(Utils.formatUid(orgUid, routing_pool))
        //             .name(routing_pool)
        //             .order(0)
        //             .type(RoutingPoolTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemRoutingPool(routing_poolRequest);
        // }
    }

    
    
}
