/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-22 15:42:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-28 23:08:16
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
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class TokenRestService extends BaseRestService<TokenEntity, TokenRequest, TokenResponse> {

    private final TokenRepository tokenRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

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

    @Cacheable(cacheNames = "token", key = "#uid", unless = "#result == null")
    @Override
    public Optional<TokenEntity> findByUid(String uid) {
        return tokenRepository.findByUid(uid);
    }

    /**
     * 根据accessToken查找Token实体
     * 
     * @param accessToken JWT访问令牌
     * @return Optional<TokenEntity>
     */
    @Cacheable(cacheNames = "token", key = "#accessToken", unless = "#result == null")
    public Optional<TokenEntity> findByAccessToken(String accessToken) {
        return tokenRepository.findByAccessToken(accessToken);
    }

    @Override
    public TokenResponse initVisitor(TokenRequest request) {
        TokenEntity entity = modelMapper.map(request, TokenEntity.class);
        entity.setUid(uidUtils.getUid());
        //
        TokenEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create token failed");
        }

        // 手动将实体放入缓存
        cacheToken(savedEntity);

        return convertToResponse(savedEntity);
    }

    // 添加一个新方法来处理缓存
    @CachePut(cacheNames = "token", key = "#entity.accessToken")
    private TokenEntity cacheToken(TokenEntity entity) {
        log.debug("Manually caching token with key: {}", entity.getAccessToken());
        return entity;
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public TokenResponse convertToResponse(TokenEntity entity) {
        return modelMapper.map(entity, TokenResponse.class);
    }

     /**
     * 根据用户UID和类型查找有效的Token列表
     * 
     * @param userUid 用户UID
     * @param type    令牌类型
     * @return List<TokenEntity>
     */
    // @Cacheable(cacheNames = "token", key = "#userUid + '_' + #type", unless =
    // "#result == null")
    // public List<TokenEntity> findValidTokensByUserUidAndType(String userUid,
    // String type) {
    // return tokenRepository.findByUserUidAndTypeAndRevokedFalseAndExpiresAtAfter(
    // userUid, type, BdDateUtils.now());
    // }
}
