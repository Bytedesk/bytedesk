/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:44:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-12 17:00:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_text;

import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.alibaba.excel.EasyExcel;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadRestService;
import com.bytedesk.core.upload.UploadTypeEnum;
import com.bytedesk.core.upload.event.UploadCreateEvent;
import com.bytedesk.core.utils.BdUploadUtils;
import com.bytedesk.kbase.kbase.KbaseTypeEnum;
import com.bytedesk.kbase.llm_text.elastic.TextElasticService;
import com.bytedesk.kbase.llm_text.event.TextCreateEvent;
import com.bytedesk.kbase.llm_text.event.TextDeleteEvent;
import com.bytedesk.kbase.llm_text.event.TextUpdateDocEvent;
import com.bytedesk.kbase.llm_text.vector.TextVectorService;

import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TextEventListener {

    private final TextRestService textRestService;
    private final TextElasticService textElasticService;
    @Autowired(required = false)
    private TextVectorService textVectorService;
    private final UploadRestService uploadRestService;

    public TextEventListener(TextRestService textRestService, TextElasticService textElasticService, UploadRestService uploadRestService) {
        this.textRestService = textRestService;
        this.textElasticService = textElasticService;
        this.uploadRestService = uploadRestService;
    }

    @EventListener
    public void onUploadCreateEvent(UploadCreateEvent event) {
        UploadEntity upload = event.getUpload();

        if (UploadTypeEnum.LLM_TEXT.name().equalsIgnoreCase(upload.getType())) {
            // 检查文件类型是否为Excel
            String fileName = upload.getFileName();
            if (!BdUploadUtils.isExcelFile(fileName)) {
                log.warn("不是Excel文件，无法导入: {}", fileName);
                return;
            }
            log.info("TextEventListener: {}", fileName);

            // 导入
            try {
                Resource resource = uploadRestService.loadAsResource(upload);
                if (resource.exists()) {
                    String filePath = resource.getFile().getAbsolutePath();
                    log.info("TextEventListener loadAsResource: {}", filePath);
                    // 导入自动回复
                    // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
                    // https://easyexcel.opensource.alibaba.com/docs/current/quickstart/read
                    EasyExcel.read(filePath, TextExcel.class, new TextExcelListener(textRestService,
                            KbaseTypeEnum.LLM.name(),
                            upload.getUid(),
                            upload.getKbUid(),
                            upload.getOrgUid())).sheet().doRead();
                }
            } catch (Exception e) {
                log.error("TextEventListener create error: {}", e.getMessage());
            }
        }
    }

    // Text用于全文搜索和向量索引
    @EventListener
    public void onTextCreateEvent(TextCreateEvent event) {
        TextEntity text = event.getText();
        log.info("TextEventListener onTextCreateEvent: {}", text.getTitle());
        
        // 进行全文索引
        textElasticService.indexText(text);
        
        // 进行向量索引
        if (textVectorService != null) {
            try {
                textVectorService.indexTextVector(text);
            } catch (Exception e) {
                log.error("文本向量索引创建失败: {}, 错误: {}", text.getTitle(), e.getMessage());
            }
        }
    }

    @EventListener
    public void onTextUpdateDocEvent(TextUpdateDocEvent event) {
        TextEntity text = event.getText();
        log.info("TextEventListener onTextUpdateDocEvent: {}", text.getTitle());
        
        // 更新全文索引
        textElasticService.indexText(text);
        
        // 更新向量索引
        if (textVectorService != null) {
            try {
                // 先删除旧的向量索引
                textVectorService.deleteTextVector(text);
                // 再创建新的向量索引
                textVectorService.indexTextVector(text);
            } catch (Exception e) {
                log.error("文本向量索引更新失败: {}, 错误: {}", text.getTitle(), e.getMessage());
            }
        }
    }

    @EventListener
    public void onTextDeleteEvent(TextDeleteEvent event) {
        TextEntity text = event.getText();
        log.info("TextEventListener onTextDeleteEvent: {}", text.getTitle());
        
        // 从全文索引中删除
        boolean deleted = textElasticService.deleteText(text.getUid());
        if (!deleted) {
            log.warn("从Elasticsearch中删除Text全文索引失败: {}", text.getUid());
        }
        
        // 从向量索引中删除
        if (textVectorService != null) {
            boolean vectorDeleted = textVectorService.deleteTextVector(text);
            if (!vectorDeleted) {
                log.warn("从向量存储中删除Text索引失败: {}", text.getUid());
            }
        }
        if (!deleted) {
            log.warn("从Elasticsearch中删除Text索引失败: {}", text.getUid());
            // 可以考虑添加重试逻辑或者其他错误处理
        }
    }
}

