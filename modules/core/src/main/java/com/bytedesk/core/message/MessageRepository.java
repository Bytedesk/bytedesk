/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-11 09:25:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.QueryHint;

public interface MessageRepository extends JpaRepository<MessageEntity, Long>, JpaSpecificationExecutor<MessageEntity> {

    Optional<MessageEntity> findByUid(String uid);

    // 或者更好的选择：使用JPQL查询，更灵活且性能更可控
    @Query("SELECT m FROM MessageEntity m WHERE m.type = :type AND m.content LIKE %:messageUid%")
    Optional<MessageEntity> findTransferMessage(
            @Param("type") String type, 
            @Param("messageUid") String messageUid
            );

    // 根据thread.uid查询最新一条消息
    Optional<MessageEntity> findFirstByThread_UidOrderByCreatedAtDesc(String threadUid);

    List<MessageEntity> findByThread_UidOrderByCreatedAtAsc(String threadUid);

    /**
     * 按创建时间窗口查询会话消息（用于“客服回复后批量标记之前未回复访客消息”为已回复）。
     * between 为闭区间（含边界）。
     */
    List<MessageEntity> findByThread_UidAndCreatedAtBetweenOrderByCreatedAtAsc(
            String threadUid,
            ZonedDateTime start,
            ZonedDateTime end);

    @Query("SELECT m.thread.uid AS threadUid, m.createdAt AS createdAt, m.agentRepliedAt AS agentRepliedAt, "
            + "m.type AS type, m.user AS user "
            + "FROM MessageEntity m "
            + "WHERE m.thread.uid IN :threadUids AND m.createdAt BETWEEN :start AND :end "
            + "ORDER BY m.thread.uid ASC, m.createdAt ASC")
    List<MessageStatisticRow> findStatisticRowsByThreadUidInAndCreatedAtBetween(
            @Param("threadUids") List<String> threadUids,
            @Param("start") ZonedDateTime start,
            @Param("end") ZonedDateTime end);

    Optional<MessageEntity> findFirstByThread_TopicOrderByCreatedAtDesc(String threadTopic);

    List<MessageEntity> findByThread_TopicAndCreatedAtBetweenOrderByCreatedAtAsc(
            String threadTopic,
            ZonedDateTime start,
            ZonedDateTime end);

    // 根据threadTopic查询最新n条消息
    @Query("SELECT m FROM MessageEntity m WHERE m.thread.topic = :threadTopic ORDER BY m.createdAt DESC")
    List<MessageEntity> findLatestByThreadTopicOrderByCreatedAtDesc(@Param("threadTopic") String threadTopic, org.springframework.data.domain.Pageable pageable);

    // thread.uid + type + user contains uid
    Optional<MessageEntity> findFirstByThread_UidAndTypeAndUserContainsOrderByCreatedAtDesc(
            @Param("threadUid") String threadUid, 
            @Param("type") String type, 
            @Param("userUid") String userUid);

    /**
     * 根据会话UID和状态列表查询消息，按创建时间升序排列
     * 
     * @param threadUid 会话UID
     * @param statuses 状态列表
     * @return 消息列表
     */
    List<MessageEntity> findByThread_UidAndStatusInOrderByCreatedAtAsc(String threadUid, List<String> statuses);

    /**
     * 校验指定会话中是否真实存在引用某个工单的 TICKET 卡片消息。
     * 用于工单卡片分享的只读详情豁免：仅当查看者所在会话中确实有人发送过该工单的卡片时，
     * 才允许其越过工单可见性设置查看该工单详情（防止伪造 threadUid 越权查看任意工单）。
     * content 片段匹配 JSON 序列化后的 "uid":"{ticketUid}" 精确片段。
     */
    boolean existsByThread_UidAndTypeAndContentContainingAndDeletedFalse(
            @Param("threadUid") String threadUid,
            @Param("type") String type,
            @Param("contentFragment") String contentFragment);

    /**
     * 查询某客服负责的“含未回复访客消息”的会话 UID（按最早未回复消息时间升序）。
     *
     * 说明：
        * - 以 message.agentReplied = false 标识“未回复访客消息”，由 Hibernate 按数据库方言生成布尔条件。
     * - 通过 JSON 序列化后的精确片段匹配访客消息，避免误匹配 visitor:false，并保持多数据库兼容。
     * - 通过 thread.agent 中的 uid 片段过滤到当前客服，避免依赖各数据库 JSON 函数方言。
     */
    default List<Object[]> pageUnrepliedVisitorThreadUidsByAgentUid(
            @Param("agentUidPattern") String agentUidPattern,
            @Param("limit") int limit,
            @Param("offset") int offset) {
        return pageUnrepliedVisitorThreadUidsByAgentUid(
                agentUidPattern,
                PageRequest.of(offset / limit, limit));
    }

    @Query("SELECT t.uid, MIN(m.createdAt) "
            + "FROM MessageEntity m "
            + "JOIN m.thread t "
            + "WHERE m.agentReplied = false "
            + "  AND m.type NOT IN ('SYSTEM', 'NOTICE') "
            + "  AND (m.user LIKE '%\"visitor\":true%' "
            + "       OR m.user LIKE '%\"type\":\"VISITOR\"%' "
            + "       OR m.user LIKE '%\"type\":\"visitor\"%') "
            + "  AND t.deleted = false "
            + "  AND t.status NOT IN ('CLOSED', 'TIMEOUT') "
            + "  AND t.agent IS NOT NULL AND t.agent <> '' "
            + "  AND t.agent LIKE :agentUidPattern "
            + "GROUP BY t.uid "
            + "ORDER BY MIN(m.createdAt) ASC")
    List<Object[]> pageUnrepliedVisitorThreadUidsByAgentUid(
            @Param("agentUidPattern") String agentUidPattern,
            Pageable pageable);

