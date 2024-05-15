/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-13 12:49:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.workgroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bytedesk.core.constant.UserConsts;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.agent.Agent;
import com.bytedesk.service.agent.AgentService;
import lombok.AllArgsConstructor;
import static java.util.Arrays.asList;

// @Slf4j
@Service
@AllArgsConstructor
public class WorkgroupService {

    private final WorkgroupRepository workgroupRepository;

    private final AgentService agentService;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    public Page<WorkgroupResponse> query(WorkgroupRequest workgroupRequest) {

        Pageable pageable = PageRequest.of(workgroupRequest.getPageNumber(),
                workgroupRequest.getPageSize(), Sort.Direction.DESC,
                "id");

        Page<Workgroup> workgroupPage = workgroupRepository.findByOrgUid(workgroupRequest.getOrgUid(),
                pageable);

        return workgroupPage.map(this::convertToWorkgroupResponse);
    }

    public Workgroup create(WorkgroupRequest workgroupRequest) {
        //
        Workgroup workgroup = modelMapper.map(workgroupRequest, Workgroup.class);
        workgroup.setUid(uidUtils.getCacheSerialUid());
        //
        Iterator<String> iterator = workgroupRequest.getAgentAids().iterator();
        while (iterator.hasNext()) {
            String agentAid = iterator.next();
            Optional<Agent> agentOptional = agentService.findByUid(agentAid);
            if (agentOptional.isPresent()) {
                Agent agentEntity = agentOptional.get();
                workgroup.getAgents().add(agentEntity);
            } else {
                return null;
            }
        }
        //
        return save(workgroup);
    }

    Workgroup update(WorkgroupRequest workgroupRequest) {

        Optional<Workgroup> workgroupOptional = findByWid(workgroupRequest.getUid());
        if (!workgroupOptional.isPresent()) {
            return null;
        }
        //
        Workgroup workgroup = workgroupOptional.get();
        workgroup = modelMapper.map(workgroupRequest, Workgroup.class);
        // workgroupOptional.get().setNickname(workgroupRequest.getNickname());
        // workgroupOptional.get().setAvatar(workgroupRequest.getAvatar());
        // workgroupOptional.get().setDescription(workgroupRequest.getDescription());
        // workgroupOptional.get().setRouteType(workgroupRequest.getRouteType());
        // workgroupOptional.get().setRecent(workgroupRequest.getRecent());
        // workgroupOptional.get().setAutoPop(workgroupRequest.getAutoPop());
        //
        //
        Iterator<String> iterator = workgroupRequest.getAgentAids().iterator();
        while (iterator.hasNext()) {
            String agentAid = iterator.next();
            Optional<Agent> agentOptional = agentService.findByUid(agentAid);
            if (agentOptional.isPresent()) {
                Agent agentEntity = agentOptional.get();
                workgroup.getAgents().add(agentEntity);
            } else {
                return null;
            }
        }
        //
        return save(workgroup);
    }

    @Cacheable(value = "workgroup", key = "#wid", unless = "#result == null")
    public Optional<Workgroup> findByWid(String wid) {
        return workgroupRepository.findByUid(wid);
    }

    @Cacheable(value = "workgroup", key = "#nickname", unless = "#result == null")
    public Optional<Workgroup> findByNickname(String nickname) {
        return workgroupRepository.findByNickname(nickname);
    }

    // @SuppressWarnings("null")
    private Workgroup save(Workgroup workgroup) {
        return workgroupRepository.save(workgroup);
    }

    public WorkgroupResponse convertToWorkgroupResponse(Workgroup workgroup) {
        return modelMapper.map(workgroup, WorkgroupResponse.class);
    }

    public void initData() {

        if (workgroupRepository.count() > 0) {
            return;
        }

        List<String> agents = new ArrayList<>();
        Optional<Agent> agent1Optional = agentService.findByMobile("18888888008");
        agent1Optional.ifPresent(agent -> {
            agents.add(agent.getUid());
        });
        Optional<Agent> agent2Optional = agentService.findByMobile("18888888009");
        agent2Optional.ifPresent(agent -> {
            agents.add(agent.getUid());
        });

        // add workgroups
        WorkgroupRequest workgroup1Request = WorkgroupRequest.builder()
                .nickname("客服组1")
                .agentAids(agents)
                .orgUid(UserConsts.DEFAULT_ORGANIZATION_UID)
                .build();
        create(workgroup1Request);

        //
        WorkgroupRequest workgroup2Request = WorkgroupRequest.builder()
                .nickname("客服组2")
                .agentAids(asList(agent1Optional.get().getUid()))
                .orgUid(UserConsts.DEFAULT_ORGANIZATION_UID)
                .build();
        create(workgroup2Request);

    }

}
