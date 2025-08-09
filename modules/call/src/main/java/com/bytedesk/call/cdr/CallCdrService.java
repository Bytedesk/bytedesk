/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-04 10:31:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.cdr;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.core.utils.BdDateUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Call CDR服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "bytedesk.freeswitch.enabled", havingValue = "true", matchIfMissing = false)
public class CallCdrService {

    private final CallCdrRepository cdrRepository;

    /**
     * 创建CDR记录
     */
    @Transactional
    public CallCdrEntity createCdr(CallCdrEntity cdr) {
        log.info("创建CDR记录: UUID={}, 主叫={}, 被叫={}", 
                cdr.getUid(), cdr.getCallerIdNumber(), cdr.getDestinationNumber());
        
        // 检查UUID是否已存在
        if (cdrRepository.existsByUid(cdr.getUid())) {
            log.warn("CDR记录已存在: {}", cdr.getUid());
            return cdr;
        }
        
        return cdrRepository.save(cdr);
    }

    /**
     * 更新CDR记录
     */
    @Transactional
    public CallCdrEntity updateCdr(CallCdrEntity cdr) {
        log.info("更新CDR记录: UUID={}", cdr.getUid());
        return cdrRepository.save(cdr);
    }

    /**
     * 删除CDR记录
     */
    @Transactional
    public void deleteCdr(Long id) {
        log.info("删除CDR记录: {}", id);
        cdrRepository.deleteById(id);
    }

    /**
     * 根据ID查找CDR记录
     */
    public Optional<CallCdrEntity> findById(Long id) {
        return cdrRepository.findById(id);
    }

    /**
     * 根据UUID查找CDR记录
     */
    public Optional<CallCdrEntity> findByUid(String uuid) {
        return cdrRepository.findByUid(uuid);
    }

    /**
     * 根据主叫号码查找CDR记录
     */
    public List<CallCdrEntity> findByCallerNumber(String callerNumber) {
        return cdrRepository.findByCallerIdNumber(callerNumber);
    }

    /**
     * 根据被叫号码查找CDR记录
     */
    public List<CallCdrEntity> findByDestinationNumber(String destinationNumber) {
        return cdrRepository.findByDestinationNumber(destinationNumber);
    }

    /**
     * 获取指定号码的通话历史
     */
    public Page<CallCdrEntity> getCallHistory(String number, Pageable pageable) {
        return cdrRepository.findCallHistory(number, pageable);
    }

    /**
     * 获取最近的通话记录
     */
    public Page<CallCdrEntity> getRecentCalls(Pageable pageable) {
        return cdrRepository.findRecentCalls(pageable);
    }

    /**
     * 根据时间范围查找CDR记录
     */
    public List<CallCdrEntity> findByTimeRange(ZonedDateTime startTime, ZonedDateTime endTime) {
        return cdrRepository.findByStartStampBetween(startTime, endTime);
    }

    /**
     * 获取成功接通的通话记录
     */
    public List<CallCdrEntity> getAnsweredCalls() {
        return cdrRepository.findByAnswerStampIsNotNull();
    }

    /**
     * 获取有录音的通话记录
     */
    public List<CallCdrEntity> getRecordedCalls() {
        return cdrRepository.findByRecordFileIsNotNull();
    }

    /**
     * 分页查询CDR记录
     */
    public Page<CallCdrEntity> findCdrs(Pageable pageable) {
        return cdrRepository.findAll(pageable);
    }

    /**
     * 获取通话统计信息
     */
    public CallStatistics getCallStatistics(ZonedDateTime startTime, ZonedDateTime endTime) {
        long totalCalls = cdrRepository.countCallsInTimeRange(startTime, endTime);
        long answeredCalls = cdrRepository.countAnsweredCallsInTimeRange(startTime, endTime);
        long totalDuration = cdrRepository.sumBillSecInTimeRange(startTime, endTime);
        
        double answerRate = totalCalls > 0 ? (double) answeredCalls / totalCalls * 100 : 0;
        double avgDuration = answeredCalls > 0 ? (double) totalDuration / answeredCalls : 0;
        
        return CallStatistics.builder()
                .totalCalls(totalCalls)
                .answeredCalls(answeredCalls)
                .missedCalls(totalCalls - answeredCalls)
                .answerRate(answerRate)
                .totalDuration(totalDuration)
                .avgDuration(avgDuration)
                .build();
    }

    /**
     * 清理过期的CDR记录
     */
    @Transactional
    public void cleanupOldCdrs(int daysToKeep) {
        ZonedDateTime cutoffDate = BdDateUtils.now().minusDays(daysToKeep);
        cdrRepository.deleteByStartStampBefore(cutoffDate);
        log.info("清理了{}天前的CDR记录", daysToKeep);
    }

