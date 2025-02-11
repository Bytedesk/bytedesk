package com.bytedesk.ticket.ticket;

import org.flowable.engine.TaskService;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDateTime;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserRestService;
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

    private final UserRestService userRestService;

    private final UidUtils uidUtils;

    private final AgentRestService agentRestService;

    private final WorkgroupRestService workgroupRestService;

    private final ThreadRestService threadRestService;

    // private final CategoryRestService categoryRestService;

    private final UploadRestService uploadRestService;

    @Override
    public Page<TicketResponse> queryByOrg(TicketRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "updatedAt");
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
        request.setReporterUid(user.getUid());
        // 
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.ASC,
                "id");
        Specification<TicketEntity> spec = TicketSpecification.search(request);
        Page<TicketEntity> ticketPage = ticketRepository.findAll(spec, pageable);
        return ticketPage.map(this::convertToResponse);
    }

    @Cacheable(value = "ticket", key = "#uid", unless = "#result == null")
    @Override
    public Optional<TicketEntity> findByUid(String uid) {
        return ticketRepository.findByUid(uid);
    }


    @Transactional
    @Override
    public TicketResponse create(TicketRequest request) {
        // UserEntity user = authService.getUser();
        // if (user == null) {
        //     throw new RuntimeException("user not found");
        // }
        // String userUid = user.getUid();
        // 检查用户是否有创建工单的权限
        // if (!identityService.hasPrivilege(userId, "TICKET_CREATE")) {
        //     throw new AccessDeniedException("No permission to create ticket");
        // }
        // 创建工单...
        TicketEntity ticket = modelMapper.map(request, TicketEntity.class);
        ticket.setUid(uidUtils.getUid());
        ticket.setStatus(TicketStatusEnum.NEW.name());
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
            ticket.setType(TicketTypeEnum.AGENT.name());
            ticket.setStatus(TicketStatusEnum.ASSIGNED.name());
            // 
            UserProtobuf userProtobuf = UserProtobuf.builder()
                .nickname(assigneeOptional.get().getNickname())
                .avatar(assigneeOptional.get().getAvatar())
                .build();
            userProtobuf.setUid(assigneeOptional.get().getUid());
            userProtobuf.setType(UserTypeEnum.AGENT.name());
            String userJson = JSON.toJSONString(userProtobuf);
            // 创建工单会话
            ThreadEntity thread = createTicketThread(request, TicketTypeEnum.AGENT, userJson);
            ticket.setThreadTopic(thread.getTopic());
        } else {
            ticket.setType(TicketTypeEnum.WORKGROUP.name());
            ticket.setStatus(TicketStatusEnum.NEW.name());
            // 
            UserProtobuf userProtobuf = UserProtobuf.builder()
                .nickname(workgroupOptional.get().getNickname())
                .avatar(workgroupOptional.get().getAvatar())
                .build();
            userProtobuf.setUid(workgroupOptional.get().getUid());
            userProtobuf.setType(UserTypeEnum.WORKGROUP.name());
            String userJson = JSON.toJSONString(userProtobuf);
            // 创建工单会话 
            ThreadEntity thread = createTicketThread(request, TicketTypeEnum.WORKGROUP, userJson);
            ticket.setThreadTopic(thread.getTopic());
        }
        Optional<UserEntity> reporterOptional = userRestService.findByUid(request.getReporterUid());
        if (reporterOptional.isPresent()) {
            UserProtobuf reporterProtobuf = UserProtobuf.builder()
                .nickname(reporterOptional.get().getNickname())
                .avatar(reporterOptional.get().getAvatar())
                .build();
            reporterProtobuf.setUid(reporterOptional.get().getUid());
            reporterProtobuf.setType(UserTypeEnum.USER.name());
            ticket.setReporter(JSON.toJSONString(reporterProtobuf));
        }
        // 先保存工单
        TicketEntity savedTicket = save(ticket);
        // 保存附件
        List<TicketAttachmentEntity> attachments = new ArrayList<>();
        if (request.getUploadUids() != null) {
            for (String uploadUid : request.getUploadUids()) {
                Optional<UploadEntity> uploadOptional = uploadRestService.findByUid(uploadUid);
                if (uploadOptional.isPresent()) {
                    TicketAttachmentEntity attachment = new TicketAttachmentEntity();
                    attachment.setUid(uidUtils.getUid());
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
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());
        ticket.setStatus(request.getStatus());
        // 
        Optional<AgentEntity> assigneeOptional = agentRestService.findByUid(request.getAssigneeUid());
        if (assigneeOptional.isPresent()) {
            UserProtobuf assigneeProtobuf = UserProtobuf.builder()
                .nickname(assigneeOptional.get().getNickname())
                .avatar(assigneeOptional.get().getAvatar())
                .build();
            assigneeProtobuf.setUid(assigneeOptional.get().getUid());
            assigneeProtobuf.setType(UserTypeEnum.AGENT.name());
            ticket.setAssignee(JSON.toJSONString(assigneeProtobuf));
        }
        // 
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
         // 先保存工单
         TicketEntity savedTicket = save(ticket);
        // 
        List<TicketAttachmentEntity> attachments = new ArrayList<>();
        if (request.getUploadUids() != null) {
            for (String uploadUid : request.getUploadUids()) {
                Optional<UploadEntity> uploadOptional = uploadRestService.findByUid(uploadUid);
                if (uploadOptional.isPresent()) {
                    TicketAttachmentEntity attachment = new TicketAttachmentEntity();
                    attachment.setUid(uidUtils.getUid());
                    attachment.setTicket(savedTicket);
                    attachment.setUpload(uploadOptional.get());
                    attachmentRepository.save(attachment);
                    // 
                    attachments.add(attachment);
                }
            }
        }
        savedTicket.setAttachments(attachments);
        // 
        savedTicket = save(savedTicket);
        if (savedTicket == null) {
            throw new RuntimeException("save ticket failed");
        }
        // 
        return convertToResponse(savedTicket);
    }

    // 创建工单会话
    public ThreadEntity createTicketThread(TicketRequest request, TicketTypeEnum ticketType, String userJson) {
        // 
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        //
        String topic = "";
        if (ticketType == TicketTypeEnum.AGENT) {
            topic = TopicUtils.formatOrgAgentTicketThreadTopic(request.getAssigneeUid(), user.getUid());
        } else if (ticketType == TicketTypeEnum.WORKGROUP) {
            topic = TopicUtils.formatOrgWorkgroupTicketThreadTopic(request.getWorkgroupUid(), user.getUid());
        }
        Optional<ThreadEntity> threadOptional = threadRestService.findFirstByTopic(topic);
        if (threadOptional.isPresent()) {
            return threadOptional.get();
        }
        // 创建工单会话
        ThreadEntity thread = ThreadEntity.builder()
            .type(ThreadTypeEnum.TICKET.name())
            .state(ThreadStateEnum.STARTED.name())
            .topic(topic)
            .user(userJson)
            .owner(user)
            .build();
        thread.setUid(uidUtils.getUid());
        thread.setOrgUid(user.getOrgUid());
        thread.setClient(request.getClient());
        // 
        return threadRestService.save(thread);
    }

    @Transactional
    public void assignTicket(Long ticketId, AgentEntity assignee) {
        // UserEntity user = authService.getUser();
        // if (user == null) {
        //     throw new RuntimeException("user not found");
        // }
        // String userId = user.getUid();
        // 检查用户是否是主管
        // if (!identityService.isUserInGroup(userId, "supervisors")) {
        //     throw new AccessDeniedException("Only supervisors can assign tickets");
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
    //     TicketStatistics stats = new TicketStatistics();
    //     stats.setTotalTickets(ticketRepository.count());
    //     stats.setOpenTickets(ticketRepository.countByStatusNot("已关闭"));
    //     stats.setClosedTickets(ticketRepository.countByStatus("已关闭"));
    //     // TODO: 计算平均解决时间和SLA违规数
    //     return stats;
    // }

    @Override
    public TicketResponse convertToResponse(TicketEntity entity) {
        return modelMapper.map(entity, TicketResponse.class);
    }

    
}   