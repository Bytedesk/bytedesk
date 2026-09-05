package com.bytedesk.core.rbac.user;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class UserTools extends BaseTools<UserRequest, UserResponse> {

    public UserTools(UserRestService restService, ObjectMapper objectMapper) {
        super("user", UserRequest.class, restService, objectMapper);
    }

    @Tool(name = "user_query_by_uid", description = "Query user by uid. This tool returns structured data for AI tool invocation.")
    public Object userQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(name = "user_query_by_org", description = "Query user by org with request json. This tool returns structured data for AI tool invocation.")
    public Object userQueryByOrg(@ToolParam(description = "UserRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(name = "user_query_by_user", description = "Query user by user with request json. This tool returns structured data for AI tool invocation.")
    public Object userQueryByUser(@ToolParam(description = "UserRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(name = "user_create", description = "Create user with request json. This tool returns structured data for AI tool invocation.")
    public Object userCreate(@ToolParam(description = "UserRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(name = "user_update", description = "Update user with request json. This tool returns structured data for AI tool invocation.")
    public Object userUpdate(@ToolParam(description = "UserRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(name = "user_delete_by_uid", description = "Delete user by uid. This tool returns structured data for AI tool invocation.")
    public Object userDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
