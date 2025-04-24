/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-07 15:42:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-24 16:12:21
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
import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.message.event.MessageUpdateEvent;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadRestService;
import com.bytedesk.core.upload.UploadTypeEnum;
import com.bytedesk.core.upload.event.UploadCreateEvent;
import com.bytedesk.core.utils.BdFileUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class FaqEventListener {

    private final FaqRestService faqRestService;

    private final UploadRestService uploadRestService;

    // @Order(3)
    // @EventListener
    // public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
    //     OrganizationEntity organization = (OrganizationEntity) event.getSource();
    //     // String orgUid = organization.getUid();
    //     log.info("faq - organization created: {}", organization.getName());
    // }

    @EventListener
    public void onMessageUpdateEvent(MessageUpdateEvent event) {
        MessageEntity message = event.getMessage();
        //
        if (message.getStatus().equals(MessageStatusEnum.RATE_UP.name())
                || message.getStatus().equals(MessageStatusEnum.RATE_DOWN.name())) {
            log.info("message faq update event: {}", message);
            //
            FaqMessageExtra extra = JSON.parseObject(message.getExtra(), FaqMessageExtra.class);
            log.info("faq rate extra faqUid {}, rate {}", extra.getFaqUid(), extra.getRate());
            //
            if (message.getStatus().equals(MessageStatusEnum.RATE_UP.name())) {
                faqRestService.rateUp(extra.getFaqUid());
            } else if (message.getStatus().equals(MessageStatusEnum.RATE_DOWN.name())) {
                faqRestService.rateDown(extra.getFaqUid());
            }
        }
    }

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
                            UploadTypeEnum.FAQ.name(),
                            upload.getUid(),
                            upload.getKbUid(),
                            upload.getOrgUid())).sheet().doRead();
                }
            } catch (Exception e) {
                log.error("FaqEventListener UploadEventListener create error: {}", e.getMessage());
            }
        }
    }
}
