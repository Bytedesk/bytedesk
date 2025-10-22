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

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

// /**
//  * FreeSWITCH Calls 表数据访问接口
//  * 
//  * 此 Repository 使用独立的 FreeSWITCH 数据源
//  * 通过 FreeSwitchDataSourceConfig 配置关联
//  */
// @Repository
// public interface FreeSwitchCallRepository extends JpaRepository<CallEntity, String> {

//     /**
//      * 根据通话UUID查找通话记录
//      */
//     Optional<CallEntity> findByCallUuid(String callUuid);

//     /**
//      * 根据主叫UUID查找通话记录
//      */
//     List<CallEntity> findByCallerUuid(String callerUuid);

//     /**
//      * 根据被叫UUID查找通话记录
//      */
//     List<CallEntity> findByCalleeUuid(String calleeUuid);

//     /**
//      * 根据主机名查找通话记录
//      */
//     List<CallEntity> findByHostname(String hostname);

//     /**
//      * 查询所有通话记录(按创建时间倒序)
//      * 注意: calls表结构简单,没有通话状态字段,此方法返回所有记录
//      */
//     @Query("SELECT c FROM FreeSwitchCallEntity c ORDER BY c.callCreatedEpoch DESC")
//     List<CallEntity> findActiveCalls();

//     /**
//      * 根据主叫UUID和主机名查找通话记录
//      */
//     List<CallEntity> findByCallerUuidAndHostname(String callerUuid, String hostname);

//     /**
//      * 统计指定主机名的通话数量
//      */
//     @Query("SELECT COUNT(c) FROM FreeSwitchCallEntity c WHERE c.hostname = :hostname")
//     long countByHostname(@Param("hostname") String hostname);

//     /**
//      * 统计所有通话数量
//      */
//     @Query("SELECT COUNT(c) FROM FreeSwitchCallEntity c")
//     long countActiveCalls();
// }
