package com.bytedesk.core.workflow.flow.model.block.model.options;

import com.bytedesk.core.workflow.flow.model.block.model.BlockOptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ImageBlockOptions extends BlockOptions {
    private String url;           // 图片URL
    private String content;       // Base64编码的图片内容
    private String sourceType;    // URL, UPLOAD
    private String mimeType;      // 图片类型
    private String alt;           // 替代文本
    private String caption;       // 图片说明
    private String link;          // 图片链接
    private String variableName;  // 存储状态的变量名
    private ImageSize size;       // 图片尺寸配置
}
