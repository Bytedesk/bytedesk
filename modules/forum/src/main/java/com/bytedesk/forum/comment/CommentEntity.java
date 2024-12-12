package com.bytedesk.forum.comment;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "bytedesk_forum_comment")
@EqualsAndHashCode(callSuper = true)
public class CommentEntity extends BaseEntity {

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "parent_id")
    private Long parentId;  // 用于回复其他评论

    @Column(name = "like_count")
    private Integer likeCount = 0;

    private String status = "active"; // active, deleted
} 