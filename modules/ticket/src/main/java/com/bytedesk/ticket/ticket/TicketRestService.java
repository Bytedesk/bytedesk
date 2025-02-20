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
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
// import com.bytedesk.core.rbac.user.UserRestService;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.ThreadStateEnum;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.upload.UploadEntity;
import com.bytedesk.kbase.upload.UploadRestService;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupRestService;
import com.bytedesk.ticket.attachment.TicketAttachmentEntity;
import com.bytedesk.ticket.attachment.TicketAttachmentRepository;
import com.bytedesk.ticket.comment.TicketCommentRequest;
import com.bytedesk.ticket.ticket.dto.TicketRequest;
import com.bytedesk.ticket.ticket.dto.TicketResponse;
import com.bytedesk.ticket.ticket.event.TicketUpdateAssigneeEvent;
import com.bytedesk.ticket.ticket.event.TicketUpdateWorkgroupEvent;
import com.bytedesk.ticket.utils.TicketConvertUtils;
import com.bytedesk.ticket.comment.TicketCommentEntity;
import com.bytedesk.ticket.comment.TicketCommentRepository;
import com.bytedesk.core.topic.TopicUtils;

import lombok.AllArgsConstructor;

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

    private final AgentRestService agentRestService;

    private final WorkgroupRestService workgroupRestService;

    private final ThreadRestService threadRestService;

    private final UploadRestService uploadRestService;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public Page<TicketResponse> queryByOrg(TicketRequest request) {
        Pageable pageable = request.getPageable();
        Specification<TicketEntity> spec = TicketSpecification.search(request);
        Page<TicketEntity> ticketPage = ticketRepository.findAll(spec, pageable);
        return ticketPage.map(this::convertToResponse);
    }

    @Override
    public Page<TicketResponse> queryByUser(TicketRequest request) {
        // UserEntity user = authService.getUser();
        // if (user == null) {
        // throw new RuntimeException("user not found");
        // }
        // request.setReporterUid(user.getUid());
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
        // String userUid = user.getUid();
        // 检查用户是否有创建工单的权限
        // if (!identityService.hasPrivilege(userId, "TICKET_CREATE")) {
        // throw new AccessDeniedException("No permission to create ticket");
        // }
        // 创建工单...
        TicketEntity ticket = modelMapper.map(request, TicketEntity.class);
        ticket.setUid(uidUtils.getUid());
        ticket.setStatus(TicketStatusEnum.NEW.name());
        // 默认是工作组工单，暂不启用一对一
        ticket.setType(TicketTypeEnum.WORKGROUP.name());
        ticket.setOwner(owner);
        //
        Optional<WorkgroupEntity> workgroupOptional = workgroupRestService.findByUid(request.getWorkgroupUid());
        if (workgroupOptional.isPresent()) {
            UserProtobuf workgroupProtobuf = UserProtobuf.builder()
                    .nickname(workgroupOptional.get().getNickname())
                    .avatar(workgroupOptional.get().getAvatar())
                    .build();
            workgroupProtobuf.setUid(workgroupOptional.get().getUid());
            workgroupProtobuf.setType(UserTypeEnum.WORKGROUP.name());
            String workgroupJson = JSON.toJSONString(workgroupProtobuf);
            ticket.setWorkgroup(workgroupJson);
        } else {
            throw new RuntimeException("workgroup not found");
        }
        //
        Optional<AgentEntity> assigneeOptional = agentRestService.findByUid(request.getAssigneeUid());
        if (assigneeOptional.isPresent()) {
            UserProtobuf assigneeProtobuf = UserProtobuf.builder()
                    .nickname(assigneeOptional.get().getNickname())
                    .avatar(assigneeOptional.get().getAvatar())
                    .build();
            assigneeProtobuf.setUid(assigneeOptional.get().getUid());
            assigneeProtobuf.setType(UserTypeEnum.AGENT.name());
            String assigneeJson = JSON.toJSONString(assigneeProtobuf);
            ticket.setAssignee(assigneeJson);
            ticket.setStatus(TicketStatusEnum.CLAIMED.name());
            //
            String userJson = BytedeskConsts.EMPTY_JSON_STRING;
            // 使用在线客服工单会话user info
            if (StringUtils.hasText(request.getServiceThreadTopic())) {
                String serviceThreadTopic = request.getServiceThreadTopic();
                Optional<ThreadEntity> serviceThreadOptional = threadRestService.findFirstByTopic(serviceThreadTopic);
                if (serviceThreadOptional.isPresent()) {
                    userJson = serviceThreadOptional.get().getUser();
                    ticket.setUser(userJson);
                }
            }
        } else {
            ticket.setStatus(TicketStatusEnum.NEW.name());

            String userJson = BytedeskConsts.EMPTY_JSON_STRING;
            // 使用在线客服工单会话user info
            if (StringUtils.hasText(request.getServiceThreadTopic())) {
                String serviceThreadTopic = request.getServiceThreadTopic();
                Optional<ThreadEntity> serviceThreadOptional = threadRestService.findFirstByTopic(serviceThreadTopic);
                if (serviceThreadOptional.isPresent()) {
                    userJson = serviceThreadOptional.get().getUser();
                    ticket.setUser(userJson);
                }
            }
        }
        // 
        ticket.setReporter(JSON.toJSONString(request.getReporter()));
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
        ticket = updateAssigneeAndWorkgroup(ticket, request);

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
        if (StringUtils.hasText(request.getAssigneeUid()) && StringUtils.hasText(ticket.getAssigneeString())) {
            String oldAssigneeUid = ticket.getAssignee().getUid();
            if (oldAssigneeUid != null && !oldAssigneeUid.equals(request.getAssigneeUid())) {
                TicketUpdateAssigneeEvent ticketUpdateAssigneeEvent = new TicketUpdateAssigneeEvent(ticket,
                        oldAssigneeUid, request.getAssigneeUid());
                applicationEventPublisher.publishEvent(ticketUpdateAssigneeEvent);
            }
        }

        // 发布事件，判断workgroupUid是否被修改
        if (StringUtils.hasText(request.getWorkgroupUid()) && StringUtils.hasText(ticket.getWorkgroupString())) {
            String oldWorkgroupUid = ticket.getWorkgroup().getUid();
            if (oldWorkgroupUid != null && !oldWorkgroupUid.equals(request.getWorkgroupUid())) {
                TicketUpdateWorkgroupEvent ticketUpdateWorkgroupEvent = new TicketUpdateWorkgroupEvent(ticket,
                        oldWorkgroupUid, request.getWorkgroupUid());
                applicationEventPublisher.publishEvent(ticketUpdateWorkgroupEvent);
            }
        }

        return convertToResponse(ticket);
    }

    private TicketEntity updateAssigneeAndWorkgroup(TicketEntity ticket, TicketRequest request) {
        if (StringUtils.hasText(request.getAssigneeUid())) {
            Optional<AgentEntity> assigneeOptional = agentRestService.findByUid(request.getAssigneeUid());
            if (assigneeOptional.isPresent()) {
                UserProtobuf assigneeProtobuf = UserProtobuf.builder()
                        .nickname(assigneeOptional.get().getNickname())
                        .avatar(assigneeOptional.get().getAvatar())
                        .build();
                assigneeProtobuf.setUid(assigneeOptional.get().getUid());
                assigneeProtobuf.setType(UserTypeEnum.AGENT.name());
                ticket.setAssignee(JSON.toJSONString(assigneeProtobuf));
                ticket.setStatus(TicketStatusEnum.CLAIMED.name());
            }
        }

        if (StringUtils.hasText(request.getWorkgroupUid())) {
            Optional<WorkgroupEntity> workgroupOptional = workgroupRestService.findByUid(request.getWorkgroupUid());
            if (workgroupOptional.isPresent()) {
                UserProtobuf workgroupProtobuf = UserProtobuf.builder()
                        .nickname(workgroupOptional.get().getNickname())
                        .avatar(workgroupOptional.get().getAvatar())
                        .build();
                workgroupProtobuf.setUid(workgroupOptional.get().getUid());
                workgroupProtobuf.setType(UserTypeEnum.WORKGROUP.name());
                ticket.setWorkgroup(JSON.toJSONString(workgroupProtobuf));
            }
        }
        return ticket;
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
        String visitorJson = BytedeskConsts.EMPTY_JSON_STRING;
        String topic = "";
        //
        if (ticket.getType().equals(TicketTypeEnum.AGENT.name())) {
            topic = TopicUtils.formatOrgAgentTicketThreadTopic(ticket.getAssignee().getUid(), ticket.getUid());
            // 使用在线客服工单会话user info
            if (StringUtils.hasText(ticket.getServiceThreadTopic())) {
                String serviceThreadTopic = ticket.getServiceThreadTopic();
                Optional<ThreadEntity> serviceThreadOptional = threadRestService.findFirstByTopic(serviceThreadTopic);
                if (serviceThreadOptional.isPresent()) {
                    visitorJson = serviceThreadOptional.get().getUser();
                }
            } else {
                //
                UserProtobuf userProtobuf = UserProtobuf.builder()
                        .nickname(ticket.getAssignee().getNickname())
                        .avatar(ticket.getAssignee().getAvatar())
                        .build();
                userProtobuf.setUid(ticket.getAssignee().getUid());
                userProtobuf.setType(UserTypeEnum.AGENT.name());
                visitorJson = JSON.toJSONString(userProtobuf);
            }
        } else if (ticket.getType().equals(TicketTypeEnum.WORKGROUP.name())) {
            topic = TopicUtils.formatOrgWorkgroupTicketThreadTopic(ticket.getWorkgroup().getUid(), ticket.getUid());
            // 使用在线客服工单会话user info
            if (StringUtils.hasText(ticket.getServiceThreadTopic())) {
                String serviceThreadTopic = ticket.getServiceThreadTopic();
                Optional<ThreadEntity> serviceThreadOptional = threadRestService.findFirstByTopic(serviceThreadTopic);
                if (serviceThreadOptional.isPresent()) {
                    visitorJson = serviceThreadOptional.get().getUser();
                } else {
                    //
                    UserProtobuf userProtobuf = UserProtobuf.builder()
                            .nickname(ticket.getWorkgroup().getNickname())
                            .avatar(ticket.getWorkgroup().getAvatar())
                            .build();
                    userProtobuf.setUid(ticket.getWorkgroup().getUid());
                    userProtobuf.setType(UserTypeEnum.WORKGROUP.name());
                    visitorJson = JSON.toJSONString(userProtobuf);
                }
            }
        }
        // 每次创建新工单会话，不能使用已存在的会话
        ThreadEntity thread = ThreadEntity.builder()
                .type(ThreadTypeEnum.TICKET.name())
                .state(ThreadStateEnum.STARTED.name())
                .topic(topic)
                .user(visitorJson)
                .owner(owner)
                .build();
        thread.setUid(uidUtils.getUid());
        thread.setOrgUid(ticket.getOrgUid());
        thread.setClient(ticket.getClient());
        //
        return threadRestService.save(thread);
    }

    @Transactional
    public void assignTicket(Long ticketId, AgentEntity assignee) {
        // UserEntity user = authService.getUser();
        // if (user == null) {
        // throw new RuntimeException("user not found");
        // }
        // String userId = user.getUid();
        // 检查用户是否是主管
        // if (!identityService.isUserInGroup(userId, "supervisors")) {
        // throw new AccessDeniedException("Only supervisors can assign tickets");
        // }
        // 分配工单...
        TicketEntity ticket = findTicketById(ticketId);
        UserProtobuf assigneeProtobuf = UserProtobuf.builder()
                .nickname(assignee.getNickname())
                .avatar(assignee.getAvatar())
                .build();
        assigneeProtobuf.setUid(assignee.getUid());
        assigneeProtobuf.setType(UserTypeEnum.AGENT.name());
        ticket.setAssignee(JSON.toJSONString(assigneeProtobuf));
        ticket.setStatus("处理中");
        ticket.setUpdatedAt(LocalDateTime.now());

        // 更新流程变量
        taskService.complete(getTaskIdByTicketId(ticketId), Map.of("assignee", assignee));
    }

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

    // public TicketStatistics getStatistics() {
    // TicketStatistics stats = new TicketStatistics();
    // stats.setTotalTickets(ticketRepository.count());
    // stats.setOpenTickets(ticketRepository.countByStatusNot("已关闭"));
    // stats.setClosedTickets(ticketRepository.countByStatus("已关闭"));
    // // TODO: 计算平均解决时间和SLA违规数
    // return stats;
    // }

    @Override
    public TicketResponse convertToResponse(TicketEntity entity) {
        return TicketConvertUtils.convertToResponse(entity);
    }

}