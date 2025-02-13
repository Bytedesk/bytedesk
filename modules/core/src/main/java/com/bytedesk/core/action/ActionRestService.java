/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-25 15:41:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-13 09:20:56
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ActionRestService extends BaseRestService<ActionEntity, ActionRequest, ActionResponse> {

    private final ActionRepository actionRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    public ActionResponse create(ActionRequest actionRequest) {

        ActionEntity action = modelMapper.map(actionRequest, ActionEntity.class);
        action.setUid(uidUtils.getCacheSerialUid());
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
            // TODO: handle exception
        }
        //
        return convertToResponse(savedAction);
    }

    public ActionEntity save(ActionEntity action) {
        try {
            return actionRepository.save(action);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    public ActionResponse convertToResponse(ActionEntity action) {
        if (action == null) {
            return null;
        }
        return modelMapper.map(action, ActionResponse.class);
    }

    @Override
    public Page<ActionResponse> queryByOrg(ActionRequest request) {

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Direction.DESC, "updatedAt");
        //
        Specification<ActionEntity> spec = ActionSpecification.search(request);
        Page<ActionEntity> page = actionRepository.findAll(spec, pageable);

        return page.map(action -> convertToResponse(action));
    }

    @Override
    public Page<ActionResponse> queryByUser(ActionRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<ActionEntity> findByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByUid'");
    }

    @Override
    public ActionResponse update(ActionRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void deleteByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void delete(ActionRequest entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ActionEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

}
