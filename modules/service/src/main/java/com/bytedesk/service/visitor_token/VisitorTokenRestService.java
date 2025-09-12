/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-22 15:42:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-12 15:31:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor_token;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.PlatformEnum;
import com.bytedesk.core.exception.NotLoginException;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.JwtUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class VisitorTokenRestService extends BaseRestService<VisitorTokenEntity, VisitorTokenRequest, VisitorTokenResponse> {

    private final VisitorTokenRepository tokenRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<VisitorTokenEntity> createSpecification(VisitorTokenRequest request) {
        return VisitorTokenSpecification.search(request, authService);
    }

    @Override
    protected Page<VisitorTokenEntity> executePageQuery(Specification<VisitorTokenEntity> spec, Pageable pageable) {
        return tokenRepository.findAll(spec, pageable);
    }

    @Cacheable(cacheNames = "token", key = "#uid", unless = "#result == null")
    @Override
    public Optional<VisitorTokenEntity> findByUid(String uid) {
        return tokenRepository.findByUid(uid);
    }

    /**
     * 根据accessVisitorToken查找VisitorToken实体
     * 
     * @param accessVisitorToken JWT访问令牌
     * @return Optional<VisitorTokenEntity&gt;
     */
    @Cacheable(cacheNames = "token", key = "#accessVisitorToken", unless = "#result == null")
    public Optional<VisitorTokenEntity> findByAccessVisitorToken(String accessVisitorToken) {
        return tokenRepository.findFirstByAccessVisitorTokenAndRevokedFalseAndDeletedFalse(accessVisitorToken);
    }

    @Override
    public VisitorTokenResponse create(VisitorTokenRequest request) {
        VisitorTokenEntity entity = modelMapper.map(request, VisitorTokenEntity.class);
        entity.setUid(uidUtils.getUid());
        // 
        UserEntity user = authService.getCurrentUser();
        if (user == null) {
            throw new NotLoginException(I18Consts.I18N_LOGIN_REQUIRED);
        }
        entity.setUserUid(user.getUid());
        
        // 非永久有效，且未设置过期时间，则根据channel设置默认过期时间
        if (!Boolean.TRUE.equals(entity.getPermanent()) && entity.getExpiresAt() == null) {
            entity.setExpiresAt(JwtUtils.calculateExpirationTime(request.getChannel()));
        }
        
        VisitorTokenEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create token failed");
        }

        return convertToResponse(savedEntity);
    }

    @Override
    public VisitorTokenResponse update(VisitorTokenRequest request) {
        Optional<VisitorTokenEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            VisitorTokenEntity entity = optional.get();
            // 更新实体属性
            modelMapper.map(request, entity);
            // 保存更新后的实体
            VisitorTokenEntity updatedEntity = save(entity);
            if (updatedEntity == null) {
                throw new RuntimeException("Update token failed");
            }
            // 手动将更新后的实体放入缓存
            // cacheVisitorToken(updatedEntity);
            return convertToResponse(updatedEntity);
        } else {
            throw new RuntimeException("VisitorToken not found for uid: " + request.getUid());
        }
    }

    @Override
    protected VisitorTokenEntity doSave(VisitorTokenEntity entity) {
        return tokenRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<VisitorTokenEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            VisitorTokenEntity entity = optional.get();
            entity.setDeleted(true);
            save(entity);
        }
    }


    @Override
    public void delete(VisitorTokenRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public VisitorTokenEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            VisitorTokenEntity entity) {
        try {
            // 尝试重新加载实体
            Optional<VisitorTokenEntity> optional = findByUid(entity.getUid());
            if (optional.isPresent()) {
                VisitorTokenEntity existingEntity = optional.get();
                // 更新实体属性
                modelMapper.map(entity, existingEntity);
                return doSave(existingEntity);
            } else {
                throw new RuntimeException("VisitorToken not found for uid: " + entity.getUid());
            }
        } catch (Exception ex) {
            log.error("Error handling optimistic locking failure: {}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public VisitorTokenResponse convertToResponse(VisitorTokenEntity entity) {
        return modelMapper.map(entity, VisitorTokenResponse.class);
    }

    /**
     * 生成JWT访问令牌
     * 
     * @param request VisitorTokenRequest 请求参数
     * @return String 生成的访问令牌
     */
    public String generateAccessVisitorToken(VisitorTokenRequest request) {
        UserEntity user = authService.getCurrentUser();
        if (user == null) {
            throw new NotLoginException(I18Consts.I18N_LOGIN_REQUIRED);
        }
        String username = user.getUsername();
        String platform = request.getPlatform();
        if (!StringUtils.hasText(platform)) {
            platform = PlatformEnum.BYTEDESK.name(); // 默认平台
        }
        String channel = request.getChannel();
        
        // 生成访问令牌，传递渠道信息以设置合适的过期时间
        return JwtUtils.generateJwtToken(username, platform, channel);
    }

    /**
     * 验证accessVisitorToken是否有效
     * 参考AuthVisitorTokenFilter中的验证逻辑
     *
     * @param accessVisitorToken JWT访问令牌
     * @return boolean 是否有效
     */
    public boolean validateAccessVisitorToken(String accessVisitorToken) {
        if (!StringUtils.hasText(accessVisitorToken)) {
            log.debug("accessVisitorToken is empty");
            return false;
        }
        try {
            log.debug("validate accessVisitorToken {}", accessVisitorToken);
            // 首先验证JWT格式和签名
            if (!JwtUtils.validateJwtToken(accessVisitorToken)) {
                log.debug("accessVisitorToken is invalid");
                return false;
            }
            // 从数据库验证token是否有效（未被撤销且未过期）
            Optional<VisitorTokenEntity> tokenOpt = findByAccessVisitorToken(accessVisitorToken);
            if (tokenOpt.isPresent() && tokenOpt.get().isValid()) {
                log.debug("accessVisitorToken is valid");
                return true;
            } else {
                log.debug("VisitorToken is invalid or revoked");
                return false;
            }
        } catch (Exception e) {
            log.error("Error validating access token: {}", e.getMessage());
            return false;
        }
    }


    /**
     * 撤销指定的accessVisitorToken，使其失效
     * 
     * @param accessVisitorToken JWT访问令牌
     */
    public void revokeAccessVisitorToken(@NonNull String accessVisitorToken, String reason) {
        Optional<VisitorTokenEntity> optional = findByAccessVisitorToken(accessVisitorToken);
        if (optional.isPresent()) {
            VisitorTokenEntity entity = optional.get();
            entity.setRevoked(true);
            entity.setRevokeReason(reason);
            save(entity);
        }
    }

    

}
