/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-06 12:18:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-12 10:40:13
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.reply;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.voc.feedback.FeedbackService;

@Service
public class ReplyServiceImpl implements ReplyService {

    @Autowired
    private ReplyRepository replyRepository;
    
    @Autowired
    private FeedbackService feedbackService;

    @Override
    @Transactional
    public ReplyEntity createReply(String content, Long feedbackId, Long userId, Long parentId, Boolean isOfficial) {
        ReplyEntity reply = new ReplyEntity();
        reply.setContent(content);
        reply.setFeedbackId(feedbackId);
        reply.setUserId(userId);
        reply.setParentId(parentId);
        // reply.setIsOfficial(isOfficial);
        
        // 保存回复
        reply = replyRepository.save(reply);
        
        // 更新反馈的回复数
        feedbackService.incrementReplyCount(feedbackId);
        
        return reply;
    }

    @Override
    @Transactional
    public ReplyEntity updateReply(Long replyId, String content) {
        ReplyEntity reply = getReply(replyId);
        reply.setContent(content);
        return replyRepository.save(reply);
    }

    @Override
    @Transactional
    public void deleteReply(Long replyId) {
        ReplyEntity reply = getReply(replyId);
        // reply.setStatus("deleted");
        replyRepository.save(reply);
    }

    @Override
    public ReplyEntity getReply(Long replyId) {
        return replyRepository.findById(replyId)
            .orElseThrow(() -> new RuntimeException("Reply not found"));
    }

    @Override
    public Page<ReplyEntity> getRepliesByFeedback(Long feedbackId, Pageable pageable) {
        return replyRepository.findByFeedbackId(feedbackId, pageable);
    }

    @Override
    public Page<ReplyEntity> getRepliesByUser(Long userId, Pageable pageable) {
        return replyRepository.findByUserId(userId, pageable);
    }

    @Override
    @Transactional
    public void incrementLikeCount(Long replyId) {
        // replyRepository.incrementLikeCount(replyId);
    }
} 