package com.bytedesk.core.member;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MemberTools extends BaseTools<MemberRequest, MemberResponse> {

    public MemberTools(MemberRestService restService, ObjectMapper objectMapper) {
        super("member", MemberRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query member by uid")
    public Object memberQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query member by org with request json")
    public Object memberQueryByOrg(@ToolParam(description = "MemberRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query member by user with request json")
    public Object memberQueryByUser(@ToolParam(description = "MemberRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create member with request json")
    public Object memberCreate(@ToolParam(description = "MemberRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update member with request json")
    public Object memberUpdate(@ToolParam(description = "MemberRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete member by uid")
    public Object memberDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
