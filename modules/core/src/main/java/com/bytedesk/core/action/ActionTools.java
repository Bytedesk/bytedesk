package com.bytedesk.core.action;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ActionTools extends BaseTools<ActionRequest, ActionResponse> {

    public ActionTools(ActionRestService restService, ObjectMapper objectMapper) {
        super("action", ActionRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query action by uid")
    public Object actionQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query action by org with request json")
    public Object actionQueryByOrg(@ToolParam(description = "ActionRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query action by user with request json")
    public Object actionQueryByUser(@ToolParam(description = "ActionRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create action with request json")
    public Object actionCreate(@ToolParam(description = "ActionRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update action with request json")
    public Object actionUpdate(@ToolParam(description = "ActionRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete action by uid")
    public Object actionDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
