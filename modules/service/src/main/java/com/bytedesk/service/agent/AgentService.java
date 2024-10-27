/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-23 11:17:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotService;
import com.bytedesk.core.action.ActionRequest;
import com.bytedesk.core.action.ActionService;
import com.bytedesk.core.action.ActionTypeEnum;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.config.BytedeskProperties;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.event.GenericApplicationEvent;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.auto_reply.AutoReplySettings;
import com.bytedesk.kbase.faq.FaqEntity;
import com.bytedesk.kbase.faq.FaqService;
import com.bytedesk.service.settings.ServiceSettings;
import com.bytedesk.service.settings.ServiceSettingsRequest;
import com.bytedesk.service.utils.ConvertServiceUtils;
import com.bytedesk.service.worktime.WorktimeEntity;
import com.bytedesk.service.worktime.WorktimeService;
import com.bytedesk.team.member.MemberEntity;
import com.bytedesk.team.member.MemberService;

// import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class AgentService {

    private final AgentRepository agentRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final MemberService memberService;

    private final AuthService authService;

    private final RobotService robotService;

    private final FaqService faqService;

    private final WorktimeService worktimeService;

    private final BytedeskProperties bytedeskProperties;

    private final BytedeskEventPublisher bytedeskEventPublisher;
    
    private final ActionService actionService;

    public Page<AgentResponse> queryByOrg(AgentRequest request) {

        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.ASC,
                "id");

        Specification<AgentEntity> spec = AgentSpecification.search(request);
        Page<AgentEntity> agentPage = agentRepository.findAll(spec, pageable);

        return agentPage.map(ConvertServiceUtils::convertToAgentResponse);
    }

    public AgentResponse query(AgentRequest agentRequest) {
        //
        UserEntity user = authService.getCurrentUser();
        Optional<AgentEntity> agentOptional = findByUserUidAndOrgUid(user.getUid(), agentRequest.getOrgUid());
        if (!agentOptional.isPresent()) {
            throw new RuntimeException("agent not found");
        }
        return ConvertServiceUtils.convertToAgentResponse(agentOptional.get());
    }

    @Transactional
    public AgentResponse create(AgentRequest agentRequest) {

        Optional<MemberEntity> memberOptional = memberService.findByUid(agentRequest.getMemberUid());
        if (!memberOptional.isPresent() || memberOptional.get().getUser() == null) {
            throw new RuntimeException("member uid: " + agentRequest.getMemberUid() + " not found");
        }
        // 一个user仅能绑定一个坐席
        if (existsByUserUidAndOrgUid(memberOptional.get().getUser().getUid(), agentRequest.getOrgUid())) {
            throw new RuntimeException("user already has an agent..!!");
        }
        //
        // Agent agent = modelMapper.map(agentRequest, Agent.class);
        AgentEntity agent = AgentEntity.builder()
                .nickname(agentRequest.getNickname())
                .email(agentRequest.getEmail())
                .mobile(agentRequest.getMobile())
                .build();
        if (StringUtils.hasText(agentRequest.getUid())) {
            agent.setUid(agentRequest.getUid());
        } else {
            agent.setUid(uidUtils.getCacheSerialUid());
        }
        agent.setOrgUid(agentRequest.getOrgUid());
        agent.setMember(memberOptional.get());
        agent.setUserUid(memberOptional.get().getUser().getUid());
        // agent.setUser(memberOptional.get().getUser());
        //
        if (agentRequest.getServiceSettings() == null
                || agentRequest.getServiceSettings().getWorktimeUids() == null
                || agentRequest.getServiceSettings().getWorktimeUids().isEmpty()) {
            ServiceSettingsRequest serviceSettings = ServiceSettingsRequest.builder().build();
            List<String> worktimeUids = new ArrayList<>();
            String worktimeUid = worktimeService.createDefault();
            worktimeUids.add(worktimeUid);
            serviceSettings.setWorktimeUids(worktimeUids);
            agentRequest.setServiceSettings(serviceSettings);
        }
        //
        Iterator<String> worktimeTterator = agentRequest.getServiceSettings().getWorktimeUids().iterator();
        while (worktimeTterator.hasNext()) {
            String worktimeUid = worktimeTterator.next();
            // log.info("worktimeUid {}", worktimeUid);
            Optional<WorktimeEntity> worktimeOptional = worktimeService.findByUid(worktimeUid);
            if (worktimeOptional.isPresent()) {
                WorktimeEntity worktimeEntity = worktimeOptional.get();

                agent.getServiceSettings().getWorktimes().add(worktimeEntity);
            } else {
                throw new RuntimeException(worktimeUid + " is not found.");
            }
        }
        //
        // if (agentRequest.getServiceSettings() != null
        // && agentRequest.getServiceSettings().getQuickButtonUids() != null
        // && agentRequest.getServiceSettings().getQuickButtonUids().size() > 0) {
        // Iterator<String> iterator =
        // agentRequest.getServiceSettings().getQuickButtonUids().iterator();
        // while (iterator.hasNext()) {
        // String quickButtonUid = iterator.next();
        // // log.info("quickButtonUid: {}", quickButtonUid);
        // Optional<QuickButton> quickButtonOptional =
        // quickButtonService.findByUid(quickButtonUid);
        // if (quickButtonOptional.isPresent()) {
        // QuickButton quickButtonEntity = quickButtonOptional.get();
        // log.info("agent quickButtonUid added: {}", quickButtonUid);
        // agent.getServiceSettings().getQuickButtons().add(quickButtonEntity);
        // } else {
        // throw new RuntimeException("agent quickButtonUid " + quickButtonUid + " not
        // found");
        // }
        // }
        // } else {
        // log.info("agent quickButtonUids is null");
        // }
        //
        if (agentRequest.getServiceSettings() != null
                && agentRequest.getServiceSettings().getFaqUids() != null
                && agentRequest.getServiceSettings().getFaqUids().size() > 0) {
            Iterator<String> iterator = agentRequest.getServiceSettings().getFaqUids().iterator();
            while (iterator.hasNext()) {
                String faqUid = iterator.next();
                Optional<FaqEntity> faqOptional = faqService.findByUid(faqUid);
                if (faqOptional.isPresent()) {
                    FaqEntity faqEntity = faqOptional.get();
                    log.info("agent faqUid added {}", faqUid);

                    agent.getServiceSettings().getFaqs().add(faqEntity);
                } else {
                    throw new RuntimeException("agent faq " + faqUid + " not found");
                }
            }
        } else {
            log.info("agent faquids is null");
        }
        // log.info("agent {}", agent);
        if (agentRequest.getServiceSettings() != null
                && agentRequest.getServiceSettings().getQuickFaqUids() != null
                && agentRequest.getServiceSettings().getQuickFaqUids().size() > 0) {
            Iterator<String> iterator = agentRequest.getServiceSettings().getQuickFaqUids().iterator();
            while (iterator.hasNext()) {
                String quickFaqUid = iterator.next();
                Optional<FaqEntity> quickFaqOptional = faqService.findByUid(quickFaqUid);
                if (quickFaqOptional.isPresent()) {
                    FaqEntity quickFaqEntity = quickFaqOptional.get();
                    log.info("quickFaqUid added {}", quickFaqUid);
                    agent.getServiceSettings().getQuickFaqs().add(quickFaqEntity);
                } else {
                    throw new RuntimeException("quickFaq " + quickFaqUid + " not found");
                }
            }
        }

        // 保存Agent并检查返回值
        AgentEntity savedAgent = save(agent);
        if (savedAgent == null) {
            // 根据业务逻辑决定如何处理保存失败的情况
            // 例如，可以抛出一个异常或返回一个错误响应
            throw new RuntimeException("Failed to save agent.");
        }
        //
        return ConvertServiceUtils.convertToAgentResponse(savedAgent);
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
        agent.setAvatar(request.getAvatar());
        agent.setMobile(request.getMobile());
        agent.setEmail(request.getEmail());
        agent.setDescription(request.getDescription());
        agent.setStatus(request.getStatus()); // 更新接待状态
        agent.setMaxThreadCount(request.getMaxThreadCount());
        // 暂不允许修改绑定成员
        // agent.setMember(memberOptional.get());
        // agent.setUserUid(memberOptional.get().getUser().getUid());
        // 
        ServiceSettings serviceSettings = modelMapper.map(request.getServiceSettings(), ServiceSettings.class);
        //
        if (StringUtils.hasText(request.getServiceSettings().getRobotUid())) {
            Optional<RobotEntity> robotOptional = robotService.findByUid(request.getServiceSettings().getRobotUid());
            if (robotOptional.isPresent()) {
                RobotEntity robot = robotOptional.get();
                serviceSettings.setRobot(robot);
            } else {
                throw new RuntimeException(request.getServiceSettings().getRobotUid() + " is not found.");
            }
        }
        //
        Iterator<String> worktimeTterator = request.getServiceSettings().getWorktimeUids().iterator();
        while (worktimeTterator.hasNext()) {
            String worktimeUid = worktimeTterator.next();
            Optional<WorktimeEntity> worktimeOptional = worktimeService.findByUid(worktimeUid);
            if (worktimeOptional.isPresent()) {
                WorktimeEntity worktimeEntity = worktimeOptional.get();
                serviceSettings.getWorktimes().add(worktimeEntity);
            } else {
                throw new RuntimeException(worktimeUid + " is not found.");
            }
        }
        //
        if (request.getServiceSettings() != null
                && request.getServiceSettings().getFaqUids() != null
                && request.getServiceSettings().getFaqUids().size() > 0) {
            Iterator<String> iterator = request.getServiceSettings().getFaqUids().iterator();
            while (iterator.hasNext()) {
                String faqUid = iterator.next();
                log.info("update faq {}", faqUid);
                Optional<FaqEntity> faqOptional = faqService.findByUid(faqUid);
                if (faqOptional.isPresent()) {
                    FaqEntity faqEntity = faqOptional.get();

                    log.info("save update faq {}", faqEntity.getUid());
                    serviceSettings.getFaqs().add(faqEntity);
                } else {
                    throw new RuntimeException("faq " + faqUid + " not found");
                }
            }
        }
        //
        if (request.getServiceSettings() != null
                && request.getServiceSettings().getQuickFaqUids() != null
                && request.getServiceSettings().getQuickFaqUids().size() > 0) {
            Iterator<String> iterator = request.getServiceSettings().getQuickFaqUids().iterator();
            while (iterator.hasNext()) {
                String quickFaqUid = iterator.next();
                Optional<FaqEntity> quickFaqOptional = faqService.findByUid(quickFaqUid);
                if (quickFaqOptional.isPresent()) {
                    FaqEntity quickFaqEntity = quickFaqOptional.get();
                    log.info("quickFaqUid added {}", quickFaqUid);
                    serviceSettings.getQuickFaqs().add(quickFaqEntity);
                } else {
                    throw new RuntimeException("quickFaq " + quickFaqUid + " not found");
                }
            }
        }
        //
        if (request.getServiceSettings().getGuessFaqUids() != null
                && request.getServiceSettings().getGuessFaqUids().size() > 0) {
            Iterator<String> iterator = request.getServiceSettings().getGuessFaqUids().iterator();
            while (iterator.hasNext()) {
                String guessFaqUid = iterator.next();
                Optional<FaqEntity> guessFaqOptional = faqService.findByUid(guessFaqUid);
                if (guessFaqOptional.isPresent()) {
                    FaqEntity guessFaq = guessFaqOptional.get();
                    log.info("guessFaqUid added {}", guessFaqUid);
                    serviceSettings.getGuessFaqs().add(guessFaq);
                } else {
                    throw new RuntimeException("guessFaq " + guessFaqUid + " not found");
                }
            }
        }
        //
        if (request.getServiceSettings().getHotFaqUids() != null
                && request.getServiceSettings().getHotFaqUids().size() > 0) {
            Iterator<String> iterator = request.getServiceSettings().getHotFaqUids().iterator();
            while (iterator.hasNext()) {
                String hotFaqUid = iterator.next();
                Optional<FaqEntity> hotFaqOptional = faqService.findByUid(hotFaqUid);
                if (hotFaqOptional.isPresent()) {
                    FaqEntity hotFaq = hotFaqOptional.get();
                    log.info("hotFaqUid added {}", hotFaqUid);
                    serviceSettings.getHotFaqs().add(hotFaq);
                } else {
                    throw new RuntimeException("hotFaq " + hotFaqUid + " not found");
                }
            }
        }
        //
        if (request.getServiceSettings().getShortcutFaqUids() != null
                && request.getServiceSettings().getShortcutFaqUids().size() > 0) {
            Iterator<String> iterator = request.getServiceSettings().getShortcutFaqUids().iterator();
            while (iterator.hasNext()) {
                String shortcutFaqUid = iterator.next();
                Optional<FaqEntity> shortcutFaqOptional = faqService.findByUid(shortcutFaqUid);
                if (shortcutFaqOptional.isPresent()) {
                    FaqEntity shortcutFaq = shortcutFaqOptional.get();
                    log.info("shortcutFaqUid added {}", shortcutFaqUid);
                    serviceSettings.getShortcutFaqs().add(shortcutFaq);
                } else {
                    throw new RuntimeException("shortcutFaq " + shortcutFaqUid + " not found");
                }
            }
        }
        // 
        agent.setServiceSettings(serviceSettings);
        // 自动回复
        AutoReplySettings autoReplySettings = modelMapper.map(request.getAutoReplySettings(),
                AutoReplySettings.class);
        agent.setAutoReplySettings(autoReplySettings);
        // 保存Agent，并检查返回值
        AgentEntity updatedAgent = save(agent);
        if (updatedAgent == null) {
            // 如果保存失败，可以选择抛出异常或记录日志，这里以抛出异常为例
            throw new RuntimeException("Failed to update agent with uid: " + request.getUid());
        }
        // 转换并返回AgentResponse
        return ConvertServiceUtils.convertToAgentResponse(updatedAgent);
    }

    public AgentResponse updateStatus(AgentRequest request) {
        Optional<AgentEntity> agentOptional = findByUid(request.getUid());
        if (!agentOptional.isPresent()) {
            // 如果找不到对应的Agent，则返回null
            throw new RuntimeException("null agent found with uid: " + request.getUid());
        }
        // 
        AgentEntity agent = agentOptional.get();
        agent.setStatus(request.getStatus()); // 更新接待状态
        // 保存Agent并检查返回值
        AgentEntity updatedAgent = save(agent);
        if (updatedAgent == null) {
            // 根据业务逻辑决定如何处理保存失败的情况
            // 例如，可以抛出一个异常或返回一个错误响应
            throw new RuntimeException("Failed to save agent.");
        }
        bytedeskEventPublisher.publishGenericApplicationEvent(
                new GenericApplicationEvent<AgentUpdateEvent>(this, new AgentUpdateEvent(this, updatedAgent, 
                         AgentUpdateTypeEnum.STATUS.name())));

        return ConvertServiceUtils.convertToAgentResponse(updatedAgent);
    }

    @Transactional
    public AgentResponse updateAutoReply(AgentRequest request) {
        // User user = authService.getCurrentUser();
        Optional<AgentEntity> agentOptional = findByUid(request.getUid());
        if (!agentOptional.isPresent()) {
            // 如果找不到对应的Agent，则返回null
            throw new RuntimeException("null agent found with uid: " + request.getUid());
        }
        //
        AgentEntity agent = agentOptional.get();
        // 自动回复
        AutoReplySettings autoReplySettings = modelMapper.map(request.getAutoReplySettings(),
                AutoReplySettings.class);
        agent.setAutoReplySettings(autoReplySettings);
        //
        // 保存Agent，并检查返回值
        AgentEntity updatedAgent = save(agent);
        if (updatedAgent == null) {
            // 如果保存失败，可以选择抛出异常或记录日志，这里以抛出异常为例
            throw new RuntimeException("Failed to update agent with uid: " + request.getUid());
        }
        // 转换并返回AgentResponse
        return ConvertServiceUtils.convertToAgentResponse(updatedAgent);
    }

    // FIXME: org.springframework.orm.ObjectOptimisticLockingFailureException: Row
    // was updated or deleted by another transaction (or unsaved-value mapping was
    // incorrect) : [com.bytedesk.service.agent.Agent#1]
    @Async
    public void updateConnect(String uid, boolean connected) {
        // 参数uid是userUid，非agent uid，所以无法直接更新
        agentRepository.updateConnectedByUid(connected, uid);
    }

    @Cacheable(value = "agent", key = "#uid", unless = "#result == null")
    public Optional<AgentEntity> findByUid(String uid) {
        return agentRepository.findByUid(uid);
    }

    @Cacheable(value = "agent", key = "#mobile", unless = "#result == null")
    public Optional<AgentEntity> findByMobileAndOrgUid(String mobile, String orgUid) {
        return agentRepository.findByMobileAndOrgUidAndDeleted(mobile, orgUid, false);
    }

    @Cacheable(value = "agent", key = "#email", unless = "#result == null")
    public Optional<AgentEntity> findByEmailAndOrgUid(String email, String orgUid) {
        return agentRepository.findByEmailAndOrgUidAndDeleted(email, orgUid, false);
    }

    @Cacheable(value = "agent", key = "#userUid", unless = "#result == null")
    public Optional<AgentEntity> findByUserUidAndOrgUid(String userUid, String orgUid) {
        return agentRepository.findByUserUidAndOrgUidAndDeleted(userUid, orgUid, false);
    }

    public Boolean existsByUserUidAndOrgUid(String userUid, String orgUid) {
        return agentRepository.existsByUserUidAndOrgUidAndDeleted(userUid, orgUid, false);
    }

    @Caching(put = {
            @CachePut(value = "agent", key = "#agent.uid"),
            @CachePut(value = "agent", key = "#agent.mobile", unless = "#agent.mobile == null"),
            @CachePut(value = "agent", key = "#agent.email", unless = "#agent.email == null"),
    })
    public AgentEntity save(AgentEntity agent) {
        try {
            return agentRepository.save(agent);
        } catch (ObjectOptimisticLockingFailureException e) {
            // 乐观锁冲突处理逻辑
            handleOptimisticLockingFailureException(e, agent);
        }
        return null;
    }

    @CacheEvict(value = "agent", key = "#uid")
    public void deleteByUid(String uid) {
        agentRepository.updateDeletedByUid(true, uid);
    }

    private static final int MAX_RETRY_ATTEMPTS = 3; // 设定最大重试次数
    private static final long RETRY_DELAY_MS = 5000; // 设定重试间隔（毫秒）
    private final Queue<AgentEntity> retryQueue = new LinkedList<>();

    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, AgentEntity agent) {
        retryQueue.add(agent);
        processRetryQueue();
    }

    private void processRetryQueue() {
        while (!retryQueue.isEmpty()) {
            AgentEntity agent = retryQueue.poll(); // 从队列中取出一个元素
            if (agent == null) {
                break; // 队列为空，跳出循环
            }

            int retryCount = 0;
            while (retryCount < MAX_RETRY_ATTEMPTS) {
                try {
                    // 尝试更新Topic对象
                    agentRepository.save(agent);
                    // 更新成功，无需进一步处理
                    log.info("Optimistic locking succeeded for agent: {}", agent.getUid());
                    break; // 跳出内部循环
                } catch (ObjectOptimisticLockingFailureException ex) {
                    // 捕获乐观锁异常
                    log.error("Optimistic locking failure for agent: {}, retry count: {}", agent.getUid(),
                            retryCount + 1);
                    // 等待一段时间后重试
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("Interrupted while waiting for retry", ie);
                        return;
                    }
                    retryCount++; // 增加重试次数

                    // 如果还有重试机会，则将agent放回队列末尾
                    if (retryCount < MAX_RETRY_ATTEMPTS) {
                        // FIXME: 发现会一直失败，暂时不重复处理
                        // retryQueue.add(agent);
                    } else {
                        // 所有重试都失败了
                        handleFailedRetries(agent);
                    }
                }
            }
        }
    }

    private void handleFailedRetries(AgentEntity agent) {
        String agentJSON = JSONObject.toJSONString(agent);
        ActionRequest actionRequest = ActionRequest.builder()
                .title("agent")
                .action("save")
                .description("All retry attempts failed for optimistic locking")
                .extra(agentJSON)
                .build();
        actionRequest.setType(ActionTypeEnum.FAILED.name());
        actionService.create(actionRequest);
        // bytedeskEventPublisher.publishActionCreateEvent(actionRequest);
        log.error("All retry attempts failed for optimistic locking of agent: {}", agent.getUid());
        // 根据业务逻辑决定如何处理失败，例如通知用户稍后重试或执行其他操作
        // notifyUserOfFailure(agent);
    }

    public void initData() {

        if (agentRepository.count() > 0) {
            return;
        }

        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        //
        Optional<MemberEntity> memberOptional = memberService.findByMobileAndOrgUid(bytedeskProperties.getMobile(), orgUid);
        if (!memberOptional.isPresent()) {
            return;
        }
        //
        List<String> faqUids = Arrays.asList(
                orgUid + I18Consts.I18N_FAQ_DEMO_TITLE_1,
                orgUid + I18Consts.I18N_FAQ_DEMO_TITLE_2);
        //
        // List<String> quickButtonUids = Arrays.asList(
        // orgUid + I18Consts.I18N_QUICK_BUTTON_DEMO_TITLE_1,
        // orgUid + I18Consts.I18N_QUICK_BUTTON_DEMO_TITLE_2);
        //
        List<String> worktimeUids = new ArrayList<>();
        String worktimeUid = worktimeService.createDefault();
        worktimeUids.add(worktimeUid);
        //
        MemberEntity member = memberOptional.get();
        // add agent
        AgentRequest agentRequest = AgentRequest.builder()
                .nickname(I18Consts.I18N_AGENT_NICKNAME)
                .email(member.getEmail())
                .mobile(member.getMobile())
                .memberUid(member.getUid())
                .build();
        agentRequest.setUid(BytedeskConsts.DEFAULT_AGENT_UID);
        agentRequest.setOrgUid(orgUid);
        //
        agentRequest.getServiceSettings().setFaqUids(faqUids);
        agentRequest.getServiceSettings().setQuickFaqUids(faqUids);
        agentRequest.getServiceSettings().setWorktimeUids(worktimeUids);
        //
        create(agentRequest);
    }

    //

}
