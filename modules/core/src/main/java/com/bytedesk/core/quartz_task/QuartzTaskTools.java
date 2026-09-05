package com.bytedesk.core.quartz_task;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class QuartzTaskTools extends BaseTools<QuartzTaskRequest, QuartzTaskResponse> {

    public QuartzTaskTools(QuartzTaskRestService restService, ObjectMapper objectMapper) {
        super("quartz_task", QuartzTaskRequest.class, restService, objectMapper);
    }

    @Tool(name = "quartz_task_query_by_uid", description = "Query quartz_task by uid. This tool returns structured data for AI tool invocation.")
    public Object quartzTaskQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(name = "quartz_task_query_by_org", description = "Query quartz_task by org with request json. This tool returns structured data for AI tool invocation.")
    public Object quartzTaskQueryByOrg(@ToolParam(description = "QuartzTaskRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(name = "quartz_task_query_by_user", description = "Query quartz_task by user with request json. This tool returns structured data for AI tool invocation.")
    public Object quartzTaskQueryByUser(@ToolParam(description = "QuartzTaskRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(name = "quartz_task_create", description = "Create quartz_task with request json. This tool returns structured data for AI tool invocation.")
    public Object quartzTaskCreate(@ToolParam(description = "QuartzTaskRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(name = "quartz_task_update", description = "Update quartz_task with request json. This tool returns structured data for AI tool invocation.")
    public Object quartzTaskUpdate(@ToolParam(description = "QuartzTaskRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(name = "quartz_task_delete_by_uid", description = "Delete quartz_task by uid. This tool returns structured data for AI tool invocation.")
    public Object quartzTaskDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
