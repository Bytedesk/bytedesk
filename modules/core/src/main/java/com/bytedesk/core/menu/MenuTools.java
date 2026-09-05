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

    @Tool(name = "menu_query_by_uid", description = "Query menu by uid. This tool returns structured data for AI tool invocation.")
    public Object menuQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(name = "menu_query_by_org", description = "Query menu by org with request json. This tool returns structured data for AI tool invocation.")
    public Object menuQueryByOrg(@ToolParam(description = "MenuRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(name = "menu_query_by_user", description = "Query menu by user with request json. This tool returns structured data for AI tool invocation.")
    public Object menuQueryByUser(@ToolParam(description = "MenuRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(name = "menu_create", description = "Create menu with request json. This tool returns structured data for AI tool invocation.")
    public Object menuCreate(@ToolParam(description = "MenuRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(name = "menu_update", description = "Update menu with request json. This tool returns structured data for AI tool invocation.")
    public Object menuUpdate(@ToolParam(description = "MenuRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(name = "menu_delete_by_uid", description = "Delete menu by uid. This tool returns structured data for AI tool invocation.")
    public Object menuDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
