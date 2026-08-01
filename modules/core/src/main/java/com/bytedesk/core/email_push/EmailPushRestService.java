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
package com.bytedesk.core.email_push;

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
import com.bytedesk.core.constant.I18Consts;
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
public class EmailPushRestService extends BaseRestServiceWithExport<EmailPushEntity, EmailPushRequest, EmailPushResponse, EmailPushExcel> {

    private final EmailPushRepository emailPushRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<EmailPushEntity> queryByOrgEntity(EmailPushRequest request) {
        Pageable pageable = request.getPageable();
        Specification<EmailPushEntity> specs = EmailPushSpecification.search(request, authService);
        return emailPushRepository.findAll(specs, pageable);
    }

    @Override
    public Page<EmailPushResponse> queryByOrg(EmailPushRequest request) {
        Page<EmailPushEntity> email_pushPage = queryByOrgEntity(request);
        return email_pushPage.map(this::convertToResponse);
    }

    @Override
    public Page<EmailPushResponse> queryByUser(EmailPushRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "email_push", key = "#uid", unless="#result==null")
    @Override
    public Optional<EmailPushEntity> findByUid(String uid) {
        return emailPushRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return emailPushRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public EmailPushResponse create(EmailPushRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public EmailPushResponse createSystemEmailPush(EmailPushRequest request) {
        return createInternal(request, true);
    }

    private EmailPushResponse createInternal(EmailPushRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(EmailPushPermissions.MODULE_NAME, level)) {
            throw new RuntimeException(I18Consts.I18N_PERMISSION_CREATE_DENIED);
        }
        
        // 
        EmailPushEntity entity = modelMapper.map(request, EmailPushEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        EmailPushEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException(I18Consts.I18N_CREATE_FAILED);
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public EmailPushResponse update(EmailPushRequest request) {
        Optional<EmailPushEntity> optional = emailPushRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            EmailPushEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(EmailPushPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException(I18Consts.I18N_PERMISSION_UPDATE_DENIED);
            }
            
            modelMapper.map(request, entity);
            //
            EmailPushEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException(I18Consts.I18N_UPDATE_FAILED);
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @Override
    protected EmailPushEntity doSave(EmailPushEntity entity) {
        return emailPushRepository.save(entity);
    }

    @Override
    public EmailPushEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, EmailPushEntity entity) {
        try {
            Optional<EmailPushEntity> latest = emailPushRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                EmailPushEntity latestEntity = latest.get();
                latestEntity.setSender(entity.getSender());
                latestEntity.setContent(entity.getContent());
                latestEntity.setCountry(entity.getCountry());
                latestEntity.setReceiver(entity.getReceiver());
                latestEntity.setIp(entity.getIp());
                latestEntity.setIpLocation(entity.getIpLocation());
                latestEntity.setDeviceUid(entity.getDeviceUid());
                latestEntity.setType(entity.getType());
                latestEntity.setStatus(entity.getStatus());
                latestEntity.setChannel(entity.getChannel());
                latestEntity.setSendSuccess(entity.getSendSuccess());
                latestEntity.setSendMessage(entity.getSendMessage());
                return emailPushRepository.save(latestEntity);
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
        Optional<EmailPushEntity> optional = emailPushRepository.findByUid(uid);
        if (optional.isPresent()) {
            EmailPushEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(EmailPushPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException(I18Consts.I18N_PERMISSION_DELETE_DENIED);
            }
            
            entity.setDeleted(true);
            save(entity);
            // email_pushRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @Override
    public void delete(EmailPushRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public EmailPushResponse convertToResponse(EmailPushEntity entity) {
        return modelMapper.map(entity, EmailPushResponse.class);
    }

    @Override
    public EmailPushExcel convertToExcel(EmailPushEntity entity) {
        return modelMapper.map(entity, EmailPushExcel.class);
    }

    @Override
    protected Specification<EmailPushEntity> createSpecification(EmailPushRequest request) {
        return EmailPushSpecification.search(request, authService);
    }

    @Override
    protected Page<EmailPushEntity> executePageQuery(Specification<EmailPushEntity> spec, Pageable pageable) {
        return emailPushRepository.findAll(spec, pageable);
    }
    
    public void initEmailPushs(String orgUid) {
        // log.info("initEmailPushEmailPush");
        // for (String email_push : EmailPushInitData.getAllEmailPushs()) {
        //     EmailPushRequest email_pushRequest = EmailPushRequest.builder()
        //             .uid(Utils.formatUid(orgUid, email_push))
        //             .name(email_push)
        //             .order(0)
        //             .type(EmailPushTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemEmailPush(email_pushRequest);
        // }
    }

    
    
}
