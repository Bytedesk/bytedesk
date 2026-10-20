package com.bytedesk.meet.assistant;

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

/**
 * 会议助手上下文绑定（bytedesk_meet_assistant_ctx，2026-10-19 规划 §5.4）
 *
 * 只存会议材料绑定，不另建通用聊天表；实际会话/问答复用 robot_thread / robot_message。
 * 默认按用户建立个人问答历史，同一房间用户不能互看私人问题。
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_meet_assistant_ctx", indexes = {
        @Index(name = "idx_meet_assistant_ctx_uid", columnList = "uuid"),
        @Index(name = "idx_meet_assistant_ctx_user", columnList = "org_uid, user_uid"),
        @Index(name = "idx_meet_assistant_ctx_room", columnList = "org_uid, room_uid, user_uid")
})
public class MeetAssistantContextEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 关联 robot_thread uid（唯一） */
    @Column(name = "thread_uid", length = 64, nullable = false, unique = true)
    private String threadUid;

    /** 会话归属用户（个人问答历史仅本人可见） */
    @Column(name = "owner_user_uid", length = 64, nullable = false)
    private String ownerUserUid;

    @Column(name = "room_uid", length = 64)
    private String roomUid;

    /** 绑定材料哈希（有序来源 + 版本），换材料即新上下文 */
    @Column(name = "source_hash", length = 128, nullable = false)
    private String sourceHash;

    /** 有序来源快照 JSON：[{recordingUid, transcriptUid, revision, minutesUid?}] */
    @Column(name = "source_snapshot", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String sourceSnapshot;

    /** 可选绑定的纪要 uid */
    @Column(name = "minutes_uid", length = 64)
    private String minutesUid;

    /** ACTIVE / INVALIDATED（来源删除/版本切换后失效，历史保留旧快照） */
    @Builder.Default
    @Column(name = "ctx_status", length = 32, nullable = false)
    private String status = "ACTIVE";

    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
}
