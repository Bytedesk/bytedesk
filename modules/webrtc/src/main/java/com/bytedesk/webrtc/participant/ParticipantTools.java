package com.bytedesk.webrtc.participant;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ParticipantTools extends BaseTools<ParticipantRequest, ParticipantResponse> {

    public ParticipantTools(ParticipantRestService restService, ObjectMapper objectMapper) {
        super("participant", ParticipantRequest.class, restService, objectMapper);
    }

    @Tool(name = "participant_query_by_uid", description = "Query participant by uid. This tool returns structured data for AI tool invocation.")
    public Object participantQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(name = "participant_query_by_org", description = "Query participant by org with request json. This tool returns structured data for AI tool invocation.")
    public Object participantQueryByOrg(@ToolParam(description = "ParticipantRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(name = "participant_query_by_user", description = "Query participant by user with request json. This tool returns structured data for AI tool invocation.")
    public Object participantQueryByUser(@ToolParam(description = "ParticipantRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(name = "participant_create", description = "Create participant with request json. This tool returns structured data for AI tool invocation.")
    public Object participantCreate(@ToolParam(description = "ParticipantRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(name = "participant_update", description = "Update participant with request json. This tool returns structured data for AI tool invocation.")
    public Object participantUpdate(@ToolParam(description = "ParticipantRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(name = "participant_delete_by_uid", description = "Delete participant by uid. This tool returns structured data for AI tool invocation.")
    public Object participantDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
