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

    @Tool(description = "Query user by uid")
    public Object userQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query user by org with request json")
    public Object userQueryByOrg(@ToolParam(description = "UserRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query user by user with request json")
    public Object userQueryByUser(@ToolParam(description = "UserRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create user with request json")
    public Object userCreate(@ToolParam(description = "UserRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update user with request json")
    public Object userUpdate(@ToolParam(description = "UserRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete user by uid")
    public Object userDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
