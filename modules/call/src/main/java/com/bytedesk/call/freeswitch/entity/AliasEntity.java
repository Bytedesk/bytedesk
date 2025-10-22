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
//  * FreeSWITCH Aliases表实体
//  * 对应表: aliases
//  */
// @Data
// @Entity(name = "FreeSwitchAliasEntity")
// @SuperBuilder
// @NoArgsConstructor
// @AllArgsConstructor
// @Table(name = "aliases", indexes = {
//     @Index(name = "alias1", columnList = "alias")
// })
// @IdClass(AliasEntity.AliasId.class)
// public class AliasEntity {

//     /**
//      * 别名 - 复合主键之一
//      */
//     @Id
//     @Column(name = "alias", length = 128)
//     private String alias;

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
//      * 命令
//      */
//     @Column(name = "command", length = 4096)
//     private String command;

//     /**
//      * 复合主键类
//      */
//     @Data
//     @NoArgsConstructor
//     @AllArgsConstructor
//     public static class AliasId implements java.io.Serializable {
//         private String alias;
//         private String hostname;
//     }
// }
