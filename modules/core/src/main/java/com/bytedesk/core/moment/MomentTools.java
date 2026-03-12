package com.bytedesk.core.moment;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MomentTools extends BaseTools<MomentRequest, MomentResponse> {

    public MomentTools(MomentRestService restService, ObjectMapper objectMapper) {
        super("moment", MomentRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query moment by uid")
    public Object momentQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query moment by org with request json")
    public Object momentQueryByOrg(@ToolParam(description = "MomentRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query moment by user with request json")
    public Object momentQueryByUser(@ToolParam(description = "MomentRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create moment with request json")
    public Object momentCreate(@ToolParam(description = "MomentRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update moment with request json")
    public Object momentUpdate(@ToolParam(description = "MomentRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete moment by uid")
    public Object momentDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
