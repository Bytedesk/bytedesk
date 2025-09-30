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

import com.bytedesk.core.base.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;

/**
 * Call通话详单请求实体
 */
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class CallCdrRequest extends BaseRequest {
    private static final long serialVersionUID = 1L;


    /**
     * 通话UUID
     */
    private String uuid;

    /**
     * 主叫号码
     */
    private String callerIdNumber;

    /**
     * 被叫号码
     */
    private String destinationNumber;

    /**
     * 挂断原因
     */
    private String hangupCause;

    /**
     * 主叫IP地址
     */
    private String networkAddr;

    /**
     * 最小通话时长
     */
    private Integer minDuration;

    /**
     * 最大通话时长
     */
    private Integer maxDuration;

    /**
     * 开始日期
     */
    private ZonedDateTime startDate;

    /**
     * 结束日期
     */
    private ZonedDateTime endDate;

}
