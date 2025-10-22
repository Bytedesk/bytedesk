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
//  * FreeSWITCH NAT表实体
//  * 对应表: nat
//  */
// @Data
// @Entity(name = "FreeSwitchNatEntity")
// @SuperBuilder
// @NoArgsConstructor
// @AllArgsConstructor
// @Table(name = "nat", indexes = {
//     @Index(name = "nat_map_port_proto", columnList = "port,proto,hostname")
// })
// @IdClass(NatEntity.NatId.class)
// public class NatEntity {

//     /**
//      * 端口 - 复合主键之一
//      */
//     @Id
//     @Column(name = "port")
//     private Integer port;

//     /**
//      * 协议 - 复合主键之一
//      */
//     @Id
//     @Column(name = "proto")
//     private Integer proto;

//     /**
//      * 主机名 - 复合主键之一
//      */
//     @Id
//     @Column(name = "hostname", length = 256)
//     private String hostname;

//     /**
//      * 粘性标识
//      */
//     @Column(name = "sticky")
//     private Integer sticky;

//     /**
//      * 复合主键类
//      */
//     @Data
//     @NoArgsConstructor
//     @AllArgsConstructor
//     public static class NatId implements java.io.Serializable {
//         private Integer port;
//         private Integer proto;
//         private String hostname;
//     }
// }
