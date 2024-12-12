package com.bytedesk.core.workflow.flow.model.block.model.options;

import lombok.Data;
import java.util.Map;

@Data
public class TextToSpeechConfig {
    private String text;          // 要转换的文本
    private String voice;         // 语音类型
    private String language;      // 语言
    private double speed;         // 语速
    private double pitch;         // 音调
    private Map<String, Object> provider;  // 提供商配置
} 
