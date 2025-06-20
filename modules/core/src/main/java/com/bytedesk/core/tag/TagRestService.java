/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-20 12:54:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.tag;

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
import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.Utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class TagRestService extends BaseRestServiceWithExcel<TagEntity, TagRequest, TagResponse, TagExcel> {

    private final TagRepository tagRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<TagEntity> queryByOrgEntity(TagRequest request) {
        Pageable pageable = request.getPageable();
        Specification<TagEntity> spec = TagSpecification.search(request);
        return tagRepository.findAll(spec, pageable);
    }

    @Override
    public Page<TagResponse> queryByOrg(TagRequest request) {
        Page<TagEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<TagResponse> queryByUser(TagRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    @Override
    public TagResponse queryByUid(TagRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @Cacheable(value = "tag", key = "#uid", unless="#result==null")
    @Override
    public Optional<TagEntity> findByUid(String uid) {
        return tagRepository.findByUid(uid);
    }

    @Cacheable(value = "tag", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<TagEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return tagRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return tagRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public TagResponse create(TagRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<TagEntity> tag = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (tag.isPresent()) {
                return convertToResponse(tag.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        TagEntity entity = modelMapper.map(request, TagEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        TagEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create tag failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public TagResponse update(TagRequest request) {
        Optional<TagEntity> optional = tagRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            TagEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            TagEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update tag failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Tag not found");
        }
    }

    @Override
    protected TagEntity doSave(TagEntity entity) {
        return tagRepository.save(entity);
    }

    @Override
    public TagEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, TagEntity entity) {
        try {
            Optional<TagEntity> latest = tagRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                TagEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return tagRepository.save(latestEntity);
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
        Optional<TagEntity> optional = tagRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // tagRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Tag not found");
        }
    }

    @Override
    public void delete(TagRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public TagResponse convertToResponse(TagEntity entity) {
        return modelMapper.map(entity, TagResponse.class);
    }

    @Override
    public TagExcel convertToExcel(TagEntity entity) {
        return modelMapper.map(entity, TagExcel.class);
    }
    
    public void initTags(String orgUid) {
        // log.info("initThreadTag");
        for (String tag : TagInitData.getAllTags()) {
            TagRequest tagRequest = TagRequest.builder()
                    .uid(Utils.formatUid(orgUid, tag))
                    .name(tag)
                    .order(0)
                    .type(TagTypeEnum.THREAD.name())
                    .level(LevelEnum.ORGANIZATION.name())
                    .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                    .orgUid(orgUid)
                    .build();
            create(tagRequest);
        }
    }
    
}
