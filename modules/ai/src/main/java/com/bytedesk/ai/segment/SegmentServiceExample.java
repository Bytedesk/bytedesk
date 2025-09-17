/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-09-17 15:45:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-17 15:45:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.segment;

/**
 * 分词服务使用示例
 * 
 * 使用示例：
 * 
 * 1. 基础分词 - 获取词语列表
 *    POST /segment/api/v1/words?text=这是一个伸手不见五指的黑夜。我叫孙悟空，我爱北京，我爱学习。
 *    返回：["这是", "一个", "伸手不见五指", "的", "黑夜", "。", "我", "叫", "孙悟空", "，", "我爱", "北京", "，", "我爱", "学习", "。"]
 * 
 * 2. 详细分词 - 获取包含位置信息的分词结果
 *    POST /segment/api/v1/details?text=这是一个伸手不见五指的黑夜。
 *    返回：[{"word":"这是","startIndex":0,"endIndex":2,"length":2}, ...]
 * 
 * 3. 词频统计
 *    POST /segment/api/v1/count?text=这是一个伸手不见五指的黑夜。我叫孙悟空，我爱北京，我爱学习。我爱北京天安门。
 *    返回：{"这是":1, "一个":1, "伸手不见五指":1, "我爱":3, "北京":2, ...}
 * 
 * 4. 高级分词 - 支持多种类型和过滤选项
 *    POST /segment/api/v1/segment
 *    Body: {
 *        "text": "这是一个伸手不见五指的黑夜。我叫孙悟空，我爱北京，我爱学习。",
 *        "type": "word",
 *        "filterPunctuation": true,
 *        "minWordLength": 2
 *    }
 * 
 * 5. 批量分词
 *    POST /segment/api/v1/batch?type=word
 *    Body: ["这是第一个测试文本", "这是第二个测试文本", "学习使我快乐"]
 * 
 * 6. 简单分词 - GET请求
 *    GET /segment/api/v1/simple?text=这是一个伸手不见五指的黑夜。&filterPunctuation=true&minLength=2
 * 
 * @author jackning
 */
public class SegmentServiceExample {
    
    /**
     * 使用 SegmentService 的示例代码
     */
    public static void example() {
        SegmentService segmentService = new SegmentService();
        String text = "这是一个伸手不见五指的黑夜。我叫孙悟空，我爱北京，我爱学习。";
        
        // 1. 基础分词
        System.out.println("=== 基础分词 ===");
        System.out.println(segmentService.segmentWords(text));
        
        // 2. 详细分词
        System.out.println("\n=== 详细分词 ===");
        System.out.println(segmentService.segmentDetails(text));
        
        // 3. 词频统计
        System.out.println("\n=== 词频统计 ===");
        System.out.println(segmentService.wordCount(text));
        
        // 4. 高级分词
        System.out.println("\n=== 高级分词 ===");
        SegmentRequest request = new SegmentRequest();
        request.setText(text);
        request.setType("word");
        request.setFilterPunctuation(true);
        request.setMinWordLength(2);
        System.out.println(segmentService.segment(request));
    }
    
}