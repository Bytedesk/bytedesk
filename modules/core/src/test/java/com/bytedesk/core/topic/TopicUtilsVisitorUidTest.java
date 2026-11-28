package com.bytedesk.core.topic;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * 规划 docs/plans/2026-11-25-visitor-robot-order-query-plan.md §4.1：
 * 仅客服类 4 段 topic 解析 visitorUid，管理端 5 段 LLM topic 必须返回 null。
 */
class TopicUtilsVisitorUidTest {

    @Test
    void shouldExtractVisitorUidFromRobotThreadTopic() {
        assertThat(TopicUtils.getVisitorUidFromThreadTopic("org/robot/robot_123/visitor_001"))
                .isEqualTo("visitor_001");
    }

    @Test
    void shouldExtractVisitorUidFromAgentAndWorkgroupAndUnifiedTopics() {
        assertThat(TopicUtils.getVisitorUidFromThreadTopic("org/agent/agent_1/visitor_002"))
                .isEqualTo("visitor_002");
        assertThat(TopicUtils.getVisitorUidFromThreadTopic("org/workgroup/wg_1/visitor_003"))
                .isEqualTo("visitor_003");
        assertThat(TopicUtils.getVisitorUidFromThreadTopic("org/unified/uni_1/visitor_004"))
                .isEqualTo("visitor_004");
    }

    @Test
    void shouldReturnNullForAdminLlmFiveSegmentTopic() {
        // 管理端 LLM 会话第 3 段是坐席 uid，绝不可当作 visitorUid
        assertThat(TopicUtils.getVisitorUidFromThreadTopic("org/robot/robot_1/member_1/abc123")).isNull();
    }

    @Test
    void shouldReturnNullForNonCustomerServicePrefixes() {
        assertThat(TopicUtils.getVisitorUidFromThreadTopic("org/member/member_1/other_1")).isNull();
        assertThat(TopicUtils.getVisitorUidFromThreadTopic("org/group/group_1/member_2")).isNull();
        assertThat(TopicUtils.getVisitorUidFromThreadTopic("org/kb/kb_1/visitor_001")).isNull();
        assertThat(TopicUtils.getVisitorUidFromThreadTopic("org/ticket/workgroup/wg_1/t_1")).isNull();
    }

    @Test
    void shouldReturnNullForInvalidInput() {
        assertThat(TopicUtils.getVisitorUidFromThreadTopic(null)).isNull();
        assertThat(TopicUtils.getVisitorUidFromThreadTopic("")).isNull();
        assertThat(TopicUtils.getVisitorUidFromThreadTopic("org/robot/robot_1/")).isNull();
        assertThat(TopicUtils.getVisitorUidFromThreadTopic("org/robot/robot_1")).isNull();
        assertThat(TopicUtils.getVisitorUidFromThreadTopic("org/robot")).isNull();
    }
}
