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
 * FreeSWITCH Channels表实体
 * 对应表: channels
 */
@Data
@Entity(name = "FreeSwitchChannelEntity")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "channels", indexes = {
    @Index(name = "chidx1", columnList = "hostname"),
    @Index(name = "uuindex", columnList = "uuid,hostname"),
    @Index(name = "uuindex2", columnList = "call_uuid")
})
public class ChannelEntity {

    /**
     * 通道UUID - 作为主键
     */
    @Id
    @Column(name = "uuid", length = 256)
    private String uuid;

    /**
     * 呼叫方向
     */
    @Column(name = "direction", length = 32)
    private String direction;

    /**
     * 创建时间
     */
    @Column(name = "created", length = 128)
    private String created;

    /**
     * 创建时间戳
     */
    @Column(name = "created_epoch")
    private Integer createdEpoch;

    /**
     * 通道名称
     */
    @Column(name = "name", length = 1024)
    private String name;

    /**
     * 通道状态
     */
    @Column(name = "state", length = 64)
    private String state;

    /**
     * 主叫名称
     */
    @Column(name = "cid_name", length = 1024)
    private String cidName;

    /**
     * 主叫号码
     */
    @Column(name = "cid_num", length = 256)
    private String cidNum;

    /**
     * IP地址
     */
    @Column(name = "ip_addr", length = 256)
    private String ipAddr;

    /**
     * 目标地址
     */
    @Column(name = "dest", length = 1024)
    private String dest;

    /**
     * 应用程序
     */
    @Column(name = "application", length = 128)
    private String application;

    /**
     * 应用数据
     */
    @Column(name = "application_data", columnDefinition = "TEXT")
    private String applicationData;

    /**
     * 拨号方案
     */
    @Column(name = "dialplan", length = 128)
    private String dialplan;

    /**
     * 上下文
     */
    @Column(name = "context", length = 128)
    private String context;

    /**
     * 读取编解码器
     */
    @Column(name = "read_codec", length = 128)
    private String readCodec;

    /**
     * 读取速率
     */
    @Column(name = "read_rate", length = 32)
    private String readRate;

    /**
     * 读取比特率
     */
    @Column(name = "read_bit_rate", length = 32)
    private String readBitRate;

    /**
     * 写入编解码器
     */
    @Column(name = "write_codec", length = 128)
    private String writeCodec;

    /**
     * 写入速率
     */
    @Column(name = "write_rate", length = 32)
    private String writeRate;

    /**
     * 写入比特率
     */
    @Column(name = "write_bit_rate", length = 32)
    private String writeBitRate;

    /**
     * 安全标识
     */
    @Column(name = "secure", length = 64)
    private String secure;

    /**
     * 主机名
     */
    @Column(name = "hostname", length = 256)
    private String hostname;

    /**
     * 状态ID
     */
    @Column(name = "presence_id", length = 4096)
    private String presenceId;

    /**
     * 状态数据
     */
    @Column(name = "presence_data", columnDefinition = "TEXT")
    private String presenceData;

    /**
     * 账户代码
     */
    @Column(name = "accountcode", length = 256)
    private String accountcode;

    /**
     * 呼叫状态
     */
    @Column(name = "callstate", length = 64)
    private String callstate;

    /**
     * 被叫名称
     */
    @Column(name = "callee_name", length = 1024)
    private String calleeName;

    /**
     * 被叫号码
     */
    @Column(name = "callee_num", length = 256)
    private String calleeNum;

    /**
     * 被叫方向
     */
    @Column(name = "callee_direction", length = 5)
    private String calleeDirection;

    /**
     * 通话UUID
     */
    @Column(name = "call_uuid", length = 256)
    private String callUuid;

    /**
     * 已发送被叫名称
     */
    @Column(name = "sent_callee_name", length = 1024)
    private String sentCalleeName;

    /**
     * 已发送被叫号码
     */
    @Column(name = "sent_callee_num", length = 256)
    private String sentCalleeNum;

    /**
     * 初始主叫名称
     */
    @Column(name = "initial_cid_name", length = 1024)
    private String initialCidName;

    /**
     * 初始主叫号码
     */
    @Column(name = "initial_cid_num", length = 256)
    private String initialCidNum;

    /**
     * 初始IP地址
     */
    @Column(name = "initial_ip_addr", length = 256)
    private String initialIpAddr;

    /**
     * 初始目标地址
     */
    @Column(name = "initial_dest", length = 1024)
    private String initialDest;

    /**
     * 初始拨号方案
     */
    @Column(name = "initial_dialplan", length = 128)
    private String initialDialplan;

    /**
     * 初始上下文
     */
    @Column(name = "initial_context", length = 128)
    private String initialContext;
}
