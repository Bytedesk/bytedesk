/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.webrtc.participant;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.permission.PermissionService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.webrtc.room.RoomEntity;
import com.bytedesk.webrtc.room.RoomRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ParticipantRestService extends BaseRestServiceWithExport<ParticipantEntity, ParticipantRequest, ParticipantResponse, ParticipantExcel> {

    private final ParticipantRepository participantRepository;

    private final RoomRepository roomRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;
    
    private final PermissionService permissionService;
    
    @Override
    public Page<ParticipantEntity> queryByOrgEntity(ParticipantRequest request) {
        Pageable pageable = request.getPageable();
        Specification<ParticipantEntity> specs = ParticipantSpecification.search(request, authService);
        return participantRepository.findAll(specs, pageable);
    }

    @Override
    public Page<ParticipantResponse> queryByOrg(ParticipantRequest request) {
        Page<ParticipantEntity> participantPage = queryByOrgEntity(request);
        return participantPage.map(this::convertToResponse);
    }

    @Override
    public Page<ParticipantResponse> queryByUser(ParticipantRequest request) {
        UserEntity user = authService.getUser();
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    @Cacheable(value = "participant", key = "#uid", unless="#result==null")
    @Override
    public Optional<ParticipantEntity> findByUid(String uid) {
        return participantRepository.findByUid(uid);
    }

    @Cacheable(value = "participant", key = "#name + '_' + #orgUid + '_' + #type", unless="#result==null")
    public Optional<ParticipantEntity> findByNameAndOrgUidAndType(String name, String orgUid, String type) {
        return participantRepository.findByNameAndOrgUidAndTypeAndDeletedFalse(name, orgUid, type);
    }

    public Boolean existsByUid(String uid) {
        return participantRepository.existsByUid(uid);
    }

    @Transactional
    @Override
    public ParticipantResponse create(ParticipantRequest request) {
        return createInternal(request, false);
    }

    /**
     * 记录加入会议（type=MEETING）：
     * 先关闭该用户在该会议室的遗留活跃记录（上次异常退出未 leave），
     * 再新建一条参会记录（每次加入一条，类似 CDR）。
     * 幂等：若已有在会记录且 janusParticipantId 未变，则直接返回（仅刷新时间戳）。
     */
    @Transactional
    public ParticipantResponse joinMeeting(ParticipantRequest request) {
        UserEntity user = authService.getUser();
        if (user == null || !StringUtils.hasText(user.getUid())) {
            throw new RuntimeException("未登录，无法记录参会信息");
        }
        if (!StringUtils.hasText(request.getRoomUid())) {
            throw new RuntimeException("会议室 roomUid 不能为空");
        }

        String roomUid = request.getRoomUid();

        // 关闭遗留活跃记录（进程崩溃/断网未 leave 的场景），时长按当前时间结算
        participantRepository
                .findByRoomUidAndUserUidAndStatusAndDeletedFalse(roomUid, user.getUid(),
                        ParticipantStatusEnum.JOINED.name())
                .ifPresent(this::closeParticipantRecord);

        // 新建参会记录
        String displayName = StringUtils.hasText(request.getName()) ? request.getName()
                : (StringUtils.hasText(user.getNickname()) ? user.getNickname() : user.getUid());

        // 主持人判定：前端未显式指定时，房间创建者本人加入即标记为主持人
        Boolean host = request.getHost();
        if (host == null) {
            RoomEntity room = roomRepository.findByUid(roomUid).orElse(null);
            host = room != null && user.getUid().equals(room.getUserUid());
        }

        ParticipantEntity entity = ParticipantEntity.builder()
                .uid(uidUtils.getUid())
                .name(displayName)
                .type(ParticipantTypeEnum.MEETING.name())
                .roomUid(roomUid)
                .janusParticipantId(request.getJanusParticipantId())
                .joinedAt(ZonedDateTime.now())
                .status(ParticipantStatusEnum.JOINED.name())
                .host(Boolean.TRUE.equals(host))
                .orgUid(StringUtils.hasText(request.getOrgUid()) ? request.getOrgUid() : user.getOrgUid())
                .userUid(user.getUid())
                .level(LevelEnum.ORGANIZATION.name())
                .build();

        ParticipantEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException(I18Consts.I18N_CREATE_FAILED);
        }
        return convertToResponse(savedEntity);
    }

    /**
     * 记录离开会议：按 uid 或（roomUid + 当前登录用户）定位在会记录，
     * 写入 leftAt、计算 duration、置状态 LEFT。幂等：重复 leave 无在会记录时直接返回成功。
     */
    @Transactional
    public ParticipantResponse leaveMeeting(ParticipantRequest request) {
        UserEntity user = authService.getUser();
        if (user == null || !StringUtils.hasText(user.getUid())) {
            throw new RuntimeException("未登录，无法记录离会信息");
        }

        Optional<ParticipantEntity> optional = Optional.empty();
        if (StringUtils.hasText(request.getUid())) {
            optional = participantRepository.findByUid(request.getUid());
        }
        if (optional.isEmpty() && StringUtils.hasText(request.getRoomUid())) {
            optional = participantRepository.findByRoomUidAndUserUidAndStatusAndDeletedFalse(request.getRoomUid(),
                    user.getUid(), ParticipantStatusEnum.JOINED.name());
        }
        if (optional.isEmpty()) {
            // 幂等：无在会记录视为已离会，直接成功
            return ParticipantResponse.builder().status(ParticipantStatusEnum.LEFT.name()).build();
        }

        ParticipantEntity entity = optional.get();
        // janusParticipantId 可选回填（加入时前端未传、离会时才拿到）
        if (entity.getJanusParticipantId() == null && request.getJanusParticipantId() != null) {
            entity.setJanusParticipantId(request.getJanusParticipantId());
        }
        closeParticipantRecord(entity);
        return convertToResponse(save(entity));
    }

    /** 结算一条参会记录：写入 leftAt/duration 并置状态 LEFT */
    private void closeParticipantRecord(ParticipantEntity entity) {
        if (ParticipantStatusEnum.LEFT.name().equals(entity.getStatus())) {
            return;
        }
        ZonedDateTime leftAt = ZonedDateTime.now();
        ZonedDateTime joinedAt = entity.getJoinedAt() != null ? entity.getJoinedAt() : entity.getCreatedAt();
        long durationSeconds = joinedAt != null ? Duration.between(joinedAt, leftAt).getSeconds() : 0L;
        entity.setLeftAt(leftAt);
        entity.setDuration(Math.max(durationSeconds, 0L));
        entity.setStatus(ParticipantStatusEnum.LEFT.name());
    }

    /** 某会议室全部参会记录（按加入时间倒序） */
    public List<ParticipantResponse> queryByRoomUid(String roomUid) {
        return participantRepository.findByRoomUidAndDeletedFalse(roomUid).stream()
                .sorted((a, b) -> b.getJoinedAt() != null && a.getJoinedAt() != null
                        ? b.getJoinedAt().compareTo(a.getJoinedAt())
                        : 0)
                .map(this::convertToResponse)
                .toList();
    }

    /** 某会议室当前在会参与者 */
    public List<ParticipantResponse> queryOnlineByRoomUid(String roomUid) {
        return participantRepository
                .findByRoomUidAndStatusAndDeletedFalse(roomUid, ParticipantStatusEnum.JOINED.name())
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // ===== 通话参与者（type=AUDIO_SERVICE/VIDEO_SERVICE/MEMBER_CALL，2026-09-29 规划 C 线） =====
    // 后端内部驱动（WebrtcServiceImplVip 状态机埋点），不依赖前端上报；均幂等。

    /**
     * 记录加入通话（访客客服/同事通话）：
     * 先关闭该用户在该通话的遗留活跃记录（上次异常退出未结算），再新建一条（类似 CDR）。
     * 身份/组织由调用方传入（服务端状态机持有 WebrtcEntity 上下文）。
     */
    @Transactional
    public ParticipantResponse recordCallJoin(String callUid, String type, String roomUid,
            String actorUid, String displayName, Boolean host, String orgUid) {
        if (!StringUtils.hasText(callUid) || !StringUtils.hasText(actorUid)) {
            throw new RuntimeException("callUid/actorUid 不能为空");
        }

        // 关闭遗留活跃记录（进程崩溃/断网未离开的场景），时长按当前时间结算
        participantRepository
                .findByCallUidAndUserUidAndStatusAndDeletedFalse(callUid, actorUid,
                        ParticipantStatusEnum.JOINED.name())
                .ifPresent(this::closeParticipantRecord);

        ParticipantEntity entity = ParticipantEntity.builder()
                .uid(uidUtils.getUid())
                .name(StringUtils.hasText(displayName) ? displayName : actorUid)
                .type(StringUtils.hasText(type) ? type : ParticipantTypeEnum.MEMBER_CALL.name())
                .callUid(callUid)
                .roomUid(roomUid)
                .joinedAt(ZonedDateTime.now())
                .status(ParticipantStatusEnum.JOINED.name())
                .host(Boolean.TRUE.equals(host))
                .orgUid(orgUid)
                .userUid(actorUid)
                .level(LevelEnum.ORGANIZATION.name())
                .build();

        ParticipantEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException(I18Consts.I18N_CREATE_FAILED);
        }
        return convertToResponse(savedEntity);
    }

    /**
     * 记录离开通话：按（callUid + 参与者）定位在会记录，写入 leftAt/duration 并置 LEFT。
     * 幂等：无在会记录时直接返回成功。
     */
    @Transactional
    public ParticipantResponse recordCallLeave(String callUid, String actorUid) {
        if (!StringUtils.hasText(callUid) || !StringUtils.hasText(actorUid)) {
            throw new RuntimeException("callUid/actorUid 不能为空");
        }
        Optional<ParticipantEntity> optional = participantRepository
                .findByCallUidAndUserUidAndStatusAndDeletedFalse(callUid, actorUid,
                        ParticipantStatusEnum.JOINED.name());
        if (optional.isEmpty()) {
            // 幂等：无在会记录视为已离开
            return ParticipantResponse.builder().status(ParticipantStatusEnum.LEFT.name()).build();
        }
        ParticipantEntity entity = optional.get();
        closeParticipantRecord(entity);
        return convertToResponse(save(entity));
    }

    /**
     * 终态批量结算：关闭该通话全部在会记录（hangup/reject/cancel 终态时调用）。
     * 未接通（reject/cancel）场景无在会记录，天然空转。
     */
    @Transactional
    public void closeCallRecords(String callUid) {
        if (!StringUtils.hasText(callUid)) {
            return;
        }
        participantRepository
                .findByCallUidAndStatusAndDeletedFalse(callUid, ParticipantStatusEnum.JOINED.name())
                .forEach(entity -> {
                    closeParticipantRecord(entity);
                    save(entity);
                });
    }

    /** 某通话全部参会记录（按加入时间倒序） */
    public List<ParticipantResponse> queryByCallUid(String callUid) {
        if (!StringUtils.hasText(callUid)) {
            return List.of();
        }
        return participantRepository.findByCallUidAndDeletedFalse(callUid).stream()
                .sorted((a, b) -> b.getJoinedAt() != null && a.getJoinedAt() != null
                        ? b.getJoinedAt().compareTo(a.getJoinedAt())
                        : 0)
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional
    public ParticipantResponse createSystemParticipant(ParticipantRequest request) {
        return createInternal(request, true);
    }

    private ParticipantResponse createInternal(ParticipantRequest request, boolean skipPermissionCheck) {
        // 判断是否已经存在
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        // 检查name+orgUid+type是否已经存在
        if (StringUtils.hasText(request.getName()) && StringUtils.hasText(request.getOrgUid()) && StringUtils.hasText(request.getType())) {
            Optional<ParticipantEntity> participant = findByNameAndOrgUidAndType(request.getName(), request.getOrgUid(), request.getType());
            if (participant.isPresent()) {
                return convertToResponse(participant.get());
            }
        }
        
        // 获取用户信息
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        
        // 确定数据层级
        String level = request.getLevel();
        if (!StringUtils.hasText(level)) {
            level = LevelEnum.ORGANIZATION.name();
            request.setLevel(level);
        }
        
        // 检查用户是否有权限创建该层级的数据
        if (!skipPermissionCheck && !permissionService.canCreateAtLevel(ParticipantPermissions.MODULE_NAME, level)) {
            throw new RuntimeException(I18Consts.I18N_PERMISSION_CREATE_DENIED);
        }
        
        // 
        ParticipantEntity entity = modelMapper.map(request, ParticipantEntity.class);
        if (!StringUtils.hasText(request.getUid())) {
            entity.setUid(uidUtils.getUid());
        }
        // 
        ParticipantEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException(I18Consts.I18N_CREATE_FAILED);
        }
        return convertToResponse(savedEntity);
    }

    @Transactional
    @Override
    public ParticipantResponse update(ParticipantRequest request) {
        Optional<ParticipantEntity> optional = participantRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ParticipantEntity entity = optional.get();
            
            // 检查用户是否有权限更新该实体
            if (!permissionService.hasEntityPermission(ParticipantPermissions.MODULE_NAME, "UPDATE", entity)) {
                throw new RuntimeException(I18Consts.I18N_PERMISSION_UPDATE_DENIED);
            }
            
            modelMapper.map(request, entity);
            //
            ParticipantEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException(I18Consts.I18N_UPDATE_FAILED);
            }
            return convertToResponse(savedEntity);
        }
        else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @Override
    protected ParticipantEntity doSave(ParticipantEntity entity) {
        return participantRepository.save(entity);
    }

    @Override
    public ParticipantEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, ParticipantEntity entity) {
        try {
            Optional<ParticipantEntity> latest = participantRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ParticipantEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                // latestEntity.setOrder(entity.getOrder());
                // latestEntity.setDeleted(entity.isDeleted());
                return participantRepository.save(latestEntity);
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
        Optional<ParticipantEntity> optional = participantRepository.findByUid(uid);
        if (optional.isPresent()) {
            ParticipantEntity entity = optional.get();
            
            // 检查用户是否有权限删除该实体
            if (!permissionService.hasEntityPermission(ParticipantPermissions.MODULE_NAME, "DELETE", entity)) {
                throw new RuntimeException(I18Consts.I18N_PERMISSION_DELETE_DENIED);
            }
            
            entity.setDeleted(true);
            save(entity);
        }
        else {
            throw new RuntimeException(I18Consts.I18N_RESOURCE_NOT_FOUND);
        }
    }

    @Override
    public void delete(ParticipantRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public ParticipantResponse convertToResponse(ParticipantEntity entity) {
        return modelMapper.map(entity, ParticipantResponse.class);
    }

    @Override
    public ParticipantExcel convertToExcel(ParticipantEntity entity) {
        return modelMapper.map(entity, ParticipantExcel.class);
    }

    @Override
    protected Specification<ParticipantEntity> createSpecification(ParticipantRequest request) {
        return ParticipantSpecification.search(request, authService);
    }

    @Override
    protected Page<ParticipantEntity> executePageQuery(Specification<ParticipantEntity> spec, Pageable pageable) {
        return participantRepository.findAll(spec, pageable);
    }
    
    public void initParticipants(String orgUid) {
        // log.info("initParticipantParticipant");
        // for (String participant : ParticipantInitData.getAllParticipants()) {
        //     ParticipantRequest participantRequest = ParticipantRequest.builder()
        //             .uid(Utils.formatUid(orgUid, participant))
        //             .name(participant)
        //             .order(0)
        //             .type(ParticipantTypeEnum.THREAD.name())
        //             .level(LevelEnum.ORGANIZATION.name())
        //             .platform(BytedeskConsts.PLATFORM_BYTEDESK)
        //             .orgUid(orgUid)
        //             .build();
        //     createSystemParticipant(participantRequest);
        // }
    }

    
    
}