    /**
     * 处理Call CDR事件
     */
    @Transactional
    public void processCdrEvent(String uuid, String callerNumber, String destinationNumber, 
                               String context, ZonedDateTime startStamp, ZonedDateTime answerStamp, 
                               ZonedDateTime endStamp, Integer duration, Integer billsec, 
                               String hangupCause, String direction) {
        
        Optional<CallCdrEntity> existingCdr = cdrRepository.findByUid(uuid);
        
        CallCdrEntity cdr;
        if (existingCdr.isPresent()) {
            cdr = existingCdr.get();
            log.info("更新现有CDR记录: {}", uuid);
        } else {
            cdr = CallCdrEntity.builder()
                    .uid(uuid)
                    .callerIdNumber(callerNumber)
                    .destinationNumber(destinationNumber)
                    .context(context)
                    .direction(direction)
                    .build();
            log.info("创建新CDR记录: {}", uuid);
        }
        
        // 更新CDR信息
        cdr.setStartStamp(startStamp);
        cdr.setAnswerStamp(answerStamp);
        cdr.setEndStamp(endStamp);
        cdr.setDuration(duration);
        cdr.setBillsec(billsec);
        cdr.setHangupCause(hangupCause);
        
        cdrRepository.save(cdr);
    }

    /**
     * 更新CDR应答时间
     */
    @Transactional
    public void updateCdrAnswerTime(String uuid, ZonedDateTime answerTime) {
        Optional<CallCdrEntity> optionalCdr = cdrRepository.findByUid(uuid);
        if (optionalCdr.isPresent()) {
            CallCdrEntity cdr = optionalCdr.get();
            cdr.setAnswerStamp(answerTime);
            cdrRepository.save(cdr);
            log.info("更新CDR应答时间: UUID={}, 应答时间={}", uuid, answerTime);
        } else {
            log.warn("未找到UUID为{}的CDR记录，无法更新应答时间", uuid);
        }
    }

    /**
     * 更新CDR结束时间和挂断原因
     */
    @Transactional
    public void updateCdrEndTime(String uuid, ZonedDateTime endTime, String hangupCause) {
        Optional<CallCdrEntity> optionalCdr = cdrRepository.findByUid(uuid);
        if (optionalCdr.isPresent()) {
            CallCdrEntity cdr = optionalCdr.get();
            cdr.setEndStamp(endTime);
            cdr.setHangupCause(hangupCause);
            
            // 计算通话时长
            if (cdr.getStartStamp() != null) {
                long durationSeconds = java.time.Duration.between(cdr.getStartStamp(), endTime).getSeconds();
                cdr.setDuration((int) durationSeconds);
                
                // 计算计费时长（从应答到挂断）
                if (cdr.getAnswerStamp() != null) {
                    long billsecSeconds = java.time.Duration.between(cdr.getAnswerStamp(), endTime).getSeconds();
                    cdr.setBillsec((int) billsecSeconds);
                }
            }
            
            cdrRepository.save(cdr);
            log.info("更新CDR结束时间: UUID={}, 结束时间={}, 挂断原因={}", uuid, endTime, hangupCause);
        } else {
            log.warn("未找到UUID为{}的CDR记录，无法更新结束时间", uuid);
        }
    }

    /**
     * 通话统计信息类
     */
    public static class CallStatistics {
        private long totalCalls;
        private long answeredCalls;
        private long missedCalls;
        private double answerRate;
        private long totalDuration;
        private double avgDuration;
        
        public static CallStatisticsBuilder builder() {
            return new CallStatisticsBuilder();
        }
        
        public static class CallStatisticsBuilder {
            private long totalCalls;
            private long answeredCalls;
            private long missedCalls;
            private double answerRate;
            private long totalDuration;
            private double avgDuration;
            
            public CallStatisticsBuilder totalCalls(long totalCalls) {
                this.totalCalls = totalCalls;
                return this;
            }
            
            public CallStatisticsBuilder answeredCalls(long answeredCalls) {
                this.answeredCalls = answeredCalls;
                return this;
            }
            
            public CallStatisticsBuilder missedCalls(long missedCalls) {
                this.missedCalls = missedCalls;
                return this;
            }
            
            public CallStatisticsBuilder answerRate(double answerRate) {
                this.answerRate = answerRate;
                return this;
            }
            
            public CallStatisticsBuilder totalDuration(long totalDuration) {
                this.totalDuration = totalDuration;
                return this;
            }
            
            public CallStatisticsBuilder avgDuration(double avgDuration) {
                this.avgDuration = avgDuration;
                return this;
            }
            
            public CallStatistics build() {
                CallStatistics stats = new CallStatistics();
                stats.totalCalls = this.totalCalls;
                stats.answeredCalls = this.answeredCalls;
                stats.missedCalls = this.missedCalls;
                stats.answerRate = this.answerRate;
                stats.totalDuration = this.totalDuration;
                stats.avgDuration = this.avgDuration;
                return stats;
            }
        }
        
        // Getters
        public long getTotalCalls() { return totalCalls; }
        public long getAnsweredCalls() { return answeredCalls; }
        public long getMissedCalls() { return missedCalls; }
        public double getAnswerRate() { return answerRate; }
        public long getTotalDuration() { return totalDuration; }
        public double getAvgDuration() { return avgDuration; }
    }
}
