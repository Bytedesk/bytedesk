/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-03 14:06:20
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-13 12:15:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.member;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.alibaba.excel.EasyExcel;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.event.OrganizationCreateEvent;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.core.thread.event.ThreadCreateEvent;
import com.bytedesk.core.topic.TopicCacheService;
import com.bytedesk.core.topic.TopicRequest;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadRestService;
import com.bytedesk.core.upload.UploadTypeEnum;
import com.bytedesk.core.upload.event.UploadCreateEvent;
import com.bytedesk.core.utils.BdFileUtils;
import com.bytedesk.team.department.DepartmentConsts;
import com.bytedesk.team.department.DepartmentRequest;
import com.bytedesk.team.department.DepartmentResponse;
import com.bytedesk.team.department.DepartmentRestService;
import com.bytedesk.team.member.event.MemberCreateEvent;

import jakarta.transaction.Transactional;

@Slf4j
@Component
@AllArgsConstructor
public class MemberEventListener {

    private final MemberRestService memberService;

    private final ModelMapper modelMapper;

    private final DepartmentRestService departmentService;

    private final UidUtils uidUtils;

    private final TopicCacheService topicCacheService;

    private final UploadRestService uploadService;

    @Transactional
    @Order(1)
    @EventListener
    public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
        OrganizationEntity organization = (OrganizationEntity) event.getSource();
        UserEntity user = organization.getUser();
        String orgUid = organization.getUid();
        log.info("organization created: {}", organization.getName());
        //
        DepartmentRequest departmentRequest = DepartmentRequest.builder()
                .uid(uidUtils.getUid())
                .name(DepartmentConsts.DEPT_ADMIN)
                .description("Description for" + DepartmentConsts.DEPT_ADMIN)
                .orgUid(orgUid)
                .build();
        DepartmentResponse departmentResponse = departmentService.create(departmentRequest);
        //
        if (departmentResponse != null) {
            Set<String> roleUids = new HashSet<>(Arrays.asList(BytedeskConsts.DEFAULT_ROLE_MEMBER_UID));
            // 创建团队成员
            MemberRequest memberRequest = modelMapper.map(user, MemberRequest.class);
            memberRequest.setJobNo("001");
            memberRequest.setJobTitle(I18Consts.I18N_ADMIN);
            memberRequest.setSeatNo("001");
            memberRequest.setTelephone("001");
            memberRequest.setMobile(user.getMobile());
            memberRequest.setStatus(MemberStatusEnum.ACTIVE.name());
            memberRequest.setRoleUids(roleUids);
            memberRequest.setDeptUid(departmentResponse.getUid());
            memberRequest.setOrgUid(orgUid);
            memberService.create(memberRequest);
        }
    }

    @EventListener
    public void onThreadCreateEvent(ThreadCreateEvent event) {
        ThreadEntity thread = event.getThread();
        log.info("member ThreadCreateEvent: {}", thread.getUid());
        //
        if (thread.getType().equals(ThreadTypeEnum.MEMBER.name())) {
            memberService.createMemberReverseThread(thread);
        }
    }

    @EventListener
    public void onMemberCreateEvent(MemberCreateEvent event) {
        // MemberCreateEvent memberCreateEvent = (MemberCreateEvent) event.getObject();
        MemberEntity member = event.getMember();
        UserEntity user = member.getUser();
        log.info("member created: {}", event);
        // 默认订阅成员主题
        String topic = TopicUtils.formatOrgMemberTopic(member.getUid());
        TopicRequest request = TopicRequest.builder()
                .topic(topic)
                .userUid(user.getUid())
                .build();
        // request.setUserUid(user.getUid());
        topicCacheService.pushRequest(request);
    }

    @EventListener
    public void onUploadCreateEvent(UploadCreateEvent event) throws IOException {
        UploadEntity upload = event.getUpload();
        //
        if (UploadTypeEnum.MEMBER.name().equalsIgnoreCase(upload.getType())) {
            // 检查文件类型是否为Excel
            String fileName = upload.getFileName();
            if (!BdFileUtils.isExcelFile(fileName)) {
                log.warn("不是Excel文件，无法导入成员: {}", fileName);
                return;
            }
            log.info("MemberEventListener MEMBER: {}", fileName);
        
            // 导入Excel文件
            Resource resource = uploadService.loadAsResource(upload.getFileName());
            if (resource.exists()) {
                String filePath = resource.getFile().getAbsolutePath();
                log.info("UploadEventListener loadAsResource: {}", filePath);
                // 导入成员
                // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
                // https://easyexcel.opensource.alibaba.com/docs/current/quickstart/read
                EasyExcel.read(filePath,
                        MemberExcel.class,
                        new MemberExcelListener(memberService, upload.getOrgUid())).sheet().doRead();
            }
        }

    }

}
