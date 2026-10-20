package com.bytedesk.meet.transcript;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 会议转写分段（bytedesk_meet_transcript_seg，2026-10-19 规划 §5.2）
 *
 * 唯一键 (transcriptUid, transcriptRevision, seq)；时间为文件内毫秒；
 * 供应商不提供句级时间时保存明确标为 chunk 级的时间窗，不伪造逐句时间。
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_meet_transcript_seg", uniqueConstraints = {
        @jakarta.persistence.UniqueConstraint(name = "uk_meet_segment",
                columnNames = {"transcript_uid", "transcript_revision", "seq"})
}, indexes = {
        @Index(name = "idx_meet_transcript_seg_uid", columnList = "uuid"),
        @Index(name = "idx_meet_transcript_seg_lookup", columnList = "transcript_uid, transcript_revision, seq")
})
public class MeetTranscriptSegmentEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "transcript_uid", length = 64, nullable = false)
    private String transcriptUid;

    /** 所属任务的业务版本（材料版本，非 JPA version） */
    @Column(name = "transcript_revision", nullable = false)
    private Integer transcriptRevision;

    /** 全局顺序号，时间排序分页 */
    @Column(name = "seq", nullable = false)
    private Integer seq;

    /** 来源 chunk 序号（对应任务 chunkManifest） */
    @Column(name = "chunk_seq")
    private Integer chunkSeq;

    /** 文件内起始毫秒 */
    @Column(name = "start_ms", nullable = false)
    private Long startMs;

    /** 文件内结束毫秒 */
    @Column(name = "end_ms", nullable = false)
    private Long endMs;

    /** 转写文本 */
    @Column(name = "text", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String text;

    /** 说话人标签（可空；混音 speaker_0 不是系统用户 uid，不猜实名） */
    @Column(name = "speaker_label", length = 64)
    private String speakerLabel;

    /**
     * 时间精度：SENTENCE（供应商句级时间）/ CHUNK（仅 chunk 级时间窗）
     */
    @Column(name = "timing_accuracy", length = 16)
    private String timingAccuracy;
}
