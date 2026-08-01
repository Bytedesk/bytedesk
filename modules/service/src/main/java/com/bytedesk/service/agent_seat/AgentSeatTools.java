package com.bytedesk.service.agent_seat;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AgentSeatTools extends BaseTools<AgentSeatRequest, AgentSeatResponse> {

    public AgentSeatTools(AgentSeatRestService restService, ObjectMapper objectMapper) {
        super("agent_seat", AgentSeatRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query agent_seat by uid. This tool returns structured data for AI tool invocation.")
    public Object agent_seatQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query agent_seat by org with request json. This tool returns structured data for AI tool invocation.")
    public Object agent_seatQueryByOrg(@ToolParam(description = "AgentSeatRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query agent_seat by user with request json. This tool returns structured data for AI tool invocation.")
    public Object agent_seatQueryByUser(@ToolParam(description = "AgentSeatRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create agent_seat with request json. This tool returns structured data for AI tool invocation.")
    public Object agent_seatCreate(@ToolParam(description = "AgentSeatRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update agent_seat with request json. This tool returns structured data for AI tool invocation.")
    public Object agent_seatUpdate(@ToolParam(description = "AgentSeatRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete agent_seat by uid. This tool returns structured data for AI tool invocation.")
    public Object agent_seatDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
