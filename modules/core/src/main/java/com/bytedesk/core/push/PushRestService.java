package com.bytedesk.core.push;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final PushExpireCacheService pushExpireCacheService;

    @Override
    public Optional<PushEntity> findByUid(String uid) {
        return pushRepository.findByUid(uid);
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
     * 创建推送记录
     */
    @Override
    public PushResponse create(PushRequest pushRequest) {
        
        Assert.notNull(pushRequest, "PushRequest cannot be null");
        log.info("pushRequest {}", pushRequest.toString());
        
        PushEntity push = modelMapper.map(pushRequest, PushEntity.class);
        push.setUid(uidUtils.getUid());
        push.setChannel(pushRequest.getChannel());
        // 避免 ModelMapper 将 null 覆盖默认值
        if (pushRequest.getStatus() == null) {
            push.setStatus(PushStatusEnum.PENDING.name());
        }

        PushEntity savedPush = save(push);
        if (savedPush == null) {
            throw new RuntimeException("create push failed");
        }
        
        return convertToResponse(savedPush);
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
    protected PushEntity doSave(PushEntity entity) {
        PushEntity saved = pushRepository.save(entity);
        // 保存后根据状态写入/移除过期队列（Redis 异常不影响主流程）
        try {
            pushExpireCacheService.scheduleIfPending(saved);
        } catch (Exception e) {
            log.warn("push expire cache schedule failed, uid={}", saved != null ? saved.getUid() : null, e);
        }
        return saved;
    }

    @Override
    public void deleteByUid(String uid) {
        Assert.hasText(uid, "Uid cannot be null or empty");
        
        Optional<PushEntity> entity = findByUid(uid);
        if (entity.isPresent()) {
            pushRepository.delete(entity.get());
            try {
                pushExpireCacheService.remove(uid);
            } catch (Exception e) {
                log.warn("push expire cache remove failed, uid={}", uid, e);
            }
        }
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

    // =============== 定时任务方法 ===============

    /**
     * 自动过期验证码和扫码记录
     */
    @Async
    @Transactional
    public void autoOutdateCode() {
        // 1) Redis 驱动：只处理已到期 uid，避免每分钟全量查库
        try {
            processExpiredFromRedis();
        } catch (Exception e) {
            log.warn("processExpiredFromRedis failed", e);
            // Redis 故障时，退化为 DB 兜底（避免永不过期）
            try {
                processExpiredByDbBackfill();
            } catch (Exception ex) {
                log.warn("processExpiredByDbBackfill failed", ex);
            }
            return;
        }

        // 2) 低频 DB 兜底：避免 Redis flush/重启导致数据缺失
        boolean locked = false;
        try {
            locked = pushExpireCacheService.tryAcquireBackfillLock(TimeUnit.MINUTES.toSeconds(10));
        } catch (Exception e) {
            // ignore
        }
        if (locked) {
            try {
                processExpiredByDbBackfill();
            } catch (Exception e) {
                log.warn("processExpiredByDbBackfill failed", e);
            }
        }
    }

    protected void processExpiredFromRedis() {
        final int batchSize = 200;
        final int maxBatches = 10; // 单次最多 2000 条

        for (int i = 0; i < maxBatches; i++) {
            Set<String> expiredUids = pushExpireCacheService.pollExpiredUids(batchSize);
            if (expiredUids.isEmpty()) {
                return;
            }
            pushRepository.updateStatusByUidsAndStatus(
                    new ArrayList<>(expiredUids),
                    PushStatusEnum.PENDING.name(),
                    PushStatusEnum.EXPIRED.name());
        }
    }

    protected void processExpiredByDbBackfill() {
        // 与历史逻辑保持一致：扫码 3 分钟；其余验证码 15 分钟
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime scanBefore = now.minusMinutes(3);
        ZonedDateTime codeBefore = now.minusMinutes(15);

        int scanExpired = pushRepository.expireByTypeBefore(
                AuthTypeEnum.SCAN_LOGIN.name(),
                scanBefore,
                PushStatusEnum.PENDING.name(),
                PushStatusEnum.EXPIRED.name());
        int codeExpired = pushRepository.expireNotTypeBefore(
                AuthTypeEnum.SCAN_LOGIN.name(),
                codeBefore,
                PushStatusEnum.PENDING.name(),
                PushStatusEnum.EXPIRED.name());

        if (scanExpired + codeExpired > 0) {
            log.info("push backfill expired scan={}, code={}", scanExpired, codeExpired);
        }
    }


}
