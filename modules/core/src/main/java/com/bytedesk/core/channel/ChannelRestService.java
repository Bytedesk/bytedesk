/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-26 21:06:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-11 11:19:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.channel;

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
public class ChannelRestService extends BaseRestService<ChannelEntity, ChannelRequest, ChannelResponse> {

    private final ChannelRepository channelRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    // private final AuthService authService;

    @Override
    public Page<ChannelResponse> queryByOrg(ChannelRequest request) {
        Pageable pageable = request.getPageable();
        Page<ChannelEntity> channelPage = channelRepository.findAll(pageable);
        return channelPage.map(channel -> convertToResponse(channel));
    }

    @Override
    public Page<ChannelResponse> queryByUser(ChannelRequest request) {
        // UserEntity user = authService.getUser();
        // if (user == null) {
        //     throw new RuntimeException("user is null");
        // }
        // request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
    }

    public Optional<ChannelEntity> findByUid(String uid) {
        return channelRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return channelRepository.existsByUid(uid);
    }

    public ChannelResponse create(ChannelRequest request) {
        // 判断uid是否存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }

        ChannelEntity channel = modelMapper.map(request, ChannelEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            channel.setUid(uidUtils.getUid());
        }

        // 保存
        ChannelEntity savedChannel = save(channel);
        if (savedChannel == null) {
            throw new RuntimeException("channel is null");
        }

        return convertToResponse(savedChannel);
    }




    @Override
    public ChannelResponse update(ChannelRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public ChannelEntity save(ChannelEntity entity) {
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
    protected ChannelEntity doSave(ChannelEntity entity) {
        return channelRepository.save(entity);
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<ChannelEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            ChannelEntity channel = optional.get();
            channel.setDeleted(true);
            save(channel);
            // channelRepository.delete(optional.get());
        }
    }

    @Override
    public void delete(ChannelRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public ChannelEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            ChannelEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public ChannelResponse convertToResponse(ChannelEntity channel) {
        return modelMapper.map(channel, ChannelResponse.class);
    }
}
