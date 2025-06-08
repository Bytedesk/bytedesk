package com.bytedesk.freeswitch.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.Min;

/**
 * 创建FreeSwitch会议室请求DTO
 */
@Data
public class CreateFreeSwitchConferenceRequest {
    
    @NotBlank(message = "会议室名称不能为空")
    @Size(min = 2, max = 50, message = "会议室名称长度必须在2-50个字符之间")
    private String name;
    
    @Size(max = 200, message = "描述不能超过200个字符")
    private String description;
    
    @Size(min = 4, max = 20, message = "主持人PIN长度必须在4-20个字符之间")
    private String moderatorPin;
    
    @Size(min = 4, max = 20, message = "成员PIN长度必须在4-20个字符之间")
    private String memberPin;
    
    @Min(value = 1, message = "最大成员数必须大于0")
    private Integer maxMembers;
    
    private Boolean active = true;
}
