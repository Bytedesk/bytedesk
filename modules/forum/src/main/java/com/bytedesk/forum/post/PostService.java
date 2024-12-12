package com.bytedesk.forum.post;

import com.bytedesk.core.category.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {

    PostEntity createPost(String title, String content, Long userId, CategoryEntity category);

    PostEntity updatePost(Long postId, String title, String content);

    void deletePost(Long postId);

    PostEntity getPost(Long postId);

    Page<PostEntity> getPosts(Pageable pageable);

    Page<PostEntity> getPostsByCategory(Long categoryId, Pageable pageable);

    Page<PostEntity> getPostsByUser(Long userId, Pageable pageable);

    void incrementViewCount(Long postId);

    void incrementLikeCount(Long postId);

    Page<PostEntity> searchPosts(PostSearchCriteria criteria, Pageable pageable);

    Page<PostEntity> fullTextSearch(String keyword, Pageable pageable);

    Page<PostEntity> fullTextSearchByCategory(String keyword, Long categoryId, Pageable pageable);
} 