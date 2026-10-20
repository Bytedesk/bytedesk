package com.bytedesk.meet.transcript;

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
 * 会议转写任务（bytedesk_meet_transcript，2026-10-19 规划 §5.1）
 *
 * 每个 (orgUid, recordingUid) 一条稳定主记录；任务失败或零分段时也保留。
 * JPA version 用于并发控制，业务 revision 用于材料版本，二者不能混用。
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_meet_transcript", uniqueConstraints = {
        @jakarta.persistence.UniqueConstraint(name = "uk_meet_recording", columnNames = {"org_uid", "recording_uid"})
}, indexes = {
        @Index(name = "idx_meet_transcript_uid", columnList = "uuid"),
        @Index(name = "idx_meet_transcript_recording", columnList = "org_uid, recording_uid"),
        @Index(name = "idx_meet_transcript_room", columnList = "org_uid, room_uid"),
        @Index(name = "idx_meet_transcript_queue", columnList = "status, next_retry_at")
})
public class MeetTranscriptEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    // ==================== 来源 ===================

    @Column(name = "recording_uid", length = 64, nullable = false)
    private String recordingUid;

    @Column(name = "room_uid", length = 64)
    private String roomUid;

    /** 来源指纹（文件大小/更新时间等校验信息，防止换文件后错误复用旧结果） */
    @Column(name = "source_fingerprint", length = 255)
    private String sourceFingerprint;

    /** 录制音轨覆盖范围：HOST_ONLY / MIXED_SUBSCRIBED / UNKNOWN */
    @Column(name = "audio_scope", length = 32)
    private String audioScope;

    /** 服务端实测音频时长（毫秒，ffprobe 探测） */
    @Column(name = "measured_duration_ms")
    private Long measuredDurationMs;

    // ==================== 执行 ===================

    @Builder.Default
    @Column(name = "status", length = 32, nullable = false)
    private String status = MeetTranscriptStatusEnum.PENDING.name();

    /** PREPARING / TRANSCRIBING / SAVING */
    @Column(name = "stage", length = 32)
    private String stage;

    /** 当前尝试材料版本；readyRevision 指向最后一次完整成功版本 */
    @Builder.Default
    @Column(name = "revision", nullable = false)
    private Integer revision = 1;

    /** 最后一次完整成功版本；初次成功前为空 */
    @Column(name = "ready_revision")
    private Integer readyRevision;

    @Column(name = "ready_revisions", length = 4000)
    private String readyRevisions;

    public boolean hasPublishedRevision(Integer value) {
        if (value == null || value <= 0) {
            return false;
        }
        return value.equals(readyRevision) || (readyRevisions != null
                && java.util.Arrays.asList(readyRevisions.split(",")).contains(value.toString()));
    }

    @Column(name = "provider", length = 64)
    private String provider;

    @Column(name = "model_name", length = 128)
    private String model;

    @Column(name = "language", length = 32)
    private String language;

    /** 幂等键：与来源和参数绑定；相同 key 不同参数报冲突 */
    @Column(name = "request_key", length = 128)
    private String requestKey;

    /** 关联既有 ASR 审计记录 uid（如产生） */
    @Column(name = "asr_record_uid", length = 64)
    private String asrRecordUid;

    // ==================== 持久化断点（分块 manifest） ===================

    /** chunk 清单 JSON：[{seq,offsetMs,providerTaskId,state,done}]，重启恢复用 */
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.LONG32VARCHAR)
    @Column(name = "chunk_manifest")
    private String chunkManifest;

    // ==================== 恢复与失效 ===================

    /** 执行尝试次数 */
    @Builder.Default
    @Column(name = "attempt", nullable = false)
    private Integer attempt = 0;

    /** 下次可重试时间（退避） */
    @Column(name = "next_retry_at")
    private ZonedDateTime nextRetryAt;

    /** 租约持有者标识（实例 id） */
    @Column(name = "lease_owner", length = 128)
    private String leaseOwner;

    /** 租约 fencing token，写入校验同一领取 token */
    @Column(name = "lease_token", length = 64)
    private String leaseToken;

    @Column(name = "lease_until")
    private ZonedDateTime leaseUntil;

    @Column(name = "started_at")
    private ZonedDateTime startedAt;

    @Column(name = "finished_at")
    private ZonedDateTime finishedAt;

    @Column(name = "error_code", length = 64)
    private String errorCode;

    @Column(name = "error_message", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String errorMessage;

    /** 派生失效时间（源录制删除等） */
    @Column(name = "invalidated_at")
    private ZonedDateTime invalidatedAt;

    @Column(name = "invalid_reason", length = 255)
    private String invalidReason;

    public boolean hasReadyRevision() {
        return readyRevision != null && readyRevision > 0;
    }

    public boolean isDeletedSource() {
        return invalidatedAt != null;
    }
}
