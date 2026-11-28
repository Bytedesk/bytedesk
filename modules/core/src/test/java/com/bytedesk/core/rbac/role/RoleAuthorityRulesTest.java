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

    @Test
    void defaultRoleUserIncludesDiskFileAuthorities() {
        // 网盘文件读写（目录级仍由 DiskPermissionEntity ACL 校验）
        assertThat(RoleAuthorityRules.DEFAULT_ROLE_USER_AUTHORITY_VALUES)
                .contains(RoleAuthorityRules.DISK_FILE_READ)
                .contains(RoleAuthorityRules.DISK_FILE_CREATE)
                .contains(RoleAuthorityRules.DISK_FILE_UPDATE)
                .contains(RoleAuthorityRules.DISK_FILE_DELETE);
    }

    @Test
    void defaultRoleUserIncludesGroupForumAndApplyAuthorities() {
        assertThat(RoleAuthorityRules.DEFAULT_ROLE_USER_AUTHORITY_VALUES)
                .contains(RoleAuthorityRules.GROUP_FORUM_READ)
                .contains(RoleAuthorityRules.GROUP_MEMBER_APPLY_CREATE)
                .contains(RoleAuthorityRules.ORGANIZATION_APPLY_CREATE);
    }

    @Test
    void defaultRoleUserIncludesConnectionReadAuthority() {
        // 桌面端 presence 接口（GET /api/v1/connection/presence/{userUid}）要求 CONNECTION_READ，普通成员默认具备
        assertThat(RoleAuthorityRules.DEFAULT_ROLE_USER_AUTHORITY_VALUES)
                .contains(RoleAuthorityRules.CONNECTION_READ);
    }

    @Test
    void defaultRoleUserIncludesBlackCrudAuthorities() {
        // 普通成员支持对黑名单的增删改查（exists/unblock/import 等接口均依赖）
        assertThat(RoleAuthorityRules.DEFAULT_ROLE_USER_AUTHORITY_VALUES)
                .contains(RoleAuthorityRules.BLACK_READ)
                .contains(RoleAuthorityRules.BLACK_CREATE)
                .contains(RoleAuthorityRules.BLACK_UPDATE)
                .contains(RoleAuthorityRules.BLACK_DELETE);
    }
}