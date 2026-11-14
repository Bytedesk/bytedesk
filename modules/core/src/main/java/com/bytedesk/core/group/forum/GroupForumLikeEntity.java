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
 * 论坛群帖子点赞：记录当前点赞关系（非快照）。
 * 
 * 语义为 set（liked 状态覆盖）而非 toggle：
 * (postUid, userUid) 唯一，存在=已赞，不存在=未赞；取消即删除记录。
 * 点赞用户复用 BaseEntity.userUid（含义一致，避免字段冗余）。
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = false)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_group_forum_like",
      indexes = {
          @Index(name = "idx_gfl_post", columnList = "org_uid, post_uid")
      },
      uniqueConstraints = {
          @UniqueConstraint(name = "uk_gfl_post_user", columnNames = { "post_uid", "user_uid" })
      })
public class GroupForumLikeEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 群 uid */
    @Column(name = "group_uid", nullable = false, length = 64)
    private String groupUid;

    /** 帖子 uid */
    @Column(name = "post_uid", nullable = false, length = 64)
    private String postUid;

    // 点赞用户 = 继承的 userUid（列名 user_uid）
}
