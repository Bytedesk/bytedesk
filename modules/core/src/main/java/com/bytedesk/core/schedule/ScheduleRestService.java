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
package com.bytedesk.core.schedule;

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
public class ScheduleRestService extends BaseRestServiceWithExport<ScheduleEntity, ScheduleRequest, ScheduleResponse, ScheduleExcel> {

    private final ScheduleRepository scheduleRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<ScheduleEntity> queryByOrgEntity(ScheduleRequest request) {
        Pageable pageable = request.getPageable();
        Specification<ScheduleEntity> specs = ScheduleSpecification.search(request, authService);
        return scheduleRepository.findAll(specs, pageable);
    }

    @Override
    public Page<ScheduleResponse> queryByOrg(ScheduleRequest request) {
        Page<ScheduleEntity> schedulePage = queryByOrgEntity(request);
        return schedulePage.map(this::convertToResponse);
    }

    @Override
    public Page<ScheduleResponse> queryByUser(ScheduleRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "schedule", key = "#uid", unless="#result==null")
    @Override
    public Optional<ScheduleEntity> findByUid(String uid) {
        return scheduleRepository.findByUid(uid);
    }

    @Cacheable(value = "schedule", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<ScheduleEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return scheduleRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return scheduleRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public ScheduleResponse create(ScheduleRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public ScheduleResponse createSystemSchedule(ScheduleRequest request) {
        return createInternal(request, true);
    }

    private ScheduleResponse createInternal(ScheduleRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<ScheduleEntity> schedule = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (schedule.isPresent()) {
                return convertToResponse(schedule.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(SchedulePermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        ScheduleEntity entity = modelMapper.map(request, ScheduleEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        ScheduleEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create schedule failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public ScheduleResponse update(ScheduleRequest request) {
        Optional<ScheduleEntity> optional = scheduleRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ScheduleEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(SchedulePermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            //
            ScheduleEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update schedule failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Schedule not found");
        }
    }

    @Override
    protected ScheduleEntity doSave(ScheduleEntity entity) {
        return scheduleRepository.save(entity);
    }

    @Override
    public ScheduleEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ScheduleEntity entity) {
        try {
            Optional<ScheduleEntity> latest = scheduleRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ScheduleEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return scheduleRepository.save(latestEntity);
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
        Optional<ScheduleEntity> optional = scheduleRepository.findByUid(uid);
        if (optional.isPresent()) {
            ScheduleEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(SchedulePermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // scheduleRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Schedule not found");
        }
    }

    @Override
    public void delete(ScheduleRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public ScheduleResponse convertToResponse(ScheduleEntity entity) {
        return modelMapper.map(entity, ScheduleResponse.class);
    }

    @Override
    public ScheduleExcel convertToExcel(ScheduleEntity entity) {
        return modelMapper.map(entity, ScheduleExcel.class);
    }

    @Override
    protected Specification<ScheduleEntity> createSpecification(ScheduleRequest request) {
        return ScheduleSpecification.search(request, authService);
    }

    @Override
    protected Page<ScheduleEntity> executePageQuery(Specification<ScheduleEntity> spec, Pageable pageable) {
        return scheduleRepository.findAll(spec, pageable);
    }
    
    public void initSchedules(String orgUid) {
        // log.info("initScheduleSchedule");
        for (String schedule : ScheduleInitData.getAllSchedules()) {
            ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                    .uid(Utils.formatUid(orgUid, schedule))
                    .name(schedule)
                    .order(0)
                    .type(ScheduleTypeEnum.THREAD.name())
                    .level(LevelEnum.ORGANIZATION.name())
                    .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                    .orgUid(orgUid)
                    .build();
            createSystemSchedule(scheduleRequest);
        }
    }

    
    
}
