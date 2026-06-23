package com.bytedesk.ticket.ticket_comment;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class TicketCommentTools extends BaseTools<TicketCommentRequest, TicketCommentResponse> {

    public TicketCommentTools(TicketCommentRestService restService, ObjectMapper objectMapper) {
        super("ticket_comment", TicketCommentRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query ticket_comment by uid. This tool returns structured data for AI tool invocation.")
    public Object ticket_commentQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query ticket_comment by org with request json. This tool returns structured data for AI tool invocation.")
    public Object ticket_commentQueryByOrg(@ToolParam(description = "TicketCommentRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query ticket_comment by user with request json. This tool returns structured data for AI tool invocation.")
    public Object ticket_commentQueryByUser(@ToolParam(description = "TicketCommentRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create ticket_comment with request json. This tool returns structured data for AI tool invocation.")
    public Object ticket_commentCreate(@ToolParam(description = "TicketCommentRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update ticket_comment with request json. This tool returns structured data for AI tool invocation.")
    public Object ticket_commentUpdate(@ToolParam(description = "TicketCommentRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete ticket_comment by uid. This tool returns structured data for AI tool invocation.")
    public Object ticket_commentDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
