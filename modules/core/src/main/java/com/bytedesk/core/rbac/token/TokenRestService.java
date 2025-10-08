/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-22 15:42:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-03 18:05:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.token;

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
import com.bytedesk.core.enums.PlatformEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.JwtUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class TokenRestService extends BaseRestService<TokenEntity, TokenRequest, TokenResponse> {

    private final TokenRepository tokenRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    @Override
    protected Specification<TokenEntity> createSpecification(TokenRequest request) {
        return TokenSpecification.search(request, authService);
    }

    @Override
    protected Page<TokenEntity> executePageQuery(Specification<TokenEntity> spec, Pageable pageable) {
        return tokenRepository.findAll(spec, pageable);
    }

    @Cacheable(cacheNames = "token", key = "#uid", unless = "#result == null")
    @Override
    public Optional<TokenEntity> findByUid(String uid) {
        return tokenRepository.findByUid(uid);
    }

    /**
     * 根据accessToken查找Token实体
     * 
     * @param accessToken JWT访问令牌
     * @return Optional<TokenEntity&gt;
     */
    @Cacheable(cacheNames = "token", key = "#accessToken", unless = "#result == null")
    public Optional<TokenEntity> findByAccessToken(String accessToken) {
        return tokenRepository.findFirstByAccessTokenAndRevokedFalseAndDeletedFalse(accessToken);
    }

    @Override
    public TokenResponse create(TokenRequest request) {
        TokenEntity entity = modelMapper.map(request, TokenEntity.class);
        entity.setUid(uidUtils.getUid());
        // 
        UserEntity user = authService.getCurrentUser();
        
        entity.setUserUid(user.getUid());
        
        // 非永久有效，且未设置过期时间，则根据channel设置默认过期时间
        if (!Boolean.TRUE.equals(entity.getPermanent()) && entity.getExpiresAt() == null) {
            entity.setExpiresAt(JwtUtils.calculateExpirationTime(request.getChannel()));
        }
        
        TokenEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create token failed");
        }

        return convertToResponse(savedEntity);
    }

    @Override
    public TokenResponse update(TokenRequest request) {
        Optional<TokenEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            TokenEntity entity = optional.get();
            // 更新实体属性
            modelMapper.map(request, entity);
            // 保存更新后的实体
            TokenEntity updatedEntity = save(entity);
            if (updatedEntity == null) {
                throw new RuntimeException("Update token failed");
            }
            // 手动将更新后的实体放入缓存
            // cacheToken(updatedEntity);
            return convertToResponse(updatedEntity);
        } else {
            throw new RuntimeException("Token not found for uid: " + request.getUid());
        }
    }

    @Override
    protected TokenEntity doSave(TokenEntity entity) {
        return tokenRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<TokenEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            TokenEntity entity = optional.get();
            entity.setDeleted(true);
            save(entity);
        }
    }


    @Override
    public void delete(TokenRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public TokenEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            TokenEntity entity) {
        try {
            // 尝试重新加载实体
            Optional<TokenEntity> optional = findByUid(entity.getUid());
            if (optional.isPresent()) {
                TokenEntity existingEntity = optional.get();
                // 更新实体属性
                modelMapper.map(entity, existingEntity);
                return doSave(existingEntity);
            } else {
                throw new RuntimeException("Token not found for uid: " + entity.getUid());
            }
        } catch (Exception ex) {
            log.error("Error handling optimistic locking failure: {}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public TokenResponse convertToResponse(TokenEntity entity) {
        return modelMapper.map(entity, TokenResponse.class);
    }

    /**
     * 生成JWT访问令牌
     * 
     * @param request TokenRequest 请求参数
     * @return String 生成的访问令牌
     */
    public String generateAccessToken(TokenRequest request) {
        UserEntity user = authService.getCurrentUser();
        
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
     * 验证accessToken是否有效
     * 参考AuthTokenFilter中的验证逻辑
     *
     * @param accessToken JWT访问令牌
     * @return boolean 是否有效
     */
    public boolean validateAccessToken(String accessToken) {
        if (!StringUtils.hasText(accessToken)) {
            log.debug("accessToken is empty");
            return false;
        }
        try {
            log.debug("validate accessToken {}", accessToken);
            // 首先验证JWT格式和签名
            if (!JwtUtils.validateJwtToken(accessToken)) {
                log.debug("accessToken is invalid");
                return false;
            }
            // 从数据库验证token是否有效（未被撤销且未过期）
            Optional<TokenEntity> tokenOpt = findByAccessToken(accessToken);
            if (tokenOpt.isPresent() && tokenOpt.get().isValid()) {
                log.debug("accessToken is valid");
                return true;
            } else {
                log.debug("Token is invalid or revoked");
                return false;
            }
        } catch (Exception e) {
            log.error("Error validating access token: {}", e.getMessage());
            return false;
        }
    }


    /**
     * 撤销指定的accessToken，使其失效
     * 
     * @param accessToken JWT访问令牌
     */
    public void revokeAccessToken(@NonNull String accessToken, String reason) {
        Optional<TokenEntity> optional = findByAccessToken(accessToken);
        if (optional.isPresent()) {
            TokenEntity entity = optional.get();
            entity.setRevoked(true);
            entity.setRevokeReason(reason);
            save(entity);
        }
    }

    

}
