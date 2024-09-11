/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-28 22:45:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-28 22:55:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.quartz.event.QuartzFiveSecondEvent;

import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
@Component
@AllArgsConstructor
public class AgentQuartzListener implements ApplicationListener<QuartzFiveSecondEvent> {
    
    // TODO: ping 数据库中在线的客服，如果超时则标记为离线
    @Override
    public void onApplicationEvent(QuartzFiveSecondEvent event) {
        // log.info("AgentQuartzListener onApplicationEvent: {}", event.toString());

        // 1. 获取在线的客服列表

        // 2. 遍历客服列表，判断是否超时

        // 3. 超时则标记为离线
        
    }

}
