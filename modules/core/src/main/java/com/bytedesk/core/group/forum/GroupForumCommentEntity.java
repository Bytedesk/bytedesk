package com.bytedesk.core.group.forum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import com.bytedesk.core.base.BaseEntity;

/**
 * 论坛群评论：一条评论 = 一条 POST_COMMENT 消息 + 本关系记录。
 * 
 * 树形关系：postUid 指向根帖；parentCommentUid 可空（空=根评论），
 * 非空时必须属于同帖。楼中楼按 parentCommentUid 组织，视觉最多两层由前端控制。
 * 软删复用 BaseEntity.deleted（is_deleted 列）。
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = false)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_group_forum_comment",
      indexes = {
          @Index(name = "idx_gfc_post_created", columnList = "org_uid, post_uid, is_deleted, created_at"),
          @Index(name = "idx_gfc_parent_created", columnList = "org_uid, parent_comment_uid, is_deleted, created_at"),
          @Index(name = "idx_gfc_message", columnList = "message_uid")
      },
      uniqueConstraints = {
          @UniqueConstraint(name = "uk_gfc_message", columnNames = { "message_uid" })
      })
public class GroupForumCommentEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 群 uid */
    @Column(name = "group_uid", nullable = false, length = 64)
    private String groupUid;

    /** 所属根帖 uid（GroupForumPostEntity.uid） */
    @Column(name = "post_uid", nullable = false, length = 64)
    private String postUid;

    /** 根帖消息 uid（冗余，便于按消息定位） */
    @Column(name = "post_message_uid", nullable = false, length = 64)
    private String postMessageUid;

    /** 评论消息 uid（内容真值在消息表） */
    @Column(name = "message_uid", nullable = false, length = 64)
    private String messageUid;

    /** 父评论 uid（空=根评论；非空必须同帖） */
    @Column(name = "parent_comment_uid", length = 64)
    private String parentCommentUid;

    /** 被回复用户 userUid（展示「回复某人」用） */
    @Column(name = "reply_to_user_uid", length = 64)
    private String replyToUserUid;

    /** 作者 userUid */
    @Column(name = "author_user_uid", nullable = false, length = 64)
    private String authorUserUid;

    /** 作者昵称快照（发评论时写入，展示不依赖消息表联查时序） */
    @Column(name = "author_nickname", length = 128)
    private String authorNickname;

    /** 作者头像快照 */
    @Column(name = "author_avatar", length = 512)
    private String authorAvatar;

    // 软删 = 继承的 deleted（is_deleted 列）；删除保留占位与父子关系
}
