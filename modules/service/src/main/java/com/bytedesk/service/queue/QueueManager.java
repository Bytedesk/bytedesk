/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-19 17:08:49
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-17 11:07:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue;

import java.util.Map;
import java.util.Queue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class QueueManager {

    private final QueueService queueService;

    // 使用 ConcurrentHashMap 以支持并发访问
    private Map<String, Queue<String>> queues = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // TODO: 初始化队列，从数据库中加载队列信息
    }

    public void addThreadToQueue(String queueId, String threadTopic) {
        // 确保队列存在，如果不存在则创建一个新队列
        queues.putIfAbsent(queueId, new LinkedList<>());
        // 将线程主题添加到指定队列
        queues.get(queueId).add(threadTopic);
        // 更新数据库中的排队人数和通知访客端
        updateQueueCountAndNotify(queueId);
    }

    public String getThreadFromQueue(String queueId) {
        // 从指定队列中移除并返回排在第一个的元素
        Queue<String> queue = queues.get(queueId);
        if (queue != null && !queue.isEmpty()) {
            String threadTopic = queue.poll();
            // 更新数据库中的排队人数和通知访客端
            updateQueueCountAndNotify(queueId);
            return threadTopic;
        }
        return null; // 或者你可以抛出异常，表示队列为空
    }

    // 获取threadTopic在队列queueId中的位置
    // public int getThreadTopicPositionInQueue(String queueId, String threadTopic) {
    //     // 获取threadTopic在队列queueId中的位置
    //     Queue<String> queue = (Queue<String>) queues.get(queueId);
    //     if (queue != null) {
    //         // 转换Queue为List以使用indexOf方法，但更有效的方法是直接遍历Queue
    //         List<String> list = new ArrayList<>(queue);
    //         return list.indexOf(threadTopic);
    //     }
    //     return -1; // 或者你可以抛出异常，表示队列为空
    // }

    // 获取队列中所有线程主题及其索引的映射
    public Map<String, Integer> getThreadTopicsWithPositions(String queueId) {
        Map<String, Integer> topicPositions = new HashMap<>();
        Queue<String> queue = queues.get(queueId);
        if (queue != null) {
            int position = 0;
            for (String threadTopic : queue) {
                topicPositions.put(threadTopic, position++);
            }
        }
        return topicPositions;
    }

    private void updateQueueCountAndNotify(String queueId) {
        int queueCount = 0;
        Queue<String> queue = queues.get(queueId);
        if (queue != null) {
            queueCount = queue.size();
            // 获取所有线程主题及其位置
            Map<String, Integer> topicPositions = getThreadTopicsWithPositions(queueId);
            // 使用获取到的topicPositions来通知访客端每个topic在队列中的位置
            // 例如，你可以遍历topicPositions并通过WebSocket发送消息给相应的访客端
            notifyVisitorsWithPositions(queueId, queueCount, topicPositions);
        }
        // 更新数据库中的排队人数（可能需要扩展以支持每个队列的计数）
        updateDatabaseQueueCount(queueId, queue);
    }

    private void updateDatabaseQueueCount(String queueId, Queue<String> queue) {
        // 在这里执行数据库更新操作，包括队列ID和计数
        List<String> threadTopics = new ArrayList<>(queue);
        QueueRequest queueRequest = QueueRequest.builder()
                // .uid(queueId)
                .threadTopics(threadTopics)
            .build();
        queueRequest.setUid(queueId);
        queueService.update(queueRequest);
    }

    // 新增方法用于通知访客端，包含队列ID、计数信息和每个topic的位置
    private void notifyVisitorsWithPositions(String queueId, int count, Map<String, Integer> topicPositions) {
        // TODO: 实现通知逻辑，例如使用WebSocket发送消息给访客端
        // 消息可以包含队列ID、总计数以及每个线程主题的位置信息（topicPositions）
    }

}