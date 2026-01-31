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
package com.bytedesk.crm.tender;

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
public class TenderRestService extends BaseRestServiceWithExport<TenderEntity, TenderRequest, TenderResponse, TenderExcel> {

    private final TenderRepository tenderRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<TenderEntity> queryByOrgEntity(TenderRequest request) {
        Pageable pageable = request.getPageable();
        Specification<TenderEntity> specs = TenderSpecification.search(request, authService);
        return tenderRepository.findAll(specs, pageable);
    }

    @Override
    public Page<TenderResponse> queryByOrg(TenderRequest request) {
        Page<TenderEntity> tenderPage = queryByOrgEntity(request);
        return tenderPage.map(this::convertToResponse);
    }

    @Override
    public Page<TenderResponse> queryByUser(TenderRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "tender", key = "#uid", unless="#result==null")
    @Override
    public Optional<TenderEntity> findByUid(String uid) {
        return tenderRepository.findByUid(uid);
    }

    @Cacheable(value = "tender", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<TenderEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return tenderRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return tenderRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public TenderResponse create(TenderRequest request) {
        return createInternal(request, false);
    }

    @Transactional
    public TenderResponse createSystemTender(TenderRequest request) {
        return createInternal(request, true);
    }

    private TenderResponse createInternal(TenderRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<TenderEntity> tender = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (tender.isPresent()) {
                return convertToResponse(tender.get());
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
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(TenderPermissions.MODULE_NAME, level)) {
            throw new RuntimeException("无权限创建该层级的标签数据");
        }
        
        // 
        TenderEntity entity = modelMapper.map(request, TenderEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        TenderEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create tender failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public TenderResponse update(TenderRequest request) {
        Optional<TenderEntity> optional = tenderRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            TenderEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(TenderPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException("无权限更新该标签数据");
            }
            
            modelMapper.map(request, entity);
            //
            TenderEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update tender failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Tender not found");
        }
    }

    @Override
    protected TenderEntity doSave(TenderEntity entity) {
        return tenderRepository.save(entity);
    }

    @Override
    public TenderEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, TenderEntity entity) {
        try {
            Optional<TenderEntity> latest = tenderRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                TenderEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return tenderRepository.save(latestEntity);
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
        Optional<TenderEntity> optional = tenderRepository.findByUid(uid);
        if (optional.isPresent()) {
            TenderEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(TenderPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException("无权限删除该标签数据");
            }
            
            entity.setDeleted(true);
            save(entity);
            // tenderRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Tender not found");
        }
    }

    @Override
    public void delete(TenderRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public TenderResponse convertToResponse(TenderEntity entity) {
        return modelMapper.map(entity, TenderResponse.class);
    }

    @Override
    public TenderExcel convertToExcel(TenderEntity entity) {
        return modelMapper.map(entity, TenderExcel.class);
    }

    @Override
    protected Specification<TenderEntity> createSpecification(TenderRequest request) {
        return TenderSpecification.search(request, authService);
    }

    @Override
    protected Page<TenderEntity> executePageQuery(Specification<TenderEntity> spec, Pageable pageable) {
        return tenderRepository.findAll(spec, pageable);
    }
    
    public void initTenders(String orgUid) {
        // log.info("initTenderTender");
        // for (String tender : TenderInitData.getAllTenders()) {
        //     TenderRequest tenderRequest = TenderRequest.builder()
        //             .uid(Utils.formatUid(orgUid, tender))
        //             .name(tender)
        //             .order(0)
        //             .type(TenderTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemTender(tenderRequest);
        // }
    }

    
    
}
