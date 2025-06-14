package com.bytedesk.freeswitch.acd.call;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * FreeSwitch呼叫数据访问接口
 */
@Repository
public interface FreeSwitchCallRepository extends JpaRepository<FreeSwitchCallEntity, Long> {
    
    /**
     * 根据呼叫UUID查找呼叫
     */
    Optional<FreeSwitchCallEntity> findByUuid(String uuid);
    
    /**
     * 根据呼叫状态查找呼叫列表
     */
    List<FreeSwitchCallEntity> findByStatus(FreeSwitchCallEntity.CallStatus status);
    
    /**
     * 根据呼叫类型查找呼叫列表
     */
    List<FreeSwitchCallEntity> findByType(FreeSwitchCallEntity.CallType type);
    
    /**
     * 根据坐席ID查找呼叫列表
     */
    List<FreeSwitchCallEntity> findByAgentId(String agentId);
    
    /**
     * 根据当前队列查找呼叫列表
     */
    List<FreeSwitchCallEntity> findByCurrentQueue(String queueName);
    
    /**
     * 根据时间范围查找呼叫列表
     */
    List<FreeSwitchCallEntity> findByStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据主叫号码查找呼叫列表
     */
    List<FreeSwitchCallEntity> findByCallerId(String callerId);
    
    /**
     * 根据被叫号码查找呼叫列表
     */
    List<FreeSwitchCallEntity> findByDestination(String destination);
    
    /**
     * 分页查询呼叫列表
     */
    Page<FreeSwitchCallEntity> findAll(Pageable pageable);
    
    /**
     * 统计指定时间范围内的呼叫总数
     */
    @Query("SELECT COUNT(c) FROM FreeSwitchCallEntity c WHERE c.startTime BETWEEN ?1 AND ?2")
    long countCallsInTimeRange(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 统计指定时间范围内的已接听呼叫数
     */
    @Query("SELECT COUNT(c) FROM FreeSwitchCallEntity c WHERE c.startTime BETWEEN ?1 AND ?2 AND c.status = 'ANSWERED'")
    long countAnsweredCallsInTimeRange(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 计算指定时间范围内的通话总时长
     */
    @Query("SELECT COALESCE(SUM(c.duration), 0) FROM FreeSwitchCallEntity c WHERE c.startTime BETWEEN ?1 AND ?2 AND c.status = 'ANSWERED'")
    long sumDurationInTimeRange(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 计算指定时间范围内的等待总时长
     */
    @Query("SELECT COALESCE(SUM(c.waitTime), 0) FROM FreeSwitchCallEntity c WHERE c.startTime BETWEEN ?1 AND ?2")
    long sumWaitTimeInTimeRange(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 删除指定时间之前的呼叫记录
     */
    void deleteByStartTimeBefore(LocalDateTime time);
} 