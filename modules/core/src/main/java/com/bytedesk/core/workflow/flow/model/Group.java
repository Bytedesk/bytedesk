package com.bytedesk.core.workflow.flow.model;

import lombok.Data;
import java.util.List;

import com.bytedesk.core.workflow.flow.model.block.model.Block;

@Data
public class Group {
    private String id;
    private String title;
    private Coordinates graphCoordinates;
    private List<Block> blocks;
} 