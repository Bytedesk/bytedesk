package com.bytedesk.forum.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.core.category.CategoryRepository;
import com.bytedesk.forum.exception.ForumException;
import com.bytedesk.core.category.CategoryEntity;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    @Transactional
    public PostEntity createPost(String title, String content, Long userId, CategoryEntity category) {
        PostEntity post = new PostEntity();
        post.setTitle(title);
        post.setContent(content);
        post.setUserId(userId);
        post.setCategory(category);
        
        PostEntity savedPost = postRepository.save(post);
        // categoryRepository.incrementPostCount(category.getId());
        
        return savedPost;
    }

    @Override
    @CacheEvict(value = "posts", key = "#postId")
    @Transactional
    public PostEntity updatePost(Long postId, String title, String content) {
        PostEntity post = postRepository.findById(postId)
            .orElseThrow(() -> new ForumException("Post not found"));
            
        post.setTitle(title);
        post.setContent(content);
        
        return postRepository.save(post);
    }

    @Override
    @Transactional
    public void deletePost(Long postId) {
        PostEntity post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));
            
        post.setStatus("deleted");
        postRepository.save(post);
        // categoryRepository.decrementPostCount(post.getCategory().getId());
    }

    @Override
    @Cacheable(value = "posts", key = "#postId")
    public PostEntity getPost(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new ForumException("Post not found"));
    }

    @Override
    public Page<PostEntity> getPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    @Override
    public Page<PostEntity> getPostsByCategory(Long categoryId, Pageable pageable) {
        CategoryEntity category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ForumException("Category not found"));
        return postRepository.findByCategory(category, pageable);
    }

    @Override
    public Page<PostEntity> getPostsByUser(Long userId, Pageable pageable) {
        return postRepository.findByUserId(userId, pageable);
    }

    @Override
    @Transactional
    public void incrementViewCount(Long postId) {
        postRepository.incrementViewCount(postId);
    }

    @Override
    @Transactional
    public void incrementLikeCount(Long postId) {
        postRepository.incrementLikeCount(postId);
    }

    @Override
    public Page<PostEntity> searchPosts(PostSearchCriteria criteria, Pageable pageable) {
        if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
            String keyword = prepareKeyword(criteria.getKeyword());
            if (criteria.getCategoryId() != null) {
                return postRepository.fullTextSearchByCategory(keyword, criteria.getCategoryId(), pageable);
            }
            return postRepository.fullTextSearch(keyword, pageable);
        }
        return postRepository.findAll(PostSpecification.searchByCriteria(criteria), pageable);
    }

    @Override
    public Page<PostEntity> fullTextSearch(String keyword, Pageable pageable) {
        return postRepository.fullTextSearch(prepareKeyword(keyword), pageable);
    }

    @Override
    public Page<PostEntity> fullTextSearchByCategory(String keyword, Long categoryId, Pageable pageable) {
        return postRepository.fullTextSearchByCategory(prepareKeyword(keyword), categoryId, pageable);
    }

    private String prepareKeyword(String keyword) {
        keyword = keyword.trim();
        String[] words = keyword.split("\\s+");
        return String.join("* +", words) + "*";
    }
} 