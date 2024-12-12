package com.bytedesk.voc.feedback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Override
    @Transactional
    public FeedbackEntity createFeedback(String content, Long userId, String type) {
        FeedbackEntity feedback = new FeedbackEntity();
        feedback.setContent(content);
        feedback.setUserId(userId);
        feedback.setType(type);
        return feedbackRepository.save(feedback);
    }

    @Override
    @Transactional
    public FeedbackEntity updateFeedback(Long feedbackId, String content) {
        FeedbackEntity feedback = getFeedback(feedbackId);
        feedback.setContent(content);
        return feedbackRepository.save(feedback);
    }

    @Override
    @Transactional
    public void deleteFeedback(Long feedbackId) {
        feedbackRepository.deleteById(feedbackId);
    }

    @Override
    public FeedbackEntity getFeedback(Long feedbackId) {
        return feedbackRepository.findById(feedbackId)
            .orElseThrow(() -> new RuntimeException("Feedback not found"));
    }

    @Override
    public Page<FeedbackEntity> getFeedbacks(Pageable pageable) {
        return feedbackRepository.findAll(pageable);
    }

    @Override
    public Page<FeedbackEntity> getFeedbacksByUser(Long userId, Pageable pageable) {
        return feedbackRepository.findByUserId(userId, pageable);
    }

    @Override
    public Page<FeedbackEntity> getFeedbacksByType(String type, Pageable pageable) {
        return feedbackRepository.findByType(type, pageable);
    }

    @Override
    public Page<FeedbackEntity> getFeedbacksByStatus(String status, Pageable pageable) {
        return feedbackRepository.findByStatus(status, pageable);
    }

    @Override
    @Transactional
    public void assignFeedback(Long feedbackId, Long assignedTo) {
        FeedbackEntity feedback = getFeedback(feedbackId);
        feedback.setAssignedTo(assignedTo);
        feedback.setStatus("processing");
        feedbackRepository.save(feedback);
    }

    @Override
    @Transactional
    public void updateStatus(Long feedbackId, String status) {
        FeedbackEntity feedback = getFeedback(feedbackId);
        feedback.setStatus(status);
        feedbackRepository.save(feedback);
    }

    @Override
    @Transactional
    public void incrementLikeCount(Long feedbackId) {
        FeedbackEntity feedback = getFeedback(feedbackId);
        feedback.setLikeCount(feedback.getLikeCount() + 1);
        feedbackRepository.save(feedback);
    }

    @Override
    @Transactional
    public void incrementReplyCount(Long feedbackId) {
        FeedbackEntity feedback = getFeedback(feedbackId);
        feedback.setReplyCount(feedback.getReplyCount() + 1);
        feedbackRepository.save(feedback);
    }

    @Override
    public Page<FeedbackEntity> search(String keyword, String type, String status, Pageable pageable) {
        return feedbackRepository.search(keyword, type, status, pageable);
    }
} 