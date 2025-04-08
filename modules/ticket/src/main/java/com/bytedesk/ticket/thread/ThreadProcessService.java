/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-04 13:26:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-08 09:44:45
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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.flowable.engine.HistoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
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

        // 获取活动历史，过滤掉 sequenceFlow 和其他不需要显示的活动
        List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(request.getProcessInstanceId())
                .orderByHistoricActivityInstanceStartTime().asc()
                .list()
                .stream()
                .filter(activity -> {
                    // 过滤掉不需要显示的活动类型
                    if (ThreadConsts.ACTIVITY_TYPE_SEQUENCE_FLOW.equals(activity.getActivityType())) {
                        return false;
                    }
                    
                    // 特定活动始终显示，不过滤
                    if (ThreadConsts.ACTIVITY_ID_AGENTS_OFFLINE_SERVICE.equals(activity.getActivityId()) || 
                        ThreadConsts.ACTIVITY_ID_END.equals(activity.getActivityId())) {
                        return true;
                    }
                    
                    // 过滤掉没有名称的活动
                    if (activity.getActivityName() == null || activity.getActivityName().trim().isEmpty()) {
                        return false;
                    }
                    
                    // 过滤掉 transferToHumanTask 活动，除非它确实被执行了(有结束时间)
                    if (ThreadConsts.ACTIVITY_ID_TRANSFER_TO_HUMAN_TASK.equals(activity.getActivityId()) && activity.getEndTime() == null) {
                        return false;
                    }
                    
                    // 过滤掉没有实际执行的活动（开始时间为空）
                    if (activity.getStartTime() == null) {
                        return false;
                    }
                    
                    return true;
                })
                .collect(Collectors.toList());

        // 获取任务评论
        List<Comment> comments = taskService.getProcessInstanceComments(request.getProcessInstanceId());

        // 获取流程变量
        List<HistoricVariableInstance> variables = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(request.getProcessInstanceId())
                .list();
        
        // 将变量转换为Map，便于查找
        Map<String, Object> variableMap = variables.stream()
                .collect(Collectors.toMap(
                    v -> v.getVariableName(),
                    v -> v.getValue(),
                    (v1, v2) -> v2  // 如果有重复键，保留最后一个值
                ));

        // 合并活动和评论信息
        List<ThreadHistoryActivityResponse> responses = new ArrayList<>();

        // 添加活动历史，为网关添加决策结果
        responses.addAll(activities.stream()
                .map(activity -> {
                    ThreadHistoryActivityResponse response = ThreadHistoryActivityResponse.builder()
                            .id(activity.getId())
                            .activityName(activity.getActivityName())
                            .activityType(activity.getActivityType())
                            .assignee(activity.getAssignee())
                            .startTime(activity.getStartTime())
                            .endTime(activity.getEndTime())
                            .durationInMillis(activity.getDurationInMillis())
                            .build();
                    
                    // 如果是网关，添加决策结果
                    if (ThreadConsts.ACTIVITY_TYPE_EXCLUSIVE_GATEWAY.equals(activity.getActivityType())) {
                        // 根据不同网关ID获取对应的变量决策结果
                        String gatewayId = activity.getActivityId();
                        String decisionResult = getGatewayDecisionResult(gatewayId, variableMap);
                        if (decisionResult != null) {
                            response.setDescription(decisionResult);
                            response.setActivityName(activity.getActivityName() + " " + decisionResult);
                        }
                    }
                    
                    return response;
                })
                .collect(Collectors.toList()));

        // 添加评论历史
        responses.addAll(comments.stream()
                .map(comment -> ThreadHistoryActivityResponse.builder()
                        .id(comment.getId())
                        .activityType(ThreadConsts.ACTIVITY_TYPE_COMMENT)
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

    /**
     * 根据网关ID和流程变量确定网关决策结果
     */
    private String getGatewayDecisionResult(String gatewayId, Map<String, Object> variableMap) {
        switch (gatewayId) {
            case ThreadConsts.THREAD_GATEWAY_IS_ROBOT_ENABLED:
                Boolean robotEnabled = (Boolean) variableMap.get(ThreadConsts.THREAD_VARIABLE_ROBOT_ENABLED);
                return robotEnabled != null && robotEnabled ? "结果: 是" : "结果: 否";
            case ThreadConsts.THREAD_GATEWAY_TRANSFER_TO_HUMAN:
                Boolean needHumanService = (Boolean) variableMap.get(ThreadConsts.THREAD_VARIABLE_NEED_HUMAN_SERVICE);
                return needHumanService != null && needHumanService ? "结果: 是" : "结果: 否";
            case ThreadConsts.THREAD_GATEWAY_IS_AGENTS_OFFLINE:
                Boolean agentsOffline = (Boolean) variableMap.get(ThreadConsts.THREAD_VARIABLE_AGENTS_OFFLINE);
                return agentsOffline != null && agentsOffline ? "结果: 是" : "结果: 否";
            case ThreadConsts.THREAD_GATEWAY_IS_AGENTS_BUSY:
                Boolean agentsBusy = (Boolean) variableMap.get(ThreadConsts.THREAD_VARIABLE_AGENTS_BUSY);
                return agentsBusy != null && agentsBusy ? "结果: 是" : "结果: 否";
            default:
                return null;
        }
    }
}
