/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 12:20:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-23 18:12:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.black;

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
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BlackService extends BaseRestService<BlackEntity, BlackRequest, BlackResponse> {

    private final BlackRepository repository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    public Page<BlackResponse> queryByOrg(BlackRequest request) {

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "createdAt");

        Specification<BlackEntity> specification = BlackSpecification.search(request);

        Page<BlackEntity> blacks = repository.findAll(specification, pageable);

        return blacks.map(this::convertToResponse);
    }

    @Override
    public Page<BlackResponse> queryByUser(BlackRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "black", key = "#uid", unless = "#result == null")
    @Override
    public Optional<BlackEntity> findByUid(String uid) {
        return repository.findByUid(uid);
    }

    @Override
    public BlackResponse create(BlackRequest request) {
        UserEntity user = authService.getCurrentUser();
        // 
        BlackEntity entity = modelMapper.map(request, BlackEntity.class);
        entity.setUid(uidUtils.getUid());
        entity.setUserUid(user.getUid());
        // 
        BlackEntity savedBlack = save(entity);
        if (savedBlack == null) {
            throw new RuntimeException("Create black failed");
        }
        return convertToResponse(savedBlack);
    }

    @Override
    public BlackResponse update(BlackRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public BlackEntity save(BlackEntity entity) {
        try {
            return repository.save(entity);
        } catch (Exception e) {
           e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void delete(BlackRequest entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, BlackEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public BlackResponse convertToResponse(BlackEntity entity) {
        return modelMapper.map(entity, BlackResponse.class);
    }

}
