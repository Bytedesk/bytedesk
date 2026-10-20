package com.bytedesk.meet.minutes;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;

/**
 * 会议纪要（bytedesk_meet_minutes，2026-10-19 规划 §5.3）
 *
 * 每次重生成创建新记录并保留上一版可读结果；生成状态与源材料失效分开表达。
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_meet_minutes", uniqueConstraints = {
        @jakarta.persistence.UniqueConstraint(name = "uk_meet_request", columnNames = {"org_uid", "request_key"})
}, indexes = {
        @Index(name = "idx_meet_minutes_uid", columnList = "uuid"),
        @Index(name = "idx_meet_minutes_room", columnList = "org_uid, room_uid"),
        @Index(name = "idx_meet_minutes_queue", columnList = "status, next_retry_at")
})
public class MeetMinutesEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "room_uid", length = 64)
    private String roomUid;

    @Column(name = "title", length = 255)
    private String title;

    @Builder.Default
    @Column(name = "status", length = 32, nullable = false)
    private String status = MeetMinutesStatusEnum.PENDING.name();

    /** 短摘要 */
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.LONG32VARCHAR)
    @Column(name = "summary")
    private String summary;

    /** 渲染用 Markdown（由已校验结构渲染，不直接信任模型输出） */
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.LONG32VARCHAR)
    @Column(name = "content_markdown")
    private String contentMarkdown;

    /** 结构化结果 JSON（title/summary/topics/actionItems/openQuestions/sourceRefs） */
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.LONG32VARCHAR)
    @Column(name = "structured_result")
    private String structuredResult;

    @Column(name = "provider_uid", length = 64)
    private String providerUid;

    @Column(name = "model_name", length = 128)
    private String model;

    @Column(name = "model_uid", length = 64)
    private String modelUid;

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.LONG32VARCHAR)
    @Column(name = "summary_checkpoint")
    private String summaryCheckpoint;

    @Builder.Default
    @Column(name = "model_request_pending", nullable = false)
    private boolean modelRequestPending = false;

    /** 生成 prompt 版本标识 */
    @Column(name = "prompt_version", length = 32)
    private String promptVersion;

    /** 供应商支持时的 token 用量 JSON */
    @Column(name = "token_usage", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String tokenUsage;

    /** 幂等键：组织与操作者范围内唯一，绑定材料/模型参数 */
    @Column(name = "request_key", length = 128)
    private String requestKey;

    /** 来源材料哈希（有序录制列表 + 转写版本） */
    @Column(name = "source_hash", length = 128)
    private String sourceHash;

    /** 重生成父纪要 uid（首版为空） */
    @Column(name = "parent_uid", length = 64)
    private String parentUid;

    // ==================== 执行租约与错误 ===================

    @Column(name = "lease_owner", length = 128)
    private String leaseOwner;

    @Column(name = "lease_token", length = 64)
    private String leaseToken;

    @Column(name = "lease_until")
    private ZonedDateTime leaseUntil;

    @Column(name = "next_retry_at")
    private ZonedDateTime nextRetryAt;

    @Column(name = "attempt")
    private Integer attempt;

    @Column(name = "started_at")
    private ZonedDateTime startedAt;

    @Column(name = "finished_at")
    private ZonedDateTime finishedAt;

    @Column(name = "error_code", length = 64)
    private String errorCode;

    @Column(name = "error_message", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String errorMessage;

    /** 源材料失效（来源转写/录制被删）时间；生成状态之外单独表达 */
    @Column(name = "invalidated_at")
    private ZonedDateTime invalidatedAt;

    @Column(name = "invalid_reason", length = 255)
    private String invalidReason;

    public boolean isInvalidated() {
        return invalidatedAt != null;
    }
}
