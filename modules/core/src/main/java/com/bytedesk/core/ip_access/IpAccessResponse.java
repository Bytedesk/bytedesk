/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-17 15:27:44
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-01 21:36:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.ip_access;

import java.time.ZonedDateTime;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.utils.BdDateUtils;

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
public class IpAccessResponse extends BaseResponse {
    // 
    private String ip;
    private String ipLocation;
    private String endpoint;  // 访问的接口
    private String params; // 访问的参数

    private ZonedDateTime accessTime;
    private Integer accessCount; // 访问次数
    private ZonedDateTime lastAccessTime;
    // 

    public String getAccessTime() {
        return BdDateUtils.formatDatetimeToString(accessTime); 
    }

    public String getLastAccessTime() {
        return BdDateUtils.formatDatetimeToString(lastAccessTime); 
    }
}
