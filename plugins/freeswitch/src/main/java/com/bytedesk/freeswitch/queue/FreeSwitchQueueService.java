package com.bytedesk.freeswitch.queue;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.freeswitch.call.FreeSwitchCallEntity;
import com.bytedesk.freeswitch.call.FreeSwitchCallEntity.CallStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * FreeSwitch队列服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FreeSwitchQueueService {
    
    private final FreeSwitchQueueRepository queueRepository;
    
    /**
     * 创建队列
     */
    @Transactional
    public FreeSwitchQueueEntity createQueue(FreeSwitchQueueEntity queue) {
        if (queueRepository.existsByName(queue.getName())) {
            throw new RuntimeException("队列名称已存在: " + queue.getName());
        }
        
        queue.setStatus(FreeSwitchQueueEntity.QueueStatus.ACTIVE);
        queue.setCreatedAt(ZonedDateTime.now());
        queue.setUpdatedAt(ZonedDateTime.now());
        
        return queueRepository.save(queue);
    }
    
    /**
     * 更新队列信息
     */
    @Transactional
    public FreeSwitchQueueEntity updateQueue(FreeSwitchQueueEntity queue) {
        FreeSwitchQueueEntity existingQueue = queueRepository.findByName(queue.getName())
            .orElseThrow(() -> new RuntimeException("队列不存在: " + queue.getName()));
            
        existingQueue.setType(queue.getType());
        existingQueue.setSkills(queue.getSkills());
        existingQueue.setMaxWaitTime(queue.getMaxWaitTime());
        existingQueue.setMaxLength(queue.getMaxLength());
        existingQueue.setWeight(queue.getWeight());
        existingQueue.setNotes(queue.getNotes());
        existingQueue.setUpdatedAt(ZonedDateTime.now());
        
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
    public Optional<FreeSwitchQueueEntity> getQueue(String queueName) {
        return queueRepository.findByName(queueName);
    }
    
    /**
     * 获取所有队列
     */
    public List<FreeSwitchQueueEntity> getAllQueues() {
        return queueRepository.findAll();
    }
    
    /**
     * 根据类型获取队列列表
     */
    public List<FreeSwitchQueueEntity> getQueuesByType(FreeSwitchQueueEntity.QueueType type) {
        return queueRepository.findByType(type);
    }
    
    /**
     * 更新队列状态
     */
    @Transactional
    public FreeSwitchQueueEntity updateQueueStatus(String queueName, FreeSwitchQueueEntity.QueueStatus status) {
        FreeSwitchQueueEntity queue = queueRepository.findByName(queueName)
            .orElseThrow(() -> new RuntimeException("队列不存在: " + queueName));
            
        queue.setStatus(status);
        queue.setUpdatedAt(ZonedDateTime.now());
        
        return queueRepository.save(queue);
    }
    
    /**
     * 将呼叫加入队列
     */
    @Transactional
    public void enqueueCall(FreeSwitchCallEntity call, String queueName) {
        FreeSwitchQueueEntity queue = queueRepository.findByName(queueName)
            .orElseThrow(() -> new RuntimeException("队列不存在: " + queueName));
            
        if (queue.getStatus() != FreeSwitchQueueEntity.QueueStatus.ACTIVE) {
            throw new RuntimeException("队列未激活: " + queueName);
        }
        
        // 检查队列是否已满
        if (queue.getMaxLength() != null) {
            // TODO: 实现队列长度检查
        }
        
        // call.setCurrentQueue(queueName);
        call.setStatus(CallStatus.QUEUED);
        // call.setQueueTime(ZonedDateTime.now());
    }
    
    /**
     * 将呼叫从队列中移除
     */
    @Transactional
    public void dequeueCall(FreeSwitchCallEntity call) {
        // call.setCurrentQueue(null);
        // call.setQueueTime(null);
    }
    
    /**
     * 检查呼叫是否匹配队列技能要求
     */
    public boolean matchesQueueSkills(FreeSwitchCallEntity call, FreeSwitchQueueEntity queue) {
        // TODO: 实现技能匹配逻辑
        return true;
    }
    
    /**
     * 获取队列统计信息
     */
    public FreeSwitchQueueStatistics getQueueStatistics(String queueName) {
        // FreeSwitchQueueEntity queue = queueRepository.findByName(queueName)
        //     .orElseThrow(() -> new RuntimeException("队列不存在: " + queueName));
            
        // TODO: 实现队列统计信息计算
        return FreeSwitchQueueStatistics.builder()
            .queueName(queueName)
            .totalCalls(0)
            .waitingCalls(0)
            .ringingCalls(0)
            .answeredCalls(0)
            .avgWaitTime(0)
            .build();
    }
} 