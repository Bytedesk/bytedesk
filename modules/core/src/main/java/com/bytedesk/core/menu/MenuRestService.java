/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-22 07:04:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.menu;

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
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class MenuRestService extends BaseRestServiceWithExport<MenuEntity, MenuRequest, MenuResponse, MenuExcel> {

    private final MenuRepository menuRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<MenuEntity> createSpecification(MenuRequest request) {
        return MenuSpecification.search(request, authService);
    }

    @Override
    protected Page<MenuEntity> executePageQuery(Specification<MenuEntity> spec, Pageable pageable) {
        return menuRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "menu", key = "#uid", unless="#result==null")
    @Override
    public Optional<MenuEntity> findByUid(String uid) {
        return menuRepository.findByUid(uid);
    }

    @Cacheable(value = "menu", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<MenuEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return menuRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return menuRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public MenuResponse create(MenuRequest request) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<MenuEntity> menu = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (menu.isPresent()) {
                return convertToResponse(menu.get());
            }
        }
        // 
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        // 
        MenuEntity entity = modelMapper.map(request, MenuEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        MenuEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create menu failed");
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public MenuResponse update(MenuRequest request) {
        Optional<MenuEntity> optional = menuRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            MenuEntity entity = optional.get();
            modelMapper.map(request, entity);
            //
            MenuEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update menu failed");
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException("Menu not found");
        }
    }

    @Override
    protected MenuEntity doSave(MenuEntity entity) {
        return menuRepository.save(entity);
    }

    @Override
    public MenuEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, MenuEntity entity) {
        try {
            Optional<MenuEntity> latest = menuRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                MenuEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return menuRepository.save(latestEntity);
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
        Optional<MenuEntity> optional = menuRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // menuRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("Menu not found");
        }
    }

    @Override
    public void delete(MenuRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public MenuResponse convertToResponse(MenuEntity entity) {
        return modelMapper.map(entity, MenuResponse.class);
    }

    @Override
    public MenuExcel convertToExcel(MenuEntity entity) {
        return modelMapper.map(entity, MenuExcel.class);
    }
    
    public void initMenus(String orgUid) {
        // log.info("initThreadMenu");
        // for (String menu : MenuInitData.getAllMenus()) {
        //     MenuRequest menuRequest = MenuRequest.builder()
        //             .uid(Utils.formatUid(orgUid, menu))
        //             .name(menu)
        //             .order(0)
        //             .type(MenuTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     create(menuRequest);
        // }
    }

    
    
}
