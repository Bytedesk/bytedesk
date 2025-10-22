/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-15 00:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-10-15 00:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.freeswitch;

// import java.util.List;
// import java.util.Optional;

// import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// /**
//  * FreeSWITCH 通话服务
//  * 
//  * 演示如何使用 FreeSWITCH 数据源访问数据
//  * 
//  * 使用说明：
//  * 1. 注入 FreeSwitchCallRepository
//  * 2. 使用 @Transactional 时指定事务管理器：
//  *    @Transactional("freeswitchTransactionManager")
//  */
// @Slf4j
// @Service
// @RequiredArgsConstructor
// @ConditionalOnProperty(
//     prefix = "bytedesk.datasource.freeswitch",
//     name = "enabled",
//     havingValue = "true",
//     matchIfMissing = false
// )
// public class FreeSwitchCallService {

//     private final FreeSwitchCallRepository freeSwitchCallRepository;

//     /**
//      * 获取所有通话记录
//      */
//     @Transactional(value = "freeswitchTransactionManager", readOnly = true)
//     public List<CallEntity> getAllCalls() {
//         log.debug("Getting all FreeSWITCH calls");
//         return freeSwitchCallRepository.findAll();
//     }

//     /**
//      * 根据UUID获取通话记录
//      */
//     @Transactional(value = "freeswitchTransactionManager", readOnly = true)
//     public Optional<CallEntity> getCallByUuid(String callUuid) {
//         log.debug("Getting FreeSWITCH call by UUID: {}", callUuid);
//         return freeSwitchCallRepository.findByCallUuid(callUuid);
//     }

//     /**
//      * 获取活动通话列表
//      */
//     @Transactional(value = "freeswitchTransactionManager", readOnly = true)
//     public List<CallEntity> getActiveCalls() {
//         log.debug("Getting active FreeSWITCH calls");
//         return freeSwitchCallRepository.findActiveCalls();
//     }

//     /**
//      * 统计活动通话数量
//      */
//     @Transactional(value = "freeswitchTransactionManager", readOnly = true)
//     public long countActiveCalls() {
//         log.debug("Counting active FreeSWITCH calls");
//         return freeSwitchCallRepository.countActiveCalls();
//     }

//     /**
//      * 根据主叫UUID获取通话记录
//      */
//     @Transactional(value = "freeswitchTransactionManager", readOnly = true)
//     public List<CallEntity> getCallsByCallerUuid(String callerUuid) {
//         log.debug("Getting FreeSWITCH calls by caller UUID: {}", callerUuid);
//         return freeSwitchCallRepository.findByCallerUuid(callerUuid);
//     }

//     /**
//      * 根据被叫UUID获取通话记录
//      */
//     @Transactional(value = "freeswitchTransactionManager", readOnly = true)
//     public List<CallEntity> getCallsByCalleeUuid(String calleeUuid) {
//         log.debug("Getting FreeSWITCH calls by callee UUID: {}", calleeUuid);
//         return freeSwitchCallRepository.findByCalleeUuid(calleeUuid);
//     }

//     /**
//      * 根据主机名获取通话记录
//      */
//     @Transactional(value = "freeswitchTransactionManager", readOnly = true)
//     public List<CallEntity> getCallsByHostname(String hostname) {
//         log.debug("Getting FreeSWITCH calls by hostname: {}", hostname);
//         return freeSwitchCallRepository.findByHostname(hostname);
//     }

//     /**
//      * 统计指定主机的通话数量
//      */
//     @Transactional(value = "freeswitchTransactionManager", readOnly = true)
//     public long countCallsByHostname(String hostname) {
//         log.debug("Counting FreeSWITCH calls by hostname: {}", hostname);
//         return freeSwitchCallRepository.countByHostname(hostname);
//     }

//     /**
//      * 演示：同时访问两个数据库
//      * 
//      * 注意：跨数据源事务需要使用分布式事务（JTA）
//      * 这里仅演示如何在同一个方法中访问不同数据源
//      */
//     public void demonstrateDualDatabaseAccess() {
//         // 1. 访问 FreeSWITCH 数据库
//         List<CallEntity> freeswitchCalls = getAllCalls();
//         log.info("FreeSWITCH database - Total calls: {}", freeswitchCalls.size());
        
//         // 2. 如果需要访问主数据库（bytedesk），注入相应的 Service 或 Repository
//         // 例如：CallCallService callService
//         // List<CallCallEntity> bytedeskCalls = callService.getAllCalls();
//         // log.info("Bytedesk database - Total calls: {}", bytedeskCalls.size());
        
//         log.info("Successfully accessed FreeSWITCH database independently");
//     }
// }
