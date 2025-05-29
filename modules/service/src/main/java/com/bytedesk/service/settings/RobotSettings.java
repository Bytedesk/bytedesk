/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-29 13:57:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-10 21:59:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.settings;

import java.io.Serializable;

import com.bytedesk.ai.robot.RobotEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Embeddable
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class RobotSettings  implements Serializable {
    
    private static final long serialVersionUID = 1L;

    // 开启机器人之后，robot字段为必填
    @Builder.Default
    private Boolean defaultRobot = false;
    
    @Builder.Default
    private Boolean offlineRobot = false;

    @Builder.Default
    private Boolean nonWorktimeRobot = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private RobotEntity robot;

    // 是否应该转接到机器人
    public Boolean shouldTransferToRobot(Boolean isOffline, Boolean isInServiceTime) {

        if (defaultRobot) {
            // 默认机器人优先接待
            return true;
        } else if (offlineRobot && isOffline) {
            // 所有客服离线,且设置机器人离线接待
            return true;
        } else if (nonWorktimeRobot && !isInServiceTime) {
            // 非工作时间,且设置机器人非工作时间接待
            return true;
        }
        // 
        return false;
    }

    

}
