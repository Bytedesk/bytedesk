/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-29
 * @Description: 意图检测服务实现类
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.settings_intention.intention.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRepository;
import com.bytedesk.kbase.settings_intention.IntentionSettingsEntity;
import com.bytedesk.kbase.settings_intention.intention.IntentionCategory;
import com.bytedesk.kbase.settings_intention.intention.IntentionDetectionService;
import com.bytedesk.kbase.settings_intention.intention.IntentionResult;
import com.bytedesk.kbase.settings_intention.intention.IntentionTrainingData;
import com.bytedesk.kbase.settings_intention.intention.IntentionTransition;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IntentionDetectionServiceImpl implements IntentionDetectionService {

    // @Autowired
    // private IntentionSettingsRepository intentionSettingsRepository;
    
    @Autowired
    private ThreadRepository threadRepository;
    
    // @Autowired
    // private MessageRepository messageRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // 存储会话的意图转换历史记录
    private Map<String, List<IntentionTransition>> intentionTransitionHistory = new HashMap<>();
    
    /**
     * 内存缓存当前设置
     */
    private IntentionSettingsEntity currentSettings;
    
    @Override
    public IntentionResult detectIntention(String messageContent, List<String> contextMessages) {
        // 获取当前意图设置
        IntentionSettingsEntity settings = getDefaultSettings();
        
        // 模拟意图检测结果
        // 实际环境中，这里应该调用NLU模型或LLM来进行意图识别
        IntentionResult result = simulateIntentionDetection(messageContent, contextMessages, settings);
        
        return result;
    }

    @Override
    public IntentionResult detectThreadIntention(String threadId) {
        // 查找会话
        Optional<ThreadEntity> threadOptional = threadRepository.findByUid(threadId);
        if (threadOptional.isEmpty()) {
            throw new IllegalArgumentException("会话不存在: " + threadId);
        }
        
        // ThreadEntity thread = threadOptional.get();
        
        // 获取会话最近的消息
        // List<MessageEntity> messages = messageRepository.findByThreadUidOrderByCreatedAtDesc(threadId);
        // if (messages.isEmpty()) {
        //     log.warn("会话 {} 没有消息记录", threadId);
        //     return IntentionResult.builder()
        //             .primaryIntention("未知")
        //             .primaryConfidence(0.0)
        //             .isConfident(false)
        //             .build();
        // }
        
        // // 提取消息内容
        // List<String> messageContents = messages.stream()
        //         .map(MessageEntity::getContent)
        //         .collect(Collectors.toList());
        
        // // 取最新的消息内容用于意图检测
        // String latestMessage = messageContents.get(0);
        // List<String> context = messageContents.subList(1, Math.min(messageContents.size(), 6)); // 使用最近5条历史消息作为上下文
        
        // // 调用意图检测
        // IntentionResult result = detectIntention(latestMessage, context);
        
        // 记录意图转换
        // recordIntentionTransition(threadId, thread.getIntention(), result.getPrimaryIntention(), 
        //         0.0, result.getPrimaryConfidence(), latestMessage, "AUTO_DETECT");
        
        // // 更新会话的意图
        // if (result.isConfident() && !result.getPrimaryIntention().equals(thread.getIntention())) {
        //     thread.setIntention(result.getPrimaryIntention());
        //     threadRepository.save(thread);
        //     result.setIntentionChanged(true);
        // }
        
        // return result;

        return null;
    }

    @Override
    public List<IntentionCategory> getAvailableIntentions() {
        IntentionSettingsEntity settings = getDefaultSettings();
        
        // 从设置中构建意图类别
        List<IntentionCategory> categories = new ArrayList<>();
        Map<String, List<String>> subIntentionMap = getSubIntentionMapping(settings);
        
        for (String intention : settings.getIntentionList()) {
            IntentionCategory category = IntentionCategory.builder()
                    .name(intention)
                    .description(intention + "相关的咨询和请求")
                    .build();
            
            // 添加子意图（如果存在）
            if (subIntentionMap.containsKey(intention)) {
                List<String> subIntentions = subIntentionMap.get(intention);
                for (String subIntention : subIntentions) {
                    category.getSubIntentions().add(
                            IntentionCategory.builder()
                            .name(subIntention)
                            .parentIntention(intention)
                            .build()
                    );
                }
            }
            
            categories.add(category);
        }
        
        return categories;
    }

    @Override
    public boolean updateIntentionModel(List<IntentionTrainingData> trainingData) {
        // 实际实现中，这里应保存训练数据并触发模型更新
        log.info("收到 {} 条训练数据，准备更新意图模型", trainingData.size());
        
        // 模拟模型更新过程
        try {
            Thread.sleep(1000); // 模拟训练延迟
            return true;
        } catch (InterruptedException e) {
            log.error("模型更新中断", e);
            return false;
        }
    }

    @Override
    public String getRecommendedResponse(String intention) {
        IntentionSettingsEntity settings = getDefaultSettings();
        
        try {
            Map<String, String> responses = objectMapper.readValue(
                    settings.getIntentionResponses(), 
                    new TypeReference<Map<String, String>>() {});
            
            // 返回配置的回复，如果没有则返回通用回复
            return responses.getOrDefault(intention, 
                    "我了解到您关心的是" + intention + "，请问有什么具体需要帮助的呢？");
            
        } catch (JsonProcessingException e) {
            log.error("无法解析意图回复配置", e);
            return "我了解到您关心的是" + intention + "，请问有什么具体需要帮助的呢？";
        }
    }

    @Override
    public List<IntentionTransition> getIntentionHistory(String threadId) {
        return intentionTransitionHistory.getOrDefault(threadId, new ArrayList<>());
    }

    @Override
    public boolean setThreadIntention(String threadId, String intention, double confidence) {
        Optional<ThreadEntity> threadOptional = threadRepository.findByUid(threadId);
        if (threadOptional.isEmpty()) {
            log.error("设置意图失败：会话不存在 {}", threadId);
            return false;
        }
        
        ThreadEntity thread = threadOptional.get();
        // String previousIntention = thread.getIntention();
        
        // 记录意图转换
        // recordIntentionTransition(threadId, previousIntention, intention, 
        //         0.0, confidence, "手动设置", "MANUAL");
        
        // 更新会话意图
        // thread.setIntention(intention);
        threadRepository.save(thread);
        
        return true;
    }
    
    /**
     * 获取默认设置
     */
    private IntentionSettingsEntity getDefaultSettings() {
        // 如果缓存存在，直接返回
        if (currentSettings != null) {
            return currentSettings;
        }
        
        // 从数据库读取默认设置
        // Optional<IntentionSettingsEntity> settingsOpt = intentionSettingsRepository.findByDefaultTemplateIsTrue();
        // if (settingsOpt.isPresent()) {
        //     currentSettings = settingsOpt.get();
        //     return currentSettings;
        // }
        
        // // 如果没有默认设置，选择第一个设置
        // List<IntentionSettingsEntity> allSettings = intentionSettingsRepository.findAll();
        // if (!allSettings.isEmpty()) {
        //     currentSettings = allSettings.get(0);
        //     return currentSettings;
        // }
        
        // // 如果仍然没有，创建一个默认设置
        // IntentionSettingsEntity defaultSettings = new IntentionSettingsEntity();
        // defaultSettings.setName("默认意图设置");
        // defaultSettings.setDescription("系统自动创建的默认意图设置");
        // defaultSettings.setDefaultTemplate(true);
        // intentionSettingsRepository.save(defaultSettings);
        
        // currentSettings = defaultSettings;
        // return currentSettings;

        return null;
    }
    
    /**
     * 记录意图转换历史
     */
    // private void recordIntentionTransition(String threadId, String previousIntention, String currentIntention,
    //                                       double previousConfidence, double currentConfidence, 
    //                                       String triggerMessage, String transitionType) {
        
    //     // 如果意图没有变化，不记录
    //     if (previousIntention != null && previousIntention.equals(currentIntention)) {
    //         return;
    //     }
        
    //     IntentionTransition transition = IntentionTransition.builder()
    //             .threadId(threadId)
    //             .timestamp(System.currentTimeMillis())
    //             .previousIntention(previousIntention)
    //             .previousConfidence(previousConfidence)
    //             .currentIntention(currentIntention)
    //             .currentConfidence(currentConfidence)
    //             .triggerMessage(triggerMessage)
    //             .transitionType(transitionType)
    //             .build();
        
    //     // 添加到历史记录
    //     intentionTransitionHistory.computeIfAbsent(threadId, k -> new ArrayList<>()).add(transition);
    // }
    
    /**
     * 获取子意图映射
     */
    private Map<String, List<String>> getSubIntentionMapping(IntentionSettingsEntity settings) {
        try {
            return objectMapper.readValue(
                    settings.getSubIntentionMapping(), 
                    new TypeReference<Map<String, List<String>>>() {});
        } catch (JsonProcessingException e) {
            log.error("无法解析子意图映射", e);
            return new HashMap<>();
        }
    }
    
    /**
     * 模拟意图检测
     * 注意：这只是演示实现，实际应使用NLP或LLM模型
     */
    private IntentionResult simulateIntentionDetection(String messageContent, List<String> contextMessages, IntentionSettingsEntity settings) {
        // 获取可能的意图列表
        List<String> possibleIntentions = settings.getIntentionList();
        Map<String, Double> intentionScores = new HashMap<>();
        
        // 简单的关键词匹配策略（仅用于演示）
        for (String intention : possibleIntentions) {
            // 计算简单的相似度分数（实际应使用更复杂的算法）
            double score = calculateSimilarityScore(messageContent, intention);
            intentionScores.put(intention, score);
        }
        
        // 找到最高分数的意图
        Map.Entry<String, Double> topIntent = intentionScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);
        
        if (topIntent == null) {
            return IntentionResult.builder()
                    .primaryIntention("未知")
                    .primaryConfidence(0.0)
                    .isConfident(false)
                    .needClarification(true)
                    .clarificationQuestion("抱歉，我不太理解您的意思。您是想咨询产品信息，还是需要帮助解决问题？")
                    .build();
        }
        
        String primaryIntention = topIntent.getKey();
        double confidence = topIntent.getValue();
        
        // 判断是否达到置信度阈值
        boolean isConfident = confidence >= settings.getConfidenceThreshold();
        
        // 构建意图结果
        IntentionResult.IntentionResultBuilder builder = IntentionResult.builder()
                .primaryIntention(primaryIntention)
                .primaryConfidence(confidence)
                .allIntentions(intentionScores)
                .isConfident(isConfident);
        
        // 如果不够确定，需要澄清
        if (!isConfident) {
            builder.needClarification(true)
                   .clarificationQuestion("您是想咨询关于" + primaryIntention + "的问题吗？或者您有其他需要？");
        } else {
            // 获取推荐回复
            String recommendedResponse = getRecommendedResponse(primaryIntention);
            builder.recommendedResponse(recommendedResponse);
        }
        
        return builder.build();
    }
    
    /**
     * 计算文本与意图的匹配度
     * 这是一个简化实现，实际应使用更复杂的NLP算法
     */
    private double calculateSimilarityScore(String text, String intention) {
        // 将文本和意图转为小写以便比较
        text = text.toLowerCase();
        intention = intention.toLowerCase();
        
        // 简单判断文本中是否包含意图关键词
        if (text.contains(intention)) {
            return 0.9; // 高度匹配
        }
        
        // 根据相关词汇映射判断
        Map<String, List<String>> intentionKeywords = getIntentionKeywordMapping();
        if (intentionKeywords.containsKey(intention)) {
            List<String> keywords = intentionKeywords.get(intention);
            for (String keyword : keywords) {
                if (text.contains(keyword)) {
                    return 0.75; // 部分匹配
                }
            }
        }
        
        // 返回低置信度
        return 0.1;
    }
    
    /**
     * 获取意图关键词映射
     * 实际应从数据库或配置文件中加载
     */
    private Map<String, List<String>> getIntentionKeywordMapping() {
        Map<String, List<String>> mapping = new HashMap<>();
        
        mapping.put("产品咨询", List.of("产品", "功能", "特点", "怎么用", "操作", "使用说明"));
        mapping.put("价格查询", List.of("价格", "多少钱", "费用", "收费", "优惠"));
        mapping.put("订单查询", List.of("订单", "下单", "购买", "买了", "物流", "发货", "到货"));
        mapping.put("投诉反馈", List.of("投诉", "不满", "问题", "差评", "退款", "不好用"));
        mapping.put("售后服务", List.of("售后", "维修", "保修", "坏了", "故障", "换货"));
        
        return mapping;
    }
}