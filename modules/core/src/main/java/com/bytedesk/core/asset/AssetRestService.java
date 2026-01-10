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
package com.bytedesk.core.asset;

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
public class AssetRestService extends BaseRestServiceWithExport<AssetEntity, AssetRequest, AssetResponse, AssetExcel> {

    private final AssetRepository assetRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<AssetEntity> queryByOrgEntity(AssetRequest request) {
        Pageable pageable = request.getPageable();
        Specification<AssetEntity> specs = AssetSpecification.search(request, authService);
        return assetRepository.findAll(specs, pageable);
    }

    @Override
    public Page<AssetResponse> queryByOrg(AssetRequest request) {
        Page<AssetEntity> assetPage = queryByOrgEntity(request);
        return assetPage.map(this::convertToResponse);
    }

    @Override
    public Page<AssetResponse> queryByUser(AssetRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "asset", key = "#uid", unless="#result==null")
    @Override
    public Optional<AssetEntity> findByUid(String uid) {
        return assetRepository.findByUid(uid);
    }

    @Cacheable(value = "asset", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<AssetEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return assetRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return assetRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public AssetResponse create(AssetRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public AssetResponse createSystemAsset(AssetRequest request) {
        return createInternal(request, true);
    }

    private AssetResponse createInternal(AssetRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<AssetEntity> asset = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (asset.isPresent()) {
                return convertToResponse(asset.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(AssetPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        AssetEntity entity = modelMapper.map(request, AssetEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        AssetEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create asset failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public AssetResponse update(AssetRequest request) {
        Optional<AssetEntity> optional = assetRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            AssetEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(AssetPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            //
            AssetEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update asset failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Asset not found");
        }
    }

    @Override
    protected AssetEntity doSave(AssetEntity entity) {
        return assetRepository.save(entity);
    }

    @Override
    public AssetEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, AssetEntity entity) {
        try {
            Optional<AssetEntity> latest = assetRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                AssetEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return assetRepository.save(latestEntity);
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
        Optional<AssetEntity> optional = assetRepository.findByUid(uid);
        if (optional.isPresent()) {
            AssetEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(AssetPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // assetRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Asset not found");
        }
    }

    @Override
    public void delete(AssetRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public AssetResponse convertToResponse(AssetEntity entity) {
        return modelMapper.map(entity, AssetResponse.class);
    }

    @Override
    public AssetExcel convertToExcel(AssetEntity entity) {
        return modelMapper.map(entity, AssetExcel.class);
    }

    @Override
    protected Specification<AssetEntity> createSpecification(AssetRequest request) {
        return AssetSpecification.search(request, authService);
    }

    @Override
    protected Page<AssetEntity> executePageQuery(Specification<AssetEntity> spec, Pageable pageable) {
        return assetRepository.findAll(spec, pageable);
    }
    
    public void initAssets(String orgUid) {
        // log.info("initAssetAsset");
        // for (String asset : AssetInitData.getAllAssets()) {
        //     AssetRequest assetRequest = AssetRequest.builder()
        //             .uid(Utils.formatUid(orgUid, asset))
        //             .name(asset)
        //             .order(0)
        //             .type(AssetTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemAsset(assetRequest);
        // }
    }

    
    
}
