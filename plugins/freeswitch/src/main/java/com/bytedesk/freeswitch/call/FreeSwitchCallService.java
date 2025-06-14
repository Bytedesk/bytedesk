package com.bytedesk.freeswitch.call;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.freeswitch.queue.FreeSwitchQueueService;
import com.bytedesk.freeswitch.statistic.CallStatistics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * FreeSwitch呼叫服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FreeSwitchCallService {
    
    private final FreeSwitchCallRepository callRepository;
    // private final FreeSwitchAgentService agentService;
    private final FreeSwitchQueueService queueService;
    
    /**
     * 创建呼叫记录
     */
    @Transactional
    public FreeSwitchCallEntity createCall(FreeSwitchCallEntity call) {
        // call.setStatus(FreeSwitchCallEntity.CallStatus.INIT);
        call.setStartTime(LocalDateTime.now());
        call.setCreatedAt(LocalDateTime.now());
        call.setUpdatedAt(LocalDateTime.now());
        
        return callRepository.save(call);
    }
    
    /**
     * 更新呼叫状态
     */
    @Transactional
    public FreeSwitchCallEntity updateCallStatus(String uuid, FreeSwitchCallEntity.CallStatus status) {
        FreeSwitchCallEntity call = callRepository.findByUuid(uuid)
            .orElseThrow(() -> new RuntimeException("呼叫不存在: " + uuid));
            
        call.setStatus(status);
        call.setUpdatedAt(LocalDateTime.now());
        
        // 如果呼叫结束，更新结束时间和通话时长
        if (status == FreeSwitchCallEntity.CallStatus.COMPLETED 
            || status == FreeSwitchCallEntity.CallStatus.FAILED 
            || status == FreeSwitchCallEntity.CallStatus.ABANDONED) {
            call.setEndTime(LocalDateTime.now());
            if (call.getStartTime() != null) {
                call.setDuration((int) ChronoUnit.SECONDS.between(call.getStartTime(), call.getEndTime()));
            }
        }
        
        return callRepository.save(call);
    }
    
    /**
     * 分配坐席
     */
    @Transactional
    public FreeSwitchCallEntity assignAgent(String uuid, String agentId) {
        FreeSwitchCallEntity call = callRepository.findByUuid(uuid)
            .orElseThrow(() -> new RuntimeException("呼叫不存在: " + uuid));
            
        // FreeSwitchAgentEntity agent = agentService.getAgent(agentId)
        //     .orElseThrow(() -> new RuntimeException("坐席不存在: " + agentId));
            
        // call.setAgentId(agentId);
        call.setStatus(FreeSwitchCallEntity.CallStatus.RINGING);
        call.setUpdatedAt(LocalDateTime.now());
        
        return callRepository.save(call);
    }
    
    /**
     * 将呼叫加入队列
     */
    @Transactional
    public FreeSwitchCallEntity enqueueCall(String uuid, String queueName) {
        FreeSwitchCallEntity call = callRepository.findByUuid(uuid)
            .orElseThrow(() -> new RuntimeException("呼叫不存在: " + uuid));
            
        queueService.enqueueCall(call, queueName);
        call.setUpdatedAt(LocalDateTime.now());
        
        return callRepository.save(call);
    }
    
    /**
     * 将呼叫从队列中移除
     */
    @Transactional
    public FreeSwitchCallEntity dequeueCall(String uuid) {
        FreeSwitchCallEntity call = callRepository.findByUuid(uuid)
            .orElseThrow(() -> new RuntimeException("呼叫不存在: " + uuid));
            
        queueService.dequeueCall(call);
        call.setUpdatedAt(LocalDateTime.now());
        
        return callRepository.save(call);
    }
    
    /**
     * 获取呼叫信息
     */
    public Optional<FreeSwitchCallEntity> getCall(String uuid) {
        return callRepository.findByUuid(uuid);
    }
    
    /**
     * 获取所有呼叫
     */
    public List<FreeSwitchCallEntity> getAllCalls() {
        return callRepository.findAll();
    }
    
    /**
     * 分页查询呼叫列表
     */
    public Page<FreeSwitchCallEntity> getCalls(Pageable pageable) {
        return callRepository.findAll(pageable);
    }
    
    /**
     * 根据状态查询呼叫列表
     */
    public List<FreeSwitchCallEntity> getCallsByStatus(FreeSwitchCallEntity.CallStatus status) {
        return callRepository.findByStatus(status);
    }
    
    /**
     * 根据类型查询呼叫列表
     */
    public List<FreeSwitchCallEntity> getCallsByType(FreeSwitchCallEntity.CallType type) {
        return callRepository.findByType(type);
    }
    
    /**
     * 根据坐席ID查询呼叫列表
     */
    public List<FreeSwitchCallEntity> getCallsByAgent(String agentId) {
        return callRepository.findByAgentId(agentId);
    }
    
    /**
     * 根据队列名称查询呼叫列表
     */
    public List<FreeSwitchCallEntity> getCallsByQueue(String queueName) {
        return callRepository.findByCurrentQueue(queueName);
    }
    
    /**
     * 根据时间范围查询呼叫列表
     */
    public List<FreeSwitchCallEntity> getCallsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return callRepository.findByStartTimeBetween(startTime, endTime);
    }
    
    /**
     * 获取呼叫统计信息
     */
    public CallStatistics getCallStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        // long totalCalls = callRepository.countCallsInTimeRange(startTime, endTime);
        // long answeredCalls = callRepository.countAnsweredCallsInTimeRange(startTime, endTime);
        // long totalDuration = callRepository.sumDurationInTimeRange(startTime, endTime);
        // long totalWaitTime = callRepository.sumWaitTimeInTimeRange(startTime, endTime);
        
        return CallStatistics.builder()
            // .totalCalls(totalCalls)
            // .answeredCalls(answeredCalls)
            // .totalDuration(totalDuration)
            // .totalWaitTime(totalWaitTime)
            // .avgDuration(totalCalls > 0 ? (double) totalDuration / totalCalls : 0)
            // .avgWaitTime(totalCalls > 0 ? (double) totalWaitTime / totalCalls : 0)
            // .answerRate(totalCalls > 0 ? (double) answeredCalls / totalCalls * 100 : 0)
            .build();
    }
    
    /**
     * 清理历史呼叫记录
     */
    @Transactional
    public void cleanupOldCalls(LocalDateTime before) {
        callRepository.deleteByStartTimeBefore(before);
    }
} 