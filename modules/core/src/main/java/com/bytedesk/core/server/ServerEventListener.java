/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:44:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-25 06:06:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.quartz.event.QuartzFiveMinEvent;
import com.bytedesk.core.server_metrics.ServerMetricsRestService;
import com.bytedesk.core.uid.UidUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ServerEventListener {

    @Value("${server.port}")
    private Integer serverPort;

    private final BytedeskProperties bytedeskProperties;
    private final ServerRestService serverRestService;
    private final ServerMetricsRestService serverMetricsRestService;
    private final UidUtils uidUtils;

    public ServerEventListener(BytedeskProperties bytedeskProperties, 
                             ServerRestService serverRestService, 
                             ServerMetricsRestService serverMetricsRestService, 
                             UidUtils uidUtils) {
        this.bytedeskProperties = bytedeskProperties;
        this.serverRestService = serverRestService;
        this.serverMetricsRestService = serverMetricsRestService;
        this.uidUtils = uidUtils;
    }

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
            
            // 尝试查找现有服务器记录
            ServerEntity existingServer = serverRestService.findByServerName(serverName);
            
            if (existingServer != null) {
                // 更新现有服务器基本信息（保持最新状态）
                existingServer.setServerPort(serverPort);
                existingServer.setCpuUsage(currentMetrics.getCpuUsage());
                existingServer.setMemoryUsage(currentMetrics.getMemoryUsage());
                existingServer.setDiskUsage(currentMetrics.getDiskUsage());
                existingServer.setTotalMemoryMb(currentMetrics.getTotalMemoryMb());
                existingServer.setUsedMemoryMb(currentMetrics.getUsedMemoryMb());
                existingServer.setTotalDiskGb(currentMetrics.getTotalDiskGb());
                existingServer.setUsedDiskGb(currentMetrics.getUsedDiskGb());
                existingServer.setUptimeSeconds(currentMetrics.getUptimeSeconds());
                existingServer.setLastHeartbeat(currentMetrics.getLastHeartbeat());
                existingServer.setOsInfo(currentMetrics.getOsInfo());
                existingServer.setJavaVersion(currentMetrics.getJavaVersion());
                existingServer.setAppVersion(bytedeskProperties.getVersion());
                existingServer.setEnvironment(bytedeskProperties.getDebug() ? "DEV" : "PROD");
                existingServer.setLevel(LevelEnum.PLATFORM.name());
                
                // 根据资源使用情况更新状态
                if (currentMetrics.getCpuUsage() > 90 || currentMetrics.getMemoryUsage() > 90 || currentMetrics.getDiskUsage() > 95) {
                    existingServer.setStatus(ServerStatusEnum.OVERLOADED.name());
                } else if (currentMetrics.getCpuUsage() > 80 || currentMetrics.getMemoryUsage() > 80 || currentMetrics.getDiskUsage() > 85) {
                    existingServer.setStatus(ServerStatusEnum.WARNING.name());
                } else {
                    existingServer.setStatus(ServerStatusEnum.ONLINE.name());
                }
                
                serverRestService.updateServer(existingServer);
                
                // 记录历史指标数据（每次创建新记录）
                serverMetricsRestService.recordMetrics(existingServer);
                
                log.debug("Updated server and recorded metrics for: {}", serverName);
            } else {
                // 创建新的服务器记录
                currentMetrics.setUid(uidUtils.getUid());
                currentMetrics.setServerPort(serverPort);
                currentMetrics.setServerIp(currentMetrics.getServerIp());
                currentMetrics.setType(ServerTypeEnum.APPLICATION.name());
                currentMetrics.setStatus(ServerStatusEnum.ONLINE.name());
                currentMetrics.setAppVersion(bytedeskProperties.getVersion());
                currentMetrics.setEnvironment(bytedeskProperties.getDebug() ? "DEV" : "PROD");
                currentMetrics.setMonitoringEnabled(true);
                currentMetrics.setLevel(LevelEnum.PLATFORM.name());
                
                ServerEntity savedServer = serverRestService.createServer(currentMetrics);
                
                // 记录历史指标数据
                serverMetricsRestService.recordMetrics(savedServer);
                
                log.info("Created new server and recorded metrics for: {}", serverName);
            }
            
        } catch (Exception e) {
            log.error("Error updating server metrics: ", e);
        }
    }

}

