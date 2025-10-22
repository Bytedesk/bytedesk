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
//  * FreeSWITCH Tasks表实体
//  * 对应表: tasks
//  */
// @Data
// @Entity(name = "FreeSwitchTaskEntity")
// @SuperBuilder
// @NoArgsConstructor
// @AllArgsConstructor
// @Table(name = "tasks", indexes = {
//     @Index(name = "tasks1", columnList = "hostname,task_id")
// })
// @IdClass(TaskEntity.TaskId.class)
// public class TaskEntity {

//     /**
//      * 主机名 - 复合主键之一
//      */
//     @Id
//     @Column(name = "hostname", length = 256)
//     private String hostname;

//     /**
//      * 任务ID - 复合主键之一
//      */
//     @Id
//     @Column(name = "task_id")
//     private Integer taskId;

//     /**
//      * 任务描述
//      */
//     @Column(name = "task_desc", length = 4096)
//     private String taskDesc;

//     /**
//      * 任务组
//      */
//     @Column(name = "task_group", length = 1024)
//     private String taskGroup;

//     /**
//      * 任务运行时间
//      */
//     @Column(name = "task_runtime")
//     private Long taskRuntime;

//     /**
//      * SQL管理器标识
//      */
//     @Column(name = "task_sql_manager")
//     private Integer taskSqlManager;

//     /**
//      * 复合主键类
//      */
//     @Data
//     @NoArgsConstructor
//     @AllArgsConstructor
//     public static class TaskId implements java.io.Serializable {
//         private String hostname;
//         private Integer taskId;
//     }
// }
