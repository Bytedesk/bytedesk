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
package com.bytedesk.call.freeswitch;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * FreeSWITCH Interfaces表实体
 * 对应表: interfaces
 */
@Data
@Entity(name = "FreeSwitchInterfaceEntity")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "interfaces")
@IdClass(InterfaceEntity.InterfaceId.class)
public class InterfaceEntity {

    /**
     * 接口类型 - 复合主键之一
     */
    @Id
    @Column(name = "type", length = 128)
    private String type;

    /**
     * 接口名称 - 复合主键之一
     */
    @Id
    @Column(name = "name", length = 1024)
    private String name;

    /**
     * 主机名 - 复合主键之一
     */
    @Id
    @Column(name = "hostname", length = 256)
    private String hostname;

    /**
     * 描述
     */
    @Column(name = "description", length = 4096)
    private String description;

    /**
     * 接口键
     */
    @Column(name = "ikey", length = 1024)
    private String ikey;

    /**
     * 文件名
     */
    @Column(name = "filename", length = 4096)
    private String filename;

    /**
     * 语法
     */
    @Column(name = "syntax", length = 4096)
    private String syntax;

    /**
     * 复合主键类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InterfaceId implements java.io.Serializable {
        private String type;
        private String name;
        private String hostname;
    }
}
