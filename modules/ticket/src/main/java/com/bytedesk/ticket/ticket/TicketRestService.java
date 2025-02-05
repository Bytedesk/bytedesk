package com.bytedesk.ticket.ticket;

import org.flowable.engine.TaskService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;
import java.time.LocalDateTime;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupRestService;
import com.bytedesk.ticket.attachment.TicketAttachmentEntity;
import com.bytedesk.ticket.attachment.TicketAttachmentRepository;
import com.bytedesk.ticket.comment.TicketCommentRequest;
import com.bytedesk.ticket.comment.TicketCommentEntity;
import com.bytedesk.ticket.comment.TicketCommentRepository;

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

    @Override
    public Page<TicketResponse> queryByOrg(TicketRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.ASC,
                "id");
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
            ticket.setWorkgroup(workgroupOptional.get());
        } else {
            throw new RuntimeException("workgroup not found");
        }
        // 
        Optional<AgentEntity> assigneeOptional = agentRestService.findByUid(request.getAssigneeUid());
        if (assigneeOptional.isPresent()) {
            ticket.setAssignee(assigneeOptional.get());
            ticket.setType(TicketTypeEnum.AGENT.name());
        } else {
            ticket.setType(TicketTypeEnum.GROUP.name());
        }
        Optional<UserEntity> reporterOptional = userRestService.findByUid(request.getReporterUid());
        if (reporterOptional.isPresent()) {
            ticket.setReporter(reporterOptional.get());
        }
        // 
        TicketEntity savedTicket = save(ticket);
        if (savedTicket == null) {
            throw new RuntimeException("create ticket failed");
        }

        return convertToResponse(savedTicket);
    }

    @Override
    public TicketResponse update(TicketRequest request) {
        Optional<TicketEntity> ticketOptional = findByUid(request.getUid());
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("ticket not found");
        }
        TicketEntity ticket = ticketOptional.get();
        // ticket = updateTicket(ticket.getId(), request);
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());
        ticket.setCategoryUid(request.getCategoryUid());
        ticket.setStatus(request.getStatus());
        // 
        Optional<AgentEntity> assigneeOptional = agentRestService.findByUid(request.getAssigneeUid());
        if (assigneeOptional.isPresent()) {
            ticket.setAssignee(assigneeOptional.get());
        }
        TicketEntity savedTicket = save(ticket);
        if (savedTicket == null) {
            throw new RuntimeException("update ticket failed");
        }
        // 
        return convertToResponse(savedTicket);
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
        ticket.setAssignee(assignee);
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
        comment.setCreatedAt(LocalDateTime.now());
        
        return commentRepository.save(comment);
    }
    
    @Transactional
    public TicketAttachmentEntity uploadAttachment(Long ticketId, MultipartFile file) {
        TicketEntity ticket = findTicketById(ticketId);
        
        TicketAttachmentEntity attachment = new TicketAttachmentEntity();
        attachment.setTicket(ticket);
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFileType(file.getContentType());
        attachment.setFileSize(file.getSize());
        attachment.setUploadTime(LocalDateTime.now());
        
        // TODO: 实现文件存储逻辑
        attachment.setFilePath("/uploads/" + file.getOriginalFilename());
        
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
                throw new RuntimeException("update ticket failed");
            }
            return ticket;
        } catch (Exception e) {
            throw new RuntimeException("update ticket failed");
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
    public Optional<TicketEntity> findByUid(String uid) {
        return ticketRepository.findByUid(uid);
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