/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-08 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.cdr;

import com.bytedesk.core.base.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;

/**
 * Call通话详单响应实体
 */
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class CallCdrResponse extends BaseResponse {

    /**
     * 通话UUID
     */
    private String uuid;

    /**
     * 主叫号码名称
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
     * 用户上下文
     */
    private String context;

    /**
     * 挂断原因
     */
    private String hangupCause;

    /**
     * 账户代码
     */
    private String accountCode;

    /**
     * 通话时长（秒）
     */
    private Integer duration;

    /**
     * 计费时长（秒）
     */
    private Integer billSec;

    /**
     * 铃声时长（秒）
     */
    private Integer progressSec;

    /**
     * 振铃时长（秒）
     */
    private Integer progressMediaSec;

    /**
     * 开始时间戳
     */
    private ZonedDateTime startStamp;

    /**
     * 接听时间戳
     */
    private ZonedDateTime answerStamp;

    /**
     * 结束时间戳
     */
    private ZonedDateTime endStamp;

    /**
     * 主叫IP地址
     */
    private String networkAddr;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 录音文件路径
     */
    private String recordFile;

}
