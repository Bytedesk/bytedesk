package com.bytedesk.core.task_comment;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class TaskCommentTools extends BaseTools<TaskCommentRequest, TaskCommentResponse> {

    public TaskCommentTools(TaskCommentRestService restService, ObjectMapper objectMapper) {
        super("taskComment", TaskCommentRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query taskComment by uid")
    public Object taskCommentQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query taskComment by org with request json")
    public Object taskCommentQueryByOrg(@ToolParam(description = "TaskCommentRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query taskComment by user with request json")
    public Object taskCommentQueryByUser(@ToolParam(description = "TaskCommentRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create taskComment with request json")
    public Object taskCommentCreate(@ToolParam(description = "TaskCommentRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update taskComment with request json")
    public Object taskCommentUpdate(@ToolParam(description = "TaskCommentRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete taskComment by uid")
    public Object taskCommentDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
