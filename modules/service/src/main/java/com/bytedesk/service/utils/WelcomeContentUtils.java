/*
 * @Author: refactor by Copilot
 * @Date: 2025-11-10
 * @Description: Utilities to build structured WelcomeContent for Agent and Robot
 */
package com.bytedesk.service.utils;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.core.message.content.WelcomeContent;
import com.bytedesk.core.workflow.WorkflowEntity;
import com.bytedesk.kbase.llm_faq.FaqEntity;
import com.bytedesk.service.agent.AgentEntity;

/**
 * Centralized builder for WelcomeContent to avoid duplication across strategies.
 */
public final class WelcomeContentUtils {

    private WelcomeContentUtils() {}

    // ================= Agent =================

    /**
     * Resolve welcome tip for agent with fallback to default rules in caller.
     */
    public static String resolveAgentWelcomeTip(AgentEntity agent) {
        if (agent == null || agent.getSettings() == null || agent.getSettings().getServiceSettings() == null) {
            return null;
        }
        return agent.getSettings().getServiceSettings().getWelcomeTip();
    }

    public static WelcomeContent buildAgentWelcomeContent(AgentEntity agent) {
        String tip = resolveAgentWelcomeTip(agent);
        return buildAgentWelcomeContent(agent, tip);
    }

    public static WelcomeContent buildAgentWelcomeContent(AgentEntity agent, String tip) {
        WelcomeContent.WelcomeContentBuilder<?, ?> builder = WelcomeContent.builder().content(tip);
        var settings = (agent != null && agent.getSettings() != null) ? agent.getSettings().getServiceSettings() : null;
        if (settings != null) {
            builder.kbUid(settings.getWelcomeKbUid());
            List<FaqEntity> faqs = settings.getWelcomeFaqs();
            if (faqs != null && !faqs.isEmpty()) {
                List<WelcomeContent.QA> qas = new ArrayList<>();
                for (FaqEntity f : faqs) {
                    qas.add(WelcomeContent.QA.builder()
                            .uid(f.getUid())
                            .question(f.getQuestion())
                            .answer(f.getAnswer())
                            .type(f.getType())
                            .build());
                }
                builder.faqs(qas);
            }
        }
        return builder.build();
    }

    // ================= Robot =================

    /**
     * Resolve welcome tip for robot with fallback to default rules in caller.
     */
    public static String resolveRobotWelcomeTip(RobotEntity robot) {
        if (robot == null || robot.getSettings() == null || robot.getSettings().getServiceSettings() == null) {
            return null;
        }
        return robot.getSettings().getServiceSettings().getWelcomeTip();
    }

    public static WelcomeContent buildRobotWelcomeContent(RobotEntity robot) {
        String tip = resolveRobotWelcomeTip(robot);
        return buildRobotWelcomeContent(robot, tip);
    }

    public static WelcomeContent buildRobotWelcomeContent(RobotEntity robot, String tip) {
        WelcomeContent.WelcomeContentBuilder<?, ?> builder = WelcomeContent.builder().content(tip);
        var settings = (robot != null && robot.getSettings() != null) ? robot.getSettings().getServiceSettings() : null;
        if (settings != null) {
            builder.kbUid(settings.getWelcomeKbUid());
            List<FaqEntity> faqs = settings.getWelcomeFaqs();
            if (faqs != null && !faqs.isEmpty()) {
                List<WelcomeContent.QA> qas = new ArrayList<>();
                for (FaqEntity f : faqs) {
                    qas.add(WelcomeContent.QA.builder()
                            .uid(f.getUid())
                            .question(f.getQuestion())
                            .answer(f.getAnswer())
                            .type(f.getType())
                            .build());
                }
                builder.faqs(qas);
            }
        }
        return builder.build();
    }

    // ================= Workflow =================

    /**
     * Resolve welcome tip for workflow with fallback to description.
     */
    public static String resolveWorkflowWelcomeTip(WorkflowEntity workflow) {
        if (workflow == null) {
            return null;
        }
        // 工作流目前没有设置实体，使用描述作为欢迎消息
        return workflow.getDescription();
    }

    public static WelcomeContent buildWorkflowWelcomeContent(WorkflowEntity workflow) {
        String tip = resolveWorkflowWelcomeTip(workflow);
        return buildWorkflowWelcomeContent(workflow, tip);
    }

    public static WelcomeContent buildWorkflowWelcomeContent(WorkflowEntity workflow, String tip) {
        WelcomeContent.WelcomeContentBuilder<?, ?> builder = WelcomeContent.builder().content(tip);
        // 工作流目前没有设置实体，暂不设置 kbUid 和 faqs
        // 未来可扩展：
        // if (workflow != null && workflow.getSettings() != null) {
        //     var settings = workflow.getSettings().getServiceSettings();
        //     if (settings != null) {
        //         builder.kbUid(settings.getWelcomeKbUid());
        //         // ... 设置 faqs
        //     }
        // }
        return builder.build();
    }
}
