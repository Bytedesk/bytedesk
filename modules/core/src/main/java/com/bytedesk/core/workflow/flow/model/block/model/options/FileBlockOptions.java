package com.bytedesk.core.workflow.flow.model.block.model.options;

import com.bytedesk.core.workflow.flow.model.block.model.BlockOptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FileBlockOptions extends BlockOptions {
    private String variableName;
    private String buttonLabel;
    private String mimeTypes;  // 允许的文件类型，如 "image/*,application/pdf"
    private Long maxSize;  // 最大文件大小（字节）
    private boolean isMultiple;
    private String storageProvider;  // s3, local, etc.
    private StorageConfig storageConfig;
}
