/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-25 15:41:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-30 16:02:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
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

import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.constant.UserConsts;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ActionService extends BaseService<Action, ActionRequest, ActionResponse> {

    private final ActionRepository actionRepository;

    private final ModelMapper modelMapper;
  
    private final UidUtils uidUtils;

    private final AuthService authService;

    public ActionResponse create(ActionRequest actionRequest) {

        Action action = modelMapper.map(actionRequest, Action.class);
        action.setUid(uidUtils.getCacheSerialUid());
        // 
        User user = authService.getCurrentUser();
        if (user != null) {
            action.setUser(user);
            action.setOrgUid(user.getOrgUid());
        } else {
            action.setOrgUid(UserConsts.DEFAULT_ORGANIZATION_UID);
        }
        // 
        return convertToResponse(save(action));
    }

    public Action save(Action action) {
        return actionRepository.save(action);
    }

    public ActionResponse convertToResponse(Action action) {
        return modelMapper.map(action, ActionResponse.class);
    }

    @Override
    public Page<ActionResponse> queryByOrg(ActionRequest request) {
        
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Direction.DESC, "updatedAt");
        // 
        Specification<Action> spec = ActionSpecs.search(request);
        Page<Action> page = actionRepository.findAll(spec, pageable);

        return page.map(action -> convertToResponse(action));
    }

    @Override
    public Page<ActionResponse> queryByUser(ActionRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<Action> findByUid(String uid) {
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
    public void delete(Action entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, Action entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }
    
}
