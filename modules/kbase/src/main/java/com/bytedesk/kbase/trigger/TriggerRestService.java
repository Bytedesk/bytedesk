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
package com.bytedesk.kbase.trigger;

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

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.permission.PermissionService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.trigger.config.VisitorNoResponseProactiveMessageConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class TriggerRestService extends BaseRestServiceWithExport<TriggerEntity, TriggerRequest, TriggerResponse, TriggerExcel> {

    private final TriggerRepository triggerRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<TriggerEntity> queryByOrgEntity(TriggerRequest request) {
        Pageable pageable = request.getPageable();
        Specification<TriggerEntity> specs = TriggerSpecification.search(request, authService);
        return triggerRepository.findAll(specs, pageable);
    }

    @Override
    public Page<TriggerResponse> queryByOrg(TriggerRequest request) {
        Page<TriggerEntity> triggerPage = queryByOrgEntity(request);
        return triggerPage.map(this::convertToResponse);
    }

    @Override
    public Page<TriggerResponse> queryByUser(TriggerRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "trigger", key = "#uid", unless="#result==null")
    @Override
    public Optional<TriggerEntity> findByUid(String uid) {
        return triggerRepository.findByUid(uid);
    }

    @Cacheable(value = "trigger", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<TriggerEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return triggerRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return triggerRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public TriggerResponse create(TriggerRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public TriggerResponse createSystemTrigger(TriggerRequest request) {
        return createInternal(request, true);
    }

    private TriggerResponse createInternal(TriggerRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<TriggerEntity> trigger = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (trigger.isPresent()) {
                return convertToResponse(trigger.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(TriggerPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        TriggerEntity entity = modelMapper.map(request, TriggerEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        TriggerEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create trigger failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public TriggerResponse update(TriggerRequest request) {
        Optional<TriggerEntity> optional = triggerRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            TriggerEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(TriggerPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            //
            TriggerEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update trigger failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Trigger not found");
        }
    }

    @Override
    protected TriggerEntity doSave(TriggerEntity entity) {
        return triggerRepository.save(entity);
    }

    @Override
    public TriggerEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, TriggerEntity entity) {
        try {
            Optional<TriggerEntity> latest = triggerRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                TriggerEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return triggerRepository.save(latestEntity);
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
        Optional<TriggerEntity> optional = triggerRepository.findByUid(uid);
        if (optional.isPresent()) {
            TriggerEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(TriggerPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // triggerRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Trigger not found");
        }
    }

    @Override
    public void delete(TriggerRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public TriggerResponse convertToResponse(TriggerEntity entity) {
        return modelMapper.map(entity, TriggerResponse.class);
    }

    @Override
    public TriggerExcel convertToExcel(TriggerEntity entity) {
        return modelMapper.map(entity, TriggerExcel.class);
    }

    @Override
    protected Specification<TriggerEntity> createSpecification(TriggerRequest request) {
        return TriggerSpecification.search(request, authService);
    }

    @Override
    protected Page<TriggerEntity> executePageQuery(Specification<TriggerEntity> spec, Pageable pageable) {
        return triggerRepository.findAll(spec, pageable);
    }
    
    public void initTriggers(String orgUid) {
        if (!StringUtils.hasText(orgUid)) {
            return;
        }

        // default trigger: visitor long time no response proactive message
        triggerRepository
                .findByTriggerKeyAndOrgUidAndDeletedFalse(TriggerKeyConsts.VISITOR_NO_RESPONSE_PROACTIVE_MESSAGE, orgUid)
                .ifPresentOrElse(existing -> {
                    // already initialized
                }, () -> {
                    VisitorNoResponseProactiveMessageConfig config = VisitorNoResponseProactiveMessageConfig.defaults();

                    TriggerEntity entity = TriggerEntity.builder()
                            .uid(uidUtils.getUid())
                            .orgUid(orgUid)
                            .level(LevelEnum.ORGANIZATION.name())
                            .type(TriggerTypeEnum.THREAD.name())
                            .triggerKey(TriggerKeyConsts.VISITOR_NO_RESPONSE_PROACTIVE_MESSAGE)
                            .enabled(true)
                            .name("访客长时间未回复提醒")
                            .description("访客长时间未发送消息时，主动发送提醒消息")
                            .config(JSON.toJSONString(config))
                            .build();
                    save(entity);
                    log.info("initTriggers: created default trigger {} for org {}", entity.getTriggerKey(), orgUid);
                });
    }

    
    
}
