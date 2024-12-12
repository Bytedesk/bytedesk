package com.bytedesk.core.workflow.flow.model.block.model.options;

import com.bytedesk.core.workflow.flow.model.block.model.BlockOptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WaitBlockOptions extends BlockOptions {
    private String type;            // time, variable, condition
    private Integer seconds;        // 等待秒数
    private String message;         // 等待时显示的消息
    private boolean showProgress;   // 是否显示进度条
    private String variableName;    // 存储等待状态的变量名
    private WaitCondition condition;// 等待条件
}
