package com.bytedesk.forum.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.forum.post.PostEntity;
import com.bytedesk.forum.post.PostRepository;
// import com.bytedesk.forum.comment.CommentRepository;

// import com.bytedesk.core.rbac.user.UserService;

@Service
public class ForumUserServiceImpl implements ForumUserService {

    @Autowired
    private PostRepository postRepository;

    // @Autowired
    // private CommentRepository commentRepository;

    // @Autowired
    // private UserService userService; // 注入core模块的UserService

    @Override
    public ForumUserStats getUserStats(Long userId) {
        ForumUserStats stats = new ForumUserStats();
        stats.setUserId(userId);
        stats.setPostCount(postRepository.countByUserId(userId));
        // stats.setCommentCount(commentRepository.countByUserId(userId));
        // TODO: 实现获赞数统计
        return stats;
    }

    @Override
    public Page<PostEntity> getUserLikes(Long userId, Pageable pageable) {
        // TODO: 实现用户点赞的帖子列表
        return Page.empty(pageable);
    }

    @Override
    @Transactional
    public void updateProfile(Long userId, String nickname, String email, String bio) {
        // 调用core模块的UserService来更新用户资料
        // userService.updateProfile(userId, nickname, email);
        // TODO: 处理bio字段的更新
    }

    @Override
    @Transactional
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        // 调用core模块的UserService来更新密码
        // userService.updatePassword(userId, oldPassword, newPassword);
    }
} 