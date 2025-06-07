/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-10 23:50:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-11 12:27:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.auth.oauth2;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;

@Service
public class OAuth2RestService extends BaseRestService<OAuth2Entity, OAuth2Request, OAuth2Response> {

    @Override
    public Page<OAuth2Response> queryByOrg(OAuth2Request request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByOrg'");
    }

    @Override
    public Page<OAuth2Response> queryByUser(OAuth2Request request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<OAuth2Entity> findByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByUid'");
    }

    @Override
    public OAuth2Response create(OAuth2Request request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public OAuth2Response update(OAuth2Request request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public OAuth2Entity save(OAuth2Entity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    @Override
    protected OAuth2Entity doSave(OAuth2Entity entity) {
        throw new UnsupportedOperationException("实现保存逻辑");
    }

    @Override
    public OAuth2Entity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, OAuth2Entity entity) {
        // 乐观锁处理逻辑
        throw new UnsupportedOperationException("实现乐观锁处理逻辑");
    }

    @Override
    public void deleteByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void delete(OAuth2Request entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public OAuth2Response convertToResponse(OAuth2Entity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToResponse'");
    }

    @Override
    public OAuth2Response queryByUid(OAuth2Request request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
}
