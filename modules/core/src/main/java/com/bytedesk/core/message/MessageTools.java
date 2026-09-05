package com.bytedesk.core.message;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MessageTools extends BaseTools<MessageRequest, MessageResponse> {

    public MessageTools(MessageRestService restService, ObjectMapper objectMapper) {
        super("message", MessageRequest.class, restService, objectMapper);
    }

    @Tool(name = "message_query_by_uid", description = "Query message by uid. This tool returns structured data for AI tool invocation.")
    public Object messageQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(name = "message_query_by_org", description = "Query message by org with request json. This tool returns structured data for AI tool invocation.")
    public Object messageQueryByOrg(@ToolParam(description = "MessageRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(name = "message_query_by_user", description = "Query message by user with request json. This tool returns structured data for AI tool invocation.")
    public Object messageQueryByUser(@ToolParam(description = "MessageRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(name = "message_create", description = "Create message with request json. This tool returns structured data for AI tool invocation.")
    public Object messageCreate(@ToolParam(description = "MessageRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(name = "message_update", description = "Update message with request json. This tool returns structured data for AI tool invocation.")
    public Object messageUpdate(@ToolParam(description = "MessageRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(name = "message_delete_by_uid", description = "Delete message by uid. This tool returns structured data for AI tool invocation.")
    public Object messageDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
