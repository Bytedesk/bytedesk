/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-03 16:57:51
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-10 15:44:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
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
import com.bytedesk.kbase.auto_reply.settings.AutoReplySettings;
import com.bytedesk.kbase.faq.FaqEntity;
import com.bytedesk.kbase.faq.FaqRestService;
import com.bytedesk.kbase.settings.InviteSettings;
import com.bytedesk.kbase.settings.ServiceSettings;
import com.bytedesk.service.agent.AgentRequest;
import com.bytedesk.service.leave_msg.settings.LeaveMsgSettings;
import com.bytedesk.service.queue.settings.QueueSettings;
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

    private final FaqRestService faqService;

    private final WorktimeService worktimeService;

    private final RobotRestService robotService;

    public LeaveMsgSettings formatAgentLeaveMsgSettings(AgentRequest request) {
        // 
        if (request == null || request.getLeaveMsgSettings() == null) {
            return LeaveMsgSettings.builder().build();
        }
        //
        LeaveMsgSettings serviceSettings = modelMapper.map(request.getLeaveMsgSettings(), LeaveMsgSettings.class);
        //
        Iterator<String> worktimeIterator = request.getLeaveMsgSettings().getWorktimeUids().iterator();
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
    //
    // public RobotSettings formatAgentRobotSettings(AgentRequest request) {
    //     // 
    //     if (request == null || request.getRobotSettings() == null) {
    //         return RobotSettings.builder().build();
    //     }
    //     //
    //     RobotSettings serviceSettings = modelMapper.map(request.getRobotSettings(), RobotSettings.class);
    //     //
    //     if (StringUtils.hasText(request.getRobotSettings().getRobotUid())) {
    //         Optional<RobotEntity> robotOptional = robotService.findByUid(request.getRobotSettings().getRobotUid());
    //         if (robotOptional.isPresent()) {
    //             RobotEntity robot = robotOptional.get();
    //             serviceSettings.setRobot(robot);
    //         } else {
    //             throw new RuntimeException(request.getRobotSettings().getRobotUid() + " is not found.");
    //         }
    //     }
    //     return serviceSettings;
    // }

    public ServiceSettings formatAgentServiceSettings(AgentRequest request) {
        // 
        if (request == null || request.getServiceSettings() == null) {
            return ServiceSettings.builder().build();
        }
        //
        ServiceSettings serviceSettings = modelMapper.map(request.getServiceSettings(), ServiceSettings.class);
        //
        if (request.getServiceSettings().getFaqUids() != null
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
        if (request.getServiceSettings().getQuickFaqUids() != null
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

        return serviceSettings;
    }

    public AutoReplySettings formatAgentAutoReplySettings(AgentRequest request) {
        // 
        if (request == null || request.getAutoReplySettings() == null) {
            return AutoReplySettings.builder().build();
        }
        //
        AutoReplySettings serviceSettings = modelMapper.map(request.getAutoReplySettings(), AutoReplySettings.class);
        //
        return serviceSettings;
    }

    public QueueSettings formatAgentQueueSettings(AgentRequest request) {
        // 
        if (request == null || request.getQueueSettings() == null) {
            return QueueSettings.builder().build();
        }
        //
        QueueSettings serviceSettings = modelMapper.map(request.getQueueSettings(), QueueSettings.class);
        //
        return serviceSettings;
    }

    public InviteSettings formatAgentInviteSettings(AgentRequest request) {
        // 
        if (request == null || request.getInviteSettings() == null) {
            return InviteSettings.builder().build();
        }
        //
        InviteSettings serviceSettings = modelMapper.map(request.getInviteSettings(), InviteSettings.class);
        //
        return serviceSettings;
    }



    public LeaveMsgSettings formatWorkgroupLeaveMsgSettings(WorkgroupRequest request) {
        // 
        if (request == null || request.getLeaveMsgSettings() == null) {
            return LeaveMsgSettings.builder().build();
        }
        //
        LeaveMsgSettings serviceSettings = modelMapper.map(request.getLeaveMsgSettings(), LeaveMsgSettings.class);
        //
        Iterator<String> worktimeIterator = request.getLeaveMsgSettings().getWorktimeUids().iterator();
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

    public RobotSettings formatWorkgroupRobotSettings(WorkgroupRequest request) {
        // 
        if (request == null || request.getRobotSettings() == null) {
            return RobotSettings.builder().build();
        }

        RobotSettings serviceSettings = modelMapper.map(request.getRobotSettings(), RobotSettings.class);

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
        return serviceSettings;
    }

    public ServiceSettings formatWorkgroupServiceSettings(WorkgroupRequest request) {
        // 
        if (request == null || request.getServiceSettings() == null) {
            return ServiceSettings.builder().build();
        }

        ServiceSettings serviceSettings = modelMapper.map(request.getServiceSettings(), ServiceSettings.class);

        //
        if (request.getServiceSettings().getFaqUids() != null
                && request.getServiceSettings().getFaqUids().size() > 0) {
            Iterator<String> iterator = request.getServiceSettings().getFaqUids().iterator();
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
        if (request.getServiceSettings().getQuickFaqUids() != null
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
        return serviceSettings;
    }

    public QueueSettings formatWorkgroupQueueSettings(WorkgroupRequest request) {
        // 
        if (request == null || request.getQueueSettings() == null) {
            return QueueSettings.builder().build();
        }
        //
        QueueSettings serviceSettings = modelMapper.map(request.getQueueSettings(), QueueSettings.class);
        //
        return serviceSettings;
    }

    public InviteSettings formatWorkgroupInviteSettings(WorkgroupRequest request) {
        // 
        if (request == null || request.getInviteSettings() == null) {
            return InviteSettings.builder().build();
        }
        //
        InviteSettings serviceSettings = modelMapper.map(request.getInviteSettings(), InviteSettings.class);
        //
        return serviceSettings;
    }

}
