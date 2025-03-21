/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-24 13:02:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-21 10:04:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.user;

import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.utils.ConvertUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserRestService extends BaseRestService<UserEntity, UserRequest, UserResponse> {

    private final UserRepository userRepository;
    
    private final AuthService authService;

    @Override
    public Page<UserResponse> queryByOrg(UserRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByOrg'");
    }

    @Override
    public Page<UserResponse> queryByUser(UserRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "userCache", key = "#uid", unless = "#result == null")
    @Override
    public Optional<UserEntity> findByUid(String uid) {
        return userRepository.findByUid(uid);
    }

    public UserResponse getProfile() {
        UserEntity user = authService.getUser(); // 返回的是缓存，导致修改后的数据无法获取
        if (user == null) {
            return null;
        }
        Optional<UserEntity> userOptional = userRepository.findByUid(user.getUid());
        if (userOptional.isPresent()) {
            return convertToResponse(userOptional.get());
        } else {
            return null;
        }
    }

    @Override
    public UserResponse create(UserRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public UserResponse update(UserRequest request) {
        Optional<UserEntity> userOptional = userRepository.findByUid(request.getUid());
        if (userOptional.isPresent()) {
            UserEntity userEntity = userOptional.get();
            userEntity.setNickname(request.getNickname());
            userEntity.setAvatar(request.getAvatar());
            userEntity.setDescription(request.getDescription());
            userEntity.setMobile(request.getMobile());
            userEntity.setMobileVerified(request.getMobileVerified());
            userEntity.setEmail(request.getEmail());
            userEntity.setEmailVerified(request.getEmailVerified());
            // 
            UserEntity savedUserEntity = userRepository.save(userEntity);
            if (savedUserEntity == null) {
                throw new RuntimeException("Failed to save user");
            }
            return convertToResponse(savedUserEntity);
        }
        return null;
    }

    @Override
    public UserEntity save(UserEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public void deleteByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void delete(UserRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, UserEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public UserResponse convertToResponse(UserEntity entity) {
        return ConvertUtils.convertToUserResponse(entity);
    }

    @Override
    public UserResponse queryByUid(UserRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    
}
