package com.bytedesk.core.workflow.flow.model.block.model.options;

import com.bytedesk.core.workflow.flow.model.block.model.BlockOptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VideoBlockOptions extends BlockOptions {
    private String url;           // 视频URL
    private String content;       // Base64编码的视频内容
    private String sourceType;    // URL, UPLOAD, YOUTUBE, VIMEO
    private String mimeType;      // 视频类型
    private boolean autoplay;     // 是否自动播放
    private boolean loop;         // 是否循环播放
    private boolean muted;        // 是否静音
    private String posterUrl;     // 封面图URL
    private String variableName;  // 存储播放状态的变量名
    private VideoProvider provider;  // 视频提供商配置
} 
