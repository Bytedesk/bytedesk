package com.bytedesk.core.calendar;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CalendarTools extends BaseTools<CalendarRequest, CalendarResponse> {

    public CalendarTools(CalendarRestService restService, ObjectMapper objectMapper) {
        super("calendar", CalendarRequest.class, restService, objectMapper);
    }

    @Tool(name = "calendar_query_by_uid", description = "Query calendar by uid. This tool returns structured data for AI tool invocation.")
    public Object calendarQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(name = "calendar_query_by_org", description = "Query calendar by org with request json. This tool returns structured data for AI tool invocation.")
    public Object calendarQueryByOrg(@ToolParam(description = "CalendarRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(name = "calendar_query_by_user", description = "Query calendar by user with request json. This tool returns structured data for AI tool invocation.")
    public Object calendarQueryByUser(@ToolParam(description = "CalendarRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(name = "calendar_create", description = "Create calendar with request json. This tool returns structured data for AI tool invocation.")
    public Object calendarCreate(@ToolParam(description = "CalendarRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(name = "calendar_update", description = "Update calendar with request json. This tool returns structured data for AI tool invocation.")
    public Object calendarUpdate(@ToolParam(description = "CalendarRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(name = "calendar_delete_by_uid", description = "Delete calendar by uid. This tool returns structured data for AI tool invocation.")
    public Object calendarDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
