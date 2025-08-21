/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-17 10:44:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-17 10:44:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_faq.batch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bytedesk.kbase.llm_faq.FaqEntity;
import com.bytedesk.kbase.llm_faq.FaqRestService;
import com.bytedesk.kbase.llm_faq.mq.FaqMessageService;

import lombok.extern.slf4j.Slf4j;

/**
 * FAQ数据写入器
 * 将处理后的FaqEntity批量保存到数据库
 */
@Slf4j
@Component
public class FaqItemWriter implements ItemWriter<FaqEntity> {

    @Autowired
    private FaqRestService faqRestService;
    
    @Autowired
    private FaqMessageService faqMessageService;

    /**
     * 批量写入FAQ数据
     * 
     * @param items 需要写入的FAQ实体列表
     * @throws Exception 写入过程中的异常
     */
    @Override
    public void write(Chunk<? extends FaqEntity> items) throws Exception {
        List<FaqEntity> validItems = new ArrayList<>();
        List<String> faqUids = new ArrayList<>();
        
        // 过滤掉null值（已存在的项）
        for (FaqEntity item : items) {
            if (item != null) {
                validItems.add(item);
                faqUids.add(item.getUid());
            }
        }
        
        if (!validItems.isEmpty()) {
            log.info("批量保存FAQ: {}", validItems.size());
            faqRestService.save(validItems);
            
            // 批量发送到索引队列
            faqMessageService.batchSendToIndexQueue(faqUids);
        }
    }
}
