package com.bytedesk.core.workflow.flow.model;

import lombok.Data;

@Data
public class Edge {
    private String id;
    private EdgeConnection from;
    private EdgeConnection to;
}

@Data
class EdgeConnection {
    private String eventId;
    private String blockId;
    private String groupId;
} 