/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-09 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.conference;

import com.bytedesk.core.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class CallConferenceRequest extends BaseRequest {

    /**
     * 会议室名称
     */
    private String conferenceName;

    /**
     * 会议室描述
     */
    private String description;

    /**
     * 会议室密码
     */
    private String password;

    /**
     * 最大参与者数量
     */
    private Integer maxMembers;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 是否录音
     */
    private Boolean recordEnabled;

    /**
     * 录音文件路径
     */
    private String recordPath;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 会议室配置参数（JSON格式）
     */
    private String configJson;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 获取主持人密码（兼容性方法）
     */
    public String getModeratorPin() {
        return this.password;
    }

    /**
     * 检查是否设置了密码保护
     */
    public Boolean getPasswordProtected() {
        return password != null &amp;amp;& !password.trim().isEmpty();
    }
}
