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
package com.bytedesk.core.calendar;

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
public class CalendarRestService extends BaseRestServiceWithExport<CalendarEntity, CalendarRequest, CalendarResponse, CalendarExcel> {

    private final CalendarRepository calendarRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<CalendarEntity> queryByOrgEntity(CalendarRequest request) {
        Pageable pageable = request.getPageable();
        Specification<CalendarEntity> specs = CalendarSpecification.search(request, authService);
        return calendarRepository.findAll(specs, pageable);
    }

    @Override
    public Page<CalendarResponse> queryByOrg(CalendarRequest request) {
        Page<CalendarEntity> calendarPage = queryByOrgEntity(request);
        return calendarPage.map(this::convertToResponse);
    }

    @Override
    public Page<CalendarResponse> queryByUser(CalendarRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "calendar", key = "#uid", unless="#result==null")
    @Override
    public Optional<CalendarEntity> findByUid(String uid) {
        return calendarRepository.findByUid(uid);
    }

    @Cacheable(value = "calendar", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<CalendarEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return calendarRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return calendarRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public CalendarResponse create(CalendarRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public CalendarResponse createSystemCalendar(CalendarRequest request) {
        return createInternal(request, true);
    }

    private CalendarResponse createInternal(CalendarRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<CalendarEntity> calendar = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (calendar.isPresent()) {
                return convertToResponse(calendar.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(CalendarPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        CalendarEntity entity = modelMapper.map(request, CalendarEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        CalendarEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create calendar failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public CalendarResponse update(CalendarRequest request) {
        Optional<CalendarEntity> optional = calendarRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            CalendarEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(CalendarPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            //
            CalendarEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update calendar failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Calendar not found");
        }
    }

    @Override
    protected CalendarEntity doSave(CalendarEntity entity) {
        return calendarRepository.save(entity);
    }

    @Override
    public CalendarEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, CalendarEntity entity) {
        try {
            Optional<CalendarEntity> latest = calendarRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                CalendarEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return calendarRepository.save(latestEntity);
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
        Optional<CalendarEntity> optional = calendarRepository.findByUid(uid);
        if (optional.isPresent()) {
            CalendarEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(CalendarPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // calendarRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Calendar not found");
        }
    }

    @Override
    public void delete(CalendarRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public CalendarResponse convertToResponse(CalendarEntity entity) {
        return modelMapper.map(entity, CalendarResponse.class);
    }

    @Override
    public CalendarExcel convertToExcel(CalendarEntity entity) {
        return modelMapper.map(entity, CalendarExcel.class);
    }

    @Override
    protected Specification<CalendarEntity> createSpecification(CalendarRequest request) {
        return CalendarSpecification.search(request, authService);
    }

    @Override
    protected Page<CalendarEntity> executePageQuery(Specification<CalendarEntity> spec, Pageable pageable) {
        return calendarRepository.findAll(spec, pageable);
    }
    
    public void initCalendars(String orgUid) {
        // log.info("initCalendarCalendar");
        // for (String calendar : CalendarInitData.getAllCalendars()) {
        //     CalendarRequest calendarRequest = CalendarRequest.builder()
        //             .uid(Utils.formatUid(orgUid, calendar))
        //             .name(calendar)
        //             .order(0)
        //             .type(CalendarTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemCalendar(calendarRequest);
        // }
    }

    
    
}
