/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-26 21:06:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-10 10:18:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.public_account;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PublicAccountRestService extends BaseRestService<PublicAccountEntity, PublicAccountRequest, PublicAccountResponse> {

    private final PublicAccountRepository channelRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    public Page<PublicAccountResponse> queryByOrg(PublicAccountRequest request) {
        Pageable pageable = request.getPageable();
        Page<PublicAccountEntity> channelPage = channelRepository.findAll(pageable);
        return channelPage.map(channel -> convertToResponse(channel));
    }

    @Override
    public Page<PublicAccountResponse> queryByUser(PublicAccountRequest request) {
        // UserEntity user = authService.getUser();
        // if (user == null) {
        //     throw new RuntimeException("user is null");
        // }
        // request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    public Optional<PublicAccountEntity> findByUid(String uid) {
        return channelRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return channelRepository.existsByUid(uid);
    }

    public PublicAccountResponse create(PublicAccountRequest request) {
        // 判断uid是否存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }

        PublicAccountEntity channel = modelMapper.map(request, PublicAccountEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            channel.setUid(uidUtils.getUid());
        }

        // 保存
        PublicAccountEntity savedPublicAccount = save(channel);
        if (savedPublicAccount == null) {
            throw new RuntimeException("channel is null");
        }

        return convertToResponse(savedPublicAccount);
    }

    @Override
    public PublicAccountResponse update(PublicAccountRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public PublicAccountEntity save(PublicAccountEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleOptimisticLockingFailureException(e, entity);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    @Override
    protected PublicAccountEntity doSave(PublicAccountEntity entity) {
        return channelRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<PublicAccountEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            PublicAccountEntity channel = optional.get();
            channel.setDeleted(true);
            save(channel);
            // channelRepository.delete(optional.get());
        }
    }

    @Override
    public void delete(PublicAccountRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public PublicAccountEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            PublicAccountEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public PublicAccountResponse convertToResponse(PublicAccountEntity channel) {
        return modelMapper.map(channel, PublicAccountResponse.class);
    }

    @Override
    protected Specification<PublicAccountEntity> createSpecification(PublicAccountRequest request) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
    }

    @Override
    protected Page<PublicAccountEntity> executePageQuery(Specification<PublicAccountEntity> spec, Pageable pageable) {
        return channelRepository.findAll(spec, pageable);
    }
}
