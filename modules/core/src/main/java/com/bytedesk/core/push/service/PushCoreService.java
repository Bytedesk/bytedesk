/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-08 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-08 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.bytedesk.core.push.PushEntity;
import com.bytedesk.core.push.PushRequest;
import com.bytedesk.core.push.PushResponse;
import com.bytedesk.core.push.PushStatusEnum;
import com.bytedesk.core.push.PushRepository;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.IpUtils;
import com.bytedesk.core.push.PushFilterService;
import org.modelmapper.ModelMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * Push核心业务服务
 */
@Service
@RequiredArgsConstructor
public class PushCoreService {
    
    private final PushRepository pushRepository;
    private final ModelMapper modelMapper;
    private final UidUtils uidUtils;
    private final PushFilterService pushFilterService;

    public Boolean validateCode(String receiver, String code, HttpServletRequest request) {
        
        // 参数非空校验
        Assert.hasText(receiver, "Receiver cannot be null or empty");
        Assert.hasText(code, "Code cannot be null or empty");
        Assert.notNull(request, "HttpServletRequest cannot be null");
        
        Optional<PushEntity> pushOptional = findByStatusAndReceiverAndContent(PushStatusEnum.PENDING, receiver, code);
        if (pushOptional.isPresent()) {
            PushEntity push = pushOptional.get();
            push.setStatus(PushStatusEnum.CONFIRMED.name());
            save(push);
            
            // 清除IP发送时间限制
            String ip = IpUtils.getClientIp(request);
            pushFilterService.removeIpLastSentTime(ip);
            
            return true;
        }
        return false;
    }

    public PushResponse create(PushRequest pushRequest) {
        
        Assert.notNull(pushRequest, "PushRequest cannot be null");
        
        PushEntity push = modelMapper.map(pushRequest, PushEntity.class);
        push.setUid(uidUtils.getUid());
        push.setChannel(pushRequest.getChannel());

        PushEntity savedPush = save(push);
        if (savedPush == null) {
            throw new RuntimeException("create push failed");
        }
        
        return convertToResponse(savedPush);
    }

    public Optional<PushEntity> findByStatusAndReceiverAndContent(PushStatusEnum status, String receiver, String content) {
        return pushRepository.findByStatusAndReceiverAndContent(status.name(), receiver, content);
    }

    public Optional<PushEntity> findByDeviceUid(String deviceUid) {
        return pushRepository.findByDeviceUid(deviceUid);
    }

    public Boolean existsByStatusAndTypeAndReceiver(String type, String receiver) {
        return pushRepository.existsByStatusAndTypeAndReceiver(PushStatusEnum.PENDING.name(), type, receiver);
    }

    public PushEntity save(PushEntity push) {
        return pushRepository.save(push);
    }

    public PushResponse convertToResponse(PushEntity entity) {
        return modelMapper.map(entity, PushResponse.class);
    }
}
