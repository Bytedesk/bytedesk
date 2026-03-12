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

    @Tool(description = "Query message by uid")
    public Object messageQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query message by org with request json")
    public Object messageQueryByOrg(@ToolParam(description = "MessageRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query message by user with request json")
    public Object messageQueryByUser(@ToolParam(description = "MessageRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create message with request json")
    public Object messageCreate(@ToolParam(description = "MessageRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update message with request json")
    public Object messageUpdate(@ToolParam(description = "MessageRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete message by uid")
    public Object messageDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
