package com.bytedesk.core.socket.connection;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ConnectionTools extends BaseTools<ConnectionRequest, ConnectionResponse> {

    public ConnectionTools(ConnectionRestService restService, ObjectMapper objectMapper) {
        super("connection", ConnectionRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query connection by uid. This tool returns structured data for AI tool invocation.")
    public Object connectionQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query connection by org with request json. This tool returns structured data for AI tool invocation.")
    public Object connectionQueryByOrg(@ToolParam(description = "ConnectionRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query connection by user with request json. This tool returns structured data for AI tool invocation.")
    public Object connectionQueryByUser(@ToolParam(description = "ConnectionRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create connection with request json. This tool returns structured data for AI tool invocation.")
    public Object connectionCreate(@ToolParam(description = "ConnectionRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update connection with request json. This tool returns structured data for AI tool invocation.")
    public Object connectionUpdate(@ToolParam(description = "ConnectionRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete connection by uid. This tool returns structured data for AI tool invocation.")
    public Object connectionDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
