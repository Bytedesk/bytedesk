package com.bytedesk.core.push;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthTypeEnum;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class PushRestService extends BaseRestService<PushEntity, PushRequest, PushResponse> {

    private final PushRepository pushRepository;
    private final ModelMapper modelMapper;
    private final UidUtils uidUtils;

    /**
     * 创建推送记录
     */
    @Override
    public PushResponse create(PushRequest pushRequest) {
        
        Assert.notNull(pushRequest, "PushRequest cannot be null");
        log.info("pushRequest {}", pushRequest.toString());
        
        PushEntity push = modelMapper.map(pushRequest, PushEntity.class);
        push.setUid(uidUtils.getUid());
        push.setChannel(pushRequest.getChannel());

        PushEntity savedPush = save(push);
        if (savedPush == null) {
            throw new RuntimeException("create push failed");
        }
        
        return convertToResponse(savedPush);
    }

    /**
     * 根据状态、接收者和内容查找
     */
    public Optional<PushEntity> findByStatusAndReceiverAndContent(PushStatusEnum status, String receiver,
            String content) {
        return pushRepository.findByStatusAndReceiverAndContent(status.name(), receiver, content);
    }

    /**
     * 根据设备UID查找
     */
    public Optional<PushEntity> findByDeviceUid(String deviceUid, String status, String type) {
        return pushRepository.findTopByDeviceUidAndStatusAndTypeOrderByUpdatedAtDesc(deviceUid, status, type);
    }

    /**
     * 检查是否存在指定状态、类型和接收者的记录
     */
    public Boolean existsByStatusAndTypeAndReceiver(String status, String type, String receiver) {
        return pushRepository.existsByStatusAndTypeAndReceiver(status, type, receiver);
    }

    /**
     * 查找待处理状态的记录
     */
    public List<PushEntity> findStatusPending() {
        return pushRepository.findByStatus(PushStatusEnum.PENDING.name());
    }

    @Override
    protected PushEntity doSave(PushEntity entity) {
        return pushRepository.save(entity);
    }

    @Override
    public PushEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, PushEntity entity) {
        try {
            Optional<PushEntity> latest = pushRepository.findTopByDeviceUidAndStatusAndTypeOrderByUpdatedAtDesc(entity.getDeviceUid(), entity.getStatus(), entity.getType());
            if (latest.isPresent()) {
                PushEntity latestEntity = latest.get();
                return pushRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    // =============== 定时任务方法 ===============

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
        return pushRepository.findByUid(uid);
    }

    @Override
    public PushResponse update(PushRequest request) {
        Assert.notNull(request, "PushRequest cannot be null");
        Assert.hasText(request.getUid(), "PushRequest uid cannot be null or empty");
        
        Optional<PushEntity> existingEntity = findByUid(request.getUid());
        if (existingEntity.isEmpty()) {
            throw new RuntimeException("Push entity not found with uid: " + request.getUid());
        }
        
        PushEntity push = existingEntity.get();
        // 更新允许修改的字段
        if (request.getStatus() != null) {
            push.setStatus(request.getStatus());
        }
        if (request.getContent() != null) {
            push.setContent(request.getContent());
        }
        
        PushEntity savedPush = save(push);
        return convertToResponse(savedPush);
    }

    @Override
    public void deleteByUid(String uid) {
        Assert.hasText(uid, "Uid cannot be null or empty");
        
        Optional<PushEntity> entity = findByUid(uid);
        if (entity.isPresent()) {
            pushRepository.delete(entity.get());
        }
    }

    @Override
    public void delete(PushRequest entity) {
        Assert.notNull(entity, "PushRequest cannot be null");
        Assert.hasText(entity.getUid(), "PushRequest uid cannot be null or empty");
        
        deleteByUid(entity.getUid());
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
