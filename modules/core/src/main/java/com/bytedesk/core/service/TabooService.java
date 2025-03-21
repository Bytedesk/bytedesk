/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-22 18:26:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-22 18:26:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.bytedesk.core.exception.TabooException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for handling sensitive word filtering
 */
@Slf4j
@Service
public class TabooService {

    // 敏感词集合
    private final Set<String> tabooWords = ConcurrentHashMap.newKeySet();
    
    /**
     * Initialize with some default sensitive words
     */
    @PostConstruct
    public void init() {
        // 初始化默认敏感词，实际项目中可以从数据库或配置文件加载
        tabooWords.add("暴力");
        tabooWords.add("色情");
        tabooWords.add("赌博");
        tabooWords.add("毒品");
        log.info("敏感词过滤器初始化完成，共加载 {} 个敏感词", tabooWords.size());
    }
    
    /**
     * Add a new sensitive word
     * 
     * @param word the word to add
     */
    public void addTabooWord(String word) {
        tabooWords.add(word);
    }
    
    /**
     * Remove a sensitive word
     * 
     * @param word the word to remove
     */
    public void removeTabooWord(String word) {
        tabooWords.remove(word);
    }
    
    /**
     * Check if a string contains sensitive words
     * 
     * @param content the content to check
     * @return true if contains sensitive words
     */
    public boolean containsTabooWords(String content) {
        if (content == null || content.isEmpty()) {
            return false;
        }
        
        for (String word : tabooWords) {
            if (content.contains(word)) {
                log.warn("检测到敏感词: {}", word);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Filter sensitive words from content (replace with *)
     * 
     * @param content the content to filter
     * @return filtered content
     */
    public String filterTabooWords(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }
        
        String result = content;
        for (String word : tabooWords) {
            if (result.contains(word)) {
                String replacement = "*".repeat(word.length());
                result = result.replace(word, replacement);
            }
        }
        
        return result;
    }
    
    /**
     * Filter content, either throw exception or replace sensitive words
     * 
     * @param content the content to filter
     * @param throwException whether to throw exception
     * @return filtered content if not throwing exception
     */
    public String processContent(String content, boolean throwException) {
        if (containsTabooWords(content)) {
            if (throwException) {
                throw new TabooException("内容包含敏感词: " + content);
            } else {
                return filterTabooWords(content);
            }
        }
        return content;
    }
} 