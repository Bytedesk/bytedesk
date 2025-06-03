/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-03 15:18:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-03 09:29:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bytedesk.freeswitch.model.CallStatistics;
import com.bytedesk.freeswitch.service.CallStatisticsService;

/**
 * 呼叫统计服务实现类
 */
@Service
public class CallStatisticsServiceImpl implements CallStatisticsService {
    
    private static final Logger log = LoggerFactory.getLogger(CallStatisticsServiceImpl.class);
    
    @Override
    public CallStatistics getCallStatistics() {
        // 这里应当从数据库中获取真实数据
        // 当前为示例数据
        log.info("获取当前呼叫统计数据");
        
        CallStatistics stats = new CallStatistics();
        stats.setTodayCallCount(125);
        stats.setAverageCallDuration(3.7);
        stats.setOnlineAgentsCount(12);
        stats.setActiveCallsCount(8);
        stats.setInboundCallsCount(85);
        stats.setOutboundCallsCount(40);
        stats.setAnsweredCallsCount(110);
        stats.setMissedCallsCount(15);
        stats.setAnswerRate(88.0);
        
        return stats;
    }
    
    @Override
    public CallStatistics getCallStatisticsByPeriod(String period) {
        log.info("获取{}周期的呼叫统计数据", period);
        
        CallStatistics stats = new CallStatistics();
        
        // 根据不同时间周期返回相应的统计数据
        if ("today".equals(period)) {
            return getCallStatistics();
        } else if ("week".equals(period)) {
            stats.setTodayCallCount(820);
            stats.setAverageCallDuration(4.2);
            stats.setOnlineAgentsCount(14);
            stats.setActiveCallsCount(8);
            stats.setInboundCallsCount(550);
            stats.setOutboundCallsCount(270);
            stats.setAnsweredCallsCount(720);
            stats.setMissedCallsCount(100);
            stats.setAnswerRate(87.8);
        } else if ("month".equals(period)) {
            stats.setTodayCallCount(3240);
            stats.setAverageCallDuration(3.9);
            stats.setOnlineAgentsCount(15);
            stats.setActiveCallsCount(8);
            stats.setInboundCallsCount(2180);
            stats.setOutboundCallsCount(1060);
            stats.setAnsweredCallsCount(2950);
            stats.setMissedCallsCount(290);
            stats.setAnswerRate(91.0);
        } else if ("year".equals(period)) {
            stats.setTodayCallCount(36500);
            stats.setAverageCallDuration(4.0);
            stats.setOnlineAgentsCount(15);
            stats.setActiveCallsCount(8);
            stats.setInboundCallsCount(24500);
            stats.setOutboundCallsCount(12000);
            stats.setAnsweredCallsCount(33200);
            stats.setMissedCallsCount(3300);
            stats.setAnswerRate(90.7);
        } else {
            // 默认返回当天数据
            return getCallStatistics();
        }
        
        return stats;
    }
}
