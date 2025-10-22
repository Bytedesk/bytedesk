/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-10 11:26:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-10-10 11:26:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.freeswitch.entity;

// import jakarta.persistence.*;
// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import lombok.experimental.SuperBuilder;

// /**
//  * FreeSWITCH Calls表实体
//  * 对应表: calls
//  */
// @Data
// @Entity(name = "FreeSwitchCallEntity")
// @SuperBuilder
// @NoArgsConstructor
// @AllArgsConstructor
// @Table(name = "calls", indexes = {
//     @Index(name = "callsidx1", columnList = "hostname"),
//     @Index(name = "eruuindex", columnList = "caller_uuid,hostname"),
//     @Index(name = "eeuuindex", columnList = "callee_uuid"),
//     @Index(name = "eeuuindex2", columnList = "call_uuid")
// })
// public class CallEntity {

//     /**
//      * 通话UUID - 作为主键
//      */
//     @Id
//     @Column(name = "call_uuid", length = 255)
//     private String callUuid;

//     /**
//      * 通话创建时间
//      */
//     @Column(name = "call_created", length = 128)
//     private String callCreated;

//     /**
//      * 通话创建时间戳
//      */
//     @Column(name = "call_created_epoch")
//     private Integer callCreatedEpoch;

//     /**
//      * 主叫UUID
//      */
//     @Column(name = "caller_uuid", length = 256)
//     private String callerUuid;

//     /**
//      * 被叫UUID
//      */
//     @Column(name = "callee_uuid", length = 256)
//     private String calleeUuid;

//     /**
//      * 主机名
//      */
//     @Column(name = "hostname", length = 256)
//     private String hostname;
// }
