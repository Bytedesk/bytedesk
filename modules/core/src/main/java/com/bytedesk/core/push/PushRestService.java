/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-25 15:41:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-28 21:00:10
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

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.config.BytedeskProperties;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.exception.EmailExistsException;
import com.bytedesk.core.exception.MobileExistsException;
import com.bytedesk.core.ip.IpService;
import com.bytedesk.core.ip.IpUtils;
import com.bytedesk.core.push.email.PushServiceImplEmail;
import com.bytedesk.core.push.sms.PushServiceImplSms;
import com.bytedesk.core.rbac.auth.AuthRequest;
import com.bytedesk.core.rbac.auth.AuthTypeEnum;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class PushRestService extends BaseRestService<PushEntity, PushRequest, PushResponse> {

    private final PushRepository pushRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final PushServiceImplEmail pushServiceImplEmail;

    private final PushServiceImplSms pushServiceImplSms;

    private final UserService userService;

    private final BytedeskProperties bytedeskProperties;

    private final IpService ipService;

    private final PushFilterService pushFilterService;

    // String receiver, String type, String client, String authType, String platform, String orgUid,
    public Boolean sendCode(AuthRequest authRequest, HttpServletRequest request) {

        String ip = IpUtils.getClientIp(request);
        String type = authRequest.getType();
        String receiver = authRequest.getReceiver();
        String platform = authRequest.getPlatform();
        // 
        // 验证限制同一个ip发送数量、频率
        // 检查是否短时间内已发送过验证码
        if (!pushFilterService.canSendCode(ip)) {
            log.info("验证码发送过于频繁，IP: {}", ip);
            return false;
        }
        // 
        if (type.equals(AuthTypeEnum.MOBILE_REGISTER.name())) {
            // 注册验证码，如果账号已经存在，则直接抛出异常
            if (authRequest.isMobile() && userService.existsByMobileAndPlatform(receiver, platform)) {
                throw new MobileExistsException("mobile already exists, please login directly");
            }
            if (authRequest.isEmail() && userService.existsByEmailAndPlatform(receiver, platform)) {
                throw new EmailExistsException("email already exists, please login directly");
            }
        } else if (type.equals(AuthTypeEnum.EMAIL_RESET.name())) {
            // 修改邮箱，如果账号已经存在，则直接抛出异常
            if (authRequest.isEmail() && userService.existsByEmailAndPlatform(receiver, platform)) {
                throw new EmailExistsException("email already exists, please use another one");
            }
        } else if (type.equals(AuthTypeEnum.MOBILE_RESET.name())) {
            // 修改手机号，如果账号已经存在，则直接抛出异常
            if (authRequest.isMobile() && userService.existsByMobileAndPlatform(receiver, platform)) {
                throw new MobileExistsException("mobile already exists, please use another one");
            }
        } else if (type.equals(AuthTypeEnum.EMAIL_VERIFY.name())) {
            // 验证邮箱，如果账号不存在，则直接抛出异常
            if (authRequest.isEmail() && !userService.existsByEmailAndPlatform(receiver, platform)) {
                throw new EmailExistsException("email not exists");
            } 
        } else if (type.equals(AuthTypeEnum.MOBILE_VERIFY.name())) {
            // 验证手机号，如果账号不存在，则直接抛出异常
            if (authRequest.isMobile() && !userService.existsByMobileAndPlatform(receiver, platform)) {
                throw new MobileExistsException("mobile not exists");
            }
        }
        
        // check if has already send validate code within 15min
        if (existsByStatusAndTypeAndReceiver(PushStatusEnum.PENDING, type, receiver)) {
            return false;
        }

        String code = "";
        if (bytedeskProperties.isInWhitelist(receiver) 
            || bytedeskProperties.isAdmin(receiver)) {
            code = bytedeskProperties.getValidateCode();
        } else {
            code = Utils.getRandomCode();
        }

        if (authRequest.isEmail()) {
            pushServiceImplEmail.send(receiver, code, request);
        } else if (authRequest.isMobile()) {
            pushServiceImplSms.send(receiver, code, request);
        } else {
            return false;
        }

        String ipLocation = ipService.getIpLocation(ip);
        //
        // PushRequest pushRequest = new PushRequest();
        PushRequest pushRequest = modelMapper.map(authRequest, PushRequest.class);
        // pushRequest.setType(type);
        pushRequest.setSender(TypeConsts.TYPE_SYSTEM);
        pushRequest.setContent(code);
        // pushRequest.setReceiver(receiver);
        // pushRequest.setClient(client);
        // pushRequest.setPlatform(platform);
        pushRequest.setIp(ip);
        pushRequest.setIpLocation(ipLocation);
        // pushRequest.setDeviceUid(authRequest.getDeviceUid());
        // if (StringUtils.hasText(authRequest.getUserUid())) {
        //     pushRequest.setUserUid(authRequest.getUserUid());
        // }
        // pushRequest.setOrgUid(orgUid);
        create(pushRequest);

        // 更新IP最后发送验证码的时间
        pushFilterService.updateIpLastSentTime(ip);

        return true;
    }

    public Boolean validateCode(String receiver,  String code, HttpServletRequest request) {        
        // check if has already send validate code within 15min
        Optional<PushEntity> pushOptional = findByStatusAndReceiverAndContent(PushStatusEnum.PENDING, receiver, code);
        if (pushOptional.isPresent()) {
            // pushOptional.get().setStatus(StatusConsts.CODE_STATUS_CONFIRM);
            pushOptional.get().setStatus(PushStatusEnum.CONFIRMED.name());
            save(pushOptional.get());
            // 
            String ip = IpUtils.getClientIp(request);
            pushFilterService.removeIpLastSentTime(ip);
            // 
            return true;
        }
        return false;
    }
    

    public PushResponse scanQuery(PushRequest pushRequest, HttpServletRequest request) {

        Optional<PushEntity> pushOptional = findByDeviceUid(pushRequest.getDeviceUid());
        if (pushOptional.isPresent()) {
            PushEntity push = pushOptional.get();
            // 
            if (pushRequest.getForceRefresh().booleanValue()) {
                push.setStatus(PushStatusEnum.PENDING.name());
                save(push);
            }
            // 
            return convertToResponse(push);
        }

        String ip = IpUtils.getClientIp(request);
        String ipLocation = ipService.getIpLocation(ip);
        //
        PushEntity push = modelMapper.map(pushRequest, PushEntity.class);
        push.setUid(uidUtils.getCacheSerialUid());
        // push.setType(TypeConsts.TYPE_SCAN);
        push.setType(AuthTypeEnum.SCAN_LOGIN.name());
        push.setSender(TypeConsts.TYPE_SYSTEM);
        push.setContent(Utils.getRandomCode());
        push.setIp(ip);
        push.setIpLocation(ipLocation);
        //
        PushEntity savedPush = save(push);
        if (savedPush == null) {
            throw new RuntimeException("scan query failed");
        }
        return convertToResponse(savedPush);
    }

    public PushResponse scan(PushRequest pushRequest, HttpServletRequest request) {
        
        PushEntity push = findByDeviceUid(pushRequest.getDeviceUid())
                .orElseThrow(() -> new RuntimeException("scan deviceUid " + pushRequest.getDeviceUid() + " not found"));
        
        push.setStatus(PushStatusEnum.SCANNED.name());
        //
        PushEntity savedPush = save(push);
        if (savedPush == null) {
            throw new RuntimeException("scan save failed");
        }
        return convertToResponse(savedPush);
    }

    public PushResponse scanConfirm(PushRequest pushRequest, HttpServletRequest request) {

        PushEntity push = findByDeviceUid(pushRequest.getDeviceUid())
                .orElseThrow(() -> new RuntimeException(
                        "scanConfirm deviceUid " + pushRequest.getDeviceUid() + " not found"));
        push.setReceiver(pushRequest.getReceiver());
        push.setStatus(PushStatusEnum.CONFIRMED.name());
        //
        PushEntity savedPush = save(push);
        if (savedPush == null) {
            throw new RuntimeException("scanConfirm save failed");
        }
        return convertToResponse(savedPush);
    }

    public PushResponse create(PushRequest pushRequest) {
        log.info("pushRequest {}", pushRequest.toString());

        PushEntity push = modelMapper.map(pushRequest, PushEntity.class);
        push.setUid(uidUtils.getCacheSerialUid());
        push.setClient(pushRequest.getClient());

        PushEntity savedPush = save(push);
        if (savedPush == null) {
            throw new RuntimeException("create push failed");
        }
        // 
        return convertToResponse(savedPush);
    }

    // public Boolean validateEmailCode(String email, String code) {
    //     return validateCode(email, code);
    // }

    // public Boolean validateSmsCode(String mobile, String code) {
    //     return validateCode(mobile, code);
    // }

    

    // @Cacheable(value = "push", key = "#receiver-#status-#type", unless = "#result
    // == null")
    public Optional<PushEntity> findByStatusAndReceiverAndContent(PushStatusEnum status, String receiver,
            String content) {
        return pushRepository.findByStatusAndReceiverAndContent(status.name(), receiver, content);
    }

    public Optional<PushEntity> findByDeviceUid(String deviceUid) {
        return pushRepository.findByDeviceUid(deviceUid);
    }

    public Boolean existsByStatusAndTypeAndReceiver(PushStatusEnum status, String type, String receiver) {
        return pushRepository.existsByStatusAndTypeAndReceiver(status.name(), type, receiver);
    }

    @Caching(put = {
            @CachePut(value = "push", key = "#push.receiver"),
            // TODO: 根据status, 缓存或清空缓存，clear or cache according to status
    })
    public PushEntity save(PushEntity push) {
        try {
            return pushRepository.save(push);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // TODO: 更新缓存
    // @Cacheable(value = "pushPending")
    public List<PushEntity> findStatusPending() {
        return pushRepository.findByStatus(PushStatusEnum.PENDING.name());
    }

    // 自动过期
    @Async
    public void autoOutdateCode() {
        List<PushEntity> pendingPushes = findStatusPending();
        // log.info("autoOutdateCode pendingPushes {}", pendingPushes.size());
        pendingPushes.forEach(push -> {
            // 计算两个日期之间的毫秒差
            // long diffInMilliseconds = Math.abs(new Date().getTime() - push.getUpdatedAt().getTime());
            // 将LocalDateTime转换为时间戳
            long currentTimeMillis = System.currentTimeMillis();
            ZoneId systemZone = ZoneId.systemDefault(); // 获取系统默认时区
            long updatedAtMillis = push.getUpdatedAt().atZone(systemZone).toInstant().toEpochMilli();
            long diffInMilliseconds = Math.abs(currentTimeMillis - updatedAtMillis);

            // 转换为分钟
            long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMilliseconds);
            // log.info("autoOutdateCode diffInMinutes {} {}", push.getContent(), diffInMinutes);
            //
            // if (push.getType().equals(TypeConsts.TYPE_SCAN)) {
            if (push.getType().equals(AuthTypeEnum.SCAN_LOGIN.name())) {
                // log.info("autoOutdateCode scan TypeConsts.TYPE_SCAN");
                // 扫码有效时间3分钟
                if (diffInMinutes > 3) {
                    push.setStatus(PushStatusEnum.EXPIRED.name());
                    save(push);
                }
            } else if (diffInMinutes > 15) {
                // 手机验证码有效时间15分钟
                push.setStatus(PushStatusEnum.EXPIRED.name());
                save(push);
            }
        });
    }

    @Override
    public Page<PushResponse> queryByOrg(PushRequest request) {

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "updatedAt");
        Specification<PushEntity> specification = PushSpecification.search(request);
        Page<PushEntity> page = pushRepository.findAll(specification, pageable);

        return page.map(push -> convertToResponse(push));
    }

    @Override
    public Page<PushResponse> queryByUser(PushRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<PushEntity> findByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByUid'");
    }

    @Override
    public PushResponse update(PushRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void deleteByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void delete(PushRequest entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, PushEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public PushResponse convertToResponse(PushEntity entity) {
        return modelMapper.map(entity, PushResponse.class);
    }

}
