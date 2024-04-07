/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-02 16:50:31
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.core.utils.Utils;
import com.bytedesk.team.department.Department;
import com.bytedesk.team.department.DepartmentRepository;

import lombok.AllArgsConstructor;

// @Slf4j
@Service
@AllArgsConstructor
public class AgentService {

    private final AgentRepository agentRepository;

    private final DepartmentRepository departmentRepository;

    private final ModelMapper modelMapper;

    private final UserService userService;

    public Page<AgentResponse> query(AgentRequest pageParam) {

        Pageable pageable = PageRequest.of(pageParam.getPageNumber(),
                pageParam.getPageSize(), Sort.Direction.DESC,
                "id");

        Page<Agent> agentPage = agentRepository.findAll(pageable);

        return agentPage.map(this::convertToAgentResponse);
    }
    
    public JsonResult<?> create(AgentRequest agentRequest) {
        // 
        // if (userService.existsByMobile(agentRequest.getMobile())) {
        //     return JsonResult.error("mobile already exist");
        // }
        // if (userService.existsByEmail(agentRequest.getEmail())) {
        //     return JsonResult.error("email already exist");
        // }
        // 
        Agent agent = modelMapper.map(agentRequest, Agent.class);
        agent.setAid(Utils.getUid());
        // 
        Optional<Department> depOptional = departmentRepository.findByName(TypeConsts.DEPT_CUSTOMER_SERVICE);
        if (!depOptional.isPresent()) {
            return JsonResult.error("department not exist");
        }
        // 
        User user;
        Optional<User> userOptional = userService.findByMobile(agentRequest.getMobile());
        if (!userOptional.isPresent()) {
            user = userService.createUser(
                    agentRequest.getNickname(),
                    agentRequest.getAvatar(),
                    agentRequest.getPassword(),
                    agentRequest.getMobile(),
                    agentRequest.getEmail(),
                    true,
                    depOptional.get().getOrganization().getOid()
            );
        } else {
            user = userOptional.get();
            // user = userService.updateUser(
            //         userOptional.get(),
            //         agentRequest.getPassword(),
            //         agentRequest.getMobile(),
            //         agentRequest.getEmail()
            // );
        }
        agent.setUser(user);
        // 
        return JsonResult.success(save(agent));
    }
    
    public AgentResponse update(AgentRequest agentRequest) {

        Agent agent = modelMapper.map(agentRequest, Agent.class);

        return save(agent);
    }

    @SuppressWarnings("null")
    public AgentResponse save(Agent agent) {
        return convertToAgentResponse(agentRepository.save(agent));
    }
    
    private AgentResponse convertToAgentResponse(Agent agent) {
        return new ModelMapper().map(agent, AgentResponse.class);
    }




}
