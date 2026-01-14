/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 20:17:54
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRequest;
import com.bytedesk.core.thread.ThreadResponseSimple;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.enums.ThreadProcessStatusEnum;
import com.bytedesk.core.thread.event.ThreadAcceptEvent;
import com.bytedesk.core.thread.event.ThreadAddTopicEvent;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.ConvertUtils;
import com.bytedesk.service.agent.event.AgentUpdateStatusEvent;
import com.bytedesk.service.agent_settings.AgentSettingsRestService;
import com.bytedesk.service.constant.I18ServiceConsts;
import com.bytedesk.service.utils.ServiceConvertUtils;
import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.member.MemberRestService;

import org.modelmapper.ModelMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class AgentRestService extends BaseRestService<AgentEntity, AgentRequest, AgentResponse> {

    private final AgentRepository agentRepository;

    private final UidUtils uidUtils;

    private final MemberRestService memberRestService;

    private final UserService userService;

    private final AuthService authService;

    private final BytedeskEventPublisher bytedeskEventPublisher;

    private final ThreadRestService threadRestService;

    private final AgentSettingsRestService agentSettingsRestService;

    private final ModelMapper modelMapper;

    @Override
    protected Specification<AgentEntity> createSpecification(AgentRequest request) {
        return AgentSpecification.search(request, authService);
    }

    @Override
    protected Page<AgentEntity> executePageQuery(Specification<AgentEntity> spec, Pageable pageable) {
        return agentRepository.findAll(spec, pageable);
    }

    public AgentResponse query(AgentRequest request) {
        UserEntity user = authService.getUser();
        Optional<AgentEntity> agentOptional = findByUserUidAndOrgUid(user.getUid(), request.getOrgUid());
        if (!agentOptional.isPresent()) {
            return null;
        }
        return convertToResponse(agentOptional.get());
    }

    /**
     * 根据指定 userUid 查询单个客服信息
     *
     * - 若传入 orgUid：按 userUid + orgUid 精确查询
     * - 未传 orgUid：仅按 userUid 查询
     */
    public AgentResponse queryByUserUid(AgentRequest request) {
        if (!StringUtils.hasText(request.getUserUid())) {
            throw new RuntimeException("userUid is required");
        }

        Optional<AgentEntity> agentOptional = StringUtils.hasText(request.getOrgUid())
                ? findByUserUidAndOrgUid(request.getUserUid(), request.getOrgUid())
                : findByUserUid(request.getUserUid());

        if (!agentOptional.isPresent()) {
            return null;
        }
        return convertToResponse(agentOptional.get());
    }

    @Transactional
    public AgentResponse create(AgentRequest request) {
        //
        if (StringUtils.hasText(request.getUid()) && existsByUid(request.getUid())) {
            return convertToResponse(findByUid(request.getUid()).get());
        }
        //
        Optional<MemberEntity> memberOptional = memberRestService.findByUid(request.getMemberUid());
        if (!memberOptional.isPresent() || memberOptional.get().getUser() == null) {
            throw new RuntimeException("member uid: " + request.getMemberUid() + " not found");
        }
        // 一个user仅能绑定一个坐席
        if (existsByUserUidAndOrgUid(memberOptional.get().getUser().getUid(), request.getOrgUid())) {
            throw new RuntimeException(I18ServiceConsts.I18N_AGENT_EXISTS);
        }
        //
        MemberEntity member = memberOptional.get();
        UserEntity user = member.getUser();
        // 分配组织维度角色依赖 user.currentOrganization
        userService.ensureCurrentOrganization(user, request.getOrgUid());
        userService.addRoleAgent(user);
        //
        AgentEntity agent = AgentEntity.builder()
                .nickname(request.getNickname())
                .agentNo(request.getAgentNo())
                .email(request.getEmail())
                .mobile(request.getMobile())
                .build();
        if (StringUtils.hasText(request.getUid())) {
            agent.setUid(request.getUid());
        } else {
            agent.setUid(uidUtils.getUid());
        }
        agent.setOrgUid(request.getOrgUid());
        agent.setMember(member);
        agent.setUserUid(user.getUid());
        //
        // 设置客服配置：如果指定了 settingsUid，使用指定的配置；否则使用默认配置
        if (StringUtils.hasText(request.getSettingsUid())) {
            Optional<com.bytedesk.service.agent_settings.AgentSettingsEntity> settingsOptional = agentSettingsRestService
                    .findByUid(request.getSettingsUid());
            if (settingsOptional.isPresent()) {
                agent.setSettings(settingsOptional.get());
            } else {
                agent.setSettings(agentSettingsRestService.getOrCreateDefault(request.getOrgUid()));
            }
        } else {
            agent.setSettings(agentSettingsRestService.getOrCreateDefault(request.getOrgUid()));
        }
        //
        // 保存Agent并检查返回值
        AgentEntity savedAgent = save(agent);
        if (savedAgent == null) {
            throw new RuntimeException("Failed to save agent.");
        }
        // 返回保存后的Agent
        return convertToResponse(savedAgent);
    }

    public void createFromMember(String mobile, String orgUid) {
        //
        Optional<MemberEntity> memberOptional = memberRestService.findByMobileAndOrgUid(mobile, orgUid);
        if (!memberOptional.isPresent()) {
            return;
        }
        MemberEntity member = memberOptional.get();
        // 创建默认客服
        AgentRequest agentRequest = AgentRequest.builder()
                .uid(uidUtils.getUid())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .mobile(member.getMobile())
                .memberUid(member.getUid())
                .orgUid(orgUid)
                .build();
        create(agentRequest);
    }

    public void createFromMemberByEmail(String email, String orgUid) {
        Optional<MemberEntity> memberOptional = memberRestService.findByEmailAndOrgUid(email, orgUid);
        if (!memberOptional.isPresent()) {
            return;
        }
        MemberEntity member = memberOptional.get();
        AgentRequest agentRequest = AgentRequest.builder()
                .uid(uidUtils.getUid())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .mobile(member.getMobile())
                .memberUid(member.getUid())
                .orgUid(orgUid)
                .build();
        create(agentRequest);
    }

    @Transactional
    public AgentResponse update(AgentRequest request) {
        //
        Optional<AgentEntity> agentOptional = findByUid(request.getUid());
        if (!agentOptional.isPresent()) {
            // 如果找不到对应的Agent，则返回null
            throw new RuntimeException("null agent found with uid: " + request.getUid());
        }
        //
        AgentEntity agent = agentOptional.get();
        // 更新Agent的信息
        // modelMapper.map(agentRequest, agent); // 需要排除 connected 字段，否则会改变真实连接状态
        agent.setNickname(request.getNickname());
        agent.setAgentNo(request.getAgentNo());
        agent.setAvatar(request.getAvatar());
        agent.setMobile(request.getMobile());
        agent.setEmail(request.getEmail());
        agent.setDescription(request.getDescription());
        // agent.setStatus(request.getStatus()); // 更新接待状态
        // agent.setMaxThreadCount(request.getMaxThreadCount());
        // agent.setTimeoutRemindTime(request.getTimeoutRemindTime());
        // 暂不允许修改绑定成员
        // agent.setMember(memberOptional.get());
        // agent.setUserUid(memberOptional.get().getUser().getUid());
        //
        // 更新客服配置
        if (StringUtils.hasText(request.getSettingsUid())) {
            Optional<com.bytedesk.service.agent_settings.AgentSettingsEntity> settingsOptional = agentSettingsRestService
                    .findByUid(request.getSettingsUid());
            if (settingsOptional.isPresent()) {
                agent.setSettings(settingsOptional.get());
            }
        }
        //
        // 保存Agent，并检查返回值
        AgentEntity updatedAgent = save(agent);
        if (updatedAgent == null) {
            // 如果保存失败，可以选择抛出异常或记录日志，这里以抛出异常为例
            throw new RuntimeException("Failed to update agent with uid: " + request.getUid());
        }
        // 转换并返回AgentResponse
        return convertToResponse(updatedAgent);
    }

    @Transactional
    public AgentResponse updateAvatar(AgentRequest request) {
        AgentEntity agent = findByUid(request.getUid())
                .orElseThrow(() -> new RuntimeException("agent found with uid: " + request.getUid()));
        agent.setAvatar(request.getAvatar());
        //
        AgentEntity updatedAgent = save(agent);
        if (updatedAgent == null) {
            throw new RuntimeException("Failed to update agent with uid: " + request.getUid());
        }
        return convertToResponse(updatedAgent);
    }

    @Transactional
    public AgentResponse updateStatus(AgentRequest request) {
        // agentRepository.updateStatusByUid(request.getStatus(), request.getUid());
        AgentEntity agent = findByUid(request.getUid())
                .orElseThrow(() -> new RuntimeException("agent found with uid: " + request.getUid()));
        agent.setStatus(request.getStatus()); // 更新接待状态
        //
        AgentEntity updatedAgent = save(agent);
        if (updatedAgent == null) {
            throw new RuntimeException("Failed to save agent.");
        }
        //
        bytedeskEventPublisher.publishEvent(new AgentUpdateStatusEvent(this, updatedAgent));

        return convertToResponse(updatedAgent);
    }

    @Transactional
    public ThreadResponseSimple acceptByAgent(ThreadRequest threadRequest) {
        //
        UserEntity user = authService.getUser();
        Optional<AgentEntity> agentOptional = agentRepository.findByUserUid(user.getUid());
        if (!agentOptional.isPresent()) {
            throw new RuntimeException("agent not found");
        }
        //
        Optional<ThreadEntity> threadOptional = threadRestService.findByUid(threadRequest.getUid());
        if (!threadOptional.isPresent()) {
            throw new RuntimeException("accept thread " + threadRequest.getUid() + " not found");
        }
        ThreadEntity thread = threadOptional.get();
        // 若会话非QUEUING（已存在接待坐席），则不允许重复接入
        if (!ThreadProcessStatusEnum.QUEUING.name().equals(thread.getStatus())) {
            throw new IllegalStateException("thread already accepted: " + thread.getStatus());
        }
        thread.setStatus(ThreadProcessStatusEnum.CHATTING.name());
        AgentEntity agent = agentOptional.get();
        thread.setAgent(agent.toUserProtobuf().toJson());
        MemberEntity member = agent.getMember();
        thread.setOwner(member.getUser());
        //
        ThreadEntity updateThread = threadRestService.save(thread);
        if (updateThread == null) {
            throw new RuntimeException("thread save failed");
        }
        // 通知queue更新，queue member更新, 增加agent接待数量
        bytedeskEventPublisher.publishEvent(new ThreadAddTopicEvent(this, updateThread));
        bytedeskEventPublisher.publishEvent(new ThreadAcceptEvent(this, updateThread));
        //
        return ConvertUtils.convertToThreadResponseSimple(updateThread);
    }

    @Transactional
    public AgentResponse updateAutoReply(AgentRequest request) {
        AgentEntity agent = findByUid(request.getUid())
                .orElseThrow(() -> new RuntimeException("agent found with uid: " + request.getUid()));
        //
        AgentEntity updatedAgent = save(agent);
        if (updatedAgent == null) {
            throw new RuntimeException("Failed to update agent with uid: " + request.getUid());
        }
        return convertToResponse(updatedAgent);
    }

    @Cacheable(value = "agent", key = "#entity.uid", unless = "#result == null")
    @Override
    protected AgentEntity doSave(AgentEntity entity) {
        // 确保所有延迟加载的关联都被初始化，以便正确缓存
        if (entity.getMember() != null) {
            entity.getMember().getUser(); // 触发加载
        }
        return agentRepository.save(entity);
    }

    @CacheEvict(value = "agent", key = "#uid")
    public void deleteByUid(String uid) {
        agentRepository.updateDeletedByUid(true, uid);
    }

    @Override
    public void delete(AgentRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public AgentResponse convertToResponse(AgentEntity entity) {
        return ServiceConvertUtils.convertToAgentResponse(entity);
    }

    public AgentExcel convertToExcel(AgentEntity entity) {
        return modelMapper.map(entity, AgentExcel.class);
    }

    @Override
    public AgentEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            AgentEntity agent) {
        try {
            log.warn("处理坐席乐观锁冲突: {} - {}", agent.getUid(), e.getMessage());

            // 获取最新版本的实体
            Optional<AgentEntity> latest = findByUid(agent.getUid());
            if (latest.isPresent()) {
                AgentEntity latestEntity = latest.get();
                // 将当前实体的变更应用到最新实体上
                // 注意：这里需要根据业务场景决定哪些字段需要保留，哪些需要覆盖
                log.info("最新坐席信息: {}", latestEntity.getNickname());

                // 以下是示例，实际需要根据业务需求调整
                // 保留原始实体ID和版本信息
                // agent.setId(latestEntity.getId());
                // agent.setVersion(latestEntity.getVersion());

                // 重新尝试保存
                return doSave(agent);
            } else {
                log.error("处理乐观锁冲突时找不到坐席: {}", agent.getUid());
            }
        } catch (Exception ex) {
            log.error("无法处理坐席乐观锁冲突", ex);
            throw new RuntimeException("无法处理坐席乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    // 有可能导致缓存中连接状态不准确，导致请求客服离线，暂时注释掉缓存
    // @Cacheable(value = "agent", key = "#uid", unless = "#result == null")
    public Optional<AgentEntity> findByUid(String uid) {
        Optional<AgentEntity> agentOptional = agentRepository.findByUid(uid);
        // 确保所有延迟加载的关联都被初始化，以便正确缓存
        agentOptional.ifPresent(agent -> {
            if (agent.getMember() != null) {
                agent.getMember().getUser(); // 触发加载
            }
            if (agent.getSettings() != null) {
                agent.getSettings().getQueueSettings();
                agent.getSettings().getDraftQueueSettings();
            }
        });
        return agentOptional;
    }

    /**
     * 强制从数据库加载（绕过 Spring Cache）。
     *
     * 说明：
     * - {@link #findByUid(String)} 带有 @Cacheable，可能直接命中缓存
     * - 该方法直接走 Repository 查询，用于在缓存对象缺失关联(member/user)时兜底
     */
    public Optional<AgentEntity> findByUidFromDatabase(String uid) {
        Optional<AgentEntity> agentOptional = agentRepository.findByUid(uid);
        agentOptional.ifPresent(agent -> {
            if (agent.getMember() != null) {
                agent.getMember().getUser();
            }
            if (agent.getSettings() != null) {
                agent.getSettings().getQueueSettings();
                agent.getSettings().getDraftQueueSettings();
            }
        });
        return agentOptional;
    }

    // @Cacheable(value = "agent", key = "#userUid", unless = "#result == null")
    public Optional<AgentEntity> findByUserUid(String userUid) {
        Optional<AgentEntity> agentOptional = agentRepository.findByUserUid(userUid);
        // 确保所有延迟加载的关联都被初始化，以便正确缓存
        agentOptional.ifPresent(agent -> {
            if (agent.getMember() != null) {
                agent.getMember().getUser(); // 触发加载
            }
        });
        return agentOptional;
    }

    // @Cacheable(value = "agent", key = "#mobile", unless = "#result == null")
    public Optional<AgentEntity> findByMobileAndOrgUid(String mobile, String orgUid) {
        Optional<AgentEntity> agentOptional = agentRepository.findByMobileAndOrgUidAndDeletedFalse(mobile, orgUid);
        // 确保所有延迟加载的关联都被初始化，以便正确缓存
        agentOptional.ifPresent(agent -> {
            if (agent.getMember() != null) {
                agent.getMember().getUser(); // 触发加载
            }
        });
        return agentOptional;
    }

    // @Cacheable(value = "agent", key = "#email", unless = "#result == null")
    public Optional<AgentEntity> findByEmailAndOrgUid(String email, String orgUid) {
        Optional<AgentEntity> agentOptional = agentRepository.findByEmailAndOrgUidAndDeletedFalse(email, orgUid);
        // 确保所有延迟加载的关联都被初始化，以便正确缓存
        agentOptional.ifPresent(agent -> {
            if (agent.getMember() != null) {
                agent.getMember().getUser(); // 触发加载
            }
        });
        return agentOptional;
    }

    // @Cacheable(value = "agent", key = "#userUid", unless = "#result == null")
    public Optional<AgentEntity> findByUserUidAndOrgUid(String userUid, String orgUid) {
        Optional<AgentEntity> agentOptional = agentRepository.findByUserUidAndOrgUidAndDeletedFalse(userUid, orgUid);
        // 确保所有延迟加载的关联都被初始化，以便正确缓存
        agentOptional.ifPresent(agent -> {
            if (agent.getMember() != null) {
                agent.getMember().getUser(); // 触发加载
            }
        });
        return agentOptional;
    }

    public Boolean existsByUserUidAndOrgUid(String userUid, String orgUid) {
        return agentRepository.existsByUserUidAndOrgUidAndDeletedFalse(userUid, orgUid);
    }

    public Boolean existsByUid(String uid) {
        return agentRepository.existsByUid(uid);
    }

    public List<AgentEntity> findAllConnected() {
        return java.util.Collections.emptyList();
    }

}
