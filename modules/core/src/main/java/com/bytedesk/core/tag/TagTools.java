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

    @Tool(description = "Query tag by uid")
    public Object tagQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query tag by org with request json")
    public Object tagQueryByOrg(@ToolParam(description = "TagRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query tag by user with request json")
    public Object tagQueryByUser(@ToolParam(description = "TagRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create tag with request json")
    public Object tagCreate(@ToolParam(description = "TagRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update tag with request json")
    public Object tagUpdate(@ToolParam(description = "TagRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete tag by uid")
    public Object tagDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
