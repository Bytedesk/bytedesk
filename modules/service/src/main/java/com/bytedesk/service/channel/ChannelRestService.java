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
package com.bytedesk.service.channel;

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
public class ChannelRestService extends BaseRestServiceWithExport<ChannelEntity, ChannelRequest, ChannelResponse, ChannelExcel> {

    private final ChannelRepository channelRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<ChannelEntity> queryByOrgEntity(ChannelRequest request) {
        Pageable pageable = request.getPageable();
        Specification<ChannelEntity> specs = ChannelSpecification.search(request, authService);
        return channelRepository.findAll(specs, pageable);
    }

    @Override
    public Page<ChannelResponse> queryByOrg(ChannelRequest request) {
        Page<ChannelEntity> channelPage = queryByOrgEntity(request);
        return channelPage.map(this::convertToResponse);
    }

    @Override
    public Page<ChannelResponse> queryByUser(ChannelRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "channel", key = "#uid", unless="#result==null")
    @Override
    public Optional<ChannelEntity> findByUid(String uid) {
        return channelRepository.findByUid(uid);
    }

    @Cacheable(value = "channel", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<ChannelEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return channelRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return channelRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public ChannelResponse create(ChannelRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public ChannelResponse createSystemChannel(ChannelRequest request) {
        return createInternal(request, true);
    }

    private ChannelResponse createInternal(ChannelRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<ChannelEntity> channel = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (channel.isPresent()) {
                return convertToResponse(channel.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(ChannelPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        ChannelEntity entity = modelMapper.map(request, ChannelEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        ChannelEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create channel failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public ChannelResponse update(ChannelRequest request) {
        Optional<ChannelEntity> optional = channelRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ChannelEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(ChannelPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            //
            ChannelEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update channel failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Channel not found");
        }
    }

    @Override
    protected ChannelEntity doSave(ChannelEntity entity) {
        return channelRepository.save(entity);
    }

    @Override
    public ChannelEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ChannelEntity entity) {
        try {
            Optional<ChannelEntity> latest = channelRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ChannelEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return channelRepository.save(latestEntity);
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
        Optional<ChannelEntity> optional = channelRepository.findByUid(uid);
        if (optional.isPresent()) {
            ChannelEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(ChannelPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // channelRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Channel not found");
        }
    }

    @Override
    public void delete(ChannelRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public ChannelResponse convertToResponse(ChannelEntity entity) {
        return modelMapper.map(entity, ChannelResponse.class);
    }

    @Override
    public ChannelExcel convertToExcel(ChannelEntity entity) {
        return modelMapper.map(entity, ChannelExcel.class);
    }

    @Override
    protected Specification<ChannelEntity> createSpecification(ChannelRequest request) {
        return ChannelSpecification.search(request, authService);
    }

    @Override
    protected Page<ChannelEntity> executePageQuery(Specification<ChannelEntity> spec, Pageable pageable) {
        return channelRepository.findAll(spec, pageable);
    }
    
    public void initChannels(String orgUid) {
        // log.info("initChannelChannel");
    }

    
    
}
