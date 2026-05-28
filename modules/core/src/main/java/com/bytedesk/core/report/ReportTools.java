package com.bytedesk.core.report;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ReportTools extends BaseTools<ReportRequest, ReportResponse> {

    public ReportTools(ReportRestService restService, ObjectMapper objectMapper) {
        super("report", ReportRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query report by uid")
    public Object reportQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query report by org with request json")
    public Object reportQueryByOrg(@ToolParam(description = "ReportRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query report by user with request json")
    public Object reportQueryByUser(@ToolParam(description = "ReportRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create report with request json")
    public Object reportCreate(@ToolParam(description = "ReportRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update report with request json")
    public Object reportUpdate(@ToolParam(description = "ReportRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete report by uid")
    public Object reportDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
