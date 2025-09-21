/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-08 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-19 10:03:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.bytedesk.core.push.service.PushSendService;
import com.bytedesk.core.ip.IpUtils;
import com.bytedesk.core.push.service.PushSendResult;
import com.bytedesk.core.rbac.auth.AuthRequest;
import com.bytedesk.core.rbac.auth.AuthScanService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Push业务服务 - 处理业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PushService {
    
    private final PushRepository pushRepository;
    private final PushFilterService pushFilterService;
    
    // 业务服务组件
    private final PushSendService codeSendService;
    private final AuthScanService scanLoginService;

    // =============== REST接口方法 ===============

    /**
     * 发送验证码
     */
    public PushSendResult sendCode(AuthRequest authRequest, HttpServletRequest request) {
        return codeSendService.sendCode(authRequest, request);
    }

    /**
     * 扫码查询
     */
    public PushResponse scanQuery(PushRequest pushRequest, HttpServletRequest request) {
        return scanLoginService.scanQuery(pushRequest, request);
    }

    /**
     * 扫码确认
     */
    public PushResponse scan(PushRequest pushRequest, HttpServletRequest request) {
        return scanLoginService.scan(pushRequest, request);
    }

    /**
     * 扫码登录确认
     */
    public PushResponse scanConfirm(PushRequest pushRequest, HttpServletRequest request) {
        return scanLoginService.scanConfirm(pushRequest, request);
    }

    // =============== 业务逻辑方法 ===============

    /**
     * 验证验证码
     */
    public Boolean validateCode(String receiver, String code, HttpServletRequest request) {
        // 参数非空校验
        Assert.hasText(receiver, "Receiver cannot be null or empty");
        Assert.hasText(code, "Code cannot be null or empty");
        Assert.notNull(request, "HttpServletRequest cannot be null");
        log.info("validate code for receiver: {}, code: {}", receiver, code);
        
        Optional<PushEntity> pushOptional = findByStatusAndReceiverAndContent(PushStatusEnum.PENDING.name(), receiver, code);
        if (pushOptional.isPresent()) {
            PushEntity push = pushOptional.get();
            push.setStatus(PushStatusEnum.CONFIRMED.name());
            pushRepository.save(push);
            
            // 清除IP发送时间限制
            String ip = IpUtils.getClientIp(request);
            pushFilterService.removeIpLastSentTime(ip);
            
            return true;
        } else {
            log.info("No matching push record found for receiver: {}, code: {}", receiver, code);
        }
        return false;
    }

    /**
     * 检查是否存在指定状态、类型和接收者的记录
     */
    public Boolean existsByStatusAndTypeAndReceiver(String type, String receiver) {
        return pushRepository.existsByStatusAndTypeAndReceiver(PushStatusEnum.PENDING.name(), type, receiver);
    }

    // =============== 内部辅助方法 ===============

    private Optional<PushEntity> findByStatusAndReceiverAndContent(String status, String receiver, String content) {
        return pushRepository.findByStatusAndReceiverAndContent(status, receiver, content);
    }
}
