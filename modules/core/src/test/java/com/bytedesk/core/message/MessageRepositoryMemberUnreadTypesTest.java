package com.bytedesk.core.message;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 转接/邀请发起消息计入同事会话未读数 —— 口径契约测试。
 *
 * 背景：TRANSFER/INVITE（转接/邀请发起消息）在待处理期间计入 MEMBER 同事会话未读数，
 * 接受/拒绝/超时/取消后由 MessagePersistService#dealWithTransferInviteReceipt 将原消息
 * status 改写为终态（非 SENDING/SUCCESS/DELIVERED），自动退出未读统计。
 *
 * 本测试用注解反射锁定 countMemberUnreadByTopics 的 native SQL 口径，防止后续改动
 * 意外移除这两个类型或误放行 8 类回执类型，破坏“服务端角标 = 前端本地累加 = 未读抽屉”
 * 的三方对齐（前端 shouldIncreaseUnreadCountByMessageType / enterprise
 * MessageSpecificationVip.MEMBER_UNREAD_MESSAGE_TYPES）。
 */
class MessageRepositoryMemberUnreadTypesTest {

    private String loadMemberUnreadSql() throws NoSuchMethodException {
        Method method = MessageRepository.class.getMethod("countMemberUnreadByTopics",
                String.class, String.class, String.class);
        assertThat(method.isAnnotationPresent(Query.class)).isTrue();
        assertThat(method.isAnnotationPresent(Param.class)).isFalse();
        String sql = method.getAnnotation(Query.class).value();
        assertThat(sql).isNotBlank();
        return sql;
    }

    @Test
    void memberUnreadSqlShouldIncludeTransferAndInvite() throws Exception {
        String sql = loadMemberUnreadSql().replaceAll("\\s+", " ");

        // 发起消息类型计入未读（待处理期间提示客服尽快处理）
        assertThat(sql).contains("'TRANSFER'");
        assertThat(sql).contains("'INVITE'");

        // 基础类型口径不被破坏
        assertThat(sql).contains("'TEXT'");
        assertThat(sql).contains("'IMAGE'");
        assertThat(sql).contains("'FILE'");
        assertThat(sql).contains("'AUDIO'");
        assertThat(sql).contains("'VIDEO'");
        assertThat(sql).contains("'NOTICE'");
    }

    @Test
    void memberUnreadSqlShouldExcludeReceiptTypes() throws Exception {
        String sql = loadMemberUnreadSql().replaceAll("\\s+", " ");

        // 8 类回执消息类型绝不放行：回执不作为独立消息计未读（仅用于改写原消息状态）
        assertThat(sql).doesNotContain("'TRANSFER_ACCEPT'");
        assertThat(sql).doesNotContain("'TRANSFER_REJECT'");
        assertThat(sql).doesNotContain("'TRANSFER_TIMEOUT'");
        assertThat(sql).doesNotContain("'TRANSFER_CANCEL'");
        assertThat(sql).doesNotContain("'INVITE_ACCEPT'");
        assertThat(sql).doesNotContain("'INVITE_REJECT'");
        assertThat(sql).doesNotContain("'INVITE_TIMEOUT'");
        assertThat(sql).doesNotContain("'INVITE_CANCEL'");
    }

    @Test
    void memberUnreadSqlShouldKeepUnreadStatusGating() throws Exception {
        String sql = loadMemberUnreadSql().replaceAll("\\s+", " ");

        // 未读判定维持既有口径：仅 SENDING/SUCCESS/DELIVERED 计入；
        // 转接/邀请终态回执会把原消息 status 改写为终态，依靠本条件自动退出统计
        assertThat(sql).contains("m.status IN ('SENDING','SUCCESS','DELIVERED')");
        assertThat(sql).contains("m.user_uid <> :ownerUserUid");
        assertThat(sql).contains(":topic");
        assertThat(sql).contains(":reverseTopic");
    }
}
