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
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.ip.IpService;
import com.bytedesk.core.push.PushEntity;
import com.bytedesk.core.push.PushRequest;
import com.bytedesk.core.push.PushResponse;
import com.bytedesk.core.push.PushRestService;
import com.bytedesk.core.push.PushStatusEnum;
import com.bytedesk.core.rbac.auth.AuthTypeEnum;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.IpUtils;
import com.bytedesk.core.utils.Utils;
import org.modelmapper.ModelMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * 扫码登录服务
 */
@Service
@RequiredArgsConstructor
public class ScanLoginService {
    
    private final PushRestService pushRestService;
    private final ModelMapper modelMapper;
    private final UidUtils uidUtils;
    private final IpService ipService;

    public PushResponse scanQuery(PushRequest pushRequest, HttpServletRequest request) {

        // 参数非空校验
        Assert.notNull(pushRequest, "PushRequest cannot be null");
        Assert.notNull(request, "HttpServletRequest cannot be null");
        Assert.hasText(pushRequest.getDeviceUid(), "PushRequest deviceUid cannot be null or empty");

        Optional<PushEntity> pushOptional = pushRestService.findByDeviceUid(pushRequest.getDeviceUid());
        if (pushOptional.isPresent()) {
            PushEntity push = pushOptional.get();
            if (pushRequest.getForceRefresh().booleanValue()) {
                push.setStatus(PushStatusEnum.PENDING.name());
                pushRestService.save(push);
            }
            return pushRestService.convertToResponse(push);
        }

        return createNewScanQuery(pushRequest, request);
    }

    public PushResponse scan(PushRequest pushRequest, HttpServletRequest request) {
        
        // 参数非空校验
        Assert.notNull(pushRequest, "PushRequest cannot be null");
        Assert.notNull(request, "HttpServletRequest cannot be null");
        Assert.hasText(pushRequest.getDeviceUid(), "PushRequest deviceUid cannot be null or empty");
        
        PushEntity push = pushRestService.findByDeviceUid(pushRequest.getDeviceUid())
                .orElseThrow(() -> new RuntimeException("scan deviceUid " + pushRequest.getDeviceUid() + " not found"));
        
        push.setStatus(PushStatusEnum.SCANNED.name());
        
        PushEntity savedPush = pushRestService.save(push);
        if (savedPush == null) {
            throw new RuntimeException("scan save failed");
        }
        return pushRestService.convertToResponse(savedPush);
    }

    public PushResponse scanConfirm(PushRequest pushRequest, HttpServletRequest request) {

        // 参数非空校验
        Assert.notNull(pushRequest, "PushRequest cannot be null");
        Assert.notNull(request, "HttpServletRequest cannot be null");
        Assert.hasText(pushRequest.getDeviceUid(), "PushRequest deviceUid cannot be null or empty");
        Assert.hasText(pushRequest.getReceiver(), "PushRequest receiver cannot be null or empty");

        PushEntity push = pushRestService.findByDeviceUid(pushRequest.getDeviceUid())
                .orElseThrow(() -> new RuntimeException(
                        "scanConfirm deviceUid " + pushRequest.getDeviceUid() + " not found"));
        push.setReceiver(pushRequest.getReceiver());
        push.setStatus(PushStatusEnum.CONFIRMED.name());
        
        PushEntity savedPush = pushRestService.save(push);
        if (savedPush == null) {
            throw new RuntimeException("scanConfirm save failed");
        }
        return pushRestService.convertToResponse(savedPush);
    }

    private PushResponse createNewScanQuery(PushRequest pushRequest, HttpServletRequest request) {
        String ip = IpUtils.getClientIp(request);
        String ipLocation = ipService.getIpLocation(ip);
        
        PushEntity push = modelMapper.map(pushRequest, PushEntity.class);
        push.setUid(uidUtils.getUid());
        push.setType(AuthTypeEnum.SCAN_LOGIN.name());
        push.setSender(TypeConsts.TYPE_SYSTEM);
        push.setContent(Utils.getRandomCode());
        push.setIp(ip);
        push.setIpLocation(ipLocation);
        
        PushEntity savedPush = pushRestService.save(push);
        if (savedPush == null) {
            throw new RuntimeException("scan query failed");
        }
        return pushRestService.convertToResponse(savedPush);
    }
}
