package com.bytedesk.core.workflow.flow.model.block.model.options;

import lombok.Data;

@Data
public class ImageSize {
    private Integer width;
    private Integer height;
    private String unit;  // px, %, em, etc.
} 
