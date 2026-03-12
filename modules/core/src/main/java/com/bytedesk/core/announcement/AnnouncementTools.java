package com.bytedesk.core.announcement;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AnnouncementTools extends BaseTools<AnnouncementRequest, AnnouncementResponse> {

    public AnnouncementTools(AnnouncementRestService restService, ObjectMapper objectMapper) {
        super("announcement", AnnouncementRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query announcement by uid")
    public Object announcementQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query announcement by org with request json")
    public Object announcementQueryByOrg(@ToolParam(description = "AnnouncementRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query announcement by user with request json")
    public Object announcementQueryByUser(@ToolParam(description = "AnnouncementRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create announcement with request json")
    public Object announcementCreate(@ToolParam(description = "AnnouncementRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update announcement with request json")
    public Object announcementUpdate(@ToolParam(description = "AnnouncementRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete announcement by uid")
    public Object announcementDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
