/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-12 07:17:13
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-13 20:08:15
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.alibaba.excel.EasyExcel;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.event.OrganizationCreateEvent;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadRestService;
import com.bytedesk.core.upload.UploadTypeEnum;
import com.bytedesk.core.upload.event.UploadCreateEvent;
import com.bytedesk.core.utils.BdUploadUtils;
import com.bytedesk.core.utils.Utils;
import com.bytedesk.kbase.kbase.KbaseTypeEnum;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class RobotEventListener {

    private final RobotRestService robotRestService;
    
    private final UploadRestService uploadRestService;

    @Order(5)
    @EventListener
    public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
        OrganizationEntity organization = (OrganizationEntity) event.getSource();
        String orgUid = organization.getUid();
        log.info("robot - organization created: {}", organization.getName());
        // 为新创建组织创建一个默认机器人
        String robotUid = Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_ROBOT_UID);
        robotRestService.createDefaultRobot(orgUid, robotUid);
        // 导入组织智能体
        String level = LevelEnum.ORGANIZATION.name();
        robotRestService.initRobotJson(level, orgUid);
    }

    @EventListener
    public void onUploadCreateEvent(UploadCreateEvent event) {
        UploadEntity upload = event.getUpload();
        if (UploadTypeEnum.PROMPT.name().equalsIgnoreCase(upload.getType())) {
            // 检查文件类型是否为Excel
            String fileName = upload.getFileName();
            if (!BdUploadUtils.isExcelFile(fileName)) {
                log.warn("不是Excel文件，无法导入提示词: {}", fileName);
                return;
            }
            log.info("RobotEventListener Robot: {}", fileName);

            try {
                Resource resource = uploadRestService.loadAsResource(upload);
                if (resource.exists()) {
                    String filePath = resource.getFile().getAbsolutePath();
                    log.info("RobotEventListener loadAsResource: {}", filePath);

                    // 使用原有的EasyExcel直接导入方式
                    log.info("使用EasyExcel直接导入提示词: {}", filePath);
                    EasyExcel.read(filePath,
                            RobotExcel.class,
                            new RobotExcelListener(robotRestService,
                                    KbaseTypeEnum.LLM.name(),
                                    upload.getUid(),
                                    upload.getKbUid(),
                                    upload.getOrgUid()))
                            .sheet().doRead();
                }
            } catch (Exception e) {
                log.error("RobotEventListener UploadEventListener create error: {}", e.getMessage(), e);
            }
        }
    }

}
