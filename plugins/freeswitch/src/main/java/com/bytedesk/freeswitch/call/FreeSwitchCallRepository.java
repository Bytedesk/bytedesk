/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-14 11:27:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-14 12:23:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.call;

import java.time.ZonedDateTime;
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
    Optional<FreeSwitchCallEntity> findByCallUuid(String callUuid);
    
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
    List<FreeSwitchCallEntity> findByAgentId(Long agentId);
    
    /**
     * 根据队列ID查找呼叫列表
     */
    List<FreeSwitchCallEntity> findByQueueId(Long queueId);
    
    /**
     * 根据时间范围查找呼叫列表
     */
    List<FreeSwitchCallEntity> findByStartTimeBetween(ZonedDateTime startTime, ZonedDateTime endTime);
    
    /**
     * 根据主叫号码查找呼叫列表
     */
    List<FreeSwitchCallEntity> findByCallerNumber(String callerNumber);
    
    /**
     * 根据被叫号码查找呼叫列表
     */
    List<FreeSwitchCallEntity> findByCalleeNumber(String calleeNumber);
    
    /**
     * 分页查询呼叫列表
     */
    Page<FreeSwitchCallEntity> findAll(Pageable pageable);
    
    /**
     * 统计指定时间范围内的呼叫总数
     */
    @Query("SELECT COUNT(c) FROM FreeSwitchCallEntity c WHERE c.startTime BETWEEN ?1 AND ?2")
    long countCallsInTimeRange(ZonedDateTime startTime, ZonedDateTime endTime);
    
    /**
     * 统计指定时间范围内的已接听呼叫数
     */
    @Query("SELECT COUNT(c) FROM FreeSwitchCallEntity c WHERE c.startTime BETWEEN ?1 AND ?2 AND c.status = 'ANSWERED'")
    long countAnsweredCallsInTimeRange(ZonedDateTime startTime, ZonedDateTime endTime);
    
    /**
     * 计算指定时间范围内的通话总时长
     */
    @Query("SELECT COALESCE(SUM(c.duration), 0) FROM FreeSwitchCallEntity c WHERE c.startTime BETWEEN ?1 AND ?2 AND c.status = 'ANSWERED'")
    long sumDurationInTimeRange(ZonedDateTime startTime, ZonedDateTime endTime);
    
    /**
     * 计算指定时间范围内的等待总时长
     */
    @Query("SELECT COALESCE(SUM(c.waitTime), 0) FROM FreeSwitchCallEntity c WHERE c.startTime BETWEEN ?1 AND ?2")
    long sumWaitTimeInTimeRange(ZonedDateTime startTime, ZonedDateTime endTime);
    
    /**
     * 删除指定时间之前的呼叫记录
     */
    void deleteByStartTimeBefore(ZonedDateTime time);
} 