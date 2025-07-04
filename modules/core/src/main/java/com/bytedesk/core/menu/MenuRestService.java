/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-11 11:18:12
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MenuRestService extends BaseRestService<MenuEntity, MenuRequest, MenuResponse> {

    private final MenuRepository menuRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    public Page<MenuResponse> queryByOrg(MenuRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.ASC,
                "updatedAt");
        Specification<MenuEntity> spec = MenuSpecification.search(request);
        Page<MenuEntity> page = menuRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<MenuResponse> queryByUser(MenuRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "menu", key = "#uid", unless="#result==null")
    @Override
    public Optional<MenuEntity> findByUid(String uid) {
        return menuRepository.findByUid(uid);
    }

    @Override
    public MenuResponse initVisitor(MenuRequest request) {
        
        MenuEntity entity = modelMapper.map(request, MenuEntity.class);
        entity.setUid(uidUtils.getUid());

        MenuEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create menu failed");
        }
        return convertToResponse(savedEntity);
    }

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
    public MenuEntity save(MenuEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        }
        return null;
    }

    @Override
    protected MenuEntity doSave(MenuEntity entity) {
        return menuRepository.save(entity);
    }

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
    public MenuEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, MenuEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public MenuResponse convertToResponse(MenuEntity entity) {
        return modelMapper.map(entity, MenuResponse.class);
    }

    @Override
    public MenuResponse queryByUid(MenuRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}
