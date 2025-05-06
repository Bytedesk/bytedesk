/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-07 15:42:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-06 17:45:06
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
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.alibaba.excel.EasyExcel;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadRestService;
import com.bytedesk.core.upload.UploadTypeEnum;
import com.bytedesk.core.upload.event.UploadCreateEvent;
import com.bytedesk.core.utils.BdFileUtils;
import com.bytedesk.kbase.faq.event.FaqCreateEvent;
import com.bytedesk.kbase.faq.event.FaqDeleteEvent;
import com.bytedesk.kbase.faq.event.FaqUpdateDocEvent;
import com.bytedesk.kbase.kbase.KbaseTypeEnum;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class FaqEventListener {

    private final FaqService faqService;

    private final FaqRestService faqRestService;

    private final UploadRestService uploadRestService;

    @EventListener
    public void onUploadCreateEvent(UploadCreateEvent event) {
        UploadEntity upload = event.getUpload();

        if (upload.getType().equalsIgnoreCase(UploadTypeEnum.FAQ.name())) {
            // 检查文件类型是否为Excel
            String fileName = upload.getFileName();
            if (!BdFileUtils.isExcelFile(fileName)) {
                log.warn("不是Excel文件，无法导入常见问题: {}", fileName);
                return;
            }
            log.info("FaqEventListener FAQ: {}", fileName);

            // 常见问题导入
            try {
                Resource resource = uploadRestService.loadAsResource(upload.getFileName());
                if (resource.exists()) {
                    String filePath = resource.getFile().getAbsolutePath();
                    log.info("UploadEventListener loadAsResource: {}", filePath);
                    // 导入自动回复
                    // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
                    // https://easyexcel.opensource.alibaba.com/docs/current/quickstart/read
                    EasyExcel.read(filePath, FaqExcel.class, new FaqExcelListener(faqRestService,
                            KbaseTypeEnum.LLM.name(),
                            upload.getUid(),
                            upload.getKbUid(),
                            upload.getOrgUid())).sheet().doRead();
                }
            } catch (Exception e) {
                log.error("FaqEventListener UploadEventListener create error: {}", e.getMessage());
            }
        }
    }

    // Faq仅用于全文搜索
    @EventListener
    public void onFaqCreateEvent(FaqCreateEvent event) {
        FaqEntity faq = event.getFaq();
        log.info("FaqEventListener onFaqCreateEvent: {}", faq.getQuestion());
        // 仅做全文索引
        faqService.indexFaq(faq);
    }

    // Faq仅用于全文搜索
    @EventListener
    public void onFaqUpdateDocEvent(FaqUpdateDocEvent event) {
        FaqEntity faq = event.getFaq();
        log.info("FaqEventListener FaqUpdateDocEvent: {}", faq.getQuestion());
        // 更新全文索引
        faqService.indexFaq(faq);
    }

    @EventListener
    public void onFaqDeleteEvent(FaqDeleteEvent event) {
        FaqEntity faq = event.getFaq();
        log.info("FaqEventListener onFaqDeleteEvent: {}", faq.getQuestion());
        // 从全文索引中删除
        boolean deleted = faqService.deleteFaq(faq.getUid());
        if (!deleted) {
            log.warn("从Elasticsearch中删除QA索引失败: {}", faq.getUid());
            // 可以考虑添加重试逻辑或者其他错误处理
        }
    }
}
