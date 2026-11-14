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
 * 论坛群帖子：一条根帖 = 一条 POST 消息 + 本关系记录。
 * 
 * 正文/图片存于消息表（messageUid 唯一关联，不重复存正文）；
 * 本表维护可索引的帖子关系（群/作者/排序键/有效状态）。
 * 软删复用 BaseEntity.deleted（is_deleted 列）。
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = false)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_group_forum_post",
      indexes = {
          @Index(name = "idx_gfp_group_created", columnList = "org_uid, group_uid, is_deleted, created_at"),
          @Index(name = "idx_gfp_message", columnList = "message_uid")
      },
      uniqueConstraints = {
          @UniqueConstraint(name = "uk_gfp_message", columnNames = { "message_uid" })
      })
public class GroupForumPostEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 群 uid */
    @Column(name = "group_uid", nullable = false, length = 64)
    private String groupUid;

    /** 根帖消息 uid（内容真值在消息表） */
    @Column(name = "message_uid", nullable = false, length = 64)
    private String messageUid;

    /** 作者 userUid */
    @Column(name = "author_user_uid", nullable = false, length = 64)
    private String authorUserUid;

    /** 帖子标题（可选，摘要用） */
    @Column(name = "title", length = 255)
    private String title;

    // 软删 = 继承的 deleted（is_deleted 列）；删除保留占位与关系，禁止新增互动
}
