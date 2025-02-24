/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-24 09:34:56
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-24 23:55:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.ai.document.Document;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.ai.provider.vendors.zhipuai.ZhipuaiChatService;
import com.bytedesk.ai.springai.event.VectorSplitEvent;
import com.bytedesk.kbase.faq.FaqRestService;
import com.bytedesk.kbase.upload.UploadEntity;
import com.bytedesk.kbase.upload.UploadTypeEnum;
import com.bytedesk.kbase.upload.event.UploadCreateEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpringAIEventListener {
    
    private final SpringAIVectorService springAiVectorService;

    private final Optional<ZhipuaiChatService> zhipuaiChatService;

    private final FaqRestService faqRestService;

    @EventListener
    public void onUploadCreateEvent(UploadCreateEvent event) throws IOException {
        UploadEntity upload = event.getUpload();
        log.info("SpringAIEventListener onUploadCreateEvent: {}", upload.toString());
        // etl分块处理
        if (upload.getType().equals(UploadTypeEnum.LLM.name())) {
            // 通知python ai模块处理
            springAiVectorService.readSplitWriteToVectorStore(upload);
            return;
        }
    }

    @EventListener
    public void onVectorSplitEvent(VectorSplitEvent event) {
        log.info("SpringAIEventListener onVectorSplitEvent: {}", event.toString());
        List<Document> docList = event.getDocuments();
        String kbUid = event.getKbUid();
        String orgUid = event.getOrgUid();
        // 生成问答对
		for (Document document : docList) {
            // 调用模型生成问答对
            String qaPairs = zhipuaiChatService.generateQaPairsAsync(document.getText());
            // log.info("generateQaPairsAsync qaPairs {}", qaPairs);
            // Save QA pairs to database
            faqRestService.saveQaPairs(qaPairs, kbUid, orgUid, document.getId());
        }
	
    }
    
}
