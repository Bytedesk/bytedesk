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
package com.bytedesk.training.course;

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
public class CourseRestService extends BaseRestServiceWithExport<CourseEntity, CourseRequest, CourseResponse, CourseExcel> {

    private final CourseRepository courseRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<CourseEntity> queryByOrgEntity(CourseRequest request) {
        Pageable pageable = request.getPageable();
        Specification<CourseEntity> specs = CourseSpecification.search(request, authService);
        return courseRepository.findAll(specs, pageable);
    }

    @Override
    public Page<CourseResponse> queryByOrg(CourseRequest request) {
        Page<CourseEntity> coursePage = queryByOrgEntity(request);
        return coursePage.map(this::convertToResponse);
    }

    @Override
    public Page<CourseResponse> queryByUser(CourseRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "course", key = "#uid", unless="#result==null")
    @Override
    public Optional<CourseEntity> findByUid(String uid) {
        return courseRepository.findByUid(uid);
    }

    @Cacheable(value = "course", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<CourseEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return courseRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return courseRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public CourseResponse create(CourseRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public CourseResponse createSystemCourse(CourseRequest request) {
        return createInternal(request, true);
    }

    private CourseResponse createInternal(CourseRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<CourseEntity> course = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (course.isPresent()) {
                return convertToResponse(course.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(CoursePermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        CourseEntity entity = modelMapper.map(request, CourseEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        CourseEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create course failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public CourseResponse update(CourseRequest request) {
        Optional<CourseEntity> optional = courseRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            CourseEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(CoursePermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            //
            CourseEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update course failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Course not found");
        }
    }

    @Override
    protected CourseEntity doSave(CourseEntity entity) {
        return courseRepository.save(entity);
    }

    @Override
    public CourseEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, CourseEntity entity) {
        try {
            Optional<CourseEntity> latest = courseRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                CourseEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return courseRepository.save(latestEntity);
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
        Optional<CourseEntity> optional = courseRepository.findByUid(uid);
        if (optional.isPresent()) {
            CourseEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(CoursePermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // courseRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Course not found");
        }
    }

    @Override
    public void delete(CourseRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public CourseResponse convertToResponse(CourseEntity entity) {
        return modelMapper.map(entity, CourseResponse.class);
    }

    @Override
    public CourseExcel convertToExcel(CourseEntity entity) {
        return modelMapper.map(entity, CourseExcel.class);
    }

    @Override
    protected Specification<CourseEntity> createSpecification(CourseRequest request) {
        return CourseSpecification.search(request, authService);
    }

    @Override
    protected Page<CourseEntity> executePageQuery(Specification<CourseEntity> spec, Pageable pageable) {
        return courseRepository.findAll(spec, pageable);
    }
    
    public void initCourses(String orgUid) {
        // log.info("initCourseCourse");
        // for (String course : CourseInitData.getAllCourses()) {
        //     CourseRequest courseRequest = CourseRequest.builder()
        //             .uid(Utils.formatUid(orgUid, course))
        //             .name(course)
        //             .order(0)
        //             .type(CourseTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemCourse(courseRequest);
        // }
    }

    
    
}
