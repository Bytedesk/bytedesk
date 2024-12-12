package com.bytedesk.core.workflow.flow.model;

import lombok.Data;

@Data
public class Event {
    private String id;
    private String outgoingEdgeId;
    private Coordinates graphCoordinates;
    private String type;
}

@Data
class Coordinates {
    private int x;
    private int y;
} 