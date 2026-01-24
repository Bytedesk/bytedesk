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
package com.bytedesk.core.city;

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
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class CityRestService extends BaseRestServiceWithExport<CityEntity, CityRequest, CityResponse, CityExcel> {

    private final CityRepository cityRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    private final CitySqlImportService citySqlImportService;
        
    @Override
    public Page<CityEntity> queryByOrgEntity(CityRequest request) {
        Pageable pageable = request.getPageable();
        Specification<CityEntity> specs = CitySpecification.search(request, authService);
        return cityRepository.findAll(specs, pageable);
    }

    @Override
    public Page<CityResponse> queryByOrg(CityRequest request) {
        Page<CityEntity> cityPage = queryByOrgEntity(request);
        return cityPage.map(this::convertToResponse);
    }

    @Override
    public Page<CityResponse> queryByUser(CityRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "city", key = "#uid", unless="#result==null")
    @Override
    public Optional<CityEntity> findByUid(String uid) {
        return cityRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return cityRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public CityResponse create(CityRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public CityResponse createSystemCity(CityRequest request) {
        return createInternal(request, true);
    }

    private CityResponse createInternal(CityRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        // if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
        //     Optional<CityEntity> city = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
        //     if (city.isPresent()) {
        //         return convertToResponse(city.get());
        //     }
        // }
        
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
        // if (!skipPermissionCheck && !permissionService.canCreateAtLevel(CityPermissions.MODULE_NAME, level)) {
        //     throw new RuntimeException("无权限创建该层级的标签数据");
        // }
        
        // 
        CityEntity entity = modelMapper.map(request, CityEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        CityEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create city failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public CityResponse update(CityRequest request) {
        Optional<CityEntity> optional = cityRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            CityEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            // if (!permissionService.hasEntityPermission(CityPermissions.MODULE_NAME, "UPDATE", entity)) {
            //     throw new RuntimeException("无权限更新该标签数据");
            // }
            
            modelMapper.map(request, entity);
            //
            CityEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update city failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("City not found");
        }
    }

    @Override
    protected CityEntity doSave(CityEntity entity) {
        return cityRepository.save(entity);
    }

    @Override
    public CityEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, CityEntity entity) {
        try {
            Optional<CityEntity> latest = cityRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                CityEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return cityRepository.save(latestEntity);
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
        Optional<CityEntity> optional = cityRepository.findByUid(uid);
        if (optional.isPresent()) {
            CityEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            // if (!permissionService.hasEntityPermission(CityPermissions.MODULE_NAME, "DELETE", entity)) {
            //     throw new RuntimeException("无权限删除该标签数据");
            // }
            
            entity.setDeleted(true);
            save(entity);
            // cityRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("City not found");
        }
    }

    @Override
    public void delete(CityRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public CityResponse convertToResponse(CityEntity entity) {
        return modelMapper.map(entity, CityResponse.class);
    }

    @Override
    public CityExcel convertToExcel(CityEntity entity) {
        return modelMapper.map(entity, CityExcel.class);
    }

    @Override
    protected Specification<CityEntity> createSpecification(CityRequest request) {
        return CitySpecification.search(request, authService);
    }

    @Override
    protected Page<CityEntity> executePageQuery(Specification<CityEntity> spec, Pageable pageable) {
        return cityRepository.findAll(spec, pageable);
    }
    
    public void initCitys(String orgUid) {
        // log.info("initCityCity");
        // for (String city : CityInitData.getAllCitys()) {
        //     CityRequest cityRequest = CityRequest.builder()
        //             .uid(Utils.formatUid(orgUid, city))
        //             .name(city)
        //             .order(0)
        //             .type(CityTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemCity(cityRequest);
        // }
    }

    @Transactional
    public long resetAll() {
        long before = cityRepository.count();
        cityRepository.deleteAllInBatch();
        return before;
    }

    /**
     * Initialize city data from SQL in background.
     * If there is already active (not deleted) data, it will be skipped.
     */
    public boolean initIfEmptyAsync() {
        long active = 0L;
        try {
            active = cityRepository.countByDeletedFalse();
        } catch (Exception e) {
            // ignore: if table missing etc, still try import
            log.warn("city init: cannot count active rows: {}", e.getMessage());
        }
        if (active > 0) {
            return false;
        }
        citySqlImportService.importNowAsync(false);
        return true;
    }

    
    
}
