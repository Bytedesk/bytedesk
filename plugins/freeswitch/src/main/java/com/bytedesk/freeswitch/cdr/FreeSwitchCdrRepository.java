/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-09 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bytedesk.freeswitch.model.FreeSwitchCdrEntity;

/**
 * FreeSwitch CDR仓库接口
 */
@Repository
public interface FreeSwitchCdrRepository extends JpaRepository<FreeSwitchCdrEntity, Long>, 
        JpaSpecificationExecutor<FreeSwitchCdrEntity> {

    /**
     * 根据UUID查找CDR记录
     */
    Optional<FreeSwitchCdrEntity> findByUuid(String uuid);

    /**
     * 根据主叫号码查找CDR记录
     */
    List<FreeSwitchCdrEntity> findByCallerIdNumber(String callerIdNumber);

    /**
     * 根据被叫号码查找CDR记录
     */
    List<FreeSwitchCdrEntity> findByDestinationNumber(String destinationNumber);

    /**
     * 根据账户代码查找CDR记录
     */
    List<FreeSwitchCdrEntity> findByAccountcode(String accountcode);

    /**
     * 根据通话方向查找CDR记录
     */
    List<FreeSwitchCdrEntity> findByDirection(String direction);

    /**
     * 根据挂断原因查找CDR记录
     */
    List<FreeSwitchCdrEntity> findByHangupCause(String hangupCause);

    /**
     * 查找指定时间范围内的CDR记录
     */
    List<FreeSwitchCdrEntity> findByStartStampBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查找成功接通的通话记录
     */
    List<FreeSwitchCdrEntity> findByAnswerStampIsNotNull();

    /**
     * 查找有录音文件的通话记录
     */
    List<FreeSwitchCdrEntity> findByRecordFileIsNotNull();

    /**
     * 根据主叫或被叫号码查找CDR记录
     */
    @Query("SELECT c FROM FreeSwitchCdrEntity c WHERE c.callerIdNumber = :number OR c.destinationNumber = :number")
    List<FreeSwitchCdrEntity> findByCallerOrDestination(@Param("number") String number);

    /**
     * 查找指定号码的通话历史（分页）
     */
    @Query("SELECT c FROM FreeSwitchCdrEntity c WHERE c.callerIdNumber = :number OR c.destinationNumber = :number ORDER BY c.startStamp DESC")
    Page<FreeSwitchCdrEntity> findCallHistory(@Param("number") String number, Pageable pageable);

    /**
     * 统计指定时间范围内的通话数量
     */
    @Query("SELECT COUNT(c) FROM FreeSwitchCdrEntity c WHERE c.startStamp BETWEEN :startTime AND :endTime")
    long countCallsInTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 统计指定时间范围内成功接通的通话数量
     */
    @Query("SELECT COUNT(c) FROM FreeSwitchCdrEntity c WHERE c.startStamp BETWEEN :startTime AND :endTime AND c.answerStamp IS NOT NULL")
    long countAnsweredCallsInTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 计算指定时间范围内的总通话时长
     */
    @Query("SELECT COALESCE(SUM(c.billsec), 0) FROM FreeSwitchCdrEntity c WHERE c.startStamp BETWEEN :startTime AND :endTime AND c.answerStamp IS NOT NULL")
    long sumBillSecInTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 查找最近的通话记录
     */
    @Query("SELECT c FROM FreeSwitchCdrEntity c ORDER BY c.startStamp DESC")
    Page<FreeSwitchCdrEntity> findRecentCalls(Pageable pageable);

    /**
     * 根据主叫号码统计通话次数
     */
    @Query("SELECT COUNT(c) FROM FreeSwitchCdrEntity c WHERE c.callerIdNumber = :callerNumber")
    long countByCallerNumber(@Param("callerNumber") String callerNumber);

    /**
     * 根据被叫号码统计通话次数
     */
    @Query("SELECT COUNT(c) FROM FreeSwitchCdrEntity c WHERE c.destinationNumber = :destinationNumber")
    long countByDestinationNumber(@Param("destinationNumber") String destinationNumber);

    /**
     * 检查UUID是否存在
     */
    boolean existsByUuid(String uuid);

    /**
     * 删除指定日期之前的CDR记录
     */
    void deleteByStartStampBefore(LocalDateTime cutoffDate);
}
