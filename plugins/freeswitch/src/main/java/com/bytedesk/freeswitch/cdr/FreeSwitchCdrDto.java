package com.bytedesk.freeswitch.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * FreeSwitch通话详单DTO
 */
@Data
public class FreeSwitchCdrDto {
    
    private Long id;
    
    private String uuid;
    
    private String callerIdName;
    
    private String callerIdNumber;
    
    private String destinationNumber;
    
    private String context;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime answerTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    
    private Long duration;
    
    private Long billDuration;
    
    private String hangupCause;
    
    private String accountCode;
    
    private String direction;
    
    private String readCodec;
    
    private String writeCodec;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // 计算属性
    public String getFormattedDuration() {
        if (duration == null) return "00:00:00";
        
        long hours = duration / 3600;
        long minutes = (duration % 3600) / 60;
        long seconds = duration % 60;
        
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    
    public String getCallStatus() {
        if (answerTime == null) {
            return "未接听";
        } else if ("NORMAL_CLEARING".equals(hangupCause)) {
            return "正常结束";
        } else if ("USER_BUSY".equals(hangupCause)) {
            return "用户忙";
        } else if ("NO_ANSWER".equals(hangupCause)) {
            return "无应答";
        } else {
            return "其他";
        }
    }
}
