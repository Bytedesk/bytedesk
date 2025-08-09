package com.bytedesk.call.queue;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.call.call.CallCallEntity;
import com.bytedesk.call.call.CallCallEntity.CallStatus;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Call队列服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CallQueueService {
    
    private final CallQueueRepository queueRepository;
    
    /**
     * 创建队列
     */
    @Transactional
    public CallQueueEntity createQueue(CallQueueEntity queue) {
        if (queueRepository.existsByName(queue.getName())) {
            throw new RuntimeException("队列名称已存在: " + queue.getName());
        }
        
        queue.setStatus(CallQueueEntity.QueueStatus.ACTIVE);
        queue.setCreatedAt(BdDateUtils.now());
        queue.setUpdatedAt(BdDateUtils.now());
        
        return queueRepository.save(queue);
    }
    
    /**
     * 更新队列信息
     */
    @Transactional
    public CallQueueEntity updateQueue(CallQueueEntity queue) {
        CallQueueEntity existingQueue = queueRepository.findByName(queue.getName())
            .orElseThrow(() -> new RuntimeException("队列不存在: " + queue.getName()));
            
        existingQueue.setType(queue.getType());
        existingQueue.setSkills(queue.getSkills());
        existingQueue.setMaxWaitTime(queue.getMaxWaitTime());
        existingQueue.setMaxLength(queue.getMaxLength());
        existingQueue.setWeight(queue.getWeight());
        existingQueue.setNotes(queue.getNotes());
        existingQueue.setUpdatedAt(BdDateUtils.now());
        
        return queueRepository.save(existingQueue);
    }
    
    /**
     * 删除队列
     */
    @Transactional
    public void deleteQueue(String queueName) {
        queueRepository.deleteByName(queueName);
    }
    
    /**
     * 获取队列信息
     */
    public Optional<CallQueueEntity> getQueue(String queueName) {
        return queueRepository.findByName(queueName);
    }
    
    /**
     * 获取所有队列
     */
    public List<CallQueueEntity> getAllQueues() {
        return queueRepository.findAll();
    }
    
    /**
     * 根据类型获取队列列表
     */
    public List<CallQueueEntity> getQueuesByType(CallQueueEntity.QueueType type) {
        return queueRepository.findByType(type);
    }
    
    /**
     * 更新队列状态
     */
    @Transactional
    public CallQueueEntity updateQueueStatus(String queueName, CallQueueEntity.QueueStatus status) {
        CallQueueEntity queue = queueRepository.findByName(queueName)
            .orElseThrow(() -> new RuntimeException("队列不存在: " + queueName));
            
        queue.setStatus(status);
        queue.setUpdatedAt(BdDateUtils.now());
        
        return queueRepository.save(queue);
    }
    
    /**
     * 将呼叫加入队列
     */
    @Transactional
    public void enqueueCall(CallCallEntity call, String queueName) {
        CallQueueEntity queue = queueRepository.findByName(queueName)
            .orElseThrow(() -> new RuntimeException("队列不存在: " + queueName));
            
        if (queue.getStatus() != CallQueueEntity.QueueStatus.ACTIVE) {
            throw new RuntimeException("队列未激活: " + queueName);
        }
        
        // 检查队列是否已满
        if (queue.getMaxLength() != null) {
            // TODO: 实现队列长度检查
        }
        
        // call.setCurrentQueue(queueName);
        call.setStatus(CallStatus.QUEUED);
        // call.setQueueTime(BdDateUtils.now());
    }
    
    /**
     * 将呼叫从队列中移除
     */
    @Transactional
    public void dequeueCall(CallCallEntity call) {
        // call.setCurrentQueue(null);
        // call.setQueueTime(null);
    }
    
    /**
     * 检查呼叫是否匹配队列技能要求
     */
    public boolean matchesQueueSkills(CallCallEntity call, CallQueueEntity queue) {
        // TODO: 实现技能匹配逻辑
        return true;
    }
    
    /**
     * 获取队列统计信息
     */
    public CallQueueStatistics getQueueStatistics(String queueName) {
        // CallQueueEntity queue = queueRepository.findByName(queueName)
        //     .orElseThrow(() -> new RuntimeException("队列不存在: " + queueName));
            
        // TODO: 实现队列统计信息计算
        return CallQueueStatistics.builder()
            .queueName(queueName)
            .totalCalls(0)
            .waitingCalls(0)
            .ringingCalls(0)
            .answeredCalls(0)
            .avgWaitTime(0)
            .build();
    }
} 