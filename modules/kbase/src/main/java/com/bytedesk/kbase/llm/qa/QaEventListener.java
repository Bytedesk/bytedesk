/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-07 15:42:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-29 14:23:21
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
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.message.event.MessageUpdateEvent;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.event.OrganizationCreateEvent;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadRestService;
import com.bytedesk.core.upload.UploadTypeEnum;
import com.bytedesk.core.upload.event.UploadCreateEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class QaEventListener {

    private final QaRestService qaRestService;

    private final UploadRestService uploadRestService;

    @Order(3)
    @EventListener
    public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
        OrganizationEntity organization = (OrganizationEntity) event.getSource();
        // String orgUid = organization.getUid();
        log.info("qa - organization created: {}", organization.getName());
    }

    @EventListener
    public void onMessageUpdateEvent(MessageUpdateEvent event) {
        MessageEntity message = event.getMessage();
        //
        if (message.getStatus().equals(MessageStatusEnum.RATE_UP.name())
                || message.getStatus().equals(MessageStatusEnum.RATE_DOWN.name())) {
            log.info("message qa update event: {}", message);
            //
            QaMessageExtra extra = JSON.parseObject(message.getExtra(), QaMessageExtra.class);
            log.info("qa rate extra qaUid {}, rate {}", extra.getQaUid(), extra.getRate());
            //
            if (message.getStatus().equals(MessageStatusEnum.RATE_UP.name())) {
                qaRestService.rateUp(extra.getQaUid());
            } else if (message.getStatus().equals(MessageStatusEnum.RATE_DOWN.name())) {
                qaRestService.rateDown(extra.getQaUid());
            }
        }
    }

    @EventListener
    public void onUploadCreateEvent(UploadCreateEvent event) {
        UploadEntity upload = event.getUpload();
        log.info("QaEventListener UploadEventListener create: {}", upload.toString());

        if (upload.getType().equalsIgnoreCase(UploadTypeEnum.FAQ.name())) {
            // 常见问题导入
            try {
                Resource resource = uploadRestService.loadAsResource(upload.getFileName());
                if (resource.exists()) {
                    String filePath = resource.getFile().getAbsolutePath();
                    log.info("UploadEventListener loadAsResource: {}", filePath);
                    // 导入自动回复
                    // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
                    // https://easyexcel.opensource.alibaba.com/docs/current/quickstart/read
                    EasyExcel.read(filePath, QaExcel.class, new QaExcelListener(qaRestService,
                            UploadTypeEnum.FAQ.name(),
                            upload.getKbUid(),
                            upload.getOrgUid())).sheet().doRead();
                }
            } catch (Exception e) {
                log.error("QaEventListener UploadEventListener create error: {}", e.getMessage());
            }
        } else if (upload.getType().equalsIgnoreCase(UploadTypeEnum.LLM.name())) {
            // llm qa 问答对导入
            try {
                Resource resource = uploadRestService.loadAsResource(upload.getFileName());
                if (resource.exists()) {
                    String filePath = resource.getFile().getAbsolutePath();
                    log.info("UploadEventListener loadAsResource: {}", filePath);
                    // 导入自动回复
                    // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
                    // https://easyexcel.opensource.alibaba.com/docs/current/quickstart/read
                    EasyExcel.read(filePath, QaExcel.class, new QaExcelListener(qaRestService,
                            UploadTypeEnum.LLM.name(),
                            upload.getKbUid(),
                            upload.getOrgUid())).sheet().doRead();
                }
            } catch (Exception e) {
                log.error("QaEventListener UploadEventListener create error: {}", e.getMessage());
            }
        }
    }
}
