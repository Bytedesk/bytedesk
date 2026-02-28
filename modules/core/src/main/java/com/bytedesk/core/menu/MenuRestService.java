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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

    @Override
    public Page<MenuResponse> queryByOrg(MenuRequest request) {
        Pageable pageable = request.getPageable();
        if (!StringUtils.hasText(request.getOrgUid())) {
            Specification<MenuEntity> spec = createSpecification(request);
            Page<MenuEntity> page = executePageQuery(spec, pageable);
            return page.map(this::convertToResponse);
        }

        MenuRequest platformRequest = MenuRequest.builder()
                .build();
        platformRequest.setPageNumber(request.getPageNumber());
        platformRequest.setPageSize(request.getPageSize());
        platformRequest.setSortBy(request.getSortBy());
        platformRequest.setSortDirection(request.getSortDirection());
        platformRequest.setType(request.getType());
        platformRequest.setName(request.getName());
        platformRequest.setNickname(request.getNickname());
        platformRequest.setDescription(request.getDescription());
        platformRequest.setColor(request.getColor());
        platformRequest.setLink(request.getLink());
        platformRequest.setIcon(request.getIcon());
        platformRequest.setParentUid(request.getParentUid());
        platformRequest.setEnabled(request.getEnabled());
        platformRequest.setOpenInNewWindow(request.getOpenInNewWindow());
        platformRequest.setUserUid(request.getUserUid());
        platformRequest.setLevel(LevelEnum.PLATFORM.name());

        Specification<MenuEntity> orgSpec = createSpecification(request);
        Specification<MenuEntity> platformSpec = createSpecification(platformRequest);
        Page<MenuEntity> orgPage = executePageQuery(orgSpec, pageable);
        Page<MenuEntity> platformPage = executePageQuery(platformSpec, pageable);

        Map<String, MenuResponse> merged = new LinkedHashMap<>();
        for (MenuEntity entity : platformPage.getContent()) {
            MenuResponse response = convertToResponse(entity);
            String key = StringUtils.hasText(response.getLink()) ? response.getLink() : response.getUid();
            merged.put(key, response);
        }
        for (MenuEntity entity : orgPage.getContent()) {
            MenuResponse response = convertToResponse(entity);
            String key = StringUtils.hasText(response.getLink()) ? response.getLink() : response.getUid();
            merged.put(key, response);
        }

        List<MenuResponse> combined = new ArrayList<>(merged.values());
        return new PageImpl<>(combined, pageable, combined.size());
    }

    @Cacheable(value = "menu", key = "#uid", unless="#result==null")
    @Override
    public Optional<MenuEntity> findByUid(String uid) {
        return menuRepository.findByUid(uid);
    }

    @Cacheable(value = "menu", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<MenuEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        if (!StringUtils.hasText(orgUid)) {
            return menuRepository.findByNameAndOrgUidIsNullAndTypeAndDeletedFalse(name, type);
        }
        return menuRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    @Cacheable(value = "menu", key = "#name + '_' + #orgUid + '_' + #type + '_' + #parentUid", unless="#result==null")
    public Optional<MenuEntity> findByNameAndOrgUidAndTypeAndParentUid(String name, String orgUid, String type, String parentUid) {
        if (!StringUtils.hasText(orgUid)) {
            if (parentUid == null) {
                return menuRepository.findByNameAndOrgUidIsNullAndTypeAndDeletedFalse(name, type);
            }
            return menuRepository.findByNameAndOrgUidIsNullAndTypeAndParentUidAndDeletedFalse(name, type, parentUid);
        }
        if (parentUid == null) {
            return findByNameAndOrgUidAndType(name, orgUid, type);
        }
        return menuRepository.findByNameAndOrgUidAndTypeAndParentUidAndDeletedFalse(name, orgUid, type, parentUid);
    }

    @Cacheable(value = "menu", key = "#link + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<MenuEntity> findByLinkAndOrgUidAndType(String link, String orgUid, String type) {
        if (!StringUtils.hasText(orgUid)) {
            return menuRepository.findByLinkAndOrgUidIsNullAndTypeAndDeletedFalse(link, type);
        }
        return menuRepository.findByLinkAndOrgUidAndTypeAndDeletedFalse(link, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return menuRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public MenuResponse create(MenuRequest request) {
        // applyRequestDefaults(request, false);
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        if (!StringUtils.hasText(request.getLevel())) {
            request.setLevel(LevelEnum.PLATFORM.name());
        }
        if (LevelEnum.PLATFORM.name().equalsIgnoreCase(request.getLevel())) {
            request.setOrgUid(null);
        }
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
            if (!StringUtils.hasText(request.getOrgUid())
                    && LevelEnum.ORGANIZATION.name().equalsIgnoreCase(request.getLevel())) {
                request.setOrgUid(user.getOrgUid());
            }
        }
        if (LevelEnum.ORGANIZATION.name().equalsIgnoreCase(request.getLevel())
                && !StringUtils.hasText(request.getOrgUid())) {
            throw new IllegalArgumentException("orgUid is required for organization-level menu entries");
        }
        // 检查link 或 name + orgUid + type (+ parentUid)是否已经存在
        if (StringUtils.hasText(request.getType())) {
            if (StringUtils.hasText(request.getLink())) {
                Optional<MenuEntity> menu = findByLinkAndOrgUidAndType(request.getLink(), request.getOrgUid(), request.getType());
                if (menu.isPresent()) {
                    return convertToResponse(menu.get());
                }
            }
            if (StringUtils.hasText(request.getName())) {
                Optional<MenuEntity> menu = findByNameAndOrgUidAndTypeAndParentUid(
                        request.getName(),
                        request.getOrgUid(),
                        request.getType(),
                        request.getParentUid());
                if (menu.isPresent()) {
                    return convertToResponse(menu.get());
                }
            }
        }
        // 
        MenuEntity entity = modelMapper.map(request, MenuEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // ensureEntityDefaults(entity);
        // validateMenuEntity(entity);
        entity.setColor("blue");
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
            if (Boolean.FALSE.equals(request.getEnabled())
                    && "/super".equals(entity.getLink())
                    && !StringUtils.hasText(entity.getParentUid())) {
                throw new RuntimeException("Super menu cannot be disabled");
            }
            if (!StringUtils.hasText(request.getLevel())) {
                request.setLevel(entity.getLevel());
            }
            if (!StringUtils.hasText(request.getLevel())) {
                request.setLevel(LevelEnum.PLATFORM.name());
            }
            if (LevelEnum.PLATFORM.name().equalsIgnoreCase(request.getLevel())) {
                request.setOrgUid(null);
            }
            if (!StringUtils.hasText(request.getOrgUid())
                    && !LevelEnum.PLATFORM.name().equalsIgnoreCase(request.getLevel())) {
                request.setOrgUid(entity.getOrgUid());
            }
            if (!StringUtils.hasText(request.getType())) {
                request.setType(entity.getType());
            }
            if (!StringUtils.hasText(request.getColor())) {
                request.setColor(entity.getColor());
            }
            // applyRequestDefaults(request, true);
            modelMapper.map(request, entity);
            // ensureEntityDefaults(entity);
            // validateMenuEntity(entity);
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

    public void initMenus() {
        initMenusBySeeds(MenuInitData.adminMenus(), MenuTypeEnum.ADMIN.name());
        initMenusBySeeds(MenuInitData.desktopMenus(), MenuTypeEnum.DESKTOP.name());
    }

    private void initMenusBySeeds(List<MenuInitData.MenuSeed> seeds, String type) {
        if (seeds == null || seeds.isEmpty()) {
            return;
        }
        Map<String, String> keyToUid = new HashMap<>();
        for (MenuInitData.MenuSeed seed : seeds) {
            String parentUid = null;
            if (StringUtils.hasText(seed.getParentKey())) {
                parentUid = keyToUid.get(seed.getParentKey());
            }
            Optional<MenuEntity> existing = menuRepository.findByLinkAndOrgUidIsNullAndType(seed.getLink(), type);
            if (existing.isPresent()) {
                MenuEntity entity = existing.get();
                entity.setDeleted(false);
                entity.setName(seed.getName());
                entity.setNickname(seed.getNickname());
                entity.setDescription(seed.getDescription());
                entity.setType(type);
                entity.setLevel(LevelEnum.PLATFORM.name());
                entity.setColor(seed.getColor());
                entity.setIcon(seed.getIcon());
                entity.setLink(seed.getLink());
                entity.setOrder(seed.getOrder());
                entity.setEnabled(Boolean.TRUE);
                entity.setOpenInNewWindow(Boolean.FALSE);
                entity.setParentUid(parentUid);
                entity.setOrgUid(null);
                MenuEntity savedEntity = menuRepository.save(entity);
                if (StringUtils.hasText(seed.getKey())) {
                    keyToUid.put(seed.getKey(), savedEntity.getUid());
                }
            } else {
                MenuRequest request = MenuRequest.builder()
                        .name(seed.getName())
                        .nickname(seed.getNickname())
                        .description(seed.getDescription())
                        .type(type)
                        .level(LevelEnum.PLATFORM.name())
                        .color(seed.getColor())
                        .icon(seed.getIcon())
                        .link(seed.getLink())
                        .order(seed.getOrder())
                        .enabled(Boolean.TRUE)
                        .openInNewWindow(Boolean.FALSE)
                        .parentUid(parentUid)
                        .build();
                MenuResponse response = create(request);
                if (response != null && StringUtils.hasText(seed.getKey())) {
                    keyToUid.put(seed.getKey(), response.getUid());
                }
            }
        }
    }

    @Transactional
    public void resetMenus(String orgUid) {
        // Reset defaults by upserting seed data instead of deleting existing rows.
        initMenus();
    }

    
    
}
