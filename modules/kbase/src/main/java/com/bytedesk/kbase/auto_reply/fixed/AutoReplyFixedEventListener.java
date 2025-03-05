/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-07 16:17:49
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-05 09:48:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.auto_reply.fixed;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.core.io.Resource;

import com.alibaba.excel.EasyExcel;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadRestService;
import com.bytedesk.core.upload.UploadTypeEnum;
import com.bytedesk.core.upload.event.UploadCreateEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoReplyFixedEventListener {

    private final UploadRestService uploadRestService;

    private final AutoReplyFixedRestService autoReplyFixedRestService;

    @EventListener
    public void onUploadCreateEvent(UploadCreateEvent event) {
        UploadEntity upload = event.getUpload();
        log.info("AutoReplyFixedEventListener UploadEventListener create: {}", upload.toString());

        try {
            Resource resource = uploadRestService.loadAsResource(upload.getFileName());
            if (resource.exists()) {
                String filePath = resource.getFile().getAbsolutePath();
                log.info("UploadEventListener loadAsResource: {}", filePath);
                if (upload.getType().equalsIgnoreCase(UploadTypeEnum.AUTOREPLY_FIXED.name())) {
                    // 导入自动回复
                    // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
                    // https://easyexcel.opensource.alibaba.com/docs/current/quickstart/read
                    EasyExcel.read(filePath, AutoReplyFixedExcel.class,
                            new AutoReplyFixedExcelListener(autoReplyFixedRestService,
                                    // upload.getCategoryUid(),
                                    upload.getKbUid(),
                                    upload.getOrgUid()))
                            .sheet().doRead();
                }
            }
        } catch (Exception e) {
            log.error("AutoReplyFixedEventListener UploadEventListener create error: {}", e.getMessage());
        }
    }

}
