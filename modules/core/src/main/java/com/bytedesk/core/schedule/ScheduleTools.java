package com.bytedesk.core.schedule;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ScheduleTools extends BaseTools<ScheduleRequest, ScheduleResponse> {

    public ScheduleTools(ScheduleRestService restService, ObjectMapper objectMapper) {
        super("schedule", ScheduleRequest.class, restService, objectMapper);
    }

    @Tool(name = "schedule_query_by_uid", description = "Query schedule by uid. This tool returns structured data for AI tool invocation.")
    public Object scheduleQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(name = "schedule_query_by_org", description = "Query schedule by org with request json. This tool returns structured data for AI tool invocation.")
    public Object scheduleQueryByOrg(@ToolParam(description = "ScheduleRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(name = "schedule_query_by_user", description = "Query schedule by user with request json. This tool returns structured data for AI tool invocation.")
    public Object scheduleQueryByUser(@ToolParam(description = "ScheduleRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(name = "schedule_create", description = "Create schedule with request json. This tool returns structured data for AI tool invocation.")
    public Object scheduleCreate(@ToolParam(description = "ScheduleRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(name = "schedule_update", description = "Update schedule with request json. This tool returns structured data for AI tool invocation.")
    public Object scheduleUpdate(@ToolParam(description = "ScheduleRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(name = "schedule_delete_by_uid", description = "Delete schedule by uid. This tool returns structured data for AI tool invocation.")
    public Object scheduleDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
