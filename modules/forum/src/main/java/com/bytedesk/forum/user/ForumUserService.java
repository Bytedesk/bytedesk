package com.bytedesk.forum.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.bytedesk.forum.post.PostEntity;

public interface ForumUserService {
    
    ForumUserStats getUserStats(Long userId);
    
    Page<PostEntity> getUserLikes(Long userId, Pageable pageable);
    
    void updateProfile(Long userId, String nickname, String email, String bio);
    
    void updatePassword(Long userId, String oldPassword, String newPassword);
} 