package com.bytedesk.core.tag;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class TagTools extends BaseTools<TagRequest, TagResponse> {

    public TagTools(TagRestService restService, ObjectMapper objectMapper) {
        super("tag", TagRequest.class, restService, objectMapper);
    }

    @Tool(name = "tag_query_by_uid", description = "Query tag by uid. This tool returns structured data for AI tool invocation.")
    public Object tagQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(name = "tag_query_by_org", description = "Query tag by org with request json. This tool returns structured data for AI tool invocation.")
    public Object tagQueryByOrg(@ToolParam(description = "TagRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(name = "tag_query_by_user", description = "Query tag by user with request json. This tool returns structured data for AI tool invocation.")
    public Object tagQueryByUser(@ToolParam(description = "TagRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(name = "tag_create", description = "Create tag with request json. This tool returns structured data for AI tool invocation.")
    public Object tagCreate(@ToolParam(description = "TagRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(name = "tag_update", description = "Update tag with request json. This tool returns structured data for AI tool invocation.")
    public Object tagUpdate(@ToolParam(description = "TagRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(name = "tag_delete_by_uid", description = "Delete tag by uid. This tool returns structured data for AI tool invocation.")
    public Object tagDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
