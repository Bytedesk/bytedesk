/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-03 16:57:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-23 12:48:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.settings;

import java.util.Iterator;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotRestService;
import com.bytedesk.kbase.faq.FaqEntity;
import com.bytedesk.kbase.faq.FaqService;
import com.bytedesk.kbase.service_settings.ServiceCommonSettings;
import com.bytedesk.service.agent.AgentRequest;
import com.bytedesk.service.workgroup.WorkgroupRequest;
import com.bytedesk.service.worktime.WorktimeEntity;
import com.bytedesk.service.worktime.WorktimeService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ServiceSettingsService {

    private final ModelMapper modelMapper;

    private final FaqService faqService;

    private final WorktimeService worktimeService;

    private final RobotRestService robotService;

    //
    public RobotSettings formatAgentServiceSettings(AgentRequest request) {
        // 
        if (request == null || request.getRobotSettings() == null) {
            return RobotSettings.builder().build();
        }
        //
        RobotSettings serviceSettings = modelMapper.map(request.getRobotSettings(), RobotSettings.class);
        //
        if (StringUtils.hasText(request.getRobotSettings().getRobotUid())) {
            Optional<RobotEntity> robotOptional = robotService.findByUid(request.getRobotSettings().getRobotUid());
            if (robotOptional.isPresent()) {
                RobotEntity robot = robotOptional.get();
                serviceSettings.setRobot(robot);
            } else {
                throw new RuntimeException(request.getRobotSettings().getRobotUid() + " is not found.");
            }
        }
        //
        Iterator<String> worktimeIterator = request.getRobotSettings().getWorktimeUids().iterator();
        while (worktimeIterator.hasNext()) {
            String worktimeUid = worktimeIterator.next();
            Optional<WorktimeEntity> worktimeOptional = worktimeService.findByUid(worktimeUid);
            if (worktimeOptional.isPresent()) {
                WorktimeEntity worktimeEntity = worktimeOptional.get();
                serviceSettings.getLeaveMsgSettings().getWorktimes().add(worktimeEntity);
            } else {
                throw new RuntimeException(worktimeUid + " is not found.");
            }
        }

        return serviceSettings;
    }

    public ServiceCommonSettings formatAgentServiceCommonSettings(AgentRequest request) {
        // 
        if (request == null || request.getCommonSettings() == null) {
            return ServiceCommonSettings.builder().build();
        }
        //
        ServiceCommonSettings serviceSettings = modelMapper.map(request.getCommonSettings(), ServiceCommonSettings.class);
        //
        if (request.getCommonSettings().getFaqUids() != null
                && request.getCommonSettings().getFaqUids().size() > 0) {
            Iterator<String> iterator = request.getCommonSettings().getFaqUids().iterator();
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
        if (request.getCommonSettings().getQuickFaqUids() != null
                && request.getCommonSettings().getQuickFaqUids().size() > 0) {
            Iterator<String> iterator = request.getCommonSettings().getQuickFaqUids().iterator();
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
        if (request.getCommonSettings().getGuessFaqUids() != null
                && request.getCommonSettings().getGuessFaqUids().size() > 0) {
            Iterator<String> iterator = request.getCommonSettings().getGuessFaqUids().iterator();
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
        if (request.getCommonSettings().getHotFaqUids() != null
                && request.getCommonSettings().getHotFaqUids().size() > 0) {
            Iterator<String> iterator = request.getCommonSettings().getHotFaqUids().iterator();
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
        if (request.getCommonSettings().getShortcutFaqUids() != null
                && request.getCommonSettings().getShortcutFaqUids().size() > 0) {
            Iterator<String> iterator = request.getCommonSettings().getShortcutFaqUids().iterator();
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

        return serviceSettings;
    }

    public RobotSettings formatWorkgroupServiceSettings(WorkgroupRequest request) {
        // 
        if (request == null || request.getServiceRobotSettings() == null) {
            return RobotSettings.builder().build();
        }

        RobotSettings serviceSettings = modelMapper.map(request.getServiceRobotSettings(), RobotSettings.class);

        if (StringUtils.hasText(request.getServiceRobotSettings().getRobotUid())) {
            Optional<RobotEntity> robotOptional = robotService.findByUid(request.getServiceRobotSettings().getRobotUid());
            if (robotOptional.isPresent()) {
                RobotEntity robot = robotOptional.get();
                serviceSettings.setRobot(robot);
            } else {
                throw new RuntimeException(request.getServiceRobotSettings().getRobotUid() + " is not found.");
            }
        }
        Iterator<String> worktimeIterator = request.getServiceRobotSettings().getWorktimeUids().iterator();
        while (worktimeIterator.hasNext()) {
            String worktimeUid = worktimeIterator.next();
            Optional<WorktimeEntity> worktimeOptional = worktimeService.findByUid(worktimeUid);
            if (worktimeOptional.isPresent()) {
                WorktimeEntity worktimeEntity = worktimeOptional.get();
                serviceSettings.getWorktimes().add(worktimeEntity);
            } else {
                throw new RuntimeException(worktimeUid + " is not found.");
            }
        }
        //
        return serviceSettings;
    }

    public ServiceCommonSettings formatWorkgroupServiceCommonSettings(WorkgroupRequest request) {
        // 
        if (request == null || request.getServiceCommonSettings() == null) {
            return ServiceCommonSettings.builder().build();
        }

        ServiceCommonSettings serviceSettings = modelMapper.map(request.getServiceCommonSettings(), ServiceCommonSettings.class);

        //
        if (request.getServiceCommonSettings().getFaqUids() != null
                && request.getServiceCommonSettings().getFaqUids().size() > 0) {
            Iterator<String> iterator = request.getServiceCommonSettings().getFaqUids().iterator();
            while (iterator.hasNext()) {
                String faqUid = iterator.next();
                Optional<FaqEntity> faqOptional = faqService.findByUid(faqUid);
                if (faqOptional.isPresent()) {
                    FaqEntity faqEntity = faqOptional.get();

                    serviceSettings.getFaqs().add(faqEntity);
                } else {
                    throw new RuntimeException("faq " + faqUid + " not found");
                }
            }
        }
        if (request.getServiceCommonSettings().getQuickFaqUids() != null
                && request.getServiceCommonSettings().getQuickFaqUids().size() > 0) {
            Iterator<String> iterator = request.getServiceCommonSettings().getQuickFaqUids().iterator();
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
        if (request.getServiceCommonSettings().getGuessFaqUids() != null
                && request.getServiceCommonSettings().getGuessFaqUids().size() > 0) {
            Iterator<String> iterator = request.getServiceCommonSettings().getGuessFaqUids().iterator();
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
        if (request.getServiceCommonSettings().getHotFaqUids() != null
                && request.getServiceCommonSettings().getHotFaqUids().size() > 0) {
            Iterator<String> iterator = request.getServiceCommonSettings().getHotFaqUids().iterator();
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
        if (request.getServiceCommonSettings().getShortcutFaqUids() != null
                && request.getServiceCommonSettings().getShortcutFaqUids().size() > 0) {
            Iterator<String> iterator = request.getServiceCommonSettings().getShortcutFaqUids().iterator();
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
        return serviceSettings;
    }

}
