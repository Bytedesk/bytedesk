/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-29 12:24:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-16 07:11:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.topic.TopicRestService;
import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.member.MemberRestService;
import com.bytedesk.ticket.ticket.dto.TicketHistoryActivityResponse;
import com.bytedesk.ticket.ticket.dto.TicketHistoryProcessResponse;
import com.bytedesk.ticket.ticket.dto.TicketHistoryTaskResponse;
import com.bytedesk.ticket.utils.TicketConvertUtils;
import com.bytedesk.core.utils.BdDateUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Description("Ticket Service - Workflow-based ticket management and processing service")
public class TicketService {

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;
    private final MemberRestService memberRestService;
    private final ThreadRestService threadRestService;
    private final TopicRestService topicRestService;
    private final TicketRestService ticketRestService;

    private TicketEntity getTicketOrThrow(String ticketUid) {
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(ticketUid);
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("工单不存在: " + ticketUid);
        }
        return ticketOptional.get();
    }

    private Task getActiveTaskOrThrow(TicketEntity ticket, TicketRequest request) {
        if (StringUtils.hasText(request.getTaskId())) {
            Task task = taskService.createTaskQuery()
                    .taskId(request.getTaskId())
                    .active()
                    .singleResult();
            if (task == null) {
                throw new RuntimeException("任务不存在或已结束: " + request.getTaskId());
            }
            if (!Objects.equals(task.getProcessInstanceId(), ticket.getProcessInstanceId())) {
                throw new RuntimeException("任务不属于该工单流程实例: " + request.getTaskId());
            }
            return task;
        }

        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(ticket.getProcessInstanceId())
                .active()
                .list();

        if (tasks == null || tasks.isEmpty()) {
            throw new RuntimeException("工单任务不存在: " + ticket.getUid());
        }
        if (tasks.size() > 1) {
            throw new RuntimeException("当前存在多个并行任务，请指定 taskId");
        }
        return tasks.get(0);
    }

    private void addTaskComment(Task task, TicketEntity ticket, String userId, String type, String message) {
        Comment comment = taskService.addComment(task.getId(), ticket.getProcessInstanceId(), type, message);
        if (StringUtils.hasText(userId)) {
            comment.setUserId(userId);
        }
        taskService.saveComment(comment);
    }

    /**
     * 认领工单
     * NEW -&gt; CLAIMED (认领)
     */
    @Transactional
    public TicketResponse claimTicket(TicketRequest request) {
        log.info("开始认领工单: uid={}, assigneeUid={}, orgUid={}",
                request.getUid(), request.getAssignee().getUid(), request.getOrgUid());
        // 
        String assigneeUid = request.getAssignee().getUid();
        Assert.notNull(assigneeUid, "处理人uid不能为空");
        String assigneeName = request.getAssignee().getNickname();
        // 1. 先查询工单
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("工单不存在: " + request.getUid());
        }
        TicketEntity ticket = ticketOptional.get();

        final String status = ticket.getStatus();
        final String statusNew = TicketStatusEnum.NEW.name();
        final String statusAssigned = TicketStatusEnum.ASSIGNED.name();
        final String statusUnclaimed = TicketStatusEnum.UNCLAIMED.name();

        // 判断状态是否为NEW或ASSIGNED或退回状态，如果不是，则不能认领
        if (!statusNew.equals(status) &&
                !statusAssigned.equals(status) &&
                !statusUnclaimed.equals(status)) {
            throw new RuntimeException("已经被认领，工单状态为" + status + "，不能重复认领: " + request.getUid());
        }

        // 如果是ASSIGNED状态，判断是否为本人
        if (statusAssigned.equals(status)) {
            if (!ticket.getAssignee().getUid().equals(assigneeUid)) {
                throw new RuntimeException("工单已经被分配，非本人不能认领: " + request.getUid());
            }
        }

        // 2. 查询任务
        Task task = taskService.createTaskQuery()
                .processInstanceId(ticket.getProcessInstanceId()) // 使用processInstanceId查询
                .taskDefinitionKey(TicketConsts.TICKET_USER_TASK_ASSIGN_TO_GROUP)
                .active() // 只查询活动的任务
                .singleResult();

        log.info("查询到的任务: task={}, processInstanceId={}", task, ticket.getProcessInstanceId());

        if (task == null) {
            // 如果没有找到任务，打印更多信息以便调试
            List<Task> allTasks = taskService.createTaskQuery()
                    .processInstanceId(ticket.getProcessInstanceId())
                    .list();
            log.error("未找到可认领的任务, 当前流程实例所有任务: {}", allTasks);
            throw new RuntimeException("工单任务不存在或已被认领: " + request.getUid());
        }

        try {
            // 3. 认领任务
            taskService.claim(task.getId(), assigneeUid);
            log.info("工单认领成功: taskId={}, assigneeUid={}", task.getId(), assigneeUid);

            // 只添加任务评论，指定userId参数为assigneeUid
            Comment comment = taskService.addComment(task.getId(), ticket.getProcessInstanceId(), TicketStatusEnum.CLAIMED.name(), 
                    "工单被 " + assigneeName + " 认领");
            comment.setUserId(assigneeUid); // 设置评论的userId为当前认领人
            taskService.saveComment(comment);

        } catch (Exception e) {
            log.error("工单认领失败: ", e);
            throw new RuntimeException("工单认领失败: " + e.getMessage());
        }

        // 4. 更新工单状态
        Optional<MemberEntity> assigneeOptional = memberRestService.findByUid(assigneeUid);
        if (assigneeOptional.isPresent()) {
            MemberEntity member = assigneeOptional.get();
            // 更新assignee
            UserProtobuf assigneeProtobuf = UserProtobuf.builder()
                    .uid(member.getUid())
                    .nickname(member.getNickname())
                    .avatar(member.getAvatar())
                    .type(UserTypeEnum.MEMBER.name())
                    .build();
            ticket.setAssignee(assigneeProtobuf.toJson());
            ticket.setStatus(TicketStatusEnum.CLAIMED.name());

            // 4. 更新流程变量
            Map<String, Object> variables = new HashMap<>();
            variables.put(TicketConsts.TICKET_VARIABLE_ASSIGNEE, ticket.getAssignee());
            variables.put(TicketConsts.TICKET_VARIABLE_STATUS, ticket.getStatus());
            variables.put(TicketConsts.TICKET_VARIABLE_CLAIM_TIME, new Date());
            runtimeService.setVariables(ticket.getProcessInstanceId(), variables);

            // 5. 创建工单会话，迁移到工单创建时创建
            // serviceThreadTopic跟threadUid合并
            // if (!StringUtils.hasText(ticket.getThreadUid())) {
            //     // 如果创建工单的时候没有绑定会话，则创建会话
            //     ThreadEntity thread = ticketRestService.createTicketThread(ticket);
            //     if (thread != null) {
            //         ticket.setTopic(thread.getTopic());
            //         ticket.setThreadUid(thread.getUid());
            //     }
            // }

            // 将claimer添加到会话中
            Optional<ThreadEntity> threadOptional = threadRestService.findByUid(ticket.getThreadUid());
            if (threadOptional.isPresent()) {
                ThreadEntity thread = threadOptional.get();
                // 添加claimer到会话中
                thread.getTicketors().add(assigneeProtobuf.toJson());
                // thread.setAgent(assigneeProtobuf.toJson());
                // 保存
                threadRestService.save(thread);
                // 添加订阅
                String userUid = member.getUser().getUid();
                topicRestService.create(thread.getTopic(), userUid);
            }

            // 6. 发布工单分配消息事件
            // 此处没有使用ticket自带消息机制，便于扩展
            // eventPublisher.publishEvent(TicketMessageEvent.builder()
            // .ticketUid(ticket.getUid())
            // .processInstanceId(ticket.getProcessInstanceId())
            // .type(TicketMessageType.ASSIGNED.name())
            // .assignee(assigneeProtobuf)
            // .description("工单已分配给 " + assigneeProtobuf.getNickname())
            // .createTime(new Date())
            // .build());
        }

        // 6. 保存工单
        ticketRestService.save(ticket);

        // 7. 返回工单响应
        return TicketConvertUtils.convertToResponse(ticket);
    }

    /**
     * 开始处理工单
     * CLAIMED/REOPENED -&gt; PROCESSING (开始处理)
     */
    @Transactional
    public TicketResponse startTicket(TicketRequest request) {
        log.info("开始处理工单: uid={}, assigneeUid={}, orgUid={}",
                request.getUid(), request.getAssignee().getUid(), request.getOrgUid());
        
        String assigneeUid = request.getAssignee().getUid();
        Assert.notNull(assigneeUid, "处理人uid不能为空");
        String assigneeName = request.getAssignee().getNickname();

        // 1. 查询工单
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("工单不存在: " + request.getUid());
        }
        TicketEntity ticket = ticketOptional.get();

        // 2. 判断工单状态 - 修改此处以支持REOPENED状态
        if (!ticket.getStatus().equals(TicketStatusEnum.CLAIMED.name()) && 
            !ticket.getStatus().equals(TicketStatusEnum.REOPENED.name())) {
            throw new RuntimeException("工单状态为" + ticket.getStatus() + "，不能开始处理: " + request.getUid());
        }

        // 3. 判断处理人是否为本人 - 对于REOPENED状态，可能需要判断是否为原处理人
        if (!StringUtils.hasText(ticket.getAssigneeString()) && 
            !ticket.getStatus().equals(TicketStatusEnum.REOPENED.name())) {
            throw new RuntimeException("工单未被认领，不能开始处理: " + request.getUid());
        }

        if (StringUtils.hasText(ticket.getAssigneeString()) &&
            !ticket.getAssignee().getUid().equals(request.getAssignee().getUid())) {
            throw new RuntimeException("非工单处理人，不能开始处理: " + request.getUid());
        }

        // 4. 判断工单是否已开始处理
        if (ticket.getStatus().equals(TicketStatusEnum.PROCESSING.name())) {
            throw new RuntimeException("工单已开始处理，不能重复开始处理: " + request.getUid());
        }

        // 5. 查询任务
        Task task = taskService.createTaskQuery()
            .processInstanceId(ticket.getProcessInstanceId())
            .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_TICKET_UID, request.getUid())
            .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
            .singleResult();

        if (task == null) {
            throw new RuntimeException("工单任务不存在: " + request.getUid());
        }

        try {
            // 5. 添加任务评论，记录开始处理
            Comment comment = taskService.addComment(task.getId(), ticket.getProcessInstanceId(),
                    TicketStatusEnum.PROCESSING.name(), 
                    "工单由" + assigneeName + "开始处理");
            comment.setUserId(assigneeUid); // 设置评论的userId为当前认领人
            taskService.saveComment(comment);

            // 6. 设置任务变量
            Map<String, Object> variables = new HashMap<>();
            variables.put("startProcessingTime", new Date());
            variables.put("processingUser", ticket.getAssigneeString());
            taskService.setVariables(task.getId(), variables);

            // 7. 更新工单状态
            ticket.setStatus(TicketStatusEnum.PROCESSING.name());
            ticketRestService.save(ticket);

            log.info("工单开始处理成功: taskId={}, assigneeUid={}", task.getId(), ticket.getAssigneeString());

            return TicketConvertUtils.convertToResponse(ticket);
        } catch (Exception e) {
            log.error("工单开始处理失败: ", e);
            throw new RuntimeException("工单开始处理失败: " + e.getMessage());
        }
    }

    /**
     * 退回工单
     * CLAIMED -&gt; UNCLAIMED (退回)
     */
    @Transactional
    public TicketResponse unclaimTicket(TicketRequest request) {
        log.info("开始退回工单: uid={}, assigneeUid={}, orgUid={}",
                request.getUid(), request.getAssignee().getUid(), request.getOrgUid());
        // 
        String assigneeUid = request.getAssignee().getUid();
        Assert.notNull(assigneeUid, "处理人uid不能为空");
        String assigneeName = request.getAssignee().getNickname();

        // 1. 查询工单
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("工单不存在: " + request.getUid());
        }
        TicketEntity ticket = ticketOptional.get();

        // 判断状态是否为已认领，如果不是，则不能退回
        if (!ticket.getStatus().equals(TicketStatusEnum.CLAIMED.name())) {
            throw new RuntimeException("工单状态为" + ticket.getStatus() + "，不能退回: " + request.getUid());
        }
        if (!StringUtils.hasText(ticket.getAssigneeString())) {
            throw new RuntimeException("非已认领工单，不能退回: " + request.getUid());
        }
        // 判断认领人是否为本人，如果不是，则不能退回
        if (!ticket.getAssignee().getUid().equals(assigneeUid)) {
            throw new RuntimeException("工单状态为" + ticket.getStatus() + "，不能退回: " + request.getUid());
        }

        // 2. 查询任务
        Task task = taskService.createTaskQuery()
            .processInstanceId(ticket.getProcessInstanceId())
                .taskDefinitionKey(TicketConsts.TICKET_USER_TASK_ASSIGN_TO_GROUP)
                .taskAssignee(assigneeUid)
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_TICKET_UID, request.getUid())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .singleResult();

        // 如果任务不存在，则返回null
        if (task == null) {
            // return null;
            throw new RuntimeException("工单任务不存在: " + request.getUid());
        }

        log.info("退回工单: task={}", task);

        // 退回任务
        taskService.unclaim(task.getId());

        // 只添加任务评论
        Comment comment = taskService.addComment(task.getId(), ticket.getProcessInstanceId(),
                TicketStatusEnum.UNCLAIMED.name(), "工单被 " + assigneeName + "退回到工作组");
        comment.setUserId(assigneeUid); // 设置评论的userId为当前认领人
        taskService.saveComment(comment);

        // 更新工单状态
        ticket.setAssignee(null);
        ticket.setStatus(TicketStatusEnum.UNCLAIMED.name());
        ticketRestService.save(ticket);

        // 发布工单退回消息事件
        // eventPublisher.publishEvent(TicketMessageEvent.builder()
        // .ticketUid(ticket.getUid())
        // .processInstanceId(ticket.getProcessInstanceId())
        // .type(TicketMessageType.UNCLAIMED.name())
        // .build());

        return TicketConvertUtils.convertToResponse(ticket);
    }

    /**
     * 转派工单: TRANSFERRED
     * CLAIMED -&gt; CLAIMED (转派)
     */
    @Transactional
    public TicketResponse transferTicket(TicketRequest request) {
        log.info("开始转派工单: uid={}, assigneeUid={}, orgUid={}",
                request.getUid(), request.getAssignee().getUid(), request.getOrgUid());
        // 
        String assigneeUid = request.getAssignee().getUid();
        Assert.notNull(assigneeUid, "处理人uid不能为空");

        // 1. 查询工单
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("工单不存在: " + request.getUid());
        }
        TicketEntity ticket = ticketOptional.get();

        // 2. 判断工单状态
        if (!ticket.getStatus().equals(TicketStatusEnum.CLAIMED.name())) {
            throw new RuntimeException("工单状态为" + ticket.getStatus() + "，不能转派: " + request.getUid());
        }

        // 3. 查询任务
        Task task = taskService.createTaskQuery()
            .processInstanceId(ticket.getProcessInstanceId())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_TICKET_UID, request.getUid())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .singleResult();

        if (task == null) {
            throw new RuntimeException("工单任务不存在: " + request.getUid());
        }

        // 4. 转派任务
        taskService.setAssignee(task.getId(), assigneeUid);

        // comment
        Comment comment = taskService.addComment(task.getId(), ticket.getProcessInstanceId(),
                TicketStatusEnum.TRANSFERRED.name(), "工单被转派给 " + assigneeUid);
        comment.setUserId(assigneeUid); // 设置评论的userId为当前认领人
        taskService.saveComment(comment);

        // 5. 更新工单状态
        ticket.setStatus(TicketStatusEnum.CLAIMED.name());
        ticketRestService.save(ticket);

        return TicketConvertUtils.convertToResponse(ticket);
    }

    /**
     * 挂起工单
     * PROCESSING -&gt; HOLDING (挂起)
     */
    @Transactional
    public TicketResponse holdTicket(TicketRequest request) {
        log.info("开始挂起工单: uid={}, assigneeUid={}, orgUid={}",
                request.getUid(), request.getAssignee().getUid(), request.getOrgUid());
        // 
        String assigneeUid = request.getAssignee().getUid();
        Assert.notNull(assigneeUid, "处理人uid不能为空");

        // 1. 查询工单
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("工单不存在: " + request.getUid());
        }
        TicketEntity ticket = ticketOptional.get();

        // 2. 判断工单状态
        if (!ticket.getStatus().equals(TicketStatusEnum.PROCESSING.name()) &&
                !ticket.getStatus().equals(TicketStatusEnum.RESUMED.name())) {
            throw new RuntimeException("工单状态为" + ticket.getStatus() + "，不能挂起: " + request.getUid());
        }

        // 3. 查询任务
        Task task = taskService.createTaskQuery()
            .processInstanceId(ticket.getProcessInstanceId())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_TICKET_UID, request.getUid())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .singleResult();

        if (task == null) {
            throw new RuntimeException("工单任务不存在: " + request.getUid());
        }

        // comment
        Comment comment = taskService.addComment(task.getId(), ticket.getProcessInstanceId(),
                TicketStatusEnum.HOLDING.name(), "工单被挂起");
        comment.setUserId(assigneeUid); // 设置评论的userId为当前认领人
        taskService.saveComment(comment);

        // 4. 挂起任务
        taskService.setAssignee(task.getId(), null);

        // 5. 更新工单状态
        ticket.setStatus(TicketStatusEnum.HOLDING.name());
        ticketRestService.save(ticket);

        return TicketConvertUtils.convertToResponse(ticket);
    }

    /**
     * 恢复工单
     * HOLDING -&gt; RESUMED (恢复)
     */
    @Transactional
    public TicketResponse resumeTicket(TicketRequest request) {
        log.info("开始恢复工单: uid={}, assigneeUid={}, orgUid={}",
                request.getUid(), request.getAssignee().getUid(), request.getOrgUid());

        // 
        String assigneeUid = request.getAssignee().getUid();
        Assert.notNull(assigneeUid, "处理人uid不能为空");

        // 1. 查询工单
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("工单不存在: " + request.getUid());
        }
        TicketEntity ticket = ticketOptional.get();

        // 2. 判断工单状态
        if (!ticket.getStatus().equals(TicketStatusEnum.HOLDING.name())) {
            throw new RuntimeException("工单状态为" + ticket.getStatus() + "，不能恢复: " + request.getUid());
        }

        // 3. 查询任务
        Task task = taskService.createTaskQuery()
            .processInstanceId(ticket.getProcessInstanceId())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_TICKET_UID, request.getUid())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .singleResult();

        if (task == null) {
            throw new RuntimeException("工单任务不存在: " + request.getUid());
        }

        // 4. 恢复任务
        taskService.setAssignee(task.getId(), assigneeUid);

        // comment
        Comment comment = taskService.addComment(task.getId(), ticket.getProcessInstanceId(),
                            TicketStatusEnum.RESUMED.name(), "工单被恢复");
        comment.setUserId(assigneeUid); // 设置评论的userId为当前认领人
        taskService.saveComment(comment);

        // 5. 更新工单状态
        ticket.setStatus(TicketStatusEnum.RESUMED.name());
        ticketRestService.save(ticket);

        return TicketConvertUtils.convertToResponse(ticket);
    }

    /**
     * 待回应工单
     * PROCESSING -&gt; PENDING (待回应)
     */
    @Transactional
    public TicketResponse pendTicket(TicketRequest request) {
        log.info("开始待回应工单: uid={}, assigneeUid={}, orgUid={}",
                request.getUid(), request.getAssignee().getUid(), request.getOrgUid());

        // 
        String assigneeUid = request.getAssignee().getUid();
        Assert.notNull(assigneeUid, "处理人uid不能为空");

        // 1. 查询工单
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("工单不存在: " + request.getUid());
        }
        TicketEntity ticket = ticketOptional.get();

        // 2. 判断工单状态
        if (!ticket.getStatus().equals(TicketStatusEnum.PROCESSING.name())) {
            throw new RuntimeException("工单状态为" + ticket.getStatus() + "，不能待处理: " + request.getUid());
        }

        // 3. 查询任务
        Task task = taskService.createTaskQuery()
            .processInstanceId(ticket.getProcessInstanceId())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_TICKET_UID, request.getUid())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .singleResult();

        if (task == null) {
            throw new RuntimeException("工单任务不存在: " + request.getUid());
        }

        // 4. 待处理任务
        taskService.setAssignee(task.getId(), null);

        // comment
        Comment comment = taskService.addComment(task.getId(), ticket.getProcessInstanceId(),
                        TicketStatusEnum.PENDING.name(), "工单被待回应");
        comment.setUserId(assigneeUid); // 设置评论的userId为当前认领人
        taskService.saveComment(comment);

        // 5. 更新工单状态
        ticket.setStatus(TicketStatusEnum.PENDING.name());
        ticketRestService.save(ticket);

        return TicketConvertUtils.convertToResponse(ticket);
    }

    /**
     * 重新打开工单
     * CLOSED/CANCELLED -&gt; REOPENED -&gt; PROCESSING (重新打开)
     */
    @Transactional
    public TicketResponse reopenTicket(TicketRequest request) {
        log.info("开始重新打开工单: uid={}, assigneeUid={}, orgUid={}",
                request.getUid(), request.getAssignee().getUid(), request.getOrgUid());

        // 
        String assigneeUid = request.getAssignee().getUid();
        Assert.notNull(assigneeUid, "处理人uid不能为空");

        // 1. 查询工单
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("工单不存在: " + request.getUid());
        }
        TicketEntity ticket = ticketOptional.get();

        // 2. 判断工单状态
        if (!ticket.getStatus().equals(TicketStatusEnum.CLOSED.name()) &&
                !ticket.getStatus().equals(TicketStatusEnum.CANCELLED.name())) {
            throw new RuntimeException("工单状态为" + ticket.getStatus() + "，不能重新打开: " + request.getUid());
        }

        // 3. 查询任务
        Task task = taskService.createTaskQuery()
            .processInstanceId(ticket.getProcessInstanceId())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_TICKET_UID, request.getUid())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .singleResult();

        if (task == null) {
            throw new RuntimeException("工单任务不存在: " + request.getUid());
        }

        // 4. 重新打开任务
        taskService.setAssignee(task.getId(), assigneeUid);

        // comment
        Comment comment = taskService.addComment(task.getId(), ticket.getProcessInstanceId(),
                    TicketStatusEnum.REOPENED.name(), "工单被重新打开");
        comment.setUserId(assigneeUid); // 设置评论的userId为当前认领人
        taskService.saveComment(comment);

        // 5. 更新工单状态
        ticket.setStatus(TicketStatusEnum.PROCESSING.name());
        ticketRestService.save(ticket);

        return TicketConvertUtils.convertToResponse(ticket);
    }

    /**
     * 升级工单
     * PROCESSING -&gt; ESCALATED (升级)
     */
    @Transactional
    public TicketResponse escalateTicket(TicketRequest request) {
        log.info("开始升级工单: uid={}, assigneeUid={}, orgUid={}",
                request.getUid(), request.getAssignee().getUid(), request.getOrgUid());

        // 
        String assigneeUid = request.getAssignee().getUid();
        Assert.notNull(assigneeUid, "处理人uid不能为空");

        // 1. 查询工单
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("工单不存在: " + request.getUid());
        }
        TicketEntity ticket = ticketOptional.get();

        // 2. 判断工单状态
        if (!ticket.getStatus().equals(TicketStatusEnum.PROCESSING.name())) {
            throw new RuntimeException("工单状态为" + ticket.getStatus() + "，不能升级: " + request.getUid());
        }

        // 3. 查询任务
        Task task = taskService.createTaskQuery()
            .processInstanceId(ticket.getProcessInstanceId())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_TICKET_UID, request.getUid())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .singleResult();

        if (task == null) {
            throw new RuntimeException("工单任务不存在: " + request.getUid());
        }

        try {
            // 4. 升级任务
            taskService.setAssignee(task.getId(), assigneeUid);

            // comment
            Comment comment = taskService.addComment(task.getId(), ticket.getProcessInstanceId(),
                                TicketStatusEnum.ESCALATED.name(), "工单被升级");
            comment.setUserId(assigneeUid); // 设置评论的userId为当前认领人
            taskService.saveComment(comment);

            // 5. 更新工单状态
            ticket.setStatus(TicketStatusEnum.ESCALATED.name());
            ticketRestService.save(ticket);

            return TicketConvertUtils.convertToResponse(ticket);

        } catch (Exception e) {
            log.error("工单升级失败: ", e);
            throw new RuntimeException("工单升级失败: " + e.getMessage());
        }
    }

    /**
     * 解决工单
     */
    @Transactional
    public TicketResponse resolveTicket(TicketRequest request) {
        log.info("开始解决工单: uid={}, assigneeUid={}, orgUid={}",
                request.getUid(), request.getAssignee().getUid(), request.getOrgUid());

        // 
        String assigneeUid = request.getAssignee().getUid();
        Assert.notNull(assigneeUid, "处理人uid不能为空");

        // 1. 查询工单
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("工单不存在: " + request.getUid());
        }
        TicketEntity ticket = ticketOptional.get();

        // 2. 判断工单状态
        if (!ticket.getStatus().equals(TicketStatusEnum.PROCESSING.name()) &&
                !ticket.getStatus().equals(TicketStatusEnum.RESUMED.name())) {
            throw new RuntimeException("工单状态为" + ticket.getStatus() + "，不能解决: " + request.getUid());
        }

        // 3. 查询任务
        Task task = taskService.createTaskQuery()
            .processInstanceId(ticket.getProcessInstanceId())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_TICKET_UID, request.getUid())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .singleResult();

        if (task == null) {
            throw new RuntimeException("工单任务不存在: " + request.getUid());
        }

        try {
            // 4. 添加评论
            Comment comment = taskService.addComment(task.getId(), ticket.getProcessInstanceId(),
                            TicketStatusEnum.RESOLVED.name(), "工单已解决");
            comment.setUserId(assigneeUid); // 设置评论的userId为当前认领人
            taskService.saveComment(comment);

            // 5. 设置流程变量，添加这段代码
            Map<String, Object> variables = new HashMap<>();
            variables.put("verified", false); // 默认设置为false，等待客户验证
            
            // 6. 完成任务，传入变量
            taskService.complete(task.getId(), variables);

            // 7. 更新工单状态
            ticket.setStatus(TicketStatusEnum.RESOLVED.name());
            ticket.setResolvedTime(BdDateUtils.now());
            ticketRestService.save(ticket);

            return TicketConvertUtils.convertToResponse(ticket);

        } catch (Exception e) {
            log.error("工单解决失败: ", e);
            throw new RuntimeException("工单解决失败: " + e.getMessage());
        }
    }

    /**
     * 客户验证工单
     * RESOLVED -&gt; CLOSED/REOPENED (验证通过/不通过)
     */
    @Transactional
    public TicketResponse verifyTicket(TicketRequest request) {
        log.info("开始验证工单: uid={}, verified={}, orgUid={}",
                request.getUid(), request.getVerified(), request.getOrgUid());

        // 
        String assigneeUid = request.getAssignee().getUid();
        Assert.notNull(assigneeUid, "处理人uid不能为空");

        // 1. 查询工单
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("工单不存在: " + request.getUid());
        }
        TicketEntity ticket = ticketOptional.get();

        // 2. 判断工单状态
        if (!ticket.getStatus().equals(TicketStatusEnum.RESOLVED.name())) {
            throw new RuntimeException("工单状态为" + ticket.getStatus() + "，不能验证: " + request.getUid());
        }

        // 3. 判断验证人是否为提交人
        if (!ticket.getReporter().getUid().equals(assigneeUid)) {
            throw new RuntimeException("非工单提交人，不能验证: " + request.getUid());
        }

        // 4. 查询任务
        Task task = taskService.createTaskQuery()
            .processInstanceId(ticket.getProcessInstanceId())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_TICKET_UID, request.getUid())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .singleResult();

        if (task == null) {
            throw new RuntimeException("工单任务不存在: " + request.getUid());
        }

        try {
            // 5. 设置验证结果变量
            Map<String, Object> variables = new HashMap<>();
            variables.put("verified", request.getVerified());

            // 6. 添加评论
            String commentType = request.getVerified() ? TicketStatusEnum.VERIFIED_OK.name() : TicketStatusEnum.VERIFIED_FAIL.name();
            String commentMessage = request.getVerified() ? "客户确认工单已解决" : "客户反馈工单未解决";
            Comment comment = taskService.addComment(task.getId(), ticket.getProcessInstanceId(),
                    commentType, commentMessage);
            comment.setUserId(assigneeUid); // 设置评论的userId为当前认领人
            taskService.saveComment(comment);

            // 7. 完成任务
            taskService.complete(task.getId(), variables);

            // 8. 更新工单状态
            if (request.getVerified()) {
                ticket.setStatus(TicketStatusEnum.VERIFIED_OK.name());
                ticket.setVerified(true);
                ticket.setClosedTime(BdDateUtils.now());
            } else {
                ticket.setStatus(TicketStatusEnum.REOPENED.name());
                ticket.setVerified(false);
                // 重置解决时间
                ticket.setResolvedTime(null);
            }
            ticketRestService.save(ticket);

            return TicketConvertUtils.convertToResponse(ticket);

        } catch (Exception e) {
            log.error("工单验证失败: ", e);
            throw new RuntimeException("工单验证失败: " + e.getMessage());
        }
    }

    /**
     * 关闭工单
     * PROCESSING/RESUMED -&gt; CLOSED (关闭)
     */
    @Transactional
    public TicketResponse closeTicket(TicketRequest request) {
        log.info("开始关闭工单: uid={}, status={}, orgUid={}",
                request.getUid(), request.getStatus(), request.getOrgUid());

        // 
        String assigneeUid = request.getAssignee().getUid();
        Assert.notNull(assigneeUid, "处理人uid不能为空");

        // 1. 查询工单
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("工单不存在: " + request.getUid());
        }
        TicketEntity ticket = ticketOptional.get();

        // 2. 判断工单状态 - 修改此处，允许RESUMED状态也可以关闭
        if (!ticket.getStatus().equals(TicketStatusEnum.PROCESSING.name()) && 
            !ticket.getStatus().equals(TicketStatusEnum.RESUMED.name())) {
            throw new RuntimeException("工单状态为" + ticket.getStatus() + "，不能关闭: " + request.getUid());
        }

        // 3. 查询任务
        Task task = taskService.createTaskQuery()
            .processInstanceId(ticket.getProcessInstanceId())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_TICKET_UID, request.getUid())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .singleResult();

        if (task == null) {
            throw new RuntimeException("工单任务不存在: " + request.getUid());
        }

        // 添加评论
        Comment comment = taskService.addComment(task.getId(), ticket.getProcessInstanceId(),
                TicketStatusEnum.CLOSED.name(), "工单已关闭");
        comment.setUserId(assigneeUid); // 设置评论的userId为当前处理人
        taskService.saveComment(comment);

        // 4. 关闭任务
        taskService.complete(task.getId());

        // 5. 更新工单状态
        ticket.setStatus(TicketStatusEnum.CLOSED.name());
        ticket.setClosedTime(BdDateUtils.now()); // 添加关闭时间记录
        ticketRestService.save(ticket);

        return TicketConvertUtils.convertToResponse(ticket);
    }

    /**
     * 取消工单
     * PROCESSING -&gt; CANCELLED (取消)
     */
    @Transactional
    public TicketResponse cancelTicket(TicketRequest request) {
        log.info("开始取消工单: uid={}, status={}, orgUid={}",
                request.getUid(), request.getStatus(), request.getOrgUid());

        // 
        String assigneeUid = request.getAssignee().getUid();
        Assert.notNull(assigneeUid, "处理人uid不能为空");

        // 1. 查询工单
        Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
        if (!ticketOptional.isPresent()) {
            throw new RuntimeException("工单不存在: " + request.getUid());
        }
        TicketEntity ticket = ticketOptional.get();

        // 2. 判断工单状态
        if (!ticket.getStatus().equals(TicketStatusEnum.PROCESSING.name())) {
            throw new RuntimeException("工单状态为" + ticket.getStatus() + "，不能取消: " + request.getUid());
        }

        // 3. 查询任务
        Task task = taskService.createTaskQuery()
            .processInstanceId(ticket.getProcessInstanceId())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_TICKET_UID, request.getUid())
                .processVariableValueEquals(TicketConsts.TICKET_VARIABLE_ORGUID, request.getOrgUid())
                .singleResult();

        if (task == null) {
            throw new RuntimeException("工单任务不存在: " + request.getUid());
        }

        // comment
        addTaskComment(task, ticket, assigneeUid, TicketStatusEnum.CANCELLED.name(), "工单已取消");

        // 4. 终止流程实例（比直接 deleteTask 更一致，避免流程实例悬挂）
        try {
            runtimeService.deleteProcessInstance(ticket.getProcessInstanceId(),
                    StringUtils.hasText(request.getReason()) ? request.getReason() : "cancel ticket");
        } catch (Exception e) {
            log.warn("终止流程实例失败，继续更新工单状态: processInstanceId={}, err={}",
                    ticket.getProcessInstanceId(), e.getMessage());
        }

        // 5. 更新工单状态
        ticket.setStatus(TicketStatusEnum.CANCELLED.name());
        ticketRestService.save(ticket);

        return TicketConvertUtils.convertToResponse(ticket);
    }

    /**
     * 委托工单任务
     */
    @Transactional
    public TicketResponse delegateTicket(TicketRequest request) {
        String operatorUid = request.getAssignee() != null ? request.getAssignee().getUid() : null;
        Assert.hasText(operatorUid, "操作人uid不能为空");
        Assert.hasText(request.getDelegateUid(), "被委托人uid不能为空");

        TicketEntity ticket = getTicketOrThrow(request.getUid());
        Task task = getActiveTaskOrThrow(ticket, request);

        // 基础校验：只有当前任务办理人才能委托（如果任务未分配，则允许委托但建议先转办/认领）
        if (StringUtils.hasText(task.getAssignee()) && !Objects.equals(task.getAssignee(), operatorUid)) {
            throw new RuntimeException("非当前任务办理人，不能委托");
        }

        // Flowable 委托语义：owner=委托人, assignee=被委托人, delegationState=PENDING
        if (!StringUtils.hasText(task.getOwner())) {
            taskService.setOwner(task.getId(), operatorUid);
        }
        taskService.delegateTask(task.getId(), request.getDelegateUid());

        addTaskComment(task, ticket, operatorUid, "DELEGATED",
                "任务被委托给 " + request.getDelegateUid() + (StringUtils.hasText(request.getReason()) ? ("，原因：" + request.getReason()) : ""));

        return TicketConvertUtils.convertToResponse(ticket);
    }

    /**
     * 被委托人处理完成后“解决委托”，任务回到委托人
     */
    @Transactional
    public TicketResponse resolveDelegatedTicket(TicketRequest request) {
        String operatorUid = request.getAssignee() != null ? request.getAssignee().getUid() : null;
        Assert.hasText(operatorUid, "操作人uid不能为空");
        Assert.hasText(request.getTaskId(), "taskId不能为空");

        TicketEntity ticket = getTicketOrThrow(request.getUid());
        Task task = getActiveTaskOrThrow(ticket, request);

        if (StringUtils.hasText(task.getAssignee()) && !Objects.equals(task.getAssignee(), operatorUid)) {
            throw new RuntimeException("非当前任务办理人，不能解决委托");
        }

        taskService.resolveTask(task.getId());

        addTaskComment(task, ticket, operatorUid, "DELEGATION_RESOLVED",
                "委托任务已由 " + operatorUid + " 处理并归还" + (StringUtils.hasText(request.getReason()) ? ("，说明：" + request.getReason()) : ""));

        return TicketConvertUtils.convertToResponse(ticket);
    }

    /**
     * 抄送工单：知会相关人员（不参与流转）
     */
    @Transactional
    public TicketResponse ccTicket(TicketRequest request) {
        String operatorUid = request.getAssignee() != null ? request.getAssignee().getUid() : null;
        Assert.hasText(operatorUid, "操作人uid不能为空");

        Set<String> ccUids = request.getCcUids();
        if (ccUids == null || ccUids.isEmpty()) {
            throw new RuntimeException("ccUids不能为空");
        }

        TicketEntity ticket = getTicketOrThrow(request.getUid());

        // 1) 尽量将抄送人加入工单会话订阅，便于接收通知/查看会话
        if (StringUtils.hasText(ticket.getThreadUid())) {
            Optional<ThreadEntity> threadOptional = threadRestService.findByUid(ticket.getThreadUid());
            if (threadOptional.isPresent()) {
                ThreadEntity thread = threadOptional.get();
                for (String ccUid : ccUids) {
                    if (!StringUtils.hasText(ccUid)) {
                        continue;
                    }
                    Optional<MemberEntity> memberOptional = memberRestService.findByUid(ccUid);
                    if (!memberOptional.isPresent()) {
                        continue;
                    }
                    MemberEntity member = memberOptional.get();
                    UserProtobuf ccProtobuf = UserProtobuf.builder()
                            .uid(member.getUid())
                            .nickname(member.getNickname())
                            .avatar(member.getAvatar())
                            .type(UserTypeEnum.MEMBER.name())
                            .build();
                    String ccJson = ccProtobuf.toJson();
                    if (!thread.getTicketors().contains(ccJson)) {
                        thread.getTicketors().add(ccJson);
                    }
                    // 订阅工单会话 topic，便于收到站内信/IM
                    String userUid = member.getUser() != null ? member.getUser().getUid() : null;
                    if (StringUtils.hasText(userUid)) {
                        topicRestService.create(thread.getTopic(), userUid);
                    }
                }
                threadRestService.save(thread);
            }
        }

        // 2) 记录到流程评论
        try {
            Task task = getActiveTaskOrThrow(ticket, request);
            addTaskComment(task, ticket, operatorUid, "CC",
                    "工单抄送给：" + String.join(",", ccUids) + (StringUtils.hasText(request.getReason()) ? ("，说明：" + request.getReason()) : ""));
        } catch (Exception e) {
            log.debug("抄送记录评论失败（可能无活动任务）: {}", e.getMessage());
        }

        return TicketConvertUtils.convertToResponse(ticket);
    }

    /**
     * 加签：最小实现为给当前任务追加候选人
     */
    @Transactional
    public TicketResponse addSignTicket(TicketRequest request) {
        String operatorUid = request.getAssignee() != null ? request.getAssignee().getUid() : null;
        Assert.hasText(operatorUid, "操作人uid不能为空");

        Set<String> addSignUids = request.getAddSignUids();
        if (addSignUids == null || addSignUids.isEmpty()) {
            throw new RuntimeException("addSignUids不能为空");
        }

        TicketEntity ticket = getTicketOrThrow(request.getUid());
        Task task = getActiveTaskOrThrow(ticket, request);

        for (String uid : addSignUids) {
            if (!StringUtils.hasText(uid)) {
                continue;
            }
            taskService.addCandidateUser(task.getId(), uid);
        }

        addTaskComment(task, ticket, operatorUid, "ADDSIGN",
                "任务加签候选人：" + String.join(",", addSignUids) + (StringUtils.hasText(request.getReason()) ? ("，原因：" + request.getReason()) : ""));

        return TicketConvertUtils.convertToResponse(ticket);
    }

    /**
     * 退回：跳转回指定节点（需要 BPMN 节点 activityId）
     */
    @Transactional
    public TicketResponse rollbackTicket(TicketRequest request) {
        String operatorUid = request.getAssignee() != null ? request.getAssignee().getUid() : null;
        Assert.hasText(operatorUid, "操作人uid不能为空");
        Assert.hasText(request.getRollbackToActivityId(), "rollbackToActivityId不能为空");

        TicketEntity ticket = getTicketOrThrow(request.getUid());
        Task task = getActiveTaskOrThrow(ticket, request);

        String fromActivityId = StringUtils.hasText(request.getRollbackFromActivityId())
                ? request.getRollbackFromActivityId()
                : task.getTaskDefinitionKey();

        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(ticket.getProcessInstanceId())
                .moveActivityIdTo(fromActivityId, request.getRollbackToActivityId())
                .changeState();

        addTaskComment(task, ticket, operatorUid, "ROLLBACK",
                "流程退回：" + fromActivityId + " -> " + request.getRollbackToActivityId()
                        + (StringUtils.hasText(request.getReason()) ? ("，原因：" + request.getReason()) : ""));

        return TicketConvertUtils.convertToResponse(ticket);
    }

    /**
     * 撤销：终止流程实例（通常用于发起人撤回/管理员撤销）
     */
    @Transactional
    public TicketResponse revokeTicket(TicketRequest request) {
        String operatorUid = request.getAssignee() != null ? request.getAssignee().getUid() : null;
        Assert.hasText(operatorUid, "操作人uid不能为空");

        TicketEntity ticket = getTicketOrThrow(request.getUid());

        // 尽量先给当前活动任务写评论（删除实例后就无法再写 task comment）
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(ticket.getProcessInstanceId())
                .active()
                .list();
        if (tasks != null) {
            for (Task t : tasks) {
                try {
                    addTaskComment(t, ticket, operatorUid, "REVOKED",
                            "流程已撤销" + (StringUtils.hasText(request.getReason()) ? ("，原因：" + request.getReason()) : ""));
                } catch (Exception ex) {
                    log.debug("写撤销评论失败: {}", ex.getMessage());
                }
            }
        }

        runtimeService.deleteProcessInstance(ticket.getProcessInstanceId(),
                StringUtils.hasText(request.getReason()) ? request.getReason() : "revoke ticket");

        // 工单侧统一落到 CANCELLED（前端已有该状态）
        ticket.setStatus(TicketStatusEnum.CANCELLED.name());
        ticketRestService.save(ticket);

        return TicketConvertUtils.convertToResponse(ticket);
    }

    /**
     * 查询某个工单实例的处理历史
     */
    public List<TicketHistoryProcessResponse> queryTicketProcessHistory(TicketRequest request) {
        // processInstanceId不能为空
        if (request.getProcessInstanceId() == null) {
            if (StringUtils.hasText(request.getUid())) {
                // 根据uid查询processInstanceId
                Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
                if (ticketOptional.isPresent()) {
                    request.setProcessInstanceId(ticketOptional.get().getProcessInstanceId());
                }
            } else {
                throw new RuntimeException("processInstanceId不能为空");
            }
        }

        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(request.getProcessInstanceId())
                .includeProcessVariables() // 重要：包含流程变量
                .orderByProcessInstanceEndTime().asc()
                .list();

        List<TicketHistoryProcessResponse> responses = historicProcessInstances.stream()
                .map(historicProcessInstance -> {
                    Map<String, Object> variables = historicProcessInstance.getProcessVariables();

                    // 从流程变量中获取 assignee
                    UserProtobuf assignee = null;
                    Object assigneeObj = variables.get(TicketConsts.TICKET_VARIABLE_ASSIGNEE);
                    if (assigneeObj != null) {
                        if (assigneeObj instanceof UserProtobuf) {
                            // 如果已经是 UserProtobuf 对象，直接使用
                            assignee = (UserProtobuf) assigneeObj;
                        } else if (assigneeObj instanceof String) {
                            // 如果是 JSON 字符串，需要解析
                            try {
                                assignee = JSON.parseObject((String) assigneeObj, UserProtobuf.class);
                            } catch (Exception e) {
                                log.warn("Failed to parse assignee JSON: {}", assigneeObj, e);
                            }
                        } else {
                            log.warn("Unexpected assignee type: {}", assigneeObj.getClass());
                        }
                    }

                    return TicketHistoryProcessResponse.builder()
                            .processInstanceId(historicProcessInstance.getId())
                            .processDefinitionId(historicProcessInstance.getProcessDefinitionId())
                            .processDefinitionName(historicProcessInstance.getProcessDefinitionName())
                            .processDefinitionKey(historicProcessInstance.getProcessDefinitionKey())
                            .processDefinitionVersion(historicProcessInstance.getProcessDefinitionVersion())
                            .businessKey(historicProcessInstance.getBusinessKey())
                            .startTime(historicProcessInstance.getStartTime())
                            .endTime(historicProcessInstance.getEndTime())
                            .durationInMillis(historicProcessInstance.getDurationInMillis())
                            .deleteReason(historicProcessInstance.getDeleteReason())
                            .tenantId(historicProcessInstance.getTenantId())
                            .name(historicProcessInstance.getName())
                            // 从流程变量中获取状态
                            .assignee(assignee)
                            .description((String) variables.get(TicketConsts.TICKET_VARIABLE_DESCRIPTION))
                            .startUserId((String) variables.get(TicketConsts.TICKET_VARIABLE_START_USER_ID))
                            .status((String) variables.get(TicketConsts.TICKET_VARIABLE_STATUS))
                            .priority((String) variables.get(TicketConsts.TICKET_VARIABLE_PRIORITY))
                            .categoryUid((String) variables.get(TicketConsts.TICKET_VARIABLE_CATEGORY_UID))
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();

        return responses;
    }

    /**
     * 查询某个工单的流程实例历史
     */
    public List<TicketHistoryTaskResponse> queryTicketTaskHistory(TicketRequest request) {
        // processInstanceId不能为空
        if (request.getProcessInstanceId() == null) {
            if (StringUtils.hasText(request.getUid())) {
                // 根据uid查询processInstanceId
                Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
                if (ticketOptional.isPresent()) {
                    request.setProcessInstanceId(ticketOptional.get().getProcessInstanceId());
                }
            } else {
                throw new RuntimeException("processInstanceId不能为空");
            }
        }

        List<HistoricTaskInstance> historicTasks = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(request.getProcessInstanceId())
                .includeTaskLocalVariables() // 包含任务局部变量
                .includeProcessVariables() // 包含流程变量
                .orderByHistoricTaskInstanceStartTime().asc()
                .list();

        List<TicketHistoryTaskResponse> responses = historicTasks.stream()
                .map(historicTask -> {
                    return TicketHistoryTaskResponse.builder()
                            .taskId(historicTask.getId())
                            .taskName(historicTask.getName())
                            .taskDefinitionKey(historicTask.getTaskDefinitionKey())
                            .taskDefinitionId(historicTask.getTaskDefinitionId())
                            .description(historicTask.getDescription())
                            .category(historicTask.getCategory())
                            .formKey(historicTask.getFormKey())
                            .processInstanceId(historicTask.getProcessInstanceId())
                            .processDefinitionId(historicTask.getProcessDefinitionId())
                            .executionId(historicTask.getExecutionId())

                            .assignee(historicTask.getAssignee())
                            .owner(historicTask.getOwner())
                            // 注意：历史任务可能无法直接获取候选人/组信息

                            .priority(historicTask.getPriority())
                            .createTime(historicTask.getCreateTime())
                            .dueDate(historicTask.getDueDate())
                            .claimTime(historicTask.getClaimTime())
                            .endTime(historicTask.getEndTime())
                            .durationInMillis(historicTask.getDurationInMillis())
                            .deleteReason(historicTask.getDeleteReason())
                            .tenantId(historicTask.getTenantId())

                            .taskLocalVariables(historicTask.getTaskLocalVariables())
                            .processVariables(historicTask.getProcessVariables())
                            .build();
                })
                .toList();
        return responses;
    }

    /**
     * 查询工单的完整活动历史
     */
    public List<TicketHistoryActivityResponse> queryTicketActivityHistory(TicketRequest request) {
        // processInstanceId不能为空
        if (request.getProcessInstanceId() == null) {
            if (StringUtils.hasText(request.getUid())) {
                Optional<TicketEntity> ticketOptional = ticketRestService.findByUid(request.getUid());
                if (ticketOptional.isPresent()) {
                    request.setProcessInstanceId(ticketOptional.get().getProcessInstanceId());
                }
            } else {
                throw new RuntimeException("processInstanceId不能为空");
            }
        }

        // 获取活动历史，过滤掉 sequenceFlow
        List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(request.getProcessInstanceId())
                .orderByHistoricActivityInstanceStartTime().asc()
                .list()
                .stream()
                .filter(activity -> !"sequenceFlow".equals(activity.getActivityType()))
                .collect(Collectors.toList());

        // 获取任务评论
        List<Comment> comments = taskService.getProcessInstanceComments(request.getProcessInstanceId());

        // 合并活动和评论信息
        List<TicketHistoryActivityResponse> responses = new ArrayList<>();

        // 添加活动历史，只保留关键信息
        responses.addAll(activities.stream()
                .map(activity -> TicketHistoryActivityResponse.builder()
                        .id(activity.getId())
                        .activityName(activity.getActivityName())
                        .activityType(activity.getActivityType())
                        .assignee(activity.getAssignee())
                        .startTime(activity.getStartTime())
                        .endTime(activity.getEndTime())
                        .durationInMillis(activity.getDurationInMillis())
                        .build())
                .collect(Collectors.toList()));

        // 添加评论历史
        responses.addAll(comments.stream()
                .map(comment -> TicketHistoryActivityResponse.builder()
                        .id(comment.getId())
                        .activityType("comment")
                        .activityName(comment.getType())
                        .description(comment.getFullMessage())
                        .startTime(comment.getTime())
                        .assignee(comment.getUserId())
                        .build())
                .collect(Collectors.toList()));

        // 按时间排序
        return responses.stream()
                .sorted(Comparator.comparing(TicketHistoryActivityResponse::getStartTime))
                .collect(Collectors.toList());
    }


}
