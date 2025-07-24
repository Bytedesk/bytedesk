/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:44:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-24 20:45:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.server;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.quartz.event.QuartzFiveMinEvent;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ServerEventListener {

    private final ServerRestService serverRestService;
    private final UidUtils uidUtils;

    /**
     * 监听5分钟定时事件，更新服务器信息
     * @param event QuartzFiveMinEvent
     */
    @EventListener
    public void onQuartzFiveMinEvent(QuartzFiveMinEvent event) {
        log.info("ServerEventListener: Processing 5-minute server update event");
        
        try {
            // 获取当前服务器指标
            ServerEntity currentMetrics = serverRestService.getCurrentServerMetrics();
            
            // 查找当前服务器是否已存在
            String serverName = currentMetrics.getServerName();
            String orgUid = "system"; // 系统级服务器监控
            
            // 尝试查找现有服务器记录
            ServerEntity existingServer = serverRestService.findByServerNameAndOrgUid(serverName, orgUid);
            
            if (existingServer != null) {
                // 更新现有服务器信息
                existingServer.setCpuUsage(currentMetrics.getCpuUsage());
                existingServer.setMemoryUsage(currentMetrics.getMemoryUsage());
                existingServer.setDiskUsage(currentMetrics.getDiskUsage());
                existingServer.setTotalMemoryMb(currentMetrics.getTotalMemoryMb());
                existingServer.setUsedMemoryMb(currentMetrics.getUsedMemoryMb());
                existingServer.setUptimeSeconds(currentMetrics.getUptimeSeconds());
                existingServer.setLastHeartbeat(currentMetrics.getLastHeartbeat());
                existingServer.setOsInfo(currentMetrics.getOsInfo());
                existingServer.setJavaVersion(currentMetrics.getJavaVersion());
                
                // 根据资源使用情况更新状态
                if (currentMetrics.getCpuUsage() > 90 || currentMetrics.getMemoryUsage() > 90 || currentMetrics.getDiskUsage() > 95) {
                    existingServer.setStatus(ServerStatusEnum.OVERLOADED.name());
                } else if (currentMetrics.getCpuUsage() > 80 || currentMetrics.getMemoryUsage() > 80 || currentMetrics.getDiskUsage() > 85) {
                    existingServer.setStatus(ServerStatusEnum.WARNING.name());
                } else {
                    existingServer.setStatus(ServerStatusEnum.ONLINE.name());
                }
                
                serverRestService.updateServer(existingServer);
                log.debug("Updated server metrics for: {}", serverName);
            } else {
                // 创建新的服务器记录
                currentMetrics.setUid(uidUtils.getUid());
                currentMetrics.setOrgUid(orgUid);
                currentMetrics.setServerIp(currentMetrics.getServerIp());
                currentMetrics.setType(ServerTypeEnum.APPLICATION.name());
                currentMetrics.setStatus(ServerStatusEnum.ONLINE.name());
                currentMetrics.setEnvironment("PROD");
                currentMetrics.setMonitoringEnabled(true);
                
                serverRestService.createServer(currentMetrics);
                log.info("Created new server record for: {}", serverName);
            }
            
        } catch (Exception e) {
            log.error("Error updating server metrics: ", e);
        }
    }

}

