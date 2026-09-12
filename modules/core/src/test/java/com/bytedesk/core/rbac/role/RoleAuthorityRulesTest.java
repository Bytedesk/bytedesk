package com.bytedesk.core.rbac.role;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RoleAuthorityRulesTest {

    @Test
    void defaultRoleUserIncludesAgentSeatAndTopicSubscriptionReadAuthorities() {
        assertThat(RoleAuthorityRules.DEFAULT_ROLE_USER_AUTHORITY_VALUES)
                .contains(RoleAuthorityRules.AGENT_SEAT_READ)
                .contains(RoleAuthorityRules.TOPIC_SUBSCRIPTION_READ);
    }

    @Test
    void defaultRoleAgentIncludesCtiReadAuthority() {
        assertThat(RoleAuthorityRules.DEFAULT_ROLE_AGENT_EXTRA_AUTHORITY_VALUES)
                .contains(RoleAuthorityRules.CTI_READ);
    }

    @Test
    void defaultRoleAgentIncludesQueueMemberAuthorities() {
        // 会话小结等 AI 接口（POST /api/v1/queue/member/summary）要求 QUEUE_MEMBER_UPDATE，客服角色默认具备
        assertThat(RoleAuthorityRules.DEFAULT_ROLE_AGENT_EXTRA_AUTHORITY_VALUES)
                .contains(RoleAuthorityRules.QUEUE_MEMBER_READ)
                .contains(RoleAuthorityRules.QUEUE_MEMBER_UPDATE);
    }
}