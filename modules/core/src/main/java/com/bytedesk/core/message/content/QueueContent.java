package com.bytedesk.core.message.content;

import com.bytedesk.core.base.BaseContent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class QueueContent extends BaseContent {

    private static final long serialVersionUID = 1L;

    /**
     * 展示给用户的排队提示文本(可国际化处理后再塞入此字段)
     */
    private String content; 

    /**
     * 当前用户所在排队位置(从1开始)。为便于前端直接数字运算采用 Integer。
     */
    private Integer position;

    /**
     * 当前队列总人数(含自己)，用于前端显示或动态估算。
     */
    private Integer queueSize;

    /**
     * 预计等待秒数(粗略估算)，前端可换算为分钟展示。为 null 表示无法估算。
     */
    private Integer waitSeconds;

    /**
     * 人性化预计等待时间描述，如 "约5分钟"；与 waitSeconds 同步，仅展示用。
     */
    private String estimatedWaitTime;
    
}
