/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-04 14:54:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-04 14:54:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.thread.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 处理坐席离线的服务委托
 */
@Component("threadAgentsOfflineServiceDelegate")
public class ThreadAgentsOfflineServiceDelegate implements JavaDelegate {

    private final Logger logger = LoggerFactory.getLogger(ThreadAgentsOfflineServiceDelegate.class);

    @Override
    public void execute(DelegateExecution execution) {
        logger.info("处理坐席离线情况，执行id: {}", execution.getId());

        // 获取流程变量
        String threadUid = (String) execution.getVariable("threadUid");
        // agentUid
        String agentUid = (String) execution.getVariable("agentUid");
        // workgroupUid
        String workgroupUid = (String) execution.getVariable("workgroupUid");
        
        logger.info("坐席离线处理 - 会话ID: {}, 坐席: {}, 工作组: {}", threadUid, agentUid, workgroupUid);
        
        // 在这里实现坐席离线的具体处理逻辑
        // 例如：发送通知给访客，记录离线状态，或者设置自动回复等
        
        // 记录处理结果
        execution.setVariable("offlineHandled", true);
        execution.setVariable("offlineHandleTime", System.currentTimeMillis());
    }
}
