/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-13 18:30:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-14 10:33:54
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_text;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.stereotype.Component;

// import com.bytedesk.kbase.llm_chunk.ChunkStatusEnum;

// import lombok.extern.slf4j.Slf4j;

// /**
//  * 在应用启动时重建Text的全文索引
//  */
// @Slf4j
// @Component
// public class TextIndexRunner implements CommandLineRunner {

//     @Autowired
//     private TextElasticService textService;

//     @Autowired
//     private TextRepository textRepository;

//     @Override
//     public void run(String... args) throws Exception {
//         // 非Debug模式：检查是否需要重新构建索引
//         try {
//             // 获取所有需要索引但尚未被索引的文本
//             List<TextEntity> textsToIndex = textRepository.findByStatusAndVectorStatusNot(
//                 ChunkStatusEnum.SUCCESS.name(), ChunkStatusEnum.SUCCESS.name());
            
//             if (!textsToIndex.isEmpty()) {
//                 log.info("开始索引{}个Text实体", textsToIndex.size());
                
//                 int count = 0;
//                 for (TextEntity text : textsToIndex) {
//                     textService.indexText(text);
//                     count++;
                    
//                     if (count % 100 == 0) {
//                         log.info("已完成索引: {}/{}", count, textsToIndex.size());
//                     }
//                 }
                
//                 log.info("Text索引重建完成，共索引{}个文档", textsToIndex.size());
//             } else {
//                 log.info("没有Text实体需要索引");
//             }
//         } catch (Exception e) {
//             log.error("Text索引重建出错: {}", e.getMessage(), e);
//         }
//     }
// }
