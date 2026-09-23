/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-21 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-09-22 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms of the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push_android_device;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.push.service.PushProviderProperties;
import com.bytedesk.core.push_android.PushAndroidRecordService;
import com.bytedesk.core.push_android.PushAndroidTypeEnum;
import com.bytedesk.core.push_android_device.service.PushDeviceResult;
import com.bytedesk.core.push_android_device.service.PushDeviceRouter;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.permission.PermissionService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 设备推送绑定对账服务（由 push 包迁入 push_device 包，并切换到 PushDeviceEntity）
 *
 * <p>阿里云账号维度推送下，移动端 bindAccount 在阿里云侧完成真实绑定；
 * 本服务仅维护后端对账记录（PushDeviceEntity，type=ANDROID_ALIYUN），
 * 供「后台候选坐席是否有可用推送通道」判定与推送漏斗观测使用。
 *
 * <p>两组入口：
 * 1. 移动端 bind/unbind（契约不可变：POST /api/v1/push/device/bind|unbind）；
 * 2. 管理后台 query/create/update/delete/export（参考 tag/apns_push 全套 CRUD）。
 *
 * <p>见 docs/plans/2026-09-22-push-device-binding-entity-plan.md
 */
@Slf4j
@Service
@AllArgsConstructor
public class PushDeviceRestService
        extends BaseRestServiceWithExport<PushDeviceEntity, PushDeviceRequest, PushDeviceResponse, PushDeviceExcel> {

    private final PushDeviceRepository pushDeviceRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    private final PermissionService permissionService;

    private final PushDeviceRouter agentDevicePushRouter;

    /** PushDeviceEntity.type 取值：阿里云 Android 通道 */
    public static final String TYPE_ANDROID_ALIYUN = "ANDROID_ALIYUN";

    /** 设备平台缺省值 */
    public static final String DEVICE_ANDROID = "ANDROID";

    // ==================================================================================
    // 移动端对账入口（接口契约不可变）
    // ==================================================================================

    /**
     * 绑定当前登录用户的设备推送（幂等 upsert）。
     * 强制校验 account 归属：仅允许为当前登录用户绑定，防止越权。
     */
    @Transactional
    public PushDeviceEntity bind(PushDeviceRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException(I18Consts.I18N_PERMISSION_CREATE_DENIED);
        }

        // 归属校验：account 必须等于当前登录用户 uid
        if (StringUtils.hasText(request.getAccount())
                && !request.getAccount().equals(user.getUid())) {
            throw new RuntimeException(I18Consts.I18N_PERMISSION_CREATE_DENIED);
        }

        String type = StringUtils.hasText(request.getType()) ? request.getType() : TYPE_ANDROID_ALIYUN;
        if (!TYPE_ANDROID_ALIYUN.equals(type)) {
            // 首期仅支持阿里云 Android 对账，其它类型拒绝避免脏数据
            throw new IllegalArgumentException("unsupported push device type: " + type);
        }

        Optional<PushDeviceEntity> existing = pushDeviceRepository
                .findFirstByUserUidAndTypeAndDeletedFalse(user.getUid(), type);

        PushDeviceEntity entity;
        if (existing.isPresent()) {
            entity = existing.get();
        } else {
            entity = PushDeviceEntity.builder()
                    .uid(uidUtils.getUid())
                    .build();
            entity.setUserUid(user.getUid());
            entity.setType(type);
        }

        entity.setProvider(normalizeProvider(request.getProvider()));
        // deviceId 仅作对账/排障记录（非推送寻址主键，阿里云侧按账号推送）
        if (StringUtils.hasText(request.getDeviceId())) {
            entity.setDeviceId(request.getDeviceId());
        }
        // account 与 BaseEntity.userUid 写时强一致
        entity.setAccount(user.getUid());
        entity.setDevice(StringUtils.hasText(request.getDevice()) ? request.getDevice() : DEVICE_ANDROID);
        if (StringUtils.hasText(request.getChannel())) {
            entity.setChannel(request.getChannel());
        }
        entity.setOrgUid(StringUtils.hasText(request.getOrgUid()) ? request.getOrgUid() : user.getOrgUid());
        entity.setDeleted(false);

        PushDeviceEntity saved = pushDeviceRepository.save(entity);
        log.info("Push device bound, userUid={}, type={}, provider={}, deviceId={}",
                user.getUid(), type, saved.getProvider(), saved.getDeviceId());
        return saved;
    }

    /**
     * 解绑当前登录用户的设备推送（软删除对账记录）。
     * 真实解绑由移动端 unbindAccount 在阿里云侧完成。
     */
    @Transactional
    public void unbind(PushDeviceRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException(I18Consts.I18N_PERMISSION_DELETE_DENIED);
        }

        String type = StringUtils.hasText(request.getType()) ? request.getType() : TYPE_ANDROID_ALIYUN;

        Optional<PushDeviceEntity> existing = pushDeviceRepository
                .findFirstByUserUidAndTypeAndDeletedFalse(user.getUid(), type);
        if (existing.isEmpty()) {
            return;
        }

        PushDeviceEntity entity = existing.get();
        entity.setDeleted(true);
        pushDeviceRepository.save(entity);
        log.info("Push device unbound, userUid={}, type={}", user.getUid(), type);
    }

    /** 当前用户是否具备某类型推送绑定（候选池判定用） */
    @Transactional(readOnly = true)
    public boolean isUserBound(String userUid, String type) {
        if (!StringUtils.hasText(userUid) || !StringUtils.hasText(type)) {
            return false;
        }
        return pushDeviceRepository.existsByUserUidAndTypeAndDeletedFalse(userUid, type);
    }

    /** 供应商标识归一（供记录/查询） */
    public String normalizeProvider(String provider) {
        if (StringUtils.hasText(provider)) {
            return provider;
        }
        return PushProviderProperties.PROVIDER_ALIYUN;
    }

    /**
     * 测试推送：向指定绑定记录的账号（userUid）发送自定义测试内容。
     *
     * <p>供管理后台 PushDeviceTable「测试」按钮使用，复用业务通知链路，
     * 验证「凭据配置 + 账号绑定」端到端可达性。
     *
     * <p>供应商选择：按绑定记录自身的 provider 字段走对应通道
     * （Android·阿里云绑定走 aliyun，不落全局 bytedesk.push.provider 开关——
     * 否则全局为 apns-direct 时 Android 用户会误报 no apns token bound for user）。
     * 指定通道凭据未配置时返回明确错误；受理≠到达，到达率以供应商控制台为准。
     *
     * <p>注意：不加事务——供应商 HTTP 调用不得被数据库事务包住，
     * Android 投递记录由 PushAndroidRecordService 以 REQUIRES_NEW 短事务独立保存
     * （source=TEST 显式标记测试类型）。
     */
    public PushDeviceResult test(PushDeviceRequest request) {
        if (!StringUtils.hasText(request.getUid())) {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }

        PushDeviceEntity entity = pushDeviceRepository.findByUid(request.getUid())
                .filter(e -> !e.isDeleted())
                .orElseThrow(() -> new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND));

        // 接收者 = 绑定账号（与 userUid 写时强一致）
        String receiver = StringUtils.hasText(entity.getAccount()) ? entity.getAccount() : entity.getUserUid();
        String provider = normalizeProvider(entity.getProvider());
        String title = StringUtils.hasText(request.getTitle()) ? request.getTitle() : "Test Push";
        String body = StringUtils.hasText(request.getContent()) ? request.getContent() : "This is a test push from bytedesk admin.";

        log.info("Push device test, uid={}, receiver={}, provider={}", request.getUid(), receiver, provider);
        // extras 仅携带受控 source 标记：记录 type=TEST，移动端点击通知仅打开 App，不路由到具体业务页
        return agentDevicePushRouter.pushToUserViaProvider(provider, receiver, title, body,
                java.util.Map.of(PushAndroidRecordService.EXTRA_KEY_SOURCE, PushAndroidTypeEnum.TEST.name()));
    }

    // ==================================================================================
    // 管理后台 CRUD（参考 TagRestService）
    // ==================================================================================

    @Override
    public Page<PushDeviceEntity> queryByOrgEntity(PushDeviceRequest request) {
        Pageable pageable = request.getPageable();
        Specification<PushDeviceEntity> specs = PushDeviceSpecification.search(request, authService);
        return pushDeviceRepository.findAll(specs, pageable);
    }

    @Override
    public Page<PushDeviceResponse> queryByOrg(PushDeviceRequest request) {
        Page<PushDeviceEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<PushDeviceResponse> queryByUser(PushDeviceRequest request) {
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        return queryByOrg(request);
    }

    @Override
    public Optional<PushDeviceEntity> findByUid(String uid) {
        return pushDeviceRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return pushDeviceRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public PushDeviceResponse create(PushDeviceRequest request) {
        // 幂等：uid 已存在直接返回
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }

        // 确定数据层级
        String level = request.getLevel();
        if (!StringUtils.hasText(level)) {
            level = LevelEnum.ORGANIZATION.name();
            request.setLevel(level);
        }

        // 检查用户是否有权限创建该层级的数据
        if (!permissionService.canCreateAtLevel(PushDevicePermissions.MODULE_NAME, level)) {
            throw new RuntimeException(I18Consts.I18N_PERMISSION_CREATE_DENIED);
        }

        // 绑定账号：优先 account，其次 userUid（account 与 userUid 语义一致）
        String account = StringUtils.hasText(request.getAccount()) ? request.getAccount() : request.getUserUid();

        String type = StringUtils.hasText(request.getType()) ? request.getType() : TYPE_ANDROID_ALIYUN;

        // 幂等 upsert：同账号+类型已存在则更新（与移动端 bind 上报的记录收敛到同一行）
        Optional<PushDeviceEntity> existing = StringUtils.hasText(account)
                ? pushDeviceRepository.findFirstByUserUidAndTypeAndDeletedFalse(account, type)
                : Optional.empty();

        PushDeviceEntity entity;
        if (existing.isPresent()) {
            entity = existing.get();
        } else {
            entity = modelMapper.map(request, PushDeviceEntity.class);
            if (!StringUtils.hasText(request.getUid())) {
                entity.setUid(uidUtils.getUid());
            }
            entity.setUserUid(account);
            entity.setType(type);
        }

        if (StringUtils.hasText(account)) {
            entity.setAccount(account);
        }
        entity.setProvider(normalizeProvider(request.getProvider()));
        if (StringUtils.hasText(request.getDeviceId())) {
            entity.setDeviceId(request.getDeviceId());
        }
        entity.setDevice(StringUtils.hasText(request.getDevice()) ? request.getDevice() : DEVICE_ANDROID);
        entity.setOrgUid(request.getOrgUid());
        entity.setLevel(level);

        PushDeviceEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException(I18Consts.I18N_CREATE_FAILED);
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public PushDeviceResponse update(PushDeviceRequest request) {
        Optional<PushDeviceEntity> optional = pushDeviceRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            PushDeviceEntity entity = optional.get();

            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(PushDevicePermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException(I18Consts.I18N_PERMISSION_UPDATE_DENIED);
            }

            modelMapper.map(request, entity);
            // account 与 userUid 保持强一致
            if (StringUtils.hasText(request.getAccount())) {
                entity.setAccount(request.getAccount());
                entity.setUserUid(request.getAccount());
            }
            entity.setProvider(normalizeProvider(request.getProvider()));

            PushDeviceEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException(I18Consts.I18N_UPDATE_FAILED);
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @Override
    protected PushDeviceEntity doSave(PushDeviceEntity entity) {
        return pushDeviceRepository.save(entity);
    }

    @Override
    public PushDeviceEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, PushDeviceEntity entity) {
        try {
            Optional<PushDeviceEntity> latest = pushDeviceRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                PushDeviceEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setProvider(entity.getProvider());
                latestEntity.setDeviceId(entity.getDeviceId());
                latestEntity.setAccount(entity.getAccount());
                latestEntity.setDevice(entity.getDevice());
                latestEntity.setChannel(entity.getChannel());
                return pushDeviceRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Transactional
    @Override
    public void deleteByUid(String uid) {
        Optional<PushDeviceEntity> optional = pushDeviceRepository.findByUid(uid);
        if (optional.isPresent()) {
            PushDeviceEntity entity = optional.get();

            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(PushDevicePermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException(I18Consts.I18N_PERMISSION_DELETE_DENIED);
            }

            entity.setDeleted(true);
            save(entity);
        } else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @Override
    public void delete(PushDeviceRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public PushDeviceResponse convertToResponse(PushDeviceEntity entity) {
        return modelMapper.map(entity, PushDeviceResponse.class);
    }

    @Override
    public PushDeviceExcel convertToExcel(PushDeviceEntity entity) {
        return modelMapper.map(entity, PushDeviceExcel.class);
    }

    @Override
    protected Specification<PushDeviceEntity> createSpecification(PushDeviceRequest request) {
        return PushDeviceSpecification.search(request, authService);
    }

    @Override
    protected Page<PushDeviceEntity> executePageQuery(Specification<PushDeviceEntity> spec, Pageable pageable) {
        return pushDeviceRepository.findAll(spec, pageable);
    }
}
