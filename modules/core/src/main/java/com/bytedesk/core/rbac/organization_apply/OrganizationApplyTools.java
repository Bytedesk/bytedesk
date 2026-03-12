package com.bytedesk.core.rbac.organization_apply;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class OrganizationApplyTools extends BaseTools<OrganizationApplyRequest, OrganizationApplyResponse> {

    public OrganizationApplyTools(OrganizationApplyRestService restService, ObjectMapper objectMapper) {
        super("organizationApply", OrganizationApplyRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query organizationApply by uid")
    public Object organizationApplyQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query organizationApply by org with request json")
    public Object organizationApplyQueryByOrg(@ToolParam(description = "OrganizationApplyRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query organizationApply by user with request json")
    public Object organizationApplyQueryByUser(@ToolParam(description = "OrganizationApplyRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create organizationApply with request json")
    public Object organizationApplyCreate(@ToolParam(description = "OrganizationApplyRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update organizationApply with request json")
    public Object organizationApplyUpdate(@ToolParam(description = "OrganizationApplyRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete organizationApply by uid")
    public Object organizationApplyDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
