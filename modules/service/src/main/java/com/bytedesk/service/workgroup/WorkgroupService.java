/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:19:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-08 11:12:26
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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.ai.robot.Robot;
import com.bytedesk.ai.robot.RobotService;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.quick_button.QuickButton;
import com.bytedesk.core.quick_button.QuickButtonService;
import com.bytedesk.core.rbac.user.UserConsts;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.agent.Agent;
import com.bytedesk.service.agent.AgentService;
import com.bytedesk.core.faq.Faq;
import com.bytedesk.core.faq.FaqService;
import com.bytedesk.service.settings.ServiceSettings;
import com.bytedesk.service.settings.ServiceSettingsRequest;
import com.bytedesk.service.utils.ConvertServiceUtils;
import com.bytedesk.service.worktime.Worktime;
import com.bytedesk.service.worktime.WorktimeService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class WorkgroupService {

    private final WorkgroupRepository workgroupRepository;

    private final AgentService agentService;

    private final RobotService robotService;

    private final WorktimeService worktimeService;

    private final QuickButtonService quickButtonService;

    private final FaqService faqService;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    // private final BytedeskProperties bytedeskProperties;

    public Page<WorkgroupResponse> queryByOrg(WorkgroupRequest workgroupRequest) {

        Pageable pageable = PageRequest.of(workgroupRequest.getPageNumber(),
                workgroupRequest.getPageSize(), Sort.Direction.DESC,
                "id");

        Specification<Workgroup> specs = WorkgroupSpecification.search(workgroupRequest);
        Page<Workgroup> workgroupPage = workgroupRepository.findAll(specs, pageable);

        return workgroupPage.map(ConvertServiceUtils::convertToWorkgroupResponse);
    }

    @Transactional
    public WorkgroupResponse create(WorkgroupRequest workgroupRequest) {
        //
        // // 使用映射会把默认值给清空为null
        // Workgroup workgroup = modelMapper.map(workgroupRequest, Workgroup.class);
        Workgroup workgroup = Workgroup.builder()
                .nickname(workgroupRequest.getNickname())
                // .orgUid(workgroupRequest.getOrgUid())
                .build();
        if (!StringUtils.hasText(workgroupRequest.getUid())) {
            workgroup.setUid(uidUtils.getCacheSerialUid());
        } else {
            workgroup.setUid(workgroupRequest.getUid());
        }
        workgroup.setOrgUid(workgroupRequest.getOrgUid());
        //
        if (workgroupRequest.getServiceSettings() == null
                || workgroupRequest.getServiceSettings().getWorktimeUids() == null
                || workgroupRequest.getServiceSettings().getWorktimeUids().isEmpty()) {
            ServiceSettingsRequest serviceSettings = ServiceSettingsRequest.builder().build();
            List<String> worktimeUids = new ArrayList<>();
            String worktimeUid = worktimeService.createDefault();
            worktimeUids.add(worktimeUid);
            serviceSettings.setWorktimeUids(worktimeUids);
            workgroupRequest.setServiceSettings(serviceSettings);
        }
        //
        Iterator<String> worktimeTterator = workgroupRequest.getServiceSettings().getWorktimeUids().iterator();
        while (worktimeTterator.hasNext()) {
            String worktimeUid = worktimeTterator.next();
            Optional<Worktime> worktimeOptional = worktimeService.findByUid(worktimeUid);
            if (worktimeOptional.isPresent()) {
                Worktime worktimeEntity = worktimeOptional.get();

                workgroup.getServiceSettings().getWorktimes().add(worktimeEntity);
            } else {
                throw new RuntimeException(worktimeUid + " is not found.");
            }
        }
        //
        if (workgroupRequest.getServiceSettings() != null
                && workgroupRequest.getServiceSettings().getQuickButtonUids() != null
                && workgroupRequest.getServiceSettings().getQuickButtonUids().size() > 0) {
            Iterator<String> iterator = workgroupRequest.getServiceSettings().getQuickButtonUids().iterator();
            while (iterator.hasNext()) {
                String quickButtonUid = iterator.next();
                Optional<QuickButton> quickButtonOptional = quickButtonService.findByUid(quickButtonUid);
                if (quickButtonOptional.isPresent()) {
                    QuickButton quickButtonEntity = quickButtonOptional.get();

                    workgroup.getServiceSettings().getQuickButtons().add(quickButtonEntity);
                } else {
                    throw new RuntimeException("quickButtonUid " + quickButtonUid + " not found");
                }
            }
        }
        //
        if (workgroupRequest.getServiceSettings() != null
                && workgroupRequest.getServiceSettings().getFaqUids() != null
                && workgroupRequest.getServiceSettings().getFaqUids().size() > 0) {
            Iterator<String> iterator = workgroupRequest.getServiceSettings().getFaqUids().iterator();
            while (iterator.hasNext()) {
                String faqUid = iterator.next();
                Optional<Faq> faqOptional = faqService.findByUid(faqUid);
                if (faqOptional.isPresent()) {
                    Faq faqEntity = faqOptional.get();

                    workgroup.getServiceSettings().getFaqs().add(faqEntity);
                } else {
                    throw new RuntimeException("faq " + faqUid + " not found");
                }
            }
        }
        //
        Iterator<String> agentIterator = workgroupRequest.getAgentUids().iterator();
        while (agentIterator.hasNext()) {
            String agentUid = agentIterator.next();
            Optional<Agent> agentOptional = agentService.findByUid(agentUid);
            if (agentOptional.isPresent()) {
                Agent agentEntity = agentOptional.get();
                workgroup.getAgents().add(agentEntity);
            } else {
                throw new RuntimeException(agentUid + " is not found.");
            }
        }
        //
        Workgroup updatedWorkgroup = save(workgroup);
        if (updatedWorkgroup == null) {
            throw new RuntimeException("save workgroup failed.");
        }

        return ConvertServiceUtils.convertToWorkgroupResponse(updatedWorkgroup);
    }

    public WorkgroupResponse update(WorkgroupRequest workgroupRequest) {

        Optional<Workgroup> workgroupOptional = findByUid(workgroupRequest.getUid());
        if (!workgroupOptional.isPresent()) {
            throw new RuntimeException(workgroupRequest.getUid() + " is not found.");
        }
        //
        Workgroup workgroup = workgroupOptional.get();
        //
        // modelMapper.map(workgroupRequest, workgroup); // 会把orgUid给清空为null
        workgroup.setNickname(workgroupRequest.getNickname());
        workgroup.setAvatar(workgroupRequest.getAvatar());
        workgroup.setDescription(workgroupRequest.getDescription());
        workgroup.setRouteType(workgroupRequest.getRouteType());
        workgroup.setRecent(workgroupRequest.getRecent());
        //
        ServiceSettings serviceSettings = modelMapper.map(workgroupRequest.getServiceSettings(), ServiceSettings.class);
        if (StringUtils.hasText(workgroupRequest.getServiceSettings().getRobotUid())) {
            Optional<Robot> robotOptional = robotService.findByUid(workgroupRequest.getServiceSettings().getRobotUid());
            if (robotOptional.isPresent()) {
                Robot robot = robotOptional.get();
                serviceSettings.setRobot(robot);
            } else {
                throw new RuntimeException(workgroupRequest.getServiceSettings().getRobotUid() + " is not found.");
            }
        }
        //
        if (workgroupRequest.getServiceSettings() != null
                && workgroupRequest.getServiceSettings().getQuickButtonUids() != null
                && workgroupRequest.getServiceSettings().getQuickButtonUids().size() > 0) {
            Iterator<String> iterator = workgroupRequest.getServiceSettings().getQuickButtonUids().iterator();
            while (iterator.hasNext()) {
                String quickButtonUid = iterator.next();
                Optional<QuickButton> quickButtonOptional = quickButtonService.findByUid(quickButtonUid);
                if (quickButtonOptional.isPresent()) {
                    QuickButton quickButtonEntity = quickButtonOptional.get();

                    serviceSettings.getQuickButtons().add(quickButtonEntity);
                } else {
                    throw new RuntimeException("quickButtonUid " + quickButtonUid + " not found");
                }
            }
        }
        //
        if (workgroupRequest.getServiceSettings() != null
                && workgroupRequest.getServiceSettings().getFaqUids() != null
                && workgroupRequest.getServiceSettings().getFaqUids().size() > 0) {
            Iterator<String> iterator = workgroupRequest.getServiceSettings().getFaqUids().iterator();
            while (iterator.hasNext()) {
                String faqUid = iterator.next();
                Optional<Faq> faqOptional = faqService.findByUid(faqUid);
                if (faqOptional.isPresent()) {
                    Faq faqEntity = faqOptional.get();

                    serviceSettings.getFaqs().add(faqEntity);
                } else {
                    throw new RuntimeException("faq " + faqUid + " not found");
                }
            }
        }
        //
        Iterator<String> worktimeTterator = workgroupRequest.getServiceSettings().getWorktimeUids().iterator();
        while (worktimeTterator.hasNext()) {
            String worktimeUid = worktimeTterator.next();
            Optional<Worktime> worktimeOptional = worktimeService.findByUid(worktimeUid);
            if (worktimeOptional.isPresent()) {
                Worktime worktimeEntity = worktimeOptional.get();
                serviceSettings.getWorktimes().add(worktimeEntity);
            } else {
                throw new RuntimeException(worktimeUid + " is not found.");
            }
        }
        //
        if (workgroupRequest.getServiceSettings().getGuessFaqUids() != null
                && workgroupRequest.getServiceSettings().getGuessFaqUids().size() > 0) {
            Iterator<String> iterator = workgroupRequest.getServiceSettings().getGuessFaqUids().iterator();
            while (iterator.hasNext()) {
                String guessFaqUid = iterator.next();
                Optional<Faq> guessFaqOptional = faqService.findByUid(guessFaqUid);
                if (guessFaqOptional.isPresent()) {
                    Faq guessFaq = guessFaqOptional.get();
                    log.info("guessFaqUid added {}", guessFaqUid);
                    serviceSettings.getGuessFaqs().add(guessFaq);
                } else {
                    throw new RuntimeException("guessFaq " + guessFaqUid + " not found");
                }
            }
        }
        //
        if (workgroupRequest.getServiceSettings().getHotFaqUids() != null
                && workgroupRequest.getServiceSettings().getHotFaqUids().size() > 0) {
            Iterator<String> iterator = workgroupRequest.getServiceSettings().getHotFaqUids().iterator();
            while (iterator.hasNext()) {
                String hotFaqUid = iterator.next();
                Optional<Faq> hotFaqOptional = faqService.findByUid(hotFaqUid);
                if (hotFaqOptional.isPresent()) {
                    Faq hotFaq = hotFaqOptional.get();
                    log.info("hotFaqUid added {}", hotFaqUid);
                    serviceSettings.getHotFaqs().add(hotFaq);
                } else {
                    throw new RuntimeException("hotFaq " + hotFaqUid + " not found");
                }
            }
        }
        //
        if (workgroupRequest.getServiceSettings().getShortcutFaqUids() != null
                && workgroupRequest.getServiceSettings().getShortcutFaqUids().size() > 0) {
            Iterator<String> iterator = workgroupRequest.getServiceSettings().getShortcutFaqUids().iterator();
            while (iterator.hasNext()) {
                String shortcutFaqUid = iterator.next();
                Optional<Faq> shortcutFaqOptional = faqService.findByUid(shortcutFaqUid);
                if (shortcutFaqOptional.isPresent()) {
                    Faq shortcutFaq = shortcutFaqOptional.get();
                    log.info("shortcutFaqUid added {}", shortcutFaqUid);
                    serviceSettings.getShortcutFaqs().add(shortcutFaq);
                } else {
                    throw new RuntimeException("shortcutFaq " + shortcutFaqUid + " not found");
                }
            }
        }
        // 
        workgroup.setServiceSettings(serviceSettings);
        //
        workgroup.getAgents().clear();
        Iterator<String> iterator = workgroupRequest.getAgentUids().iterator();
        while (iterator.hasNext()) {
            String agentUid = iterator.next();
            Optional<Agent> agentOptional = agentService.findByUid(agentUid);
            if (agentOptional.isPresent()) {
                Agent agentEntity = agentOptional.get();
                workgroup.getAgents().add(agentEntity);
            } else {
                throw new RuntimeException(agentUid + " is not found.");
            }
        }
        //
        Workgroup updatedWorkgroup = save(workgroup);
        if (updatedWorkgroup == null) {
            throw new RuntimeException("save workgroup failed.");
        }

        return ConvertServiceUtils.convertToWorkgroupResponse(updatedWorkgroup);
    }

    @Cacheable(value = "workgroup", key = "#uid", unless = "#result == null")
    public Optional<Workgroup> findByUid(String uid) {
        return workgroupRepository.findByUid(uid);
    }

    // @Cacheable(value = "workgroup", key = "#nickname-#orgUid", unless = "#result
    // == null")
    // public Optional<Workgroup> findByNicknameAndOrgUidAndDeleted(String nickname,
    // String orgUid) {
    // return workgroupRepository.findByNicknameAndOrgUidAndDeleted(nickname,
    // orgUid, false);
    // }

    private Workgroup save(Workgroup workgroup) {
        try {
            return workgroupRepository.save(workgroup);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    public void deleteByUid(String uid) {
        Optional<Workgroup> workgroupOptional = findByUid(uid);
        workgroupOptional.ifPresent(workgroup -> {
            workgroup.setDeleted(true);
            save(workgroup);
        });
    }

    public void initData() {

        if (workgroupRepository.count() > 0) {
            return;
        }

        String orgUid = UserConsts.DEFAULT_ORGANIZATION_UID;
        //
        List<String> agentUids = Arrays.asList(
                UserConsts.DEFAULT_AGENT_UID);
        //
        List<String> faqUids = Arrays.asList(
                orgUid + I18Consts.I18N_FAQ_DEMO_TITLE_1,
                orgUid + I18Consts.I18N_FAQ_DEMO_TITLE_2);
        //
        List<String> quickButtonUids = Arrays.asList(
                orgUid + I18Consts.I18N_QUICK_BUTTON_DEMO_TITLE_1,
                orgUid + I18Consts.I18N_QUICK_BUTTON_DEMO_TITLE_2);
        //
        List<String> worktimeUids = new ArrayList<>();
        String worktimeUid = worktimeService.createDefault();
        worktimeUids.add(worktimeUid);
        //
        // add workgroups
        WorkgroupRequest workgroupRequest = WorkgroupRequest.builder()
                .nickname(I18Consts.I18N_WORKGROUP_NICKNAME)
                .agentUids(agentUids)
                .build();
        workgroupRequest.setUid(UserConsts.DEFAULT_WORKGROUP_UID);
        workgroupRequest.setOrgUid(orgUid);
        //
        workgroupRequest.getServiceSettings().setFaqUids(faqUids);
        workgroupRequest.getServiceSettings().setQuickButtonUids(quickButtonUids);
        workgroupRequest.getServiceSettings().setWorktimeUids(worktimeUids);
        //
        create(workgroupRequest);
    }

}
