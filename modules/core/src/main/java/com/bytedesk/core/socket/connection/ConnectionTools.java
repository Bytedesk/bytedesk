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

    @Tool(description = "Query connection by uid")
    public Object connectionQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query connection by org with request json")
    public Object connectionQueryByOrg(@ToolParam(description = "ConnectionRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query connection by user with request json")
    public Object connectionQueryByUser(@ToolParam(description = "ConnectionRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create connection with request json")
    public Object connectionCreate(@ToolParam(description = "ConnectionRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update connection with request json")
    public Object connectionUpdate(@ToolParam(description = "ConnectionRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete connection by uid")
    public Object connectionDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
