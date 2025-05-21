/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-12 10:34:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-06 17:13:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SensitiveWordFilter {
    
    private final ConcurrentHashMap<String, Boolean> sensitiveWordMap = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("sensitive-words.txt");
            try (InputStream is = resource.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sensitiveWordMap.put(line.trim().toLowerCase(), true);
                }
            }
            log.info("Loaded {} sensitive words", sensitiveWordMap.size());
        } catch (IOException e) {
            log.error("Failed to load sensitive words", e);
        }
    }
    
    public Boolean containsSensitiveWords(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        
        String lowerText = text.toLowerCase();
        return sensitiveWordMap.keySet().stream()
            .anyMatch(lowerText::contains);
    }
    
    public Set<String> findSensitiveWords(String text) {
        Set<String> found = new HashSet<>();
        if (text == null || text.isEmpty()) {
            return found;
        }
        
        String lowerText = text.toLowerCase();
        sensitiveWordMap.keySet().stream()
            .filter(lowerText::contains)
            .forEach(found::add);
            
        return found;
    }
    
    public String filter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        String filtered = text;
        for (String word : sensitiveWordMap.keySet()) {
            filtered = filtered.replaceAll("(?i)" + word, "*".repeat(word.length()));
        }
        return filtered;
    }
} 