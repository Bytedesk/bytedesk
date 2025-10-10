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
 * FreeSWITCH Registrations表实体
 * 对应表: registrations
 */
@Data
@Entity(name = "FreeSwitchRegistrationEntity")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "registrations", indexes = {
    @Index(name = "regindex1", columnList = "reg_user,realm,hostname")
})
@IdClass(RegistrationEntity.RegistrationId.class)
public class RegistrationEntity {

    /**
     * 注册用户 - 复合主键之一
     */
    @Id
    @Column(name = "reg_user", length = 256)
    private String regUser;

    /**
     * 域 - 复合主键之一
     */
    @Id
    @Column(name = "realm", length = 256)
    private String realm;

    /**
     * 主机名 - 复合主键之一
     */
    @Id
    @Column(name = "hostname", length = 256)
    private String hostname;

    /**
     * 令牌
     */
    @Column(name = "token", length = 256)
    private String token;

    /**
     * URL
     */
    @Column(name = "url", columnDefinition = "TEXT")
    private String url;

    /**
     * 过期时间
     */
    @Column(name = "expires")
    private Integer expires;

    /**
     * 网络IP
     */
    @Column(name = "network_ip", length = 256)
    private String networkIp;

    /**
     * 网络端口
     */
    @Column(name = "network_port", length = 256)
    private String networkPort;

    /**
     * 网络协议
     */
    @Column(name = "network_proto", length = 256)
    private String networkProto;

    /**
     * 元数据
     */
    @Column(name = "metadata", length = 256)
    private String metadata;

    /**
     * 复合主键类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegistrationId implements java.io.Serializable {
        private String regUser;
        private String realm;
        private String hostname;
    }
}
