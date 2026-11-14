package com.bytedesk.ai.robot.group;

import java.time.ZonedDateTime;

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 群机器人执行任务。
 * 
 * 记录一次「群内 @机器人」的 AI 执行，保证幂等（同一源消息+同一机器人仅一条任务）、
 * 可恢复（状态机 + attempt + leaseUntil 支持多实例领取）与可审计。
 * 
 * 唯一键 (org_uid, group_uid, source_message_uid, robot_uid)：
 * 注意 MySQL utf8mb4 组合唯一索引字节上限，四个 uid 均为有界短 UID（数字串）。
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_ai_group_robot_execution",
      indexes = {
          @Index(name = "idx_gre_group_status", columnList = "org_uid, group_uid, status"),
          @Index(name = "idx_gre_status_lease", columnList = "status, lease_until")
      },
      uniqueConstraints = {
          @UniqueConstraint(name = "uk_gre_idem", columnNames = {
              "org_uid", "group_uid", "source_message_uid", "robot_uid"
          })
      })
public class GroupRobotExecutionEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_RUNNING = "RUNNING";
    public static final String STATUS_SUCCEEDED = "SUCCEEDED";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    /** 提问者 userUid（群成员） */
    @Column(name = "sender_user_uid", nullable = false, length = 64)
    private String senderUserUid;

    /** 群 topic：org/group/{groupUid} */
    @Column(name = "thread_topic", nullable = false, length = 128)
    private String threadTopic;

    /** 源消息 uid（@机器人的那条群消息） */
    @Column(name = "source_message_uid", nullable = false, length = 64)
    private String sourceMessageUid;

    /** 机器人 uid */
    @Column(name = "robot_uid", nullable = false, length = 64)
    private String robotUid;

    /** 群 uid（从 topic 解析，冗余存储便于查询） */
    @Column(name = "group_uid", nullable = false, length = 64)
    private String groupUid;

    /** 机器人回复消息 uid（执行成功后固定，保证回复幂等） */
    @Column(name = "reply_message_uid", length = 64)
    private String replyMessageUid;

    /** 任务状态 */
    @Column(name = "status", nullable = false, length = 16)
    private String status;

    /** 尝试次数 */
    @Builder.Default
    @Column(name = "attempt", nullable = false)
    private Integer attempt = 0;

    /** 执行租约到期时间（多实例领取互斥） */
    @Column(name = "lease_until")
    private ZonedDateTime leaseUntil;

    /** 领取令牌（防止过期 worker 写回） */
    @Column(name = "lease_token", length = 64)
    private String leaseToken;

    /** 安全错误码（不包含 provider 原始错误细节） */
    @Column(name = "error_code", length = 64)
    private String errorCode;

    /** 上次状态更新时间 */
    @Column(name = "status_updated_at")
    private ZonedDateTime statusUpdatedAt;
}
