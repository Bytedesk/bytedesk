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
package com.bytedesk.core.push_apns;

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
import com.bytedesk.core.push_apns_p12.ApnsP12Repository;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.organization.OrganizationRepository;
import com.bytedesk.core.rbac.permission.PermissionService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserRepository;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class PushApnsRestService extends BaseRestServiceWithExport<PushApnsEntity, PushApnsRequest, PushApnsResponse, PushApnsExcel> {

    private final PushApnsRepository apnsPushRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    private final OrganizationRepository organizationRepository;

    private final UserRepository userRepository;

    private final ApnsP12Repository apnsP12Repository;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<PushApnsEntity> queryByOrgEntity(PushApnsRequest request) {
        Pageable pageable = request.getPageable();
        Specification<PushApnsEntity> specs = PushApnsSpecification.search(request, authService);
        return apnsPushRepository.findAll(specs, pageable);
    }

    @Override
    public Page<PushApnsResponse> queryByOrg(PushApnsRequest request) {
        Page<PushApnsEntity> apns_pushPage = queryByOrgEntity(request);
        return apns_pushPage.map(this::convertToResponse);
    }

    @Override
    public Page<PushApnsResponse> queryByUser(PushApnsRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "apns_push", key = "#uid", unless="#result==null")
    @Override
    public Optional<PushApnsEntity> findByUid(String uid) {
        return apnsPushRepository.findByUid(uid);
    }

    @Cacheable(value = "apns_push", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<PushApnsEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return apnsPushRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return apnsPushRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public PushApnsResponse create(PushApnsRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public PushApnsResponse createSystemPushApns(PushApnsRequest request) {
        return createInternal(request, true);
    }

    private PushApnsResponse createInternal(PushApnsRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<PushApnsEntity> apns_push = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (apns_push.isPresent()) {
                return convertToResponse(apns_push.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(PushApnsPermissions.MODULE_NAME, level)) {
            throw new RuntimeException(I18Consts.I18N_PERMISSION_CREATE_DENIED);
        }
        
        // 
        PushApnsEntity entity = modelMapper.map(request, PushApnsEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        PushApnsEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException(I18Consts.I18N_CREATE_FAILED);
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public PushApnsResponse update(PushApnsRequest request) {
        Optional<PushApnsEntity> optional = apnsPushRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            PushApnsEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(PushApnsPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException(I18Consts.I18N_PERMISSION_UPDATE_DENIED);
            }
            
            modelMapper.map(request, entity);
            //
            PushApnsEntity savedEntity = save(entity);
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
    protected PushApnsEntity doSave(PushApnsEntity entity) {
        return apnsPushRepository.save(entity);
    }

    @Override
    public PushApnsEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, PushApnsEntity entity) {
        try {
            Optional<PushApnsEntity> latest = apnsPushRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                PushApnsEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                latestEntity.setSender(entity.getSender());
                latestEntity.setReceiver(entity.getReceiver());
                latestEntity.setDeviceToken(entity.getDeviceToken());
                latestEntity.setP12Uid(entity.getP12Uid());
                latestEntity.setBundleId(entity.getBundleId());
                latestEntity.setMessageUid(entity.getMessageUid());
                latestEntity.setThreadUid(entity.getThreadUid());
                latestEntity.setContent(entity.getContent());
                latestEntity.setDescription(entity.getDescription());
                latestEntity.setType(entity.getType());
                latestEntity.setStatus(entity.getStatus());
                latestEntity.setChannel(entity.getChannel());
                latestEntity.setSandbox(entity.getSandbox());
                latestEntity.setSendSuccess(entity.getSendSuccess());
                latestEntity.setSendMessage(entity.getSendMessage());
                return apnsPushRepository.save(latestEntity);
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
        Optional<PushApnsEntity> optional = apnsPushRepository.findByUid(uid);
        if (optional.isPresent()) {
            PushApnsEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(PushApnsPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException(I18Consts.I18N_PERMISSION_DELETE_DENIED);
            }
            
            entity.setDeleted(true);
            save(entity);
            // apns_pushRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @Override
    public void delete(PushApnsRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public PushApnsResponse convertToResponse(PushApnsEntity entity) {
        PushApnsResponse response = modelMapper.map(entity, PushApnsResponse.class);

        if (StringUtils.hasText(entity.getReceiver())) {
            userRepository.findByUid(entity.getReceiver()).ifPresent(user -> response.setReceiverNickname(user.getNickname()));
        }

        if (StringUtils.hasText(entity.getP12Uid())) {
            apnsP12Repository.findByUid(entity.getP12Uid()).ifPresent(apnsP12 -> response.setP12Name(apnsP12.getName()));
        }

        if (StringUtils.hasText(entity.getOrgUid())) {
            organizationRepository.findByUid(entity.getOrgUid()).ifPresent(org -> response.setOrgName(org.getName()));
        }

        return response;
    }

    @Override
    public PushApnsExcel convertToExcel(PushApnsEntity entity) {
        return modelMapper.map(entity, PushApnsExcel.class);
    }

    @Override
    protected Specification<PushApnsEntity> createSpecification(PushApnsRequest request) {
        return PushApnsSpecification.search(request, authService);
    }

    @Override
    protected Page<PushApnsEntity> executePageQuery(Specification<PushApnsEntity> spec, Pageable pageable) {
        return apnsPushRepository.findAll(spec, pageable);
    }
    
    public void initPushApnss(String orgUid) {
        // log.info("initPushApnsPushApns");
        // for (String apns_push : PushApnsInitData.getAllPushApnss()) {
        //     PushApnsRequest apns_pushRequest = PushApnsRequest.builder()
        //             .uid(Utils.formatUid(orgUid, apns_push))
        //             .name(apns_push)
        //             .order(0)
        //             .type(PushApnsTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemPushApns(apns_pushRequest);
        // }
    }

    
    
}
