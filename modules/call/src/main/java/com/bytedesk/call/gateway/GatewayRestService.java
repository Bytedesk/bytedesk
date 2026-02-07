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
package com.bytedesk.call.gateway;

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
public class GatewayRestService extends BaseRestServiceWithExport<GatewayEntity, GatewayRequest, GatewayResponse, GatewayExcel> {

    private final GatewayRepository gatewayRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<GatewayEntity> queryByOrgEntity(GatewayRequest request) {
        Pageable pageable = request.getPageable();
        Specification<GatewayEntity> specs = GatewaySpecification.search(request, authService);
        return gatewayRepository.findAll(specs, pageable);
    }

    @Override
    public Page<GatewayResponse> queryByOrg(GatewayRequest request) {
        Page<GatewayEntity> gatewayPage = queryByOrgEntity(request);
        return gatewayPage.map(this::convertToResponse);
    }

    @Override
    public Page<GatewayResponse> queryByUser(GatewayRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "gateway", key = "#uid", unless="#result==null")
    @Override
    public Optional<GatewayEntity> findByUid(String uid) {
        return gatewayRepository.findByUid(uid);
    }

    @Cacheable(value = "gateway", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<GatewayEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return gatewayRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return gatewayRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public GatewayResponse create(GatewayRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public GatewayResponse createSystemGateway(GatewayRequest request) {
        return createInternal(request, true);
    }

    private GatewayResponse createInternal(GatewayRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<GatewayEntity> gateway = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (gateway.isPresent()) {
                return convertToResponse(gateway.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(GatewayPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        GatewayEntity entity = modelMapper.map(request, GatewayEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        GatewayEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create gateway failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public GatewayResponse update(GatewayRequest request) {
        Optional<GatewayEntity> optional = gatewayRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            GatewayEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(GatewayPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            //
            GatewayEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update gateway failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Gateway not found");
        }
    }

    @Override
    protected GatewayEntity doSave(GatewayEntity entity) {
        return gatewayRepository.save(entity);
    }

    @Override
    public GatewayEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, GatewayEntity entity) {
        try {
            Optional<GatewayEntity> latest = gatewayRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                GatewayEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return gatewayRepository.save(latestEntity);
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
        Optional<GatewayEntity> optional = gatewayRepository.findByUid(uid);
        if (optional.isPresent()) {
            GatewayEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(GatewayPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // gatewayRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Gateway not found");
        }
    }

    @Override
    public void delete(GatewayRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public GatewayResponse convertToResponse(GatewayEntity entity) {
        return modelMapper.map(entity, GatewayResponse.class);
    }

    @Override
    public GatewayExcel convertToExcel(GatewayEntity entity) {
        return modelMapper.map(entity, GatewayExcel.class);
    }

    @Override
    protected Specification<GatewayEntity> createSpecification(GatewayRequest request) {
        return GatewaySpecification.search(request, authService);
    }

    @Override
    protected Page<GatewayEntity> executePageQuery(Specification<GatewayEntity> spec, Pageable pageable) {
        return gatewayRepository.findAll(spec, pageable);
    }
    
    public void initGateways(String orgUid) {
        // log.info("initGatewayGateway");
        // for (String gateway : GatewayInitData.getAllGateways()) {
        //     GatewayRequest gatewayRequest = GatewayRequest.builder()
        //             .uid(Utils.formatUid(orgUid, gateway))
        //             .name(gateway)
        //             .order(0)
        //             .type(GatewayTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemGateway(gatewayRequest);
        // }
    }

    
    
}
