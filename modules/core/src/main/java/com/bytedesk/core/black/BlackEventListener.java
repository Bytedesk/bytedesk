/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-17 11:03:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-04 10:27:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.black;

import org.springframework.core.io.Resource;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.quartz.event.QuartzDay0Event;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadRestService;
import com.bytedesk.core.upload.UploadTypeEnum;
import com.bytedesk.core.upload.event.UploadCreateEvent;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.core.utils.BdUploadUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BlackEventListener {

    private final BlackRestService blackRestService;
    private final UploadRestService uploadRestService;

    @EventListener
    public void onUploadCreateEvent(UploadCreateEvent event) {
        UploadEntity upload = event.getUpload();
        if (!UploadTypeEnum.BLACK.name().equalsIgnoreCase(upload.getType())) {
            return;
        }

        String fileName = upload.getFileName();
        if (!BdUploadUtils.isExcelFile(fileName)) {
            log.warn("不是Excel文件，无法导入黑名单: {}", fileName);
            return;
        }

        try {
            Resource resource = uploadRestService.loadAsResource(upload);
            if (!resource.exists()) {
                log.warn("黑名单导入文件不存在: {}", fileName);
                return;
            }

            String filePath = resource.getFile().getAbsolutePath();
            log.info("BlackEventListener import BLACK: {}", filePath);

            EasyExcel.read(filePath, BlackExcel.class, new PageReadListener<BlackExcel>(rows -> {
                for (BlackExcel row : rows) {
                    blackRestService.createFromExcelRow(row, upload.getOrgUid());
                }
            })).sheet().doRead();
        } catch (Exception e) {
            log.error("BlackEventListener UploadCreateEvent import error: {}", e.getMessage(), e);
        }
    }

    @EventListener
    public void onQuartzDay0Event(QuartzDay0Event event) {
        // 每天0点，检查到期的黑名单，并清理
        blackRestService.findByEndTimeBefore(BdDateUtils.now()).forEach(black -> {
            blackRestService.deleteByUid(black.getUid());
        });
    }

}
