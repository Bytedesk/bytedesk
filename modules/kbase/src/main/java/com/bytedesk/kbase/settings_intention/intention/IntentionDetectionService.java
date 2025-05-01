/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-29
 * @Description: 意图检测服务接口
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.settings_intention.intention;

import java.util.List;

public interface IntentionDetectionService {
    
    /**
     * 检测单条消息的意图
     * 
     * @param messageContent 消息内容
     * @param contextMessages 上下文消息列表（可选）
     * @return 意图检测结果
     */
    IntentionResult detectIntention(String messageContent, List<String> contextMessages);
    
    /**
     * 检测会话的整体意图
     * 
     * @param threadId 会话ID
     * @return 意图检测结果
     */
    IntentionResult detectThreadIntention(String threadId);
    
    /**
     * 获取所有可用的意图类别
     * 
     * @return 意图类别列表
     */
    List<IntentionCategory> getAvailableIntentions();
    
    /**
     * 更新意图模型
     * 
     * @param trainingData 训练数据
     * @return 是否更新成功
     */
    boolean updateIntentionModel(List<IntentionTrainingData> trainingData);
    
    /**
     * 获取指定意图的推荐回复
     * 
     * @param intention 意图名称
     * @return 推荐回复
     */
    String getRecommendedResponse(String intention);
    
    /**
     * 获取意图转换历史
     * 
     * @param threadId 会话ID
     * @return 意图转换历史记录
     */
    List<IntentionTransition> getIntentionHistory(String threadId);
    
    /**
     * 手动设置会话意图
     * 
     * @param threadId 会话ID
     * @param intention 意图名称
     * @param confidence 置信度
     * @return 是否设置成功
     */
    boolean setThreadIntention(String threadId, String intention, double confidence);
}