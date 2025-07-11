/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 18:50:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-05 11:03:20
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.bytedesk.ai.robot.agent.RobotAgentService;
import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.category.CategoryRequest;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.category.CategoryTypeEnum;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.exception.NotFoundException;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.ThreadProcessStatusEnum;
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
public class TicketRestService extends BaseRestServiceWithExcel<TicketEntity, TicketRequest, TicketResponse, TicketExcel> {

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

    private final RobotAgentService robotAgentService;

    private final MessageRestService messageRestService;

    @Override
    public Page<TicketEntity> queryByOrgEntity(TicketRequest request) {
        Pageable pageable = request.getPageable();
        Specification<TicketEntity> spec = TicketSpecification.search(request);
        return ticketRepository.findAll(spec, pageable);
    }

    @Override
    public Page<TicketResponse> queryByOrg(TicketRequest request) {
        Page<TicketEntity> ticketPage = queryByOrgEntity(request);
        return ticketPage.map(this::convertToResponse);
    }

    @Override
    public Page<TicketResponse> queryByUser(TicketRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
        throw new RuntimeException("login first");
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
            throw new RuntimeException("login first");
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
            throw new RuntimeException("login first");
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

    @Transactional
    public void verifyTicket(Long ticketId, boolean approved) {
        taskService.complete(getTaskIdByTicketId(ticketId),
                Map.of("approved", approved));
    }

    @Transactional
    public TicketCommentEntity addComment(Long ticketId, TicketCommentRequest commentRequest) {
        TicketEntity ticket = findTicketById(ticketId);

        TicketCommentEntity comment = new TicketCommentEntity();
        comment.setTicket(ticket);
        comment.setContent(commentRequest.getContent());
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
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        } catch (Exception e) {
            throw new RuntimeException("save ticket exception: " + e.getMessage());
        }
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
            categoryService.create(categoryRequest);
        }
    }

    @Override
    public TicketExcel convertToExcel(TicketEntity entity) {
        return modelMapper.map(entity, TicketExcel.class);
    }

    public TicketRequest autoFillTicket(TicketRequest request) {
        // 
        String content = "";
        String orgUid = request.getOrgUid();
        List<MessageEntity> messages = messageRestService.findByThreadUid(request.getThreadUid());
        for (MessageEntity message : messages) {
            content += message.getContent() + "\n";
        }
        // 
        String jsonResponse = robotAgentService.autoFillTicket(content, orgUid);
        
        // 处理返回的JSON字符串，去除可能的前缀和后缀
        if (jsonResponse != null) {
            jsonResponse = jsonResponse.replace("```json", "").replace("```", "").trim();
        }
        
        try {
            // 使用Jackson库解析JSON
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);
            
            // 构建TicketRequest对象
            TicketRequest ticketRequest = new TicketRequest();
            
            // 设置原始请求的信息
            ticketRequest.setOrgUid(request.getOrgUid());
            // ticketRequest.setThreadUid(request.getThreadUid());
            // ticketRequest.setDepartmentUid(request.getDepartmentUid());
            // ticketRequest.setAssignee(request.getAssignee());
            
            // 从JSON填充字段
            if (jsonNode.has("title")) {
                ticketRequest.setTitle(jsonNode.get("title").asText());
            }
            
            if (jsonNode.has("description")) {
                ticketRequest.setDescription(jsonNode.get("description").asText());
            }
            
            if (jsonNode.has("status")) {
                ticketRequest.setStatus(jsonNode.get("status").asText());
            }
            
            if (jsonNode.has("priority")) {
                ticketRequest.setPriority(jsonNode.get("priority").asText());
            }
            
            if (jsonNode.has("category")) {
                // 通过类别名称查询对应的类别UID
                String categoryName = jsonNode.get("category").asText();
                // 这里需要实现查找类别UID的逻辑，暂时留空
                // ticketRequest.setCategoryUid(findCategoryUidByName(categoryName, orgUid));
                ticketRequest.setCategoryUid(categoryName);
            }
            
            return ticketRequest;
        } catch (Exception e) {
            log.error("解析自动填充工单JSON失败", e);
            return request; // 解析失败时返回原始请求
        }
    }

}