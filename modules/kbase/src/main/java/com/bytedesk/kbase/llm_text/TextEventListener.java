/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:44:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-12 18:05:18
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
import com.bytedesk.core.utils.BdFileUtils;
import com.bytedesk.kbase.kbase.KbaseTypeEnum;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class TextEventListener {

    private final TextRestService textRestService;

    private final UploadRestService uploadRestService;

    @EventListener
    public void onUploadCreateEvent(UploadCreateEvent event) {
        UploadEntity upload = event.getUpload();

        if (UploadTypeEnum.LLM_TEXT.name().equalsIgnoreCase(upload.getType())) {
            // 检查文件类型是否为Excel
            String fileName = upload.getFileName();
            if (!BdFileUtils.isExcelFile(fileName)) {
                log.warn("不是Excel文件，无法导入: {}", fileName);
                return;
            }
            log.info("TextEventListener: {}", fileName);

            // 导入
            try {
                Resource resource = uploadRestService.loadAsResource(upload.getFileName());
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
 
}

