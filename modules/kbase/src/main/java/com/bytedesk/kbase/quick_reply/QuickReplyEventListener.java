/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-20 14:31:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-12 11:30:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.quick_reply;

import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadRestService;
import com.bytedesk.core.upload.UploadTypeEnum;
import com.bytedesk.core.upload.event.UploadCreateEvent;
import com.bytedesk.core.utils.BdFileUtils;
import com.alibaba.excel.EasyExcel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 快捷用语事件监听器
@Slf4j
@Component
@RequiredArgsConstructor
public class QuickReplyEventListener {

    private final UploadRestService uploadRestService;

    private final QuickReplyRestService quickReplyService;

    // 迁移到kbaseInitializer中
    // @Order(7)
    // @EventListener
    // public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
    //     OrganizationEntity organization = (OrganizationEntity) event.getSource();
    //     // User user = organization.getUser();
    //     log.info("quick_reply - organization created: {}", organization.getName());
    //     // 为保证执行顺序，迁移到KbaseEventListener中
    //     // String orgUid = organization.getUid();
    //     // 创建快捷用语
    //     // quickReplyRestService.initQuickReply(orgUid);
    //     // 创建快捷用语分类
    //     // quickReplyRestService.initQuickReplyCategory(orgUid);
    // }

    @EventListener
    public void onUploadCreateEvent(UploadCreateEvent event) {
        UploadEntity upload = event.getUpload();
        
        if (UploadTypeEnum.QUICKREPLY.name().equalsIgnoreCase(upload.getType())) {
            // 检查文件类型是否为Excel
            String fileName = upload.getFileName();
            if (!BdFileUtils.isExcelFile(fileName)) {
                log.warn("不是Excel文件，无法导入常见问题: {}", fileName);
                return;
            }
            log.info("QuickReplyEventListener QUICKREPLY: {}", fileName);

            try {
                // 导入Excel文件
                Resource resource = uploadRestService.loadAsResource(upload.getFileName());
                if (resource.exists()) {
                    String filePath = resource.getFile().getAbsolutePath();
                    log.info("UploadEventListener loadAsResource: {}", filePath);
                    // 导入快捷回复
                    // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
                    // https://easyexcel.opensource.alibaba.com/docs/current/quickstart/read
                    EasyExcel.read(filePath, QuickReplyExcel.class, new QuickReplyExcelListener(quickReplyService,
                            // upload.getCategoryUid(),
                            upload.getKbUid(),
                            upload.getOrgUid())).sheet()
                            .doRead();
                }
            } catch (Exception e) {
                log.error("QuickReplyEventListener onUploadCreateEvent error: {}", e.getMessage());
            }
        }

    }

}
