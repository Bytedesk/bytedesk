/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-17 15:25:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-17 15:25:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.segment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 分词服务测试类
 * @author jackning
 */
@ExtendWith(MockitoExtension.class)
public class SegmentServiceTest {
    
    private SegmentService segmentService;
    
    @BeforeEach
    void setUp() {
        segmentService = new SegmentService();
    }
    
    @Test
    void testSegmentWords() {
        String text = "这是一个伸手不见五指的黑夜。我叫孙悟空，我爱北京，我爱学习。";
        
        List<String> words = segmentService.segmentWords(text);
        
        assertNotNull(words);
        assertFalse(words.isEmpty());
        assertTrue(words.contains("这是"));
        assertTrue(words.contains("伸手不见五指"));
        assertTrue(words.contains("孙悟空"));
        assertTrue(words.contains("北京"));
        assertTrue(words.contains("学习"));
        
        System.out.println("基础分词结果: " + words);
    }
    
    @Test
    void testSegmentDetails() {
        String text = "这是一个伸手不见五指的黑夜。";
        
        List<SegmentWordDetail> details = segmentService.segmentDetails(text);
        
        assertNotNull(details);
        assertFalse(details.isEmpty());
        
        // 验证第一个词的详细信息
        SegmentWordDetail firstWord = details.get(0);
        assertEquals("这是", firstWord.getWord());
        assertEquals(0, firstWord.getStartIndex());
        assertEquals(2, firstWord.getEndIndex());
        assertEquals(2, firstWord.getLength());
        
        System.out.println("详细分词结果: " + details);
    }
    
    @Test
    void testWordCount() {
        String text = "这是一个伸手不见五指的黑夜。我叫孙悟空，我爱北京，我爱学习。";
        
        Map<String, Integer> wordCount = segmentService.wordCount(text);
        
        assertNotNull(wordCount);
        assertFalse(wordCount.isEmpty());
        assertEquals(2, wordCount.get("我爱"));
        assertEquals(1, wordCount.get("黑夜"));
        
        System.out.println("词频统计结果: " + wordCount);
    }
    
    @Test
    void testSegmentWithWordType() {
        SegmentRequest request = new SegmentRequest();
        request.setText("这是一个伸手不见五指的黑夜。我叫孙悟空，我爱北京，我爱学习。");
        request.setType("word");
        request.setFilterPunctuation(true);
        request.setMinWordLength(2);
        
        SegmentResponse response = segmentService.segment(request);
        
        assertNotNull(response);
        assertEquals("word", response.getType());
        assertNotNull(response.getWords());
        assertTrue(response.getTotalWords() > 0);
        assertTrue(response.getUniqueWords() > 0);
        assertNotNull(response.getProcessingTimeMs());
        
        // 验证过滤效果
        assertFalse(response.getWords().contains("。"));
        assertFalse(response.getWords().contains("，"));
        
        System.out.println("高级分词(word)结果: " + response);
    }
    
    @Test
    void testSegmentWithDetailType() {
        SegmentRequest request = new SegmentRequest();
        request.setText("学习我的最爱");
        request.setType("detail");
        
        SegmentResponse response = segmentService.segment(request);
        
        assertNotNull(response);
        assertEquals("detail", response.getType());
        assertNotNull(response.getDetails());
        assertTrue(response.getTotalWords() > 0);
        
        System.out.println("高级分词(detail)结果: " + response);
    }
    
    @Test
    void testSegmentWithCountType() {
        SegmentRequest request = new SegmentRequest();
        request.setText("这是一个伸手不见五指的黑夜。我叫孙悟空，我爱北京，我爱学习。");
        request.setType("count");
        request.setFilterPunctuation(true);
        
        SegmentResponse response = segmentService.segment(request);
        
        assertNotNull(response);
        assertEquals("count", response.getType());
        assertNotNull(response.getWordCount());
        assertTrue(response.getTotalWords() > 0);
        assertTrue(response.getUniqueWords() > 0);
        assertEquals(2, response.getWordCount().get("我爱"));
        
        System.out.println("高级分词(count)结果: " + response);
    }
    
    @Test
    void testBatchSegment() {
        List<String> texts = Arrays.asList(
                "这是第一个测试文本",
                "这是第二个测试文本",
                "学习使我快乐"
        );
        
        List<SegmentResponse> responses = segmentService.batchSegment(texts, "word");
        
        assertNotNull(responses);
        assertEquals(3, responses.size());
        
        for (SegmentResponse response : responses) {
            assertNotNull(response.getWords());
            assertTrue(response.getTotalWords() > 0);
        }
        
        System.out.println("批量分词结果: " + responses);
    }
    
    @Test
    void testFilterWords() {
        List<String> words = Arrays.asList("这是", "一个", "。", "，", "测试", "文本", "a");
        
        List<String> filtered = segmentService.filterWords(words, true, 2);
        
        assertNotNull(filtered);
        assertTrue(filtered.contains("这是"));
        assertTrue(filtered.contains("一个"));
        assertTrue(filtered.contains("测试"));
        assertTrue(filtered.contains("文本"));
        assertFalse(filtered.contains("。"));
        assertFalse(filtered.contains("，"));
        assertFalse(filtered.contains("a")); // 长度小于2
        
        System.out.println("过滤结果: " + filtered);
    }
    
    @Test
    void testEnglishText() {
        String text = "good 学生爱学习";
        
        List<String> words = segmentService.segmentWords(text);
        
        assertNotNull(words);
        assertTrue(words.contains("good"));
        assertTrue(words.contains("学生"));
        assertTrue(words.contains("学习"));
        
        System.out.println("英文混合分词结果: " + words);
    }
    
    @Test
    void testEmptyText() {
        List<String> words = segmentService.segmentWords("");
        assertNotNull(words);
        assertTrue(words.isEmpty());
        
        List<String> nullWords = segmentService.segmentWords(null);
        assertNotNull(nullWords);
        assertTrue(nullWords.isEmpty());
    }
    
    @Test
    void testInvalidSegmentType() {
        SegmentRequest request = new SegmentRequest();
        request.setText("测试文本");
        request.setType("invalid");
        
        assertThrows(RuntimeException.class, () -> {
            segmentService.segment(request);
        });
    }
    
}