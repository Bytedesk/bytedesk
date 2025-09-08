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
import org.springframework.util.Assert;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.push.service.CodeSendService;
import com.bytedesk.core.push.service.PushCoreService;
import com.bytedesk.core.push.service.ScanLoginService;
import com.bytedesk.core.rbac.auth.AuthRequest;
import com.bytedesk.core.rbac.auth.AuthTypeEnum;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class PushRestService extends BaseRestService<PushEntity, PushRequest, PushResponse> {

    private final PushRepository pushRepository;
    private final ModelMapper modelMapper;
    
    // 重构后的服务组件
    private final CodeSendService codeSendService;
    private final PushCoreService pushCoreService;
    private final ScanLoginService scanLoginService;

    /**
     * 发送验证码 - 委托给专门的服务处理
     */
    public Boolean sendCode(AuthRequest authRequest, HttpServletRequest request) {
        return codeSendService.sendCode(authRequest, request);
    }

    /**
     * 验证验证码 - 委托给核心服务处理
     */
    public Boolean validateCode(String receiver, String code, HttpServletRequest request) {
        return pushCoreService.validateCode(receiver, code, request);
    }

    /**
     * 扫码查询 - 委托给扫码登录服务处理
     */
    public PushResponse scanQuery(PushRequest pushRequest, HttpServletRequest request) {
        return scanLoginService.scanQuery(pushRequest, request);
    }

    /**
     * 扫码确认 - 委托给扫码登录服务处理
     */
    public PushResponse scan(PushRequest pushRequest, HttpServletRequest request) {
        return scanLoginService.scan(pushRequest, request);
    }

    /**
     * 扫码登录确认 - 委托给扫码登录服务处理
     */
    public PushResponse scanConfirm(PushRequest pushRequest, HttpServletRequest request) {
        return scanLoginService.scanConfirm(pushRequest, request);
    }

    /**
     * 创建推送记录 - 委托给核心服务处理
     */
    public PushResponse create(PushRequest pushRequest) {
        return pushCoreService.create(pushRequest);
    }

    // =============== 原有的查询和数据访问方法保持不变 ===============
    
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
                return pushRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    public List<PushEntity> findStatusPending() {
        return pushRepository.findByStatus(PushStatusEnum.PENDING.name());
    }

    /**
     * 自动过期验证码和扫码记录
     */
    @Async
    public void autoOutdateCode() {
        List<PushEntity> pendingPushes = findStatusPending();
        
        pendingPushes.forEach(push -> {
            long currentTimeMillis = System.currentTimeMillis();
            long updatedAtMillis = push.getUpdatedAt().toInstant().toEpochMilli();
            long diffInMilliseconds = Math.abs(currentTimeMillis - updatedAtMillis);
            long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMilliseconds);
            
            if (push.getType().equals(AuthTypeEnum.SCAN_LOGIN.name())) {
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

    // =============== BaseRestService 实现方法 ===============

    @Override
    public Page<PushResponse> queryByOrg(PushRequest request) {
        Pageable pageable = request.getPageable();
        Specification<PushEntity> specification = PushSpecification.search(request, authService);
        Page<PushEntity> page = pushRepository.findAll(specification, pageable);
        return page.map(push -> convertToResponse(push));
    }

    @Override
    public Page<PushResponse> queryByUser(PushRequest request) {
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public Optional<PushEntity> findByUid(String uid) {
        throw new UnsupportedOperationException("Unimplemented method 'findByUid'");
    }

    @Override
    public PushResponse update(PushRequest request) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void deleteByUid(String uid) {
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void delete(PushRequest entity) {
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
