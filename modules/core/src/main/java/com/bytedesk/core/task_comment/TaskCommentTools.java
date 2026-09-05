package com.bytedesk.core.task_comment;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class TaskCommentTools extends BaseTools<TaskCommentRequest, TaskCommentResponse> {

    public TaskCommentTools(TaskCommentRestService restService, ObjectMapper objectMapper) {
        super("task_comment", TaskCommentRequest.class, restService, objectMapper);
    }

    @Tool(name = "task_comment_query_by_uid", description = "Query task_comment by uid. This tool returns structured data for AI tool invocation.")
    public Object taskCommentQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(name = "task_comment_query_by_org", description = "Query task_comment by org with request json. This tool returns structured data for AI tool invocation.")
    public Object taskCommentQueryByOrg(@ToolParam(description = "TaskCommentRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(name = "task_comment_query_by_user", description = "Query task_comment by user with request json. This tool returns structured data for AI tool invocation.")
    public Object taskCommentQueryByUser(@ToolParam(description = "TaskCommentRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(name = "task_comment_create", description = "Create task_comment with request json. This tool returns structured data for AI tool invocation.")
    public Object taskCommentCreate(@ToolParam(description = "TaskCommentRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(name = "task_comment_update", description = "Update task_comment with request json. This tool returns structured data for AI tool invocation.")
    public Object taskCommentUpdate(@ToolParam(description = "TaskCommentRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(name = "task_comment_delete_by_uid", description = "Delete task_comment by uid. This tool returns structured data for AI tool invocation.")
    public Object taskCommentDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
