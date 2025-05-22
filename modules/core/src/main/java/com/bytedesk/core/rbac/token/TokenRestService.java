/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-22 15:42:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-22 15:53:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.token;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class TokenRestService extends BaseRestService<TokenEntity, TokenRequest, TokenResponse> {

    private final TokenRepository tokenRepository;

    private final ModelMapper modelMapper;
    
    @Override
    public Page<TokenResponse> queryByOrg(TokenRequest request) {
        Pageable pageable = request.getPageable();
        Specification<TokenEntity> spec = TokenSpecification.search(request);
        Page<TokenEntity> page = tokenRepository.findAll(spec, pageable);
        return page.map(entity -> modelMapper.map(entity, TokenResponse.class));
    }

    @Override
    public Page<TokenResponse> queryByUser(TokenRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<TokenEntity> findByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByUid'");
    }

    @Override
    public TokenResponse create(TokenRequest request) {
        TokenEntity entity = modelMapper.map(request, TokenEntity.class);
        entity = doSave(entity);
        return modelMapper.map(entity, TokenResponse.class);
    }

    @Override
    public TokenResponse update(TokenRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    protected TokenEntity doSave(TokenEntity entity) {
        return tokenRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void delete(TokenRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public TokenEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            TokenEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public TokenResponse convertToResponse(TokenEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToResponse'");
    }
    
    /**
     * 根据accessToken查找Token实体
     * @param accessToken JWT访问令牌
     * @return Optional<TokenEntity>
     */
    public Optional<TokenEntity> findByAccessToken(String accessToken) {
        return tokenRepository.findByAccessToken(accessToken);
    }

    /**
     * 根据用户UID和类型查找有效的Token列表
     * @param userUid 用户UID
     * @param type 令牌类型
     * @return List<TokenEntity>
     */
    public List<TokenEntity> findValidTokensByUserUidAndType(String userUid, String type) {
        return tokenRepository.findByUserUidAndTypeAndRevokedFalseAndExpiresAtAfter(
            userUid, type, LocalDateTime.now());
    }

    /**
     * 创建登录Token
     * @param userUid 用户UID
     * @param accessToken JWT访问令牌
     * @param client 客户端类型
     * @param device 设备信息
     * @return TokenEntity 创建的Token实体
     */
    public TokenEntity createLoginToken(String userUid, String accessToken, String client, String device) {
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24); // 默认24小时过期
        
        TokenEntity tokenEntity = TokenEntity.builder()
            .name("Login Token")
            .description("User login authentication token")
            .accessToken(accessToken)
            .type(TokenTypeEnum.LOGIN.name())
            .expiresAt(expiresAt)
            .client(client)
            .device(device)
            .build();
        
        tokenEntity.setUserUid(userUid);
        return doSave(tokenEntity);
    }
}