    @Query("SELECT COUNT(DISTINCT t.uid) "
            + "FROM MessageEntity m "
            + "JOIN m.thread t "
            + "WHERE m.agentReplied = false "
            + "  AND m.type NOT IN ('SYSTEM', 'NOTICE') "
            + "  AND (m.user LIKE '%\"visitor\":true%' "
            + "       OR m.user LIKE '%\"type\":\"VISITOR\"%' "
            + "       OR m.user LIKE '%\"type\":\"visitor\"%') "
            + "  AND t.deleted = false "
            + "  AND t.status NOT IN ('CLOSED', 'TIMEOUT') "
            + "  AND t.agent IS NOT NULL AND t.agent <> '' "
            + "  AND t.agent LIKE :agentUidPattern")
    long countUnrepliedVisitorThreadsByAgentUid(@Param("agentUidPattern") String agentUidPattern);

    /**
     * 统计“访客端未读消息数”：当前会话中由客服发送且状态仍为未读（未到 READ）的消息数量。
     *
     * 注意：这里沿用 {@link MessageEntity#isUnread()} 的判定规则，即 status in (SENDING, SUCCESS, DELIVERED) 视为未读。
     *
     * 事务注意：本查询为 native SQL，Hibernate 执行前会自动 flush 整个持久化上下文的脏实体
     * （JPQL 只 flush 查询涉及表的实体，native 无法判定涉及表而全量 flush）。工单创建等写事务中
     * 调用本查询时，若上下文挂有被并发修改过的会话实体（如会话关闭自动建单场景，消息管线正并发
     * 回写 thread），会在此处触发乐观锁冲突并回滚整个事务。因此强制 flushMode=COMMIT：
     * 本查询不触发预 flush，脏实体留待事务提交时统一处理。
     */
    @QueryHints(@QueryHint(name = "org.hibernate.flushMode", value = "COMMIT"))
    @Query(value = "SELECT COUNT(1) "
            + "FROM bytedesk_core_message m "
            + "INNER JOIN bytedesk_core_thread t ON m.thread_id = t.id "
            + "WHERE t.uuid = :threadUid "
            + "  AND t.is_deleted = false "
            + "  AND m.is_deleted = false "
            + "  AND m.status IN ('SENDING','SUCCESS','DELIVERED') "
            + "  AND (m.message_user LIKE '%\"agent\":true%' "
            + "       OR m.message_user LIKE '%\"type\":\"AGENT\"%' "
            + "       OR m.message_user LIKE '%\"type\":\"agent\"%')", nativeQuery = true)
    long countVisitorUnreadByThreadUid(@Param("threadUid") String threadUid);

    /**
     * 统计同事(MEMBER)会话“对我的未读消息数”。
     *
     * 背景：MEMBER 一对一会话在库中是成对的两个 thread（topic 互为反转：org/member/A/B 与 org/member/B/A），
     * 消息只挂载在发送方自己的 thread 上（MessageSocketService.sendMqttMessage 仅对 MQTT 投递做反转复制，
     * 不落库），因此统计“我”的未读数必须跨正反两个 topic 聚合。
     *
     * 口径：
     * - 排除自己发送的消息（m.user_uid = 当前会话 owner 对应的用户 uid）；
     * - 沿用 {@link MessageEntity#isUnread()} 的判定规则，status in (SENDING, SUCCESS, DELIVERED) 视为未读；
     * - 消息类型与前端 desktop shouldIncreaseUnreadCountByMessageType 对齐（TEXT/IMAGE/FILE/AUDIO/VIDEO/NOTICE），
     *   保证刷新前后端下发的未读数与客户端本地累加的口径一致。
     *
     * 事务注意：与 {@link #countVisitorUnreadByThreadUid} 相同，native SQL 会触发全量 flush，
     * 强制 flushMode=COMMIT 避免写事务中调用（如 convertToResponse 被写路径复用）时引发乐观锁冲突。
     */
    @QueryHints(@QueryHint(name = "org.hibernate.flushMode", value = "COMMIT"))
    @Query(value = "SELECT COUNT(1) "
            + "FROM bytedesk_core_message m "
            + "INNER JOIN bytedesk_core_thread t ON m.thread_id = t.id "
            + "WHERE (t.thread_topic = :topic OR t.thread_topic = :reverseTopic) "
            + "  AND t.is_deleted = false "
            + "  AND m.is_deleted = false "
            + "  AND m.status IN ('SENDING','SUCCESS','DELIVERED') "
            + "  AND m.message_type IN ('TEXT','IMAGE','FILE','AUDIO','VIDEO','NOTICE') "
            + "  AND m.user_uid <> :ownerUserUid", nativeQuery = true)
    long countMemberUnreadByTopics(@Param("topic") String topic,
            @Param("reverseTopic") String reverseTopic,
            @Param("ownerUserUid") String ownerUserUid);

        /**
         * 查找会话中全部 ROBOT_STREAM 消息（按创建时间升序）。
         * 用于热门问题统计：从 RobotContent.question 提取访客原始提问。
         */
        List<MessageEntity> findByThread_UidAndTypeOrderByCreatedAtAsc(String threadUid, String type);

    boolean existsByUid(String uid);
}
