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
 * FreeSWITCH Complete表实体
 * 对应表: complete
 */
@Data
@Entity(name = "FreeSwitchCompleteEntity")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "complete", indexes = {
    @Index(name = "complete1", columnList = "a1,hostname"),
    @Index(name = "complete2", columnList = "a2,hostname"),
    @Index(name = "complete3", columnList = "a3,hostname"),
    @Index(name = "complete4", columnList = "a4,hostname"),
    @Index(name = "complete5", columnList = "a5,hostname"),
    @Index(name = "complete6", columnList = "a6,hostname"),
    @Index(name = "complete7", columnList = "a7,hostname"),
    @Index(name = "complete8", columnList = "a8,hostname"),
    @Index(name = "complete9", columnList = "a9,hostname"),
    @Index(name = "complete10", columnList = "a10,hostname")
})
@IdClass(CompleteEntity.CompleteId.class)
public class CompleteEntity {

    /**
     * 参数1 - 复合主键之一
     */
    @Id
    @Column(name = "a1", length = 128)
    private String a1;

    /**
     * 主机名 - 复合主键之一
     */
    @Id
    @Column(name = "hostname", length = 256)
    private String hostname;

    /**
     * 粘性标识
     */
    @Column(name = "sticky")
    private Integer sticky;

    /**
     * 参数2
     */
    @Column(name = "a2", length = 128)
    private String a2;

    /**
     * 参数3
     */
    @Column(name = "a3", length = 128)
    private String a3;

    /**
     * 参数4
     */
    @Column(name = "a4", length = 128)
    private String a4;

    /**
     * 参数5
     */
    @Column(name = "a5", length = 128)
    private String a5;

    /**
     * 参数6
     */
    @Column(name = "a6", length = 128)
    private String a6;

    /**
     * 参数7
     */
    @Column(name = "a7", length = 128)
    private String a7;

    /**
     * 参数8
     */
    @Column(name = "a8", length = 128)
    private String a8;

    /**
     * 参数9
     */
    @Column(name = "a9", length = 128)
    private String a9;

    /**
     * 参数10
     */
    @Column(name = "a10", length = 128)
    private String a10;

    /**
     * 复合主键类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompleteId implements java.io.Serializable {
        private String a1;
        private String hostname;
    }
}
