/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-25 15:41:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-26 16:39:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.exception.EmailExistsException;
import com.bytedesk.core.exception.MobileExistsException;
import com.bytedesk.core.ip.IpService;
import com.bytedesk.core.push.email.PushServiceEmail;
import com.bytedesk.core.push.sms.PushServiceSms;
import com.bytedesk.core.rbac.auth.AuthRequest;
import com.bytedesk.core.rbac.auth.AuthTypeEnum;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.IpUtils;
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

    private final PushServiceEmail pushServiceEmail;

    private final PushServiceSms pushServiceSms;

    private final UserService userService;

    private final BytedeskProperties bytedeskProperties;

    private final IpService ipService;

    private final PushFilterService pushFilterService;

    // String receiver, String type, String client, String authType, String platform, String orgUid,
    public Boolean sendCode(AuthRequest authRequest, HttpServletRequest request) {

        String ip = IpUtils.getClientIp(request);
        String type = authRequest.getType();
        String receiver = authRequest.getReceiver();
        String country = authRequest.getCountry();
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
                throw new MobileExistsException(I18Consts.I18N_MOBILE_ALREADY_EXISTS);
            }
            if (authRequest.isEmail() && userService.existsByEmailAndPlatform(receiver, platform)) {
                throw new EmailExistsException(I18Consts.I18N_EMAIL_ALREADY_EXISTS);
            }
        } else if (type.equals(AuthTypeEnum.EMAIL_RESET.name())) {
            // 修改邮箱，如果账号已经存在，则直接抛出异常
            if (authRequest.isEmail() && userService.existsByEmailAndPlatform(receiver, platform)) {
                throw new EmailExistsException(I18Consts.I18N_EMAIL_ALREADY_EXISTS);
            }
        } else if (type.equals(AuthTypeEnum.MOBILE_RESET.name())) {
            // 修改手机号，如果账号已经存在，则直接抛出异常
            if (authRequest.isMobile() && userService.existsByMobileAndPlatform(receiver, platform)) {
                throw new MobileExistsException(I18Consts.I18N_MOBILE_ALREADY_EXISTS);
            }
        } else if (type.equals(AuthTypeEnum.EMAIL_VERIFY.name())) {
            // 验证邮箱，如果账号不存在，则直接抛出异常
            if (authRequest.isEmail() && !userService.existsByEmailAndPlatform(receiver, platform)) {
                throw new EmailExistsException(I18Consts.I18N_EMAIL_NOT_EXISTS);
            } 
        } else if (type.equals(AuthTypeEnum.MOBILE_VERIFY.name())) {
            // 验证手机号，如果账号不存在，则直接抛出异常
            if (authRequest.isMobile() && !userService.existsByMobileAndPlatform(receiver, platform)) {
                throw new MobileExistsException(I18Consts.I18N_MOBILE_NOT_EXISTS);
            }
        }
        
        // check if has already send validate code within 15min
        if (existsByStatusAndTypeAndReceiver(PushStatusEnum.PENDING, type, receiver)) {
            return false;
        }

        String code = "";
        if (bytedeskProperties.isInWhitelist(receiver) 
            || bytedeskProperties.isSuperUser(receiver)) {
            code = bytedeskProperties.getValidateCode();
        } else {
            code = Utils.getRandomCode();
        }

        if (authRequest.isEmail()) {
            pushServiceEmail.send(receiver, code, request);
        } else if (authRequest.isMobile()) {
            pushServiceSms.send(receiver, country, code, request);
        } else {
            return false;
        }

        String ipLocation = ipService.getIpLocation(ip);
        //
        PushRequest pushRequest = modelMapper.map(authRequest, PushRequest.class);
        pushRequest.setSender(TypeConsts.TYPE_SYSTEM);
        pushRequest.setContent(code);
        pushRequest.setIp(ip);
        pushRequest.setIpLocation(ipLocation);
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
            if (pushRequest.getForceRefresh().booleanValue()) {
                push.setStatus(PushStatusEnum.PENDING.name());
                save(push);
            }
            return convertToResponse(push);
        }

        String ip = IpUtils.getClientIp(request);
        String ipLocation = ipService.getIpLocation(ip);
        //
        PushEntity push = modelMapper.map(pushRequest, PushEntity.class);
        push.setUid(uidUtils.getUid());
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
        push.setUid(uidUtils.getUid());
        push.setChannel(pushRequest.getChannel());

        PushEntity savedPush = save(push);
        if (savedPush == null) {
            throw new RuntimeException("create push failed");
        }
        // 
        return convertToResponse(savedPush);
    }

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
            return doSave(push);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, push);
        }
    }

    @Override
    protected PushEntity doSave(PushEntity entity) {
        return pushRepository.save(entity);
    }

    @Override
    public PushEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, PushEntity entity) {
        try {
            Optional<PushEntity> latest = pushRepository.findByDeviceUid(entity.getDeviceUid());
            if (latest.isPresent()) {
                PushEntity latestEntity = latest.get();
                // 合并需要保留的数据
                return pushRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
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
            // 将ZonedDateTime转换为时间戳
            long currentTimeMillis = System.currentTimeMillis();
            // ZoneId systemZone = ZoneId.systemDefault(); // 获取系统默认时区
            long updatedAtMillis = push.getUpdatedAt().toInstant().toEpochMilli();
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

        Pageable pageable = request.getPageable();
        Specification<PushEntity> specification = PushSpecification.search(request, authService);
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
    public PushResponse convertToResponse(PushEntity entity) {
        return modelMapper.map(entity, PushResponse.class);
    }
    
    @Override
    protected Specification<PushEntity> createSpecification(PushRequest request) {
        return PushSpecification.search(request, authService);
    }

    @Override
    protected Page<PushEntity> executePageQuery(Specification<PushEntity> spec, Pageable pageable) {
        return pushRepository.findAll(spec, pageable);
    }

}
