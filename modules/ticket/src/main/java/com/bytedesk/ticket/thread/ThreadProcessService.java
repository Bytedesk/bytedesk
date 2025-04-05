/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-04 13:26:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-05 09:51:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.thread;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.flowable.engine.HistoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.task.Comment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRequest;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.ticket.thread.dto.ThreadHistoryActivityResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThreadProcessService {

    // private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;
    private final ThreadRestService threadRestService;

    

    /**
     * 查询会话的完整活动历史
     */
    public List<ThreadHistoryActivityResponse> queryThreadActivityHistory(ThreadRequest request) {
        // processInstanceId不能为空
        if (request.getProcessInstanceId() == null) {
            if (StringUtils.hasText(request.getUid())) {
                Optional<ThreadEntity> threadOptional = threadRestService.findByUid(request.getUid());
                if (threadOptional.isPresent()) {
                    request.setProcessInstanceId(threadOptional.get().getProcessInstanceId());
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
        List<ThreadHistoryActivityResponse> responses = new ArrayList<>();

        // 添加活动历史，只保留关键信息
        responses.addAll(activities.stream()
                .map(activity -> ThreadHistoryActivityResponse.builder()
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
                .map(comment -> ThreadHistoryActivityResponse.builder()
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
                .sorted(Comparator.comparing(ThreadHistoryActivityResponse::getStartTime))
                .collect(Collectors.toList());
    }

    
}
