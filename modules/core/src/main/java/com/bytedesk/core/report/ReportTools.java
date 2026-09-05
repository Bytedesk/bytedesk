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

    @Tool(name = "report_query_by_uid", description = "Query report by uid. This tool returns structured data for AI tool invocation.")
    public Object reportQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(name = "report_query_by_org", description = "Query report by org with request json. This tool returns structured data for AI tool invocation.")
    public Object reportQueryByOrg(@ToolParam(description = "ReportRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(name = "report_query_by_user", description = "Query report by user with request json. This tool returns structured data for AI tool invocation.")
    public Object reportQueryByUser(@ToolParam(description = "ReportRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(name = "report_create", description = "Create report with request json. This tool returns structured data for AI tool invocation.")
    public Object reportCreate(@ToolParam(description = "ReportRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(name = "report_update", description = "Update report with request json. This tool returns structured data for AI tool invocation.")
    public Object reportUpdate(@ToolParam(description = "ReportRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(name = "report_delete_by_uid", description = "Delete report by uid. This tool returns structured data for AI tool invocation.")
    public Object reportDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
