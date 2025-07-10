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
package com.bytedesk.core.notice_account;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class NoticeAccountRestService extends BaseRestService<NoticeAccountEntity, NoticeAccountRequest, NoticeAccountResponse> {

    private final NoticeAccountRepository channelRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    public Page<NoticeAccountResponse> queryByOrg(NoticeAccountRequest request) {
        Pageable pageable = request.getPageable();
        Page<NoticeAccountEntity> channelPage = channelRepository.findAll(pageable);
        return channelPage.map(channel -> convertToResponse(channel));
    }

    @Override
    public Page<NoticeAccountResponse> queryByUser(NoticeAccountRequest request) {
        // UserEntity user = authService.getUser();
        // if (user == null) {
        //     throw new RuntimeException("user is null");
        // }
        // request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    public Optional<NoticeAccountEntity> findByUid(String uid) {
        return channelRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return channelRepository.existsByUid(uid);
    }

    public NoticeAccountResponse create(NoticeAccountRequest request) {
        // 判断uid是否存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }

        NoticeAccountEntity channel = modelMapper.map(request, NoticeAccountEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            channel.setUid(uidUtils.getUid());
        }

        // 保存
        NoticeAccountEntity savedNoticeAccount = save(channel);
        if (savedNoticeAccount == null) {
            throw new RuntimeException("channel is null");
        }

        return convertToResponse(savedNoticeAccount);
    }

    @Override
    public NoticeAccountResponse update(NoticeAccountRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public NoticeAccountEntity save(NoticeAccountEntity entity) {
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
    protected NoticeAccountEntity doSave(NoticeAccountEntity entity) {
        return channelRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<NoticeAccountEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            NoticeAccountEntity channel = optional.get();
            channel.setDeleted(true);
            save(channel);
            // channelRepository.delete(optional.get());
        }
    }

    @Override
    public void delete(NoticeAccountRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public NoticeAccountEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            NoticeAccountEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public NoticeAccountResponse convertToResponse(NoticeAccountEntity channel) {
        return modelMapper.map(channel, NoticeAccountResponse.class);
    }
}
