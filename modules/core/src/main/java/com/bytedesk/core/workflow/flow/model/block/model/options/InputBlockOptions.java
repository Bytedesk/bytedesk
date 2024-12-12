package com.bytedesk.core.workflow.flow.model.block.model.options;

import com.bytedesk.core.workflow.flow.model.block.model.BlockOptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class InputBlockOptions extends BlockOptions {
    private String type;  // text, number, email, url, date, phone, etc.
    private String placeholder;
    private String variableName;
    private boolean isLong;
    private String validation;
} 
