package com.bytedesk.freeswitch.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;

/**
 * 创建FreeSwitch网关请求DTO
 */
@Data
public class CreateFreeSwitchGatewayRequest {
    
    @NotBlank(message = "网关名称不能为空")
    @Size(min = 2, max = 50, message = "网关名称长度必须在2-50个字符之间")
    private String name;
    
    @NotBlank(message = "配置文件名称不能为空")
    private String profile = "external";
    
    @NotBlank(message = "代理服务器地址不能为空")
    private String proxy;
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 1, max = 50, message = "用户名长度必须在1-50个字符之间")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 1, max = 100, message = "密码长度必须在1-100个字符之间")
    private String password;
    
    @NotNull(message = "端口号不能为空")
    @Min(value = 1, message = "端口号必须大于0")
    @Max(value = 65535, message = "端口号不能超过65535")
    private Integer port = 5060;
    
    private Boolean register = true;
    
    @Min(value = 60, message = "过期时间不能少于60秒")
    @Max(value = 7200, message = "过期时间不能超过7200秒")
    private Integer expireSeconds = 3600;
    
    private Boolean enabled = true;
}
