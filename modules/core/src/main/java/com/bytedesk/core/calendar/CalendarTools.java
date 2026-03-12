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

    @Tool(description = "Query calendar by uid")
    public Object calendarQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query calendar by org with request json")
    public Object calendarQueryByOrg(@ToolParam(description = "CalendarRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query calendar by user with request json")
    public Object calendarQueryByUser(@ToolParam(description = "CalendarRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create calendar with request json")
    public Object calendarCreate(@ToolParam(description = "CalendarRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update calendar with request json")
    public Object calendarUpdate(@ToolParam(description = "CalendarRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete calendar by uid")
    public Object calendarDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
