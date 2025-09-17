/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-17 15:15:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-17 15:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.segment;

import com.github.houbb.segment.api.ISegmentResult;
import com.github.houbb.segment.support.segment.result.impl.SegmentResultHandlers;
import com.github.houbb.segment.util.SegmentHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 分词服务类
 * @author jackning
 */
@Slf4j
@Service
public class SegmentService {
    
    /**
     * 标点符号正则表达式
     */
    private static final Pattern PUNCTUATION_PATTERN = Pattern.compile("[\\p{Punct}\\p{Space}]+");
    
    /**
     * 基础分词 - 返回词语列表
     * 
     * @param text 待分词文本
     * @return 词语列表
     */
    public List<String> segmentWords(String text) {
        if (!StringUtils.hasText(text)) {
            return new ArrayList<>();
        }
        
        try {
            return SegmentHelper.segment(text, SegmentResultHandlers.word());
        } catch (Exception e) {
            log.error("分词失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 详细分词 - 返回包含位置信息的分词结果
     * 
     * @param text 待分词文本
     * @return 详细分词结果列表
     */
    public List<SegmentWordDetail> segmentDetails(String text) {
        if (!StringUtils.hasText(text)) {
            return new ArrayList<>();
        }
        
        try {
            List<ISegmentResult> segmentResults = SegmentHelper.segment(text);
            return segmentResults.stream()
                    .map(result -> new SegmentWordDetail(
                            result.word(),
                            result.startIndex(),
                            result.endIndex()
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("详细分词失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 词频统计
     * 
     * @param text 待分词文本
     * @return 词频统计Map
     */
    public Map<String, Integer> wordCount(String text) {
        if (!StringUtils.hasText(text)) {
            return Map.of();
        }
        
        try {
            return SegmentHelper.wordCount(text);
        } catch (Exception e) {
            log.error("词频统计失败: {}", e.getMessage(), e);
            return Map.of();
        }
    }
    
    /**
     * 高性能分词接口
     * 
     * @param request 分词请求
     * @return 分词响应
     */
    public SegmentResponse segment(SegmentRequest request) {
        if (request == null || !StringUtils.hasText(request.getText())) {
            return new SegmentResponse()
                    .setOriginalText("")
                    .setType(request != null ? request.getType() : "word")
                    .setWords(new ArrayList<>())
                    .setTotalWords(0)
                    .setUniqueWords(0);
        }
        
        long startTime = System.currentTimeMillis();
        String text = request.getText();
        String type = request.getType() != null ? request.getType() : "word";
        
        SegmentResponse response = new SegmentResponse()
                .setOriginalText(text)
                .setType(type);
        
        try {
            switch (type.toLowerCase()) {
                case "word":
                    List<String> words = segmentWords(text);
                    words = filterWords(words, 
                            request.getFilterPunctuation() != null ? request.getFilterPunctuation() : false,
                            request.getMinWordLength() != null ? request.getMinWordLength() : 1);
                    response.setWords(words)
                            .setTotalWords(words.size())
                            .setUniqueWords((int) words.stream().distinct().count());
                    break;
                    
                case "detail":
                    List<SegmentWordDetail> details = segmentDetails(text);
                    if (request.getFilterPunctuation() != null && request.getFilterPunctuation()) {
                        details = details.stream()
                                .filter(detail -> !PUNCTUATION_PATTERN.matcher(detail.getWord()).matches())
                                .collect(Collectors.toList());
                    }
                    if (request.getMinWordLength() != null && request.getMinWordLength() > 1) {
                        details = details.stream()
                                .filter(detail -> detail.getWord().length() >= request.getMinWordLength())
                                .collect(Collectors.toList());
                    }
                    response.setDetails(details)
                            .setTotalWords(details.size())
                            .setUniqueWords((int) details.stream()
                                    .map(SegmentWordDetail::getWord)
                                    .distinct().count());
                    break;
                    
                case "count":
                    Map<String, Integer> wordCountMap = wordCount(text);
                    if (request.getFilterPunctuation() != null && request.getFilterPunctuation()) {
                        wordCountMap = wordCountMap.entrySet().stream()
                                .filter(entry -> !PUNCTUATION_PATTERN.matcher(entry.getKey()).matches())
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                    }
                    if (request.getMinWordLength() != null && request.getMinWordLength() > 1) {
                        wordCountMap = wordCountMap.entrySet().stream()
                                .filter(entry -> entry.getKey().length() >= request.getMinWordLength())
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                    }
                    response.setWordCount(wordCountMap)
                            .setTotalWords(wordCountMap.values().stream().mapToInt(Integer::intValue).sum())
                            .setUniqueWords(wordCountMap.size());
                    break;
                    
                default:
                    throw new IllegalArgumentException("不支持的分词类型: " + type);
            }
        } catch (Exception e) {
            log.error("分词处理失败: {}", e.getMessage(), e);
            throw new RuntimeException("分词处理失败: " + e.getMessage(), e);
        }
        
        long endTime = System.currentTimeMillis();
        response.setProcessingTimeMs(endTime - startTime);
        
        return response;
    }
    
    /**
     * 批量分词
     * 
     * @param texts 文本列表
     * @param type 分词类型
     * @return 分词结果列表
     */
    public List<SegmentResponse> batchSegment(List<String> texts, String type) {
        if (texts == null || texts.isEmpty()) {
            return new ArrayList<>();
        }
        
        return texts.stream()
                .map(text -> {
                    SegmentRequest request = new SegmentRequest();
                    request.setText(text);
                    request.setType(type);
                    return segment(request);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 过滤分词结果
     * 
     * @param words 词语列表
     * @param filterPunctuation 是否过滤标点符号
     * @param minLength 最小词长度
     * @return 过滤后的词语列表
     */
    public List<String> filterWords(List<String> words, boolean filterPunctuation, int minLength) {
        if (words == null || words.isEmpty()) {
            return new ArrayList<>();
        }
        
        return words.stream()
                .filter(word -> {
                    if (filterPunctuation && PUNCTUATION_PATTERN.matcher(word).matches()) {
                        return false;
                    }
                    return word.length() >= minLength;
                })
                .collect(Collectors.toList());
    }
    
}