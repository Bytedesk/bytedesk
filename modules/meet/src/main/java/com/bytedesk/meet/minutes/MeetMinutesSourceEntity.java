package com.bytedesk.meet.minutes;

import com.bytedesk.core.base.BaseEntity;

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
 * 会议纪要来源（bytedesk_meet_minutes_source，2026-10-19 规划 §5.3）
 *
 * 固化材料顺序：minutesUid + ordinal 唯一；提交时所有来源必须 ready、同组织同房间且可见。
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_meet_minutes_source", uniqueConstraints = {
        @jakarta.persistence.UniqueConstraint(name = "uk_meet_source", columnNames = {"minutes_uid", "ordinal"})
}, indexes = {
        @Index(name = "idx_meet_minutes_source_uid", columnList = "uuid"),
        @Index(name = "idx_meet_minutes_source_lookup", columnList = "minutes_uid, ordinal")
})
public class MeetMinutesSourceEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "minutes_uid", length = 64, nullable = false)
    private String minutesUid;

    /** 材料顺序（提交时固化，引用定位按此顺序拼接） */
    @Column(name = "ordinal", nullable = false)
    private Integer ordinal;

    @Column(name = "recording_uid", length = 64, nullable = false)
    private String recordingUid;

    @Column(name = "transcript_uid", length = 64, nullable = false)
    private String transcriptUid;

    /** 生成时绑定的转写版本 */
    @Column(name = "transcript_revision", nullable = false)
    private Integer transcriptRevision;
}
