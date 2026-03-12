package com.bytedesk.core.thread;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ThreadTools extends BaseTools<ThreadRequest, ThreadResponse> {

    public ThreadTools(ThreadRestService restService, ObjectMapper objectMapper) {
        super("thread", ThreadRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query thread by uid")
    public Object threadQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query thread by org with request json")
    public Object threadQueryByOrg(@ToolParam(description = "ThreadRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query thread by user with request json")
    public Object threadQueryByUser(@ToolParam(description = "ThreadRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create thread with request json")
    public Object threadCreate(@ToolParam(description = "ThreadRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update thread with request json")
    public Object threadUpdate(@ToolParam(description = "ThreadRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete thread by uid")
    public Object threadDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
