package com.bytedesk.core.menu;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MenuTools extends BaseTools<MenuRequest, MenuResponse> {

    public MenuTools(MenuRestService restService, ObjectMapper objectMapper) {
        super("menu", MenuRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query menu by uid")
    public Object menuQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query menu by org with request json")
    public Object menuQueryByOrg(@ToolParam(description = "MenuRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query menu by user with request json")
    public Object menuQueryByUser(@ToolParam(description = "MenuRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create menu with request json")
    public Object menuCreate(@ToolParam(description = "MenuRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update menu with request json")
    public Object menuUpdate(@ToolParam(description = "MenuRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete menu by uid")
    public Object menuDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
