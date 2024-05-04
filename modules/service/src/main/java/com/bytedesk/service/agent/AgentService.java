/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-04 10:33:09
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.core.config.BytedeskProperties;
import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.team.organization.Organization;
import com.bytedesk.team.organization.OrganizationService;

import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
@Service
@AllArgsConstructor
public class AgentService {

    private final AgentRepository agentRepository;

    private final ModelMapper modelMapper;

    private final UserService userService;

    private final UidUtils uidUtils;

    // private final TopicService topicService;

    // private final AuthService authService;

    private final BytedeskProperties properties;

    private final OrganizationService organizationService;

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
        Optional<User> userOptional = userService.findByEmail(agentRequest.getEmail());
        if (!userOptional.isPresent()) {
            user = userService.createUser(
                    agentRequest.getNickname(),
                    agentRequest.getAvatar(),
                    agentRequest.getPassword(),
                    agentRequest.getMobile(),
                    agentRequest.getEmail(),
                    true, 
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
        return agentRepository.save(agent);
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
        
        Optional<Organization> orgOptional = organizationService.findByName(properties.getCompany());
        if (orgOptional.isPresent()) {
            // add agent
            AgentRequest agent1Request = AgentRequest.builder()
                    .nickname("Agent1")
                    .email("agent1@email.com")
                    .mobile("18888888008")
                    .password("123456")
                    .orgUid(orgOptional.get().getUid())
                    .build();
            create(agent1Request);
            // 
            AgentRequest agent2Request = AgentRequest.builder()
                    .nickname("Agent2")
                    .email("agent2@email.com")
                    .mobile("18888888009")
                    .password("123456")
                    .orgUid(orgOptional.get().getUid())
                    .build();
            create(agent2Request);
  
        }
        

    }


}
