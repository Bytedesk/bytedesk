package com.bytedesk.freeswitch.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * FreeSwitch网关DTO
 */
@Data
public class FreeSwitchGatewayDto {
    
    private Long id;
    
    private String name;
    
    private String profile;
    
    private String proxy;
    
    private String username;
    
    private Integer port;
    
    private Boolean register;
    
    private Integer expireSeconds;
    
    private Boolean enabled;
    
    private String status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastRegisterTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // 计算属性
    public String getStatusText() {
        if (!enabled) {
            return "已禁用";
        }
        
        switch (status) {
            case "REGISTERED":
                return "已注册";
            case "UNREGISTERED":
                return "未注册";
            case "REREGISTERING":
                return "重新注册中";
            case "FAILED":
                return "注册失败";
            case "CREATED":
                return "已创建";
            default:
                return "未知状态";
        }
    }
    
    public boolean isHealthy() {
        return enabled && "REGISTERED".equals(status);
    }
    
    public boolean needsAttention() {
        if (!enabled) {
            return false;
        }
        
        // 如果需要注册但状态不是已注册，则需要关注
        if (register && !"REGISTERED".equals(status)) {
            return true;
        }
        
        // 如果最后注册时间超过过期时间的一半，也需要关注
        if (lastRegisterTime != null && expireSeconds != null) {
            LocalDateTime halfExpireTime = lastRegisterTime.plusSeconds(expireSeconds / 2);
            return LocalDateTime.now().isAfter(halfExpireTime);
        }
        
        return false;
    }
    
    public String getEndpoint() {
        return proxy + ":" + port;
    }
}
