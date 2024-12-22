/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-05 22:19:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-22 17:58:49
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.clipboard;

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
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;


import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ClipboardRestService extends BaseRestService<ClipboardEntity, ClipboardRequest, ClipboardResponse> {

    private final AuthService authService;

    private final ClipboardRepository clipboardRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    public Page<ClipboardResponse> queryByOrg(ClipboardRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Direction.DESC, "updatedAt");

        Specification<ClipboardEntity> specification = ClipboardSpecification.search(request);

        Page<ClipboardEntity> clipboardPage = clipboardRepository.findAll(specification, pageable);

        return clipboardPage.map(this::convertToResponse);
    }

    @Override
    public Page<ClipboardResponse> queryByUser(ClipboardRequest request) {
        
        UserEntity user = authService.getUser();
        if (user == null) {
            return Page.empty();
        }
        request.setUserUid(user.getUid());

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Direction.DESC, "updatedAt");

        Specification<ClipboardEntity> specification = ClipboardSpecification.search(request);

        Page<ClipboardEntity> clipboardPage = clipboardRepository.findAll(specification, pageable);

        return clipboardPage.map(this::convertToResponse);
    }

    @Override
    public Optional<ClipboardEntity> findByUid(String uid) {
        return clipboardRepository.findByUid(uid);
    }

    @Override
    public ClipboardResponse create(ClipboardRequest request) {
       
        ClipboardEntity Clipboard = modelMapper.map(request, ClipboardEntity.class);
        Clipboard.setUid(uidUtils.getCacheSerialUid());

        ClipboardEntity saved = save(Clipboard);
        if (saved == null) {
            throw new RuntimeException("Create Clipboard failed");
        }
        return convertToResponse(saved);
    }

    @Override
    public ClipboardResponse update(ClipboardRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public ClipboardEntity save(ClipboardEntity entity) {
        try {
            return clipboardRepository.save(entity);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<ClipboardEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        }
    }

    @Override
    public void delete(ClipboardRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ClipboardEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public ClipboardResponse convertToResponse(ClipboardEntity entity) {
        return modelMapper.map(entity, ClipboardResponse.class);
    }

}
