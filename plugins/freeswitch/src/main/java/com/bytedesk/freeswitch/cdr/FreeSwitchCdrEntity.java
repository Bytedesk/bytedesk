/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-14 11:33:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.freeswitch.cdr;

import java.time.ZonedDateTime;

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * FreeSwitch通话详单记录实体
 * 对应数据库表：freeswitch_cdr
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({FreeSwitchCdrEntityListener.class})
@Table(name = "bytedesk_freeswitch_cdr")
public class FreeSwitchCdrEntity extends BaseEntity {
    
    /**
     * 主叫名称
     */
    private String callerIdName;

    /**
     * 主叫号码
     */
    private String callerIdNumber;

    /**
     * 被叫号码
     */
    private String destinationNumber;

    /**
     * 上下文
     */
    private String context;

    /**
     * 通话开始时间
     */
    private ZonedDateTime startStamp;

    /**
     * 通话接通时间
     */
    private ZonedDateTime answerStamp;

    /**
     * 通话结束时间
     */
    private ZonedDateTime endStamp;

    /**
     * 通话总时长（秒）
     */
    private Integer duration;

    /**
     * 计费时长（秒）
     */
    private Integer billsec;

    /**
     * 挂断原因
     */
    private String hangupCause;

    /**
     * 账户代码
     */
    private String accountcode;

    /**
     * 读取编解码器
     */
    private String readCodec;

    /**
     * 写入编解码器
     */
    private String writeCodec;

    /**
     * SIP挂断处理
     */
    private String sipHangupDisposition;

    /**
     * 录音文件路径
     */
    private String recordFile;

    /**
     * 通话方向（inbound/outbound）
     */
    private String direction;

    /**
     * JSON格式的扩展信息
     */
    @Column(columnDefinition = "TEXT")
    private String json;

    /**
     * 获取通话状态描述
     */
    public String getCallStatusDescription() {
        if (answerStamp != null) {
            return "已接通";
        } else if (hangupCause != null) {
            return "未接通 - " + hangupCause;
        } else {
            return "未知状态";
        }
    }

    /**
     * 检查是否为成功通话
     */
    public boolean isSuccessfulCall() {
        return answerStamp != null && billsec != null && billsec > 0;
    }

    /**
     * 获取格式化的通话时长
     */
    public String getFormattedDuration() {
        if (duration == null) return "00:00:00";
        
        int hours = duration / 3600;
        int minutes = (duration % 3600) / 60;
        int seconds = duration % 60;
        
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
