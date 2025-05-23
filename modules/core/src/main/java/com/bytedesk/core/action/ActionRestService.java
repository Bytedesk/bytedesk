/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-25 15:41:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-21 16:20:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.action;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ActionRestService extends BaseRestServiceWithExcel<ActionEntity, ActionRequest, ActionResponse, ActionExcel> {

    private final ActionRepository actionRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<ActionEntity> queryByOrgEntity(ActionRequest request) {
        Pageable pageable = request.getPageable();
        Specification<ActionEntity> spec = ActionSpecification.search(request);
        return actionRepository.findAll(spec, pageable);
    }

    @Override
    public Page<ActionResponse> queryByOrg(ActionRequest request) {
        Page<ActionEntity> page = queryByOrgEntity(request);
        return page.map(action -> convertToResponse(action));
    }

    @Override
    public Page<ActionResponse> queryByUser(ActionRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(cacheNames = "action", key = "#request.uid", unless = "#result == null")
    @Override
    public Optional<ActionEntity> findByUid(String uid) {
        return actionRepository.findByUid(uid);
    }

    public ActionResponse create(ActionRequest actionRequest) {
        ActionEntity action = modelMapper.map(actionRequest, ActionEntity.class);
        action.setUid(uidUtils.getUid());
        //
        UserEntity user = authService.getUser();
        if (user != null) {
            action.setUser(user);
            action.setOrgUid(user.getOrgUid());
        } else {
            action.setOrgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
        }
        ActionEntity savedAction = save(action);
        if (savedAction == null) {
            throw new RuntimeException("create action failed");
        }
        //
        return convertToResponse(savedAction);
    }

    @Override
    public ActionResponse update(ActionRequest request) {
        Optional<ActionEntity> actionOptional = findByUid(request.getUid());
        if (actionOptional.isPresent()) {
            ActionEntity action = actionOptional.get();
            modelMapper.map(request, action);
            // 
            ActionEntity savedAction = save(action);
            return convertToResponse(savedAction);
        }
        return null;
    }
    
    @Override
    protected ActionEntity doSave(ActionEntity entity) {
        return actionRepository.save(entity);
    }

    public ActionResponse convertToResponse(ActionEntity action) {
        return modelMapper.map(action, ActionResponse.class);
    }
    
    @Override
    public void deleteByUid(String uid) {
        Optional<ActionEntity> actionOptional = findByUid(uid);
        if (actionOptional.isPresent()) {
            ActionEntity action = actionOptional.get();
            action.setDeleted(true);
            save(action);
        }
    }

    @Override
    public void delete(ActionRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public ActionEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ActionEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public ActionResponse queryByUid(ActionRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @Override
    public ActionExcel convertToExcel(ActionEntity entity) {
        ActionExcel actionExcel = modelMapper.map(entity, ActionExcel.class);
        if (entity.getUser() != null) {
            actionExcel.setUser(entity.getUser().getNickname());
        }
        actionExcel.setCreatedAt(entity.getCreatedAtString());
        return actionExcel;
    }

}
