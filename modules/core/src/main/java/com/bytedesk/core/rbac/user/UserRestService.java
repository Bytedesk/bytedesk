/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-24 13:02:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-17 09:17:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.user;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.exception.NotFoundException;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.utils.ConvertUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserRestService extends BaseRestServiceWithExcel<UserEntity, UserRequest, UserResponse, UserExcel> {

    private final UserRepository userRepository;
    
    private final AuthService authService;

    private final UserService userService;

    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public Page<UserEntity> queryByOrgEntity(UserRequest request) {
        Pageable pageable = request.getPageable();
        Specification<UserEntity> specification = UserSpecification.search(request);
        return userRepository.findAll(specification, pageable);
    }

    @Override
    public Page<UserResponse> queryByOrg(UserRequest request) {
        Page<UserEntity> page = queryByOrgEntity(request);
        return page.map(ConvertUtils::convertToUserResponse);
    }

    @Override
    public Page<UserResponse> queryByUser(UserRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotFoundException("User not found.");
        }
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Override
    public UserResponse queryByUid(UserRequest request) {
        Optional<UserEntity> userOptional = findByUid(request.getUid());
        if (userOptional.isPresent()) {
            return convertToResponse(userOptional.get());
        } else {
            throw new NotFoundException("User with UID: " + request.getUid() + " not found.");
        }
    }

    @Cacheable(value = "user", key = "#uid", unless = "#result == null")
    @Override
    public Optional<UserEntity> findByUid(String uid) {
        return userRepository.findByUid(uid);
    }

    public UserResponse getProfile() {
        UserEntity user = authService.getUser(); // 返回的是缓存，导致修改后的数据无法获取
        if (user == null) {
            return null;
        }
        Optional<UserEntity> userOptional = findByUid(user.getUid());
        if (userOptional.isPresent()) {
            return convertToResponse(userOptional.get());
        } else {
            return null;
        }
    }

    @Override
    public UserResponse create(UserRequest request) {
        return userService.register(request);
    }

    @Override
    public UserResponse update(UserRequest request) {
        // 更新时候不使用缓存，直接查询
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
            userEntity.setEnabled(request.getEnabled());
            //
            if (StringUtils.hasText(request.getPassword())) {
                String encodedPassword = passwordEncoder.encode(request.getPassword());
                userEntity.setPassword(encodedPassword);
            }   
            // 
            UserEntity savedUserEntity = save(userEntity);
            if (savedUserEntity == null) {
                throw new RuntimeException("Failed to save user");
            }
            return convertToResponse(savedUserEntity);
        }
        return null;
    }

    @Override
    protected UserEntity doSave(UserEntity entity) {
        return userRepository.save(entity);
    }

    @Override
    public UserEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, UserEntity entity) {
        try {
            Optional<UserEntity> latest = userRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                UserEntity latestEntity = latest.get();
                // 合并需要保留的数据
                return userRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<UserEntity> userOptional = userRepository.findByUid(uid);
        if (userOptional.isPresent()) {
            UserEntity userEntity = userOptional.get();
            userEntity.setDeleted(true);
            save(userEntity);
        }
    }

    @Override
    public void delete(UserRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public UserResponse convertToResponse(UserEntity entity) {
        return ConvertUtils.convertToUserResponse(entity);
    }

    public List<UserEntity> findAll(UserRequest request) {
        Specification<UserEntity> specification = UserSpecification.search(request);
        return userRepository.findAll(specification);
    }

    @Override
    public UserExcel convertToExcel(UserEntity entity) {
        UserExcel excel = new UserExcel();
        excel.setNickname(entity.getNickname());
        excel.setEmail(entity.getEmail());
        excel.setMobile(entity.getMobile());
        excel.setDescription(entity.getDescription());
        return excel;
    }


}
