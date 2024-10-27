/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-26 10:36:09
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-23 18:29:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.key;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LlmProviderKeyService extends BaseService<LlmProviderKeyEntity, LlmProviderKeyRequest, LlmProviderKeyResponse> {

    private final LlmProviderKeyRepository repository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    public Page<LlmProviderKeyResponse> queryByOrg(LlmProviderKeyRequest request) {
        
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Direction.ASC,
                "updatedAt");

        Specification<LlmProviderKeyEntity> specification = LlmProviderKeySpecification.search(request);

        Page<LlmProviderKeyEntity> page = repository.findAll(specification, pageable);

        return page.map(this::convertToResponse);
    }

    @Override
    public Page<LlmProviderKeyResponse> queryByUser(LlmProviderKeyRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "LlmProviderKey", key = "#uid", unless = "#result==null")
    @Override
    public Optional<LlmProviderKeyEntity> findByUid(String uid) {
        return repository.findByUid(uid);
    }

    @Cacheable(value = "LlmProviderKey", key = "#provider+'-'+#orgUid", unless = "#result==null")
    public Optional<LlmProviderKeyEntity> findByProviderAndOrgUid(String provider, String orgUid) {
        return repository.findByProviderAndOrgUid(provider, orgUid);
    }

    @Override
    public LlmProviderKeyResponse create(LlmProviderKeyRequest request) {
        
        LlmProviderKeyEntity entity = modelMapper.map(request, LlmProviderKeyEntity.class);
        entity.setUid(uidUtils.getUid());

        LlmProviderKeyEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create entity failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public LlmProviderKeyResponse update(LlmProviderKeyRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public LlmProviderKeyEntity save(LlmProviderKeyEntity entity) {
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
    public void delete(LlmProviderKeyRequest entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            LlmProviderKeyEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public LlmProviderKeyResponse convertToResponse(LlmProviderKeyEntity entity) {
        return modelMapper.map(entity, LlmProviderKeyResponse.class);
    }
    
}
