/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-08 12:03:27
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-23 10:01:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

import com.bytedesk.service.agent.AgentResponseSimple;
import com.bytedesk.core.constant.BdConstants;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * used for visitor thread extra info
 */
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class VisitorExtra {
 
    @Builder.Default
    private String welcomeTip = BdConstants.DEFAULT_WORK_GROUP_ACCEPT_TIP;
    
    // visitor_vid
    // private String uid;
    private VisitorResponseSimple visitor;

    private AgentResponseSimple agent;
    
    // whether thread is closed
    // @Builder.Default
    // private boolean isClosed = false;
    
    /** auto close time in min - 默认自动关闭时间，单位分钟 */
    @Builder.Default
    private Double autoCloseMin = Double.valueOf(25);
    
}
