/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-07 15:42:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-24 11:36:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm.qa;

import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.alibaba.excel.EasyExcel;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadRestService;
import com.bytedesk.core.upload.UploadTypeEnum;
import com.bytedesk.core.upload.event.UploadCreateEvent;
import com.bytedesk.core.utils.BdFileUtils;
import com.bytedesk.kbase.faq.FaqEntity;
import com.bytedesk.kbase.faq.event.FaqCreateEvent;
import com.bytedesk.kbase.llm.qa.event.QaCreateEvent;
import com.bytedesk.kbase.llm.qa.event.QaDeleteEvent;
import com.bytedesk.kbase.llm.qa.event.QaUpdateDocEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class QaEventListener {

    private final QaService qaService;

    private final QaRestService qaRestService;

    private final UploadRestService uploadRestService;

    @EventListener
    public void onUploadCreateEvent(UploadCreateEvent event) {
        UploadEntity upload = event.getUpload();
        // 
        if (upload.getType().equalsIgnoreCase(UploadTypeEnum.LLM_QA.name())) {
            // 检查文件类型是否为Excel
            String fileName = upload.getFileName();
            if (!BdFileUtils.isExcelFile(fileName)) {
                log.warn("不是Excel文件，无法导入问答对: {}", fileName);
                return;
            }
            log.info("QaEventListener LLM_QA: {}", fileName);
            
            // llm qa 问答对导入
            try {
                Resource resource = uploadRestService.loadAsResource(fileName);
                if (resource.exists()) {
                    String filePath = resource.getFile().getAbsolutePath();
                    log.info("UploadEventListener loadAsResource: {}", filePath);
                    // 导入自动回复
                    // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
                    // https://easyexcel.opensource.alibaba.com/docs/current/quickstart/read
                    EasyExcel.read(filePath, QaExcel.class, new QaExcelListener(qaRestService,
                            UploadTypeEnum.LLM_QA.name(),
                            upload.getUid(),
                            upload.getKbUid(),
                            upload.getOrgUid())).sheet().doRead();
                }
            } catch (Exception e) {
                log.error("QaEventListener UploadEventListener create error: {}", e.getMessage());
            }
        }
    }

    // Qa仅用于全文搜索
    @EventListener
    public void onQaCreateEvent(QaCreateEvent event) {
        QaEntity qa = event.getQa();
        log.info("SpringAIEventListener onQaCreateEvent: {}", qa.getQuestion());
        // 仅做全文索引
        qaService.indexQa(qa);
    }

    // Qa仅用于全文搜索
    @EventListener
    public void onQaUpdateDocEvent(QaUpdateDocEvent event) {
        QaEntity qa = event.getQa();
        log.info("SpringAIEventListener QaUpdateDocEvent: {}", qa.getQuestion());
        // 更新全文索引
        qaService.indexQa(qa);
    }

    @EventListener
    public void onQaDeleteEvent(QaDeleteEvent event) {
        QaEntity qa = event.getQa();
        log.info("SpringAIEventListener onQaDeleteEvent: {}", qa.getQuestion());
        // 从全文索引中删除
        boolean deleted = qaService.deleteQa(qa.getUid());
        if (!deleted) {
            log.warn("从Elasticsearch中删除QA索引失败: {}", qa.getUid());
            // 可以考虑添加重试逻辑或者其他错误处理
        }
    }

    @EventListener
    public void onFaqCreateEvent(FaqCreateEvent event) {
        FaqEntity faq = event.getFaq();
        log.info("SpringAIEventListener onFaqCreateEvent: {}", faq.getQuestion());
        // TODO: 将faq转换为qa, 用于全文搜索，kbUid待处理
        // if (faq.isAutoSyncLlmQa()) {
        //     // 将faq转换为qa, 用于全文搜索
        //     QaRequest qaRequest = QaRequest.builder()
        //         .question(faq.getQuestion())
        //         .answer(faq.getAnswer())
        //         .categoryUid(faq.getCategoryUid())
        //         .orgUid(faq.getOrgUid())
        //         .build();
        // }

    }


}
