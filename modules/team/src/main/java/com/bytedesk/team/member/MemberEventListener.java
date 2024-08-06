/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-03 14:06:20
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-05 22:40:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.member;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.event.GenericApplicationEvent;
import com.bytedesk.core.rbac.organization.Organization;
import com.bytedesk.core.rbac.organization.OrganizationCreateEvent;
import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.thread.ThreadCreateEvent;
import com.bytedesk.core.thread.Thread;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.core.topic.TopicCacheService;
import com.bytedesk.core.topic.TopicRequest;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.team.department.Department;
import com.bytedesk.team.department.DepartmentService;

@Slf4j
@Component
@AllArgsConstructor
public class MemberEventListener {

    private final MemberService memberService;

    private final ModelMapper modelMapper;

    private final DepartmentService departmentService;

    private final UidUtils uidUtils;

    private final TopicCacheService topicCacheService;

    @Order(1)
    @EventListener
    public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
        Organization organization = (Organization) event.getSource();
        User user = organization.getUser();
        String orgUid = organization.getUid();
        log.info("organization created: {}", organization.getName());
        //
        Department department = Department.builder()
                .name(I18Consts.I18N_PREFIX + TypeConsts.DEPT_ADMIN)
                .description(TypeConsts.DEPT_ADMIN)
                // .orgUid(organization.getUid())
                .type(TypeConsts.TYPE_SYSTEM)
                .build();
        department.setUid(uidUtils.getCacheSerialUid());
        department.setOrgUid(orgUid);
        departmentService.save(department);

        // 创建团队成员
        MemberRequest memberRequest = modelMapper.map(user, MemberRequest.class);
        memberRequest.setStatus(MemberStatusEnum.ACTIVE);
        memberRequest.setJobNo("001");
        memberRequest.setJobTitle(I18Consts.I18N_ADMIN);
        memberRequest.setSeatNo("001");
        memberRequest.setTelephone("001");
        memberRequest.setMobile(user.getMobile());
        memberRequest.setDepUid(department.getUid());
        memberRequest.setOrgUid(orgUid);
        memberService.create(memberRequest);
    }

    @EventListener
    public void onThreadCreateEvent(ThreadCreateEvent event) {
        Thread thread = event.getThread();
        // User user = thread.getOwner();
        log.info("thread ThreadCreateEvent: {}", thread.getUid());
        //
        if (thread.getType().equals(ThreadTypeEnum.MEMBER)) {
            memberService.createMemberReverseThread(thread);
        }
    }

    @EventListener
    public void onMemberCreateEvent(GenericApplicationEvent<MemberCreateEvent> event) {
        MemberCreateEvent memberCreateEvent = (MemberCreateEvent) event.getObject();
        Member member = memberCreateEvent.getMember();
        User user = member.getUser();
        log.info("member created: {}", memberCreateEvent);
        // 默认订阅成员主题
        TopicRequest request = TopicRequest.builder()
                .topic(TopicUtils.formatOrgMemberTopic(member.getUid()))
                .userUid(user.getUid())
                .build();
        topicCacheService.pushRequest(request);
        // 订阅部门主题
        // Iterator<Department> iterator = member.getDepartments().iterator();
        // while (iterator.hasNext()) {
        // Department department = iterator.next();
        // TopicRequest departRequest = TopicRequest.builder()
        // .topic(TopicUtils.getDepartmentTopic(department.getUid()))
        // .userUid(user.getUid())
        // .build();
        // topicCacheService.pushRequest(departRequest);
        // }
    }

    @EventListener
    public void onMemberUpdateEvent(GenericApplicationEvent<MemberUpdateEvent> event) {
        MemberUpdateEvent memberUpdateEvent = (MemberUpdateEvent) event.getObject();
        log.info("member updated: {}", memberUpdateEvent);
        // TODO: 删除旧的部门主题
        // 订阅部门主题
        // Iterator<Department> iterator = member.getDepartments().iterator();
        // while (iterator.hasNext()) {
        // Department department = iterator.next();
        // TopicRequest departRequest = TopicRequest.builder()
        // .topic(TopicUtils.getDepartmentTopic(department.getUid()))
        // .userUid(user.getUid())
        // .build();
        // topicCacheService.pushRequest(departRequest);
        // }
    }

}
