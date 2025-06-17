package com.bytedesk.core.message.content;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 文档消息内容类
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentContent implements Serializable {
    private String url;       // 文档文件URL
    private String name;      // 文件名称
    private String size;      // 文件大小
    private String type;      // 文件MIME类型
    private String caption;   // 文档说明文字
    private String thumbnail; // 缩略图URL
    private String label;     // 文档标签
}
