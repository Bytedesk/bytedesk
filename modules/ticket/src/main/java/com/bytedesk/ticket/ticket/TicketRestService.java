/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 18:50:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-01 16:46:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import org.flowable.engine.TaskService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.category.CategoryTypeEnum;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.ThreadStateEnum;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadRestService;
import com.bytedesk.core.utils.ConvertUtils;
import com.bytedesk.core.utils.Utils;
import com.bytedesk.ticket.attachment.TicketAttachmentEntity;
import com.bytedesk.ticket.attachment.TicketAttachmentRepository;
import com.bytedesk.ticket.comment.TicketCommentRequest;
import com.bytedesk.ticket.ticket.event.TicketUpdateAssigneeEvent;
import com.bytedesk.ticket.ticket.event.TicketUpdateDepartmentEvent;
import com.bytedesk.ticket.utils.TicketConvertUtils;
import com.bytedesk.ticket.comment.TicketCommentEntity;
import com.bytedesk.ticket.comment.TicketCommentRepository;
import com.bytedesk.core.topic.TopicUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@AllArgsConstructor
public class TicketRestService extends BaseRestService<TicketEntity, TicketRequest, TicketResponse> {

    private final TaskService taskService;

    private final TicketRepository ticketRepository;

    private final TicketCommentRepository commentRepository;

    private final TicketAttachmentRepository attachmentRepository;

    private final ModelMapper modelMapper;

    private final AuthService authService;

    private final UidUtils uidUtils;

    private final ThreadRestService threadRestService;

    private final UploadRestService uploadRestService;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final CategoryRestService categoryService;

    @Override
    public Page<TicketResponse> queryByOrg(TicketRequest request) {
        Pageable pageable = request.getPageable();
        Specification<TicketEntity> spec = TicketSpecification.search(request);
        Page<TicketEntity> ticketPage = ticketRepository.findAll(spec, pageable);
        return ticketPage.map(this::convertToResponse);
    }

    @Override
    public Page<TicketResponse> queryByUser(TicketRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
        throw new RuntimeException("user not found");
        }
        request.setUserUid(user.getUid());
        //
        return queryByOrg(request);
    }

    @Cacheable(value = "ticket", key = "#uid", unless = "#result == null")
    @Override
    public Optional<TicketEntity> findByUid(String uid) {
        return ticketRepository.findByUid(uid);
    }

    @Transactional
    @Override
    public TicketResponse create(TicketRequest request) {
        UserEntity owner = authService.getUser();
        if (owner == null) {
            throw new RuntimeException("user not found");
        }
        String userUid = owner.getUid();
        // 创建工单...
        TicketEntity ticket = modelMapper.map(request, TicketEntity.class);
        ticket.setUid(uidUtils.getUid());
        ticket.setUserUid(userUid); // 创建人
        // 默认是工作组工单，暂不启用一对一
        ticket.setType(TicketTypeEnum.DEPARTMENT.name());
        ticket.setOwner(owner); // 创建人
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
        // 
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
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("ticket not found");
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
            throw new RuntimeException("user not found");
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
                .unreadCount(0)
                .state(ThreadStateEnum.NEW.name())
                .topic(topic)
                .hide(true) // 默认隐藏
                .user(user)
                // .agent(user) // 客服会话的创建者是客服
                .userUid(owner.getUid())
                .owner(owner)
                .client(ticket.getClient())
                .orgUid(ticket.getOrgUid())
                .build();
        //
        return threadRestService.save(thread);
    }

    // @Transactional
    // public void assignTicket(Long ticketId, AgentEntity assignee) {
    //     // UserEntity user = authService.getUser();
    //     // if (user == null) {
    //     // throw new RuntimeException("user not found");
    //     // }
    //     // String userId = user.getUid();
    //     // 检查用户是否是主管
    //     // if (!identityService.isUserInGroup(userId, "supervisors")) {
    //     // throw new AccessDeniedException("Only supervisors can assign tickets");
    //     // }
    //     // 分配工单...
    //     TicketEntity ticket = findTicketById(ticketId);
    //     UserProtobuf assigneeProtobuf = UserProtobuf.builder()
    //             .uid(assignee.getUid())
    //             .nickname(assignee.getNickname())
    //             .avatar(assignee.getAvatar())
    //             .type(UserTypeEnum.AGENT.name())
    //             .build();
    //     // assigneeProtobuf.setUid(assignee.getUid());
    //     // assigneeProtobuf.setType(UserTypeEnum.AGENT.name());
    //     ticket.setAssignee(assigneeProtobuf.toJson());
    //     ticket.setStatus("处理中");
    //     ticket.setUpdatedAt(LocalDateTime.now());
    //     // 更新流程变量
    //     taskService.complete(getTaskIdByTicketId(ticketId), Map.of("assignee", assignee));
    // }

    @Transactional
    public void verifyTicket(Long ticketId, boolean approved) {
        taskService.complete(getTaskIdByTicketId(ticketId),
                Map.of("approved", approved));
    }

    @Transactional
    public TicketCommentEntity addComment(Long ticketId, TicketCommentRequest commentDTO) {
        TicketEntity ticket = findTicketById(ticketId);

        TicketCommentEntity comment = new TicketCommentEntity();
        comment.setTicket(ticket);
        comment.setContent(commentDTO.getContent());
        // comment.setAuthor(commentDTO.getAuthor());

        return commentRepository.save(comment);
    }

    @Transactional
    public TicketAttachmentEntity uploadAttachment(Long ticketId, MultipartFile file) {
        TicketEntity ticket = findTicketById(ticketId);

        TicketAttachmentEntity attachment = new TicketAttachmentEntity();
        attachment.setTicket(ticket);
        // attachment.setFileName(file.getOriginalFilename());
        // attachment.setFileType(file.getContentType());
        // attachment.setFileSize(file.getSize());
        // attachment.setFilePath("/uploads/" + file.getOriginalFilename());

        return attachmentRepository.save(attachment);
    }

    private String getTaskIdByTicketId(Long ticketId) {
        return taskService.createTaskQuery()
                .processInstanceBusinessKey(ticketId.toString())
                .singleResult()
                .getId();
    }

    private TicketEntity findTicketById(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found: " + ticketId));
    }

    @Override
    public TicketEntity save(TicketEntity entity) {
        try {
            TicketEntity ticket = ticketRepository.save(entity);
            //
            if (ticket == null) {
                throw new RuntimeException("save ticket failed");
            }
            return ticket;
        } catch (Exception e) {
            throw new RuntimeException("save ticket exception: " + e.getMessage());
        }
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
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            TicketEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
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
            categoryService.create(categoryRequest);
        }
    }

}