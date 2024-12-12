package com.bytedesk.core.workflow.flow.model.block.model.options;

import com.bytedesk.core.workflow.flow.model.block.model.BlockOptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TextBlockOptions extends BlockOptions {
    private String content;
    private String richText;
    private boolean isLongText;
} 
