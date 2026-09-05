package com.bytedesk.core.organization_apply;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class OrganizationApplyTools extends BaseTools<OrganizationApplyRequest, OrganizationApplyResponse> {

    public OrganizationApplyTools(OrganizationApplyRestService restService, ObjectMapper objectMapper) {
        super("organization_apply", OrganizationApplyRequest.class, restService, objectMapper);
    }

    @Tool(name = "organization_apply_query_by_uid", description = "Query organization_apply by uid. This tool returns structured data for AI tool invocation.")
    public Object organizationApplyQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(name = "organization_apply_query_by_org", description = "Query organization_apply by org with request json")
    public Object organizationApplyQueryByOrg(@ToolParam(description = "OrganizationApplyRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(name = "organization_apply_query_by_user", description = "Query organization_apply by user with request json")
    public Object organizationApplyQueryByUser(@ToolParam(description = "OrganizationApplyRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(name = "organization_apply_create", description = "Create organization_apply with request json. This tool returns structured data for AI tool invocation.")
    public Object organizationApplyCreate(@ToolParam(description = "OrganizationApplyRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(name = "organization_apply_update", description = "Update organization_apply with request json. This tool returns structured data for AI tool invocation.")
    public Object organizationApplyUpdate(@ToolParam(description = "OrganizationApplyRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(name = "organization_apply_delete_by_uid", description = "Delete organization_apply by uid. This tool returns structured data for AI tool invocation.")
    public Object organizationApplyDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
