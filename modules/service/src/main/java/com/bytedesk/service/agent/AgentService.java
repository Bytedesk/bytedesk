/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-13 12:49:58
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

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.constant.UserConsts;
import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class AgentService {

    private final AgentRepository agentRepository;

    private final ModelMapper modelMapper;

    private final UserService userService;

    private final UidUtils uidUtils;

    // private final TopicService topicService;

    // private final AuthService authService;

    public Page<AgentResponse> query(AgentRequest agentRequest) {

        Pageable pageable = PageRequest.of(agentRequest.getPageNumber(),
                agentRequest.getPageSize(), Sort.Direction.DESC,
                "id");

        Page<Agent> agentPage = agentRepository.findByOrgUid(agentRequest.getOrgUid(), pageable);

        return agentPage.map(this::convertToAgentResponse);
    }

    @Transactional
    public Agent create(AgentRequest agentRequest) {
        // 
        // if (userService.existsByMobile(agentRequest.getMobile())) {
        //     return JsonResult.error("mobile already exist");
        // }
        // if (userService.existsByEmail(agentRequest.getEmail())) {
        //     return JsonResult.error("email already exist");
        // }
        // 
        Agent agent = modelMapper.map(agentRequest, Agent.class);
        agent.setUid(uidUtils.getCacheSerialUid());
        // 
        User user;
        Optional<User> userOptional = userService.findByEmailAndPlatform(agentRequest.getEmail(), BdConstants.PLATFORM_BYTEDESK);
        if (!userOptional.isPresent()) {
            user = userService.createUser(
                    agentRequest.getNickname(),
                    agentRequest.getAvatar(),
                    agentRequest.getPassword(),
                    agentRequest.getMobile(),
                    agentRequest.getEmail(),
                    true,
                    BdConstants.PLATFORM_BYTEDESK,
                    agentRequest.getOrgUid()
            );
        } else {
            // just return user
            user = userOptional.get();
        }
        agent.setUser(user);

        agent = save(agent);
        // 
        return agent;
    }
    
    public AgentResponse update(AgentRequest agentRequest) {

        Agent agent = modelMapper.map(agentRequest, Agent.class);

        agent = save(agent);

        return convertToAgentResponse(agent);
    }

    // FIXME: org.springframework.orm.ObjectOptimisticLockingFailureException: Row
    // was updated or deleted by another transaction (or unsaved-value mapping was
    // incorrect) : [com.bytedesk.service.agent.Agent#1]
    @Async
    public void updateConnect(String uid, boolean isConnect) {
        Optional<Agent> agentOptional = findByUser_Uid(uid);
        agentOptional.ifPresent(agent -> {
            agent.setConnected(isConnect);
            save(agent);
        });
    }


    @Cacheable(value = "agent", key = "#uid", unless="#result == null")
    public Optional<Agent> findByUid(String uid) {
        return agentRepository.findByUid(uid);
    }

    @Cacheable(value = "agent", key = "#mobile", unless="#result == null")
    public Optional<Agent> findByMobile(String mobile) {
        return agentRepository.findByMobile(mobile);
    }

    @Cacheable(value = "agent", key = "#email", unless="#result == null")
    public Optional<Agent> findByEmail(String email) {
        return agentRepository.findByEmail(email);
    }

    @Cacheable(value = "agent", key = "#userUid", unless="#result == null")
    public Optional<Agent> findByUser_Uid(String userUid) {
        return agentRepository.findByUser_Uid(userUid);
    }
    
    @Caching(put = {
        @CachePut(value = "agent", key = "#agent.uid"),
        @CachePut(value = "agent", key = "#agent.mobile"),
        @CachePut(value = "agent", key = "#agent.email"),
    })
    public Agent save(Agent agent) {

        try {
            return agentRepository.save(agent);
        } catch (ObjectOptimisticLockingFailureException e) {
            // 乐观锁冲突处理逻辑
            handleOptimisticLockingFailureException(e, agent);
        }

        return null;
    }
    
    // TODO: 待处理
    private void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, Agent agent) {
        // 可以在这里实现重试逻辑，例如使用递归调用或定时任务
        // 也可以记录日志、发送通知或执行其他业务逻辑
        log.error("Optimistic locking failure for agent: {}", agent.getUid());
        // e.printStackTrace();
        // 根据业务逻辑决定如何处理失败，例如通知用户稍后重试或执行其他操作
    }
    
    public AgentResponse convertToAgentResponse(Agent agent) {
        return modelMapper.map(agent, AgentResponse.class);
    }

    public AgentResponseSimple convertToAgentResponseSimple(Agent agent) {
        return modelMapper.map(agent, AgentResponseSimple.class);
    }


    public void initData() {

        if (agentRepository.count() > 0) {
            return;
        }

        // add agent
        AgentRequest agent1Request = AgentRequest.builder()
                .nickname("Agent1")
                .email("agent1@email.com")
                .mobile("18888888008")
                .password("123456")
                .orgUid(UserConsts.DEFAULT_ORGANIZATION_UID)
                .build();
        create(agent1Request);
        // 
        AgentRequest agent2Request = AgentRequest.builder()
                .nickname("Agent2")
                .email("agent2@email.com")
                .mobile("18888888009")
                .password("123456")
                .orgUid(UserConsts.DEFAULT_ORGANIZATION_UID)
                .build();
        create(agent2Request);
  
    
        

    }


}
