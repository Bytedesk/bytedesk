/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-07 15:42:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-17 10:35:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq;

import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.alibaba.excel.EasyExcel;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadRestService;
import com.bytedesk.core.upload.UploadTypeEnum;
import com.bytedesk.core.upload.event.UploadCreateEvent;
import com.bytedesk.core.utils.BdFileUtils;
import com.bytedesk.kbase.faq.batch.FaqBatchService;
import com.bytedesk.kbase.faq.event.FaqCreateEvent;
import com.bytedesk.kbase.faq.event.FaqDeleteEvent;
import com.bytedesk.kbase.faq.event.FaqUpdateDocEvent;
import com.bytedesk.kbase.faq.mq.FaqMessageService;
import com.bytedesk.kbase.kbase.KbaseTypeEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class FaqEventListener {

    private final FaqRestService faqRestService;
    private final UploadRestService uploadRestService;
    private final FaqMessageService faqMessageService;
    private final FaqBatchService faqBatchService;
    private final Environment environment;

    @EventListener
    public void onUploadCreateEvent(UploadCreateEvent event) {
        UploadEntity upload = event.getUpload();
        if (UploadTypeEnum.FAQ.name().equalsIgnoreCase(upload.getType())) {
            // 检查文件类型是否为Excel
            String fileName = upload.getFileName();
            if (!BdFileUtils.isExcelFile(fileName)) {
                log.warn("不是Excel文件，无法导入常见问题: {}", fileName);
                return;
            }
            log.info("FaqEventListener FAQ: {}", fileName);

            try {
                Resource resource = uploadRestService.loadAsResource(upload.getFileName());
                if (resource.exists()) {
                    String filePath = resource.getFile().getAbsolutePath();
                    log.info("UploadEventListener loadAsResource: {}", filePath);
                    
                    // 获取导入方式配置，默认使用Spring Batch
                    boolean useSpringBatch = environment.getProperty("bytedesk.faq.use-spring-batch", Boolean.class, true);
                    
                    if (useSpringBatch) {
                        // 使用Spring Batch进行批量导入
                        log.info("使用Spring Batch导入FAQ: {}", filePath);
                        faqBatchService.importFaqFromExcel(
                                filePath,
                                KbaseTypeEnum.LLM.name(),
                                upload.getUid(),
                                upload.getKbUid(),
                                upload.getOrgUid());
                    } else {
                        // 使用原有的EasyExcel直接导入方式
                        log.info("使用EasyExcel直接导入FAQ: {}", filePath);
                        EasyExcel.read(filePath, FaqExcel.class, new FaqExcelListener(faqRestService,
                                KbaseTypeEnum.LLM.name(),
                                upload.getUid(),
                                upload.getKbUid(),
                                upload.getOrgUid())).sheet().doRead();
                    }
                }
            } catch (Exception e) {
                log.error("FaqEventListener UploadEventListener create error: {}", e.getMessage(), e);
            }
        }
    }

    // Faq仅用于全文搜索，通过消息队列异步处理
    @EventListener
    public void onFaqCreateEvent(FaqCreateEvent event) {
        FaqEntity faq = event.getFaq();
        log.info("FaqEventListener onFaqCreateEvent: {}", faq.getQuestion());
        
        // 使用消息队列异步处理索引，避免乐观锁冲突
        faqMessageService.sendToIndexQueue(faq.getUid());
    }

    // Faq用于全文搜索和向量搜索，通过消息队列异步处理
    @EventListener
    public void onFaqUpdateDocEvent(FaqUpdateDocEvent event) {
        FaqEntity faq = event.getFaq();
        log.info("FaqEventListener FaqUpdateDocEvent: {}", faq.getQuestion());
        
        // 使用消息队列异步处理索引更新
        faqMessageService.sendToIndexQueue(faq.getUid());
    }

    @EventListener
    public void onFaqDeleteEvent(FaqDeleteEvent event) {
        FaqEntity faq = event.getFaq();
        log.info("FaqEventListener onFaqDeleteEvent: {}", faq.getQuestion());
        
        // 删除操作通过消息队列异步处理
        faqMessageService.sendToDeleteQueue(faq.getUid());
    }
}
