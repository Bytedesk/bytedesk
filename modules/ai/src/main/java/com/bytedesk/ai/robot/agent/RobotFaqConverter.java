/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-23 16:25:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-23 16:25:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bytedesk.kbase.faq.FaqRequest;
import com.bytedesk.kbase.faq.FaqStatusEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.kbase.llm_chunk.ChunkStatusEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * FAQ数据转换工具类，用于解析生成FAQ的结果并转换为FaqRequest对象
 */
@Slf4j
public class RobotFaqConverter {

    /**
     * 将generateFaq返回的字符串转换为FaqRequest对象列表
     * 
     * @param jsonString 从大模型返回的JSON字符串
     * @return FaqRequest对象列表
     */
    public static List<FaqRequest> convertToFaqRequests(String jsonString) {
        // 如果输入为空，返回空列表
        if (jsonString == null || jsonString.isEmpty()) {
            return Collections.emptyList();
        }

        // 去除可能存在的```json和```标记
        String cleanedJson = cleanJsonString(jsonString);
        
        List<FaqRequest> faqRequests = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // 解析JSON
            JsonNode rootNode = objectMapper.readTree(cleanedJson);
            
            // 检查是否为直接的JSON数组格式
            JsonNode faqArrayNode;
            if (rootNode.isArray()) {
                // 直接使用根节点作为数组
                faqArrayNode = rootNode;
            } else {
                // 兼容旧格式：查找qaPairs节点
                faqArrayNode = rootNode.get("qaPairs");
                if (faqArrayNode == null || !faqArrayNode.isArray()) {
                    log.error("无法找到FAQ数组或格式不正确");
                    return Collections.emptyList();
                }
            }

            // 遍历FAQ数组
            for (JsonNode faqItem : faqArrayNode) {
                // 创建新的FaqRequest对象
                FaqRequest faqRequest = new FaqRequest();
                
                // 设置必要字段
                if (faqItem.has("question")) {
                    faqRequest.setQuestion(faqItem.get("question").asText());
                }
                
                if (faqItem.has("answer")) {
                    faqRequest.setAnswer(faqItem.get("answer").asText());
                }
                
                // 设置标签 - 首先检查tagList字段(新格式)
                if (faqItem.has("tagList") && faqItem.get("tagList").isArray()) {
                    List<String> tagList = new ArrayList<>();
                    for (JsonNode tagNode : faqItem.get("tagList")) {
                        tagList.add(tagNode.asText());
                    }
                    faqRequest.setTagList(tagList);
                } 
                // 兼容旧格式的tags字段
                else if (faqItem.has("tags") && faqItem.get("tags").isArray()) {
                    List<String> tagList = new ArrayList<>();
                    for (JsonNode tagNode : faqItem.get("tags")) {
                        tagList.add(tagNode.asText());
                    }
                    faqRequest.setTagList(tagList);
                }
                
                // 设置默认值
                faqRequest.setType(MessageTypeEnum.TEXT.name());
                faqRequest.setStatus(FaqStatusEnum.NEW.name());
                faqRequest.setVectorStatus(ChunkStatusEnum.NEW.name());
                faqRequest.setEnabled(true);
                
                // 将FaqRequest添加到列表
                faqRequests.add(faqRequest);
            }
            
            return faqRequests;
        } catch (JsonProcessingException e) {
            log.error("解析JSON时出错: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * 清理JSON字符串，去除```json和```标记
     * 
     * @param jsonString 原始JSON字符串
     * @return 清理后的JSON字符串
     */
    private static String cleanJsonString(String jsonString) {
        String cleaned = jsonString;
        
        // 去除开头的```json
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        
        // 去除结尾的```
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        
        return cleaned.trim();
    }
}
