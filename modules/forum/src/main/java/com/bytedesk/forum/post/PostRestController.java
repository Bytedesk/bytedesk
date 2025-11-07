package com.bytedesk.forum.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.CategoryRepository;
import com.bytedesk.forum.exception.ForumException;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/posts")
public class PostRestController {

    @Autowired
    private PostService postService;

    @Autowired
    private CategoryRepository categoryRepository;

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

    @PutMapping("/{postId}")
    @PreAuthorize("isAuthenticated() and @postService.isPostOwner(#postId, authentication.name)")
    public ResponseEntity<PostEntity> updatePost(
            @PathVariable Long postId,
            @RequestParam String title,
            @RequestParam String content) {
        return ResponseEntity.ok(postService.updatePost(postId, title, content));
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("isAuthenticated() and @postService.isPostOwner(#postId, authentication.name)")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostEntity> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

    @GetMapping
    public ResponseEntity<Page<PostEntity>> getPosts(Pageable pageable) {
        return ResponseEntity.ok(postService.getPosts(pageable));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<PostEntity>> getPostsByCategory(
            @PathVariable Long categoryId,
            Pageable pageable) {
        return ResponseEntity.ok(postService.getPostsByCategory(categoryId, pageable));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PostEntity>> getPostsByUser(
            @PathVariable Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(postService.getPostsByUser(userId, pageable));
    }

    @PostMapping("/{postId}/view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Long postId) {
        postService.incrementViewCount(postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> incrementLikeCount(@PathVariable Long postId) {
        postService.incrementLikeCount(postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/search")
    public ResponseEntity<Page<PostEntity>> searchPosts(
            @RequestBody PostSearchCriteria criteria,
            Pageable pageable) {
        return ResponseEntity.ok(postService.searchPosts(criteria, pageable));
    }

    @GetMapping("/search/fulltext")
    public ResponseEntity<Page<PostEntity>> fullTextSearch(
            @RequestParam String keyword,
            Pageable pageable) {
        return ResponseEntity.ok(postService.fullTextSearch(keyword, pageable));
    }

    @GetMapping("/search/fulltext/category/{categoryId}")
    public ResponseEntity<Page<PostEntity>> fullTextSearchByCategory(
            @RequestParam String keyword,
            @PathVariable Long categoryId,
            Pageable pageable) {
        return ResponseEntity.ok(postService.fullTextSearchByCategory(keyword, categoryId, pageable));
    }
} 