package com.bytedesk.core.quartz_task;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class QuartzTaskTools extends BaseTools<QuartzTaskRequest, QuartzTaskResponse> {

    public QuartzTaskTools(QuartzTaskRestService restService, ObjectMapper objectMapper) {
        super("quartzTask", QuartzTaskRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query quartzTask by uid")
    public Object quartzTaskQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query quartzTask by org with request json")
    public Object quartzTaskQueryByOrg(@ToolParam(description = "QuartzTaskRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query quartzTask by user with request json")
    public Object quartzTaskQueryByUser(@ToolParam(description = "QuartzTaskRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create quartzTask with request json")
    public Object quartzTaskCreate(@ToolParam(description = "QuartzTaskRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update quartzTask with request json")
    public Object quartzTaskUpdate(@ToolParam(description = "QuartzTaskRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete quartzTask by uid")
    public Object quartzTaskDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
