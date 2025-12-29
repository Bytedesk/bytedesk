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
package com.bytedesk.kbase.blog;

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
public class BlogRestService extends BaseRestServiceWithExport<BlogEntity, BlogRequest, BlogResponse, BlogExcel> {

    private final BlogRepository blogRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;

    // 模块名称，用于权限检查
    private static final String MODULE_NAME = "BLOG";
    
    @Override
    public Page<BlogEntity> queryByOrgEntity(BlogRequest request) {
        Pageable pageable = request.getPageable();
        Specification<BlogEntity> specs = BlogSpecification.search(request, authService);
        return blogRepository.findAll(specs, pageable);
    }

    @Override
    public Page<BlogResponse> queryByOrg(BlogRequest request) {
        Page<BlogEntity> blogPage = queryByOrgEntity(request);
        return blogPage.map(this::convertToResponse);
    }

    @Override
    public Page<BlogResponse> queryByUser(BlogRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "blog", key = "#uid", unless="#result==null")
    @Override
    public Optional<BlogEntity> findByUid(String uid) {
        return blogRepository.findByUid(uid);
    }

    @Cacheable(value = "blog", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<BlogEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return blogRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return blogRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public BlogResponse create(BlogRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public BlogResponse createSystemBlog(BlogRequest request) {
        return createInternal(request, true);
    }

    private BlogResponse createInternal(BlogRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<BlogEntity> blog = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (blog.isPresent()) {
                return convertToResponse(blog.get());
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
        BlogEntity entity = modelMapper.map(request, BlogEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        BlogEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create blog failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public BlogResponse update(BlogRequest request) {
        Optional<BlogEntity> optional = blogRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            BlogEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            //
            BlogEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update blog failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Blog not found");
        }
    }

    @Override
    protected BlogEntity doSave(BlogEntity entity) {
        return blogRepository.save(entity);
    }

    @Override
    public BlogEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, BlogEntity entity) {
        try {
            Optional<BlogEntity> latest = blogRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                BlogEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return blogRepository.save(latestEntity);
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
        Optional<BlogEntity> optional = blogRepository.findByUid(uid);
        if (optional.isPresent()) {
            BlogEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // blogRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Blog not found");
        }
    }

    @Override
    public void delete(BlogRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public BlogResponse convertToResponse(BlogEntity entity) {
        return modelMapper.map(entity, BlogResponse.class);
    }

    @Override
    public BlogExcel convertToExcel(BlogEntity entity) {
        return modelMapper.map(entity, BlogExcel.class);
    }

    @Override
    protected Specification<BlogEntity> createSpecification(BlogRequest request) {
        return BlogSpecification.search(request, authService);
    }

    @Override
    protected Page<BlogEntity> executePageQuery(Specification<BlogEntity> spec, Pageable pageable) {
        return blogRepository.findAll(spec, pageable);
    }
    
    public void initBlogs(String orgUid) {
        // log.info("initBlogBlog");
    }

    
    
}
