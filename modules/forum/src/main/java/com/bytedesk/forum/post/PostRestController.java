package com.bytedesk.forum.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.category.CategoryRepository;
import com.bytedesk.forum.exception.ForumException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/posts")
public class PostRestController {

    private final PostService postService;

    private final CategoryRepository categoryRepository;

    @Operation(summary = "Create Post", description = "Create a new forum post")
    @ActionAnnotation(title = I18Consts.I18N_POST, action = I18Consts.I18N_ACTION_CREATE, description = "create post")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostEntity> createPost(
            @RequestParam String title,
            @RequestParam String content,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long categoryId) {
        CategoryEntity category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ForumException("Category not found"));
        return ResponseEntity.ok(postService.createPost(title, content, 
            Long.valueOf(userDetails.getUsername()), category));
    }

    @Operation(summary = "Update Post", description = "Update an existing forum post")
    @ActionAnnotation(title = I18Consts.I18N_POST, action = I18Consts.I18N_ACTION_UPDATE, description = "update post")
    @PutMapping("/{postId}")
    @PreAuthorize("isAuthenticated() and @postService.isPostOwner(#postId, authentication.name)")
    public ResponseEntity<PostEntity> updatePost(
            @PathVariable Long postId,
            @RequestParam String title,
            @RequestParam String content) {
        return ResponseEntity.ok(postService.updatePost(postId, title, content));
    }

    @Operation(summary = "Delete Post", description = "Delete a forum post")
    @ActionAnnotation(title = I18Consts.I18N_POST, action = I18Consts.I18N_ACTION_DELETE, description = "delete post")
    @DeleteMapping("/{postId}")
    @PreAuthorize("isAuthenticated() and @postService.isPostOwner(#postId, authentication.name)")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize(PostPermissions.HAS_POST_READ)
    @Operation(summary = "Get Post", description = "Retrieve a forum post by its identifier")
    @ActionAnnotation(title = I18Consts.I18N_POST, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query post")
    @GetMapping("/{postId}")
    public ResponseEntity<PostEntity> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

    @PreAuthorize(PostPermissions.HAS_POST_READ)
    @Operation(summary = "Get Posts", description = "Retrieve forum posts with pagination")
    @ActionAnnotation(title = I18Consts.I18N_POST, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query post")
    @GetMapping
    public ResponseEntity<Page<PostEntity>> getPosts(Pageable pageable) {
        return ResponseEntity.ok(postService.getPosts(pageable));
    }

    @PreAuthorize(PostPermissions.HAS_POST_READ)
    @Operation(summary = "Get Posts by Category", description = "Retrieve forum posts filtered by category")
    @ActionAnnotation(title = I18Consts.I18N_POST, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query post")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<PostEntity>> getPostsByCategory(
            @PathVariable Long categoryId,
            Pageable pageable) {
        return ResponseEntity.ok(postService.getPostsByCategory(categoryId, pageable));
    }

    @PreAuthorize(PostPermissions.HAS_POST_READ)
    @Operation(summary = "Get Posts by User", description = "Retrieve forum posts filtered by author")
    @ActionAnnotation(title = I18Consts.I18N_POST, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query post")
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PostEntity>> getPostsByUser(
            @PathVariable Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(postService.getPostsByUser(userId, pageable));
    }

    @PreAuthorize(PostPermissions.HAS_POST_UPDATE)
    @Operation(summary = "Increment Post View Count", description = "Increase the view count of a post")
    @ActionAnnotation(title = I18Consts.I18N_POST, action = I18Consts.I18N_ACTION_UPDATE, description = "update post")
    @PostMapping("/{postId}/view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Long postId) {
        postService.incrementViewCount(postId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize(PostPermissions.HAS_POST_UPDATE)
    @Operation(summary = "Increment Post Like Count", description = "Increase the like count of a post")
    @ActionAnnotation(title = I18Consts.I18N_POST, action = I18Consts.I18N_ACTION_LIKE, description = "like post")
    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> incrementLikeCount(@PathVariable Long postId) {
        postService.incrementLikeCount(postId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize(PostPermissions.HAS_POST_READ)
    @Operation(summary = "Search Posts", description = "Search forum posts by keyword")
    @ActionAnnotation(title = I18Consts.I18N_POST, action = I18Consts.I18N_ACTION_SEARCH, description = "search post")
    @PostMapping("/search")
    public ResponseEntity<Page<PostEntity>> searchPosts(
            @RequestBody PostSearchCriteria criteria,
            Pageable pageable) {
        return ResponseEntity.ok(postService.searchPosts(criteria, pageable));
    }

    @PreAuthorize(PostPermissions.HAS_POST_READ)
    @Operation(summary = "Full Text Search Posts", description = "Full text search forum posts")
    @ActionAnnotation(title = I18Consts.I18N_POST, action = I18Consts.I18N_ACTION_SEARCH, description = "search post")
    @GetMapping("/search/fulltext")
    public ResponseEntity<Page<PostEntity>> fullTextSearch(
            @RequestParam String keyword,
            Pageable pageable) {
        return ResponseEntity.ok(postService.fullTextSearch(keyword, pageable));
    }

    @PreAuthorize(PostPermissions.HAS_POST_READ)
    @Operation(summary = "Full Text Search Posts by Category", description = "Full text search forum posts filtered by category")
    @ActionAnnotation(title = I18Consts.I18N_POST, action = I18Consts.I18N_ACTION_SEARCH, description = "search post")
    @GetMapping("/search/fulltext/category/{categoryId}")
    public ResponseEntity<Page<PostEntity>> fullTextSearchByCategory(
            @RequestParam String keyword,
            @PathVariable Long categoryId,
            Pageable pageable) {
        return ResponseEntity.ok(postService.fullTextSearchByCategory(keyword, categoryId, pageable));
    }
} 