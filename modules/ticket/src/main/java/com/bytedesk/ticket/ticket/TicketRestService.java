/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 18:50:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-25 16:26:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.category.CategoryTypeEnum;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.exception.NotFoundException;
import com.bytedesk.core.exception.NotLoginException;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.enums.ThreadProcessStatusEnum;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadRestService;
import com.bytedesk.core.utils.ConvertUtils;
import com.bytedesk.core.utils.Utils;
import com.bytedesk.ticket.attachment.TicketAttachmentEntity;
import com.bytedesk.ticket.attachment.TicketAttachmentRepository;
import com.bytedesk.ticket.ticket.event.TicketUpdateAssigneeEvent;
import com.bytedesk.ticket.ticket.event.TicketUpdateDepartmentEvent;
import com.bytedesk.ticket.utils.TicketConvertUtils;
import com.bytedesk.core.topic.TopicUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class TicketRestService
        extends BaseRestServiceWithExport<TicketEntity, TicketRequest, TicketResponse, TicketExcel> {

    private final TicketRepository ticketRepository;

    private final TicketAttachmentRepository attachmentRepository;

    private final ModelMapper modelMapper;

    private final AuthService authService;

    private final UidUtils uidUtils;

    private final ThreadRestService threadRestService;

    private final UploadRestService uploadRestService;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final CategoryRestService categoryRestService;

    @Cacheable(value = "ticket", key = "#uid", unless = "#result == null")
    @Override
    public Optional<TicketEntity> findByUid(String uid) {
        return ticketRepository.findByUid(uid);
    }

    // query by visitor uid
    public Page<TicketResponse> queryByVisitorUid(TicketRequest request) {
       Pageable pageable = request.getPageable();
        Specification<TicketEntity> spec = createSpecification(request);
        Page<TicketEntity> page = executePageQuery(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Transactional
    @Override
    public TicketResponse create(TicketRequest request) {
        // UserEntity owner = authService.getUser();
        // if (owner == null) {
        //     throw new NotLoginException(I18Consts.I18N_LOGIN_REQUIRED);
        // }
        // String userUid = owner.getUid();
        // 创建工单...
        TicketEntity ticket = modelMapper.map(request, TicketEntity.class);
        ticket.setUid(uidUtils.getUid());
        // ticket.setUserUid(userUid); // 创建人
        // 默认是工作组工单，暂不启用一对一
        // ticket.setType(TicketTypeEnum.DEPARTMENT.name());
        // ticket.setOwner(owner); // 创建人
        //
        ticket.setAssignee(request.getAssigneeJson());
        ticket.setReporter(request.getReporterJson());
        //
        if (StringUtils.hasText(request.getAssigneeJson())
                && StringUtils.hasText(request.getAssignee().getUid())) {
            ticket.setStatus(TicketStatusEnum.ASSIGNED.name());
        } else {
            ticket.setStatus(TicketStatusEnum.NEW.name());
        }
        ticket.setReporter(request.getReporterJson());
        // 先保存工单
        TicketEntity savedTicket = save(ticket);
        // 保存附件
        Set<TicketAttachmentEntity> attachments = new HashSet<>();
        if (request.getUploadUids() != null) {
            for (String uploadUid : request.getUploadUids()) {
                Optional<UploadEntity> uploadOptional = uploadRestService.findByUid(uploadUid);
                if (uploadOptional.isPresent()) {
                    TicketAttachmentEntity attachment = new TicketAttachmentEntity();
                    attachment.setUid(uidUtils.getUid());
                    attachment.setOrgUid(savedTicket.getOrgUid());
                    attachment.setTicket(savedTicket);
                    attachment.setUpload(uploadOptional.get());
                    attachmentRepository.save(attachment);
                    //
                    attachments.add(attachment);
                }
            }
        }
        savedTicket.setAttachments(attachments);

        // 未绑定客服会话的情况下，创建工单客服会话
        if (!StringUtils.hasText(ticket.getThreadUid())) {
            // 如果创建工单的时候没有绑定会话，则创建会话
            ThreadEntity thread = createTicketThread(ticket);
            if (thread != null) {
                ticket.setTopic(thread.getTopic());
                ticket.setThreadUid(thread.getUid());
            }
        }

        // 保存工单
        savedTicket = save(savedTicket);
        if (savedTicket == null) {
            throw new RuntimeException("create ticket failed");
        }

        return convertToResponse(savedTicket);
    }

    @Transactional
    @Override
    public TicketResponse update(TicketRequest request) {
        Optional<TicketEntity> ticketOptional = findByUid(request.getUid());
        if (ticketOptional.isEmpty()) {
            throw new NotFoundException("ticket not found");
        }
        TicketEntity ticket = ticketOptional.get();

        // 更新基本信息
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());
        ticket.setStatus(request.getStatus());

        // 更新工作组和处理人信息
        ticket.setAssignee(request.getAssigneeJson());
        ticket.setDepartmentUid(request.getDepartmentUid());
        // ticket = updateAssigneeAndWorkgroup(ticket, request);

        // 处理附件更新
        if (request.getUploadUids() != null) {
            ticket = updateAttachments(ticket, request.getUploadUids());
        }

        // 保存更新后的工单
        ticket = ticketRepository.save(ticket);
        if (ticket == null) {
            throw new RuntimeException("update ticket failed");
        }

        // 发布事件，判断assignee是否被修改
        if (StringUtils.hasText(request.getAssignee().getUid()) && StringUtils.hasText(ticket.getAssigneeString())) {
            String oldAssigneeUid = ticket.getAssignee().getUid();
            if (oldAssigneeUid != null && !oldAssigneeUid.equals(request.getAssignee().getUid())) {
                TicketUpdateAssigneeEvent ticketUpdateAssigneeEvent = new TicketUpdateAssigneeEvent(ticket,
                        oldAssigneeUid, request.getAssignee().getUid());
                applicationEventPublisher.publishEvent(ticketUpdateAssigneeEvent);
            }
        }

        // 发布事件，判断workgroupUid是否被修改
        if (StringUtils.hasText(request.getDepartmentUid())) {
            String oldWorkgroupUid = ticket.getDepartmentUid();
            if (oldWorkgroupUid != null && !oldWorkgroupUid.equals(request.getDepartmentUid())) {
                TicketUpdateDepartmentEvent TicketUpdateDepartmentEvent = new TicketUpdateDepartmentEvent(ticket,
                        oldWorkgroupUid, request.getDepartmentUid());
                applicationEventPublisher.publishEvent(TicketUpdateDepartmentEvent);
            }
        }

        return convertToResponse(ticket);
    }

    public TicketEntity updateAttachments(TicketEntity ticket, Set<String> uploadUids) {
        // 获取现有附件的 uploadUid 列表
        Set<String> existingUploadUids = ticket.getAttachments().stream()
                .map(attachment -> attachment.getUpload().getUid())
                .collect(Collectors.toSet());

        // 处理新增的附件
        for (String uploadUid : uploadUids) {
            if (!existingUploadUids.contains(uploadUid)) {
                Optional<UploadEntity> uploadOptional = uploadRestService.findByUid(uploadUid);
                if (uploadOptional.isPresent()) {
                    TicketAttachmentEntity attachment = new TicketAttachmentEntity();
                    attachment.setUid(uidUtils.getUid());
                    attachment.setTicket(ticket);
                    attachment.setUpload(uploadOptional.get());
                    attachmentRepository.save(attachment);
                    // 添加到工单的附件集合中
                    ticket.getAttachments().add(attachment);
                }
            }
        }

        // 处理需要删除的附件
        ticket.getAttachments().stream()
                .filter(attachment -> !uploadUids.contains(attachment.getUpload().getUid()))
                .forEach(attachment -> {
                    attachment.setDeleted(true);
                    attachmentRepository.save(attachment);
                });

        return ticket;
    }

    // 创建工单会话
    public ThreadEntity createTicketThread(TicketEntity ticket) {
        //
        UserEntity owner = authService.getUser();
        if (owner == null) {
            throw new NotLoginException(I18Consts.I18N_LOGIN_REQUIRED);
        }
        //
        if (ticket.getDepartmentUid() == null || ticket.getDepartmentUid().isEmpty()) {
            ticket.setDepartmentUid("all");
        }
        //
        String topic = TopicUtils.formatOrgDepartmentTicketThreadTopic(ticket.getDepartmentUid(), ticket.getUid());
        Optional<ThreadEntity> threadOptional = threadRestService.findFirstByTopic(topic);
        if (threadOptional.isPresent()) {
            return threadOptional.get();
        }
        String user = ConvertUtils.convertToUserProtobufString(owner);
        // 创建工单会话
        ThreadEntity thread = ThreadEntity.builder()
                .uid(uidUtils.getUid())
                .type(ThreadTypeEnum.TICKET.name())
                // .unreadCount(0)
                .status(ThreadProcessStatusEnum.NEW.name())
                .topic(topic)
                .hide(true) // 默认隐藏
                .user(user)
                // .agent(user) // 客服会话的创建者是客服
                .userUid(owner.getUid())
                .owner(owner)
                .channel(ticket.getChannel())
                .orgUid(ticket.getOrgUid())
                .build();
        //
        return threadRestService.save(thread);
    }

    @Override
    protected TicketEntity doSave(TicketEntity entity) {
        return ticketRepository.save(entity);
    }

    @Override
    public TicketEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            TicketEntity entity) {
        try {
            Optional<TicketEntity> latest = findByUid(entity.getUid());
            if (latest.isPresent()) {
                TicketEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setTitle(entity.getTitle());
                latestEntity.setDescription(entity.getDescription());
                latestEntity.setPriority(entity.getPriority());
                latestEntity.setStatus(entity.getStatus());
                // 其他需要合并的字段
                return ticketRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public void delete(TicketRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<TicketEntity> ticketOptional = ticketRepository.findByUid(uid);
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("ticket not found");
        }
        TicketEntity ticket = ticketOptional.get();
        ticket.setDeleted(true);
        save(ticket);
    }

    @Override
    public TicketResponse convertToResponse(TicketEntity entity) {
        return TicketConvertUtils.convertToResponse(entity);
    }

    public void initTicketCategory(String orgUid) {
        log.info("initTicketCategory", orgUid);
        // String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        for (String category : TicketCategories.getAllCategories()) {
            // log.info("initTicketCategory: {}", category);
            CategoryRequest categoryRequest = CategoryRequest.builder()
                    .uid(Utils.formatUid(orgUid, category))
                    .name(category)
                    .order(0)
                    .type(CategoryTypeEnum.TICKET.name())
                    .level(LevelEnum.ORGANIZATION.name())
                    .platform(BytedeskConsts.PLATFORM_BYTEDESK)
                    .orgUid(orgUid)
                    .build();
            categoryRestService.create(categoryRequest);
        }
    }

    @Override
    public TicketExcel convertToExcel(TicketEntity entity) {
        return modelMapper.map(entity, TicketExcel.class);
    }

    @Override
    protected Specification<TicketEntity> createSpecification(TicketRequest request) {
        return TicketSpecification.search(request, authService);
    }

    @Override
    protected Page<TicketEntity> executePageQuery(Specification<TicketEntity> specification, Pageable pageable) {
        return ticketRepository.findAll(specification, pageable);
    }

}