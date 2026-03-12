package com.bytedesk.core.relation;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RelationTools extends BaseTools<RelationRequest, RelationResponse> {

    public RelationTools(RelationRestService restService, ObjectMapper objectMapper) {
        super("relation", RelationRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query relation by uid")
    public Object relationQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query relation by org with request json")
    public Object relationQueryByOrg(@ToolParam(description = "RelationRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query relation by user with request json")
    public Object relationQueryByUser(@ToolParam(description = "RelationRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create relation with request json")
    public Object relationCreate(@ToolParam(description = "RelationRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update relation with request json")
    public Object relationUpdate(@ToolParam(description = "RelationRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete relation by uid")
    public Object relationDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
