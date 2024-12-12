package com.bytedesk.core.workflow.flow.model.block.model;

import lombok.Data;

@Data
public class Block {

    private String id;
    
    private String groupId;
    
    private BlockType type;
    
    private Object options;
    
    private Integer order;
    
    private String botId;

    private Object content;
}
