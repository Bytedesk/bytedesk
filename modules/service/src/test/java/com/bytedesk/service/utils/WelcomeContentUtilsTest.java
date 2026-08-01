package com.bytedesk.service.utils;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.bytedesk.kbase.llm_faq.FaqEntity;
import com.bytedesk.kbase.settings_service.ServiceSettingsEntity;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent_settings.AgentSettingsEntity;

class WelcomeContentUtilsTest {

    @Test
    void shouldMapRouteToRobotFlagIntoWelcomeFaqs() {
        FaqEntity faq = FaqEntity.builder()
                .uid("faq-1")
                .question("如何查询订单")
                .answer("请提供订单号")
                .type("TEXT")
                .routeToRobot(true)
                .build();

        ServiceSettingsEntity serviceSettings = ServiceSettingsEntity.builder()
                .welcomeFaqs(List.of(faq))
                .build();

        AgentSettingsEntity agentSettings = AgentSettingsEntity.builder()
                .name("agent-settings")
                .serviceSettings(serviceSettings)
                .build();

        AgentEntity agent = AgentEntity.builder()
                .uid("agent-1")
                .nickname("agent")
                .settings(agentSettings)
                .build();

        var welcomeContent = WelcomeContentUtils.buildAgentWelcomeContent(agent, "hello");

        assertTrue(Boolean.TRUE.equals(welcomeContent.getFaqs().get(0).getRouteToRobot()));
    }
}