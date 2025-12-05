/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 15:57:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.authority;

import lombok.AllArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.uid.UidUtils;

import io.jsonwebtoken.lang.Assert;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class AuthorityRestService extends BaseRestService<AuthorityEntity, AuthorityRequest, AuthorityResponse> {

    private final AuthorityRepository authorityRepository;
    private final ModelMapper modelMapper;
    private final UidUtils uidUtils;

    @Override
    public Page<AuthorityResponse> queryByOrg(AuthorityRequest request) {
        Pageable pageable = request.getPageable();
        Specification<AuthorityEntity> specification = AuthoritySpecification.search(request, authService);
        Page<AuthorityEntity> page = authorityRepository.findAll(specification, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<AuthorityResponse> queryByUser(AuthorityRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "authority", key = "#uid", unless = "#result==null")
    @Override
    public Optional<AuthorityEntity> findByUid(String uid) {
        return authorityRepository.findByUid(uid);
    }

    @Cacheable(value = "authority", key = "#value", unless = "#result == null")
    public Optional<AuthorityEntity> findByValue(String value) {
        return authorityRepository.findByValue(value);
    }

    public Boolean existsByValue(String value) {
        return authorityRepository.existsByValue(value);
    }

    /**
     * 为平台创建各层级的权限
     * 例如：传入 "TAG_READ" 会创建：
    * - TAG_PLATFORM_READ (平台级)
    * - TAG_ORGANIZATION_READ (组织级)
    * - TAG_DEPARTMENT_READ (部门级)
    * - TAG_WORKGROUP_READ (工作组级)
    * - TAG_AGENT_READ (客服级)
     * 
     * @param permissionValue 权限值，格式为 "MODULE_ACTION"，如 "TAG_READ", "TAG_CREATE"
     * @return 平台级权限的响应
     */
    public AuthorityResponse createForPlatform(String permissionValue) {
        Assert.hasText(permissionValue, "permissionValue must not be empty");
        
        AuthorityResponse firstResponse = null;
        
        // 解析权限值，提取模块前缀和操作类型
        // 例如：TAG_READ -> prefix=TAG_, action=READ
        // 例如：TICKET_CREATE -> prefix=TICKET_, action=CREATE
        int lastUnderscoreIndex = permissionValue.lastIndexOf('_');
        if (lastUnderscoreIndex > 0) {
            String modulePrefix = permissionValue.substring(0, lastUnderscoreIndex + 1); // 包含下划线，如 "TAG_"
            String action = permissionValue.substring(lastUnderscoreIndex + 1); // 如 "READ"
            
            // 创建各层级权限
            String[] levels = {
                LevelEnum.PLATFORM.name(),
                LevelEnum.ORGANIZATION.name(),
                LevelEnum.DEPARTMENT.name(),
                LevelEnum.WORKGROUP.name(),
                LevelEnum.AGENT.name(),
                LevelEnum.USER.name()
            };
            
            for (String level : levels) {
                // 构建层级权限值，如 TAG_PLATFORM_READ, TAG_ORGANIZATION_READ
                String levelPermissionValue = modulePrefix + level + "_" + action;
                
                AuthorityRequest levelRequest = AuthorityRequest.builder()
                        .uid(levelPermissionValue.toLowerCase())
                        .name(I18Consts.I18N_PREFIX + levelPermissionValue)
                        .value(levelPermissionValue)
                        .description("Permission for " + levelPermissionValue + " at " + level + " level")
                        .level(LevelEnum.PLATFORM.name()) // 权限本身都是平台级的
                        .build();
                AuthorityResponse response = create(levelRequest);
                
                // 返回第一个创建的权限（平台级）作为响应
                if (firstResponse == null) {
                    firstResponse = response;
                }
            }
        }
        
        return firstResponse;
    }

    @Override
    @Transactional
    public AuthorityResponse create(AuthorityRequest request) {
        if (existsByValue(request.getValue())) {
            return findByValue(request.getValue()).map(this::convertToResponse).orElse(null);
        }
        // 
        AuthorityEntity authorityEntity = modelMapper.map(request, AuthorityEntity.class);
        if (StringUtils.hasText(request.getUid())) {
            authorityEntity.setUid(request.getUid());
        } else {
            authorityEntity.setUid(uidUtils.getUid());
        }
        // 
        AuthorityEntity authorityEntitySaved = save(authorityEntity);
        if (authorityEntitySaved == null) {
            throw new RuntimeException("save authority failed");
        }
        return convertToResponse(authorityEntitySaved);
    }

    // @PreAuthorize(AuthorityPermissions.AUTHORITY_UPDATE)
   @Override
   @Transactional
    public AuthorityResponse update(AuthorityRequest request) {
        Optional<AuthorityEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            // 
            optional.get().setName(request.getName());
            optional.get().setDescription(request.getDescription());
            // 
            AuthorityEntity authorityEntity = save(optional.get());
            if (authorityEntity == null) {
                throw new RuntimeException("update authority failed");
            }
            return convertToResponse(authorityEntity);
        } else {
            throw new RuntimeException("authority " + request.getUid() + " not found");
        }
    }

    @CachePut(value = "authority", key = "#entity.uid")
    @Override
    public AuthorityEntity save(AuthorityEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    @Override
    protected AuthorityEntity doSave(AuthorityEntity entity) {
        return authorityRepository.save(entity);
    }

    @Override
    public AuthorityEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, AuthorityEntity entity) {
        try {
            Optional<AuthorityEntity> latest = authorityRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                AuthorityEntity latestEntity = latest.get();
                // 合并需要保留的数据
                authorityRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @CacheEvict(value = "authority", key = "#uid")
    @Override
    public void deleteByUid(String uid) {
        Optional<AuthorityEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        }
    }

    @CacheEvict(value = "authority", key = "#entity.uid")
    @Override
    public void delete(AuthorityRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public AuthorityResponse convertToResponse(AuthorityEntity entity) {
        return modelMapper.map(entity, AuthorityResponse.class);
    }

    @Override
    public AuthorityResponse queryByUid(AuthorityRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @Override
    protected Specification<AuthorityEntity> createSpecification(AuthorityRequest request) {
        return AuthoritySpecification.search(request, authService);
    }

    @Override
    protected Page<AuthorityEntity> executePageQuery(Specification<AuthorityEntity> spec, Pageable pageable) {
        return authorityRepository.findAll(spec, pageable);
    }

    public Set<AuthorityEntity> findByLevelMarker(String levelMarker) {
        List<AuthorityEntity> authorities = authorityRepository
            .findByValueContainingIgnoreCaseAndDeletedFalse(levelMarker);
        return new HashSet<>(authorities);
    }

}
