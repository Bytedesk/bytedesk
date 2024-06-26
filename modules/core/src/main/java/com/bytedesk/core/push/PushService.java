/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-25 15:41:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-20 17:08:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.bytedesk.core.config.BytedeskProperties;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.enums.PlatformEnum;
import com.bytedesk.core.exception.EmailExistsException;
import com.bytedesk.core.exception.MobileExistsException;
import com.bytedesk.core.ip.IpService;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class PushService {

    private final PushRepository pushRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final PushServiceImplEmail pushServiceImplEmail;

    private final PushServiceImplSms pushServiceImplSms;

    private final UserService userService;

    private final BytedeskProperties bytedeskProperties;

    private final IpService ipService;

    public Boolean sendEmailCode(String email, String client, String authType, PlatformEnum platform,
            HttpServletRequest request) {

        return sendCode(email, TypeConsts.TYPE_EMAIL, client, authType, platform, request);
    }

    public Boolean sendSmsCode(String mobile, String client, String authType, 
            PlatformEnum platform, HttpServletRequest request) {

        return sendCode(mobile, TypeConsts.TYPE_MOBILE, client, authType, platform, request);
    }

    public Boolean sendCode(String receiver, String type, String client, String authType, 
            PlatformEnum platform,
            HttpServletRequest request) {

        // 注册验证码，如果账号已经存在，则直接抛出异常
        if (authType.equals(TypeConsts.SEND_MOBILE_CODE_TYPE_REGISTER)) {

            if (type.equals(TypeConsts.TYPE_MOBILE) && userService.existsByMobileAndPlatform(receiver, platform)) {
                throw new MobileExistsException("mobile already exists");
            }

            if (type.equals(TypeConsts.TYPE_EMAIL) && userService.existsByEmailAndPlatform(receiver, platform)) {
                throw new EmailExistsException("email already exists");
            }
        }

        // check if has already send validate code within 15min
        if (existsByStatusAndTypeAndReceiver(PushStatus.PENDING, type, receiver)) {
            return false;
        }

        String code = "";
        if (Utils.isTestMobile(receiver) || Utils.isTestEmail(receiver) || bytedeskProperties.isInWhitelist(receiver)) {
            code = bytedeskProperties.getMobileCode();
            log.info("test code: {}", code);
        } else {
            code = Utils.getRandomCode(receiver);
        }

        if (type.equals(TypeConsts.TYPE_EMAIL)) {
            pushServiceImplEmail.send(receiver, code, request);
        } else if (type.equals(TypeConsts.TYPE_MOBILE)) {
            pushServiceImplSms.send(receiver, code, request);
        } else {
            return false;
        }

        String ip = ipService.getIp(request);
        String ipLocation = ipService.getIpLocation(ip);
        // 
        PushRequest pushRequest = new PushRequest();
        pushRequest.setType(type);
        pushRequest.setSender(TypeConsts.TYPE_SYSTEM);
        pushRequest.setContent(code);
        pushRequest.setReceiver(receiver);
        pushRequest.setClient(client);
        pushRequest.setPlatform(platform);
        pushRequest.setIp(ip);
        pushRequest.setIpLocation(ipLocation);
        create(pushRequest);

        return true;
    }
    
    public Push create(PushRequest pushRequest) {
        log.info("pushRequest {}", pushRequest.toString());

        Push push = modelMapper.map(pushRequest, Push.class);
        push.setUid(uidUtils.getCacheSerialUid());
        push.setClient(pushRequest.getClient());

        return save(push);
    }

    public Boolean validateEmailCode(String email, String code) {
        return validateCode(email, TypeConsts.TYPE_EMAIL, code);
    }

    public Boolean validateSmsCode(String mobile, String code) {
        return validateCode(mobile, TypeConsts.TYPE_MOBILE, code);
    }

    public Boolean validateCode(String receiver, String type, String code) {
        // check if has already send validate code within 15min
        
        // Boolean result = pushRepository.existsByStatusAndTypeAndReceiverAndContent(StatusConsts.CODE_STATUS_PENDING,
        //         type, receiver, code);
        // if (result) {
        //     // TODO: 更新状态
        // }
        // return result;
        // 
        Optional<Push> pushOptional = findByStatusAndTypeAndReceiverAndContent(PushStatus.PENDING, type, receiver, code);
        if (pushOptional.isPresent()) {
            // pushOptional.get().setStatus(StatusConsts.CODE_STATUS_CONFIRM);
            pushOptional.get().setStatus(PushStatus.CONFIRMED);
            save(pushOptional.get());
            return true;
        }
        return false;
    }

    // @Cacheable(value = "push", key = "#receiver-#status-#type", unless = "#result == null")
    public Optional<Push> findByStatusAndTypeAndReceiverAndContent(PushStatus status, String type, String receiver, String content) {
        return pushRepository.findByStatusAndTypeAndReceiverAndContent(status, type, receiver, content);
    }

    public Boolean existsByStatusAndTypeAndReceiver(PushStatus status, String type, String receiver) {
        return pushRepository.existsByStatusAndTypeAndReceiver(status, type, receiver);
    }
    
    @Caching(put = {
        @CachePut(value = "push", key = "#push.receiver"),
        // TODO: 根据status, 缓存或清空缓存，clear or cache according to status
    })
    public Push save(Push push) {
       return pushRepository.save(push);
    }
    
    // TODO: 更新缓存
    @Cacheable(value = "pushPending")
    public List<Push> findStatusPending() {
        // return pushRepository.findByStatus(StatusConsts.CODE_STATUS_PENDING);
        return pushRepository.findByStatus(PushStatus.PENDING);
    }

    // 自动过期
    // TODO: 频繁查库，待优化
    @Async
    public void autoOutdateCode() {
        List<Push> pendingPushes = findStatusPending();
        pendingPushes.forEach(push -> {
            // 计算两个日期之间的毫秒差
            long diffInMilliseconds = Math.abs(new Date().getTime() - push.getUpdatedAt().getTime());
            // 转换为分钟
            long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMilliseconds);
            // 验证码有效时间15分钟
            if (diffInMinutes > 15) {
                // TODO: 过期，清空缓存
                // push.setStatus(StatusConsts.CODE_STATUS_EXPIRED);
                push.setStatus(PushStatus.EXPIRED);
                save(push);
            }
        });
    }


    public PushResponse convertToPushResponse(Push push) {
        return modelMapper.map(push, PushResponse.class);
    }
    
}
