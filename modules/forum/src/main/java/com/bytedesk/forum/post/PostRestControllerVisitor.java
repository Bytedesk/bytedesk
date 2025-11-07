package com.bytedesk.forum.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 论坛匿名访问接口
 * 无需登录即可访问的公开接口
 */
@RestController
@RequestMapping("/visitor/api/v1/posts")
public class PostRestControllerVisitor {

    @Autowired
    private PostService postService;

    /**
     * 获取话题列表（匿名访问）
     */
    @GetMapping
    public ResponseEntity<Page<PostEntity>> getPosts(Pageable pageable) {
        return ResponseEntity.ok(postService.getPosts(pageable));
    }

    /**
     * 获取话题详情（匿名访问）
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostEntity> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

    /**
     * 按分类获取话题列表（匿名访问）
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<PostEntity>> getPostsByCategory(
            @PathVariable Long categoryId,
            Pageable pageable) {
        return ResponseEntity.ok(postService.getPostsByCategory(categoryId, pageable));
    }

    /**
     * 按用户获取话题列表（匿名访问）
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PostEntity>> getPostsByUser(
            @PathVariable Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(postService.getPostsByUser(userId, pageable));
    }

    /**
     * 增加浏览次数（匿名访问）
     */
    @PostMapping("/{postId}/view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Long postId) {
        postService.incrementViewCount(postId);
        return ResponseEntity.ok().build();
    }

    /**
     * 搜索话题（匿名访问）
     */
    @PostMapping("/search")
    public ResponseEntity<Page<PostEntity>> searchPosts(
            @RequestBody PostSearchCriteria criteria,
            Pageable pageable) {
        return ResponseEntity.ok(postService.searchPosts(criteria, pageable));
    }

    /**
     * 全文搜索（匿名访问）
     */
    @GetMapping("/search/fulltext")
    public ResponseEntity<Page<PostEntity>> fullTextSearch(
            @RequestParam String keyword,
            Pageable pageable) {
        return ResponseEntity.ok(postService.fullTextSearch(keyword, pageable));
    }

    /**
     * 按分类全文搜索（匿名访问）
     */
    @GetMapping("/search/fulltext/category/{categoryId}")
    public ResponseEntity<Page<PostEntity>> fullTextSearchByCategory(
            @RequestParam String keyword,
            @PathVariable Long categoryId,
            Pageable pageable) {
        return ResponseEntity.ok(postService.fullTextSearchByCategory(keyword, categoryId, pageable));
    }
}
