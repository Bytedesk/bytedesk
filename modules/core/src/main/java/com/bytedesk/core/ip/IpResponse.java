/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 11:11:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-07 14:13:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip;

import java.util.Date;

import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IpResponse extends BaseResponse {

    private String ip;
    // private String ipLocation;

    private String ipRangeStart;

    private String ipRangeEnd;

    private IpTypeEnum type;

    private String reason;

    // time duration
    private Date untilDate;
}
