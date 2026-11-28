package com.bytedesk.forum.post;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.constant.I18Consts;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

/**
 * 论坛匿名访问接口
 * 无需登录即可访问的公开接口
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/visitor/api/v1/posts")
public class PostRestControllerVisitor {

    private final PostService postService;

    /**
     * 获取话题列表（匿名访问）
     */
    @ActionAnnotation(title = I18Consts.I18N_POST, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query post")
    @GetMapping
    public ResponseEntity<Page<PostEntity>> getPosts(Pageable pageable) {
        return ResponseEntity.ok(postService.getPosts(pageable));
    }

    /**
     * 获取话题详情（匿名访问）
     */
    @ActionAnnotation(title = I18Consts.I18N_POST, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query post")
    @GetMapping("/{postId}")
    public ResponseEntity<PostEntity> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

    /**
     * 按分类获取话题列表（匿名访问）
     */
    @ActionAnnotation(title = I18Consts.I18N_POST, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query post")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<PostEntity>> getPostsByCategory(
            @PathVariable Long categoryId,
            Pageable pageable) {
        return ResponseEntity.ok(postService.getPostsByCategory(categoryId, pageable));
    }

    /**
     * 按用户获取话题列表（匿名访问）
     */
    @ActionAnnotation(title = I18Consts.I18N_POST, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query post")
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PostEntity>> getPostsByUser(
            @PathVariable Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(postService.getPostsByUser(userId, pageable));
    }

    /**
     * 增加浏览次数（匿名访问）
     */
    @ActionAnnotation(title = I18Consts.I18N_POST, action = I18Consts.I18N_ACTION_UPDATE, description = "update post")
    @PostMapping("/{postId}/view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Long postId) {
        postService.incrementViewCount(postId);
        return ResponseEntity.ok().build();
    }

    /**
     * 搜索话题（匿名访问）
     */
    @ActionAnnotation(title = I18Consts.I18N_POST, action = I18Consts.I18N_ACTION_SEARCH, description = "search post")
    @PostMapping("/search")
    public ResponseEntity<Page<PostEntity>> searchPosts(
            @RequestBody PostSearchCriteria criteria,
            Pageable pageable) {
        return ResponseEntity.ok(postService.searchPosts(criteria, pageable));
    }

    /**
     * 全文搜索（匿名访问）
     */
    @ActionAnnotation(title = I18Consts.I18N_POST, action = I18Consts.I18N_ACTION_SEARCH, description = "search post")
    @GetMapping("/search/fulltext")
    public ResponseEntity<Page<PostEntity>> fullTextSearch(
            @RequestParam String keyword,
            Pageable pageable) {
        return ResponseEntity.ok(postService.fullTextSearch(keyword, pageable));
    }

    /**
     * 按分类全文搜索（匿名访问）
     */
    @ActionAnnotation(title = I18Consts.I18N_POST, action = I18Consts.I18N_ACTION_SEARCH, description = "search post")
    @GetMapping("/search/fulltext/category/{categoryId}")
    public ResponseEntity<Page<PostEntity>> fullTextSearchByCategory(
            @RequestParam String keyword,
            @PathVariable Long categoryId,
            Pageable pageable) {
        return ResponseEntity.ok(postService.fullTextSearchByCategory(keyword, categoryId, pageable));
    }
}
