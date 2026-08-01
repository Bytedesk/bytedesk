package com.bytedesk.webrtc.webrtc_settings;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class WebrtcSettingsTools extends BaseTools<WebrtcSettingsRequest, WebrtcSettingsResponse> {

    public WebrtcSettingsTools(WebrtcSettingsRestService restService, ObjectMapper objectMapper) {
        super("webrtc_settings", WebrtcSettingsRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query webrtc_settings by uid. This tool returns structured data for AI tool invocation.")
    public Object webrtc_settingsQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query webrtc_settings by org with request json")
    public Object webrtc_settingsQueryByOrg(@ToolParam(description = "WebrtcSettingsRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query webrtc_settings by user with request json")
    public Object webrtc_settingsQueryByUser(@ToolParam(description = "WebrtcSettingsRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create webrtc_settings with request json. This tool returns structured data for AI tool invocation.")
    public Object webrtc_settingsCreate(@ToolParam(description = "WebrtcSettingsRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update webrtc_settings with request json. This tool returns structured data for AI tool invocation.")
    public Object webrtc_settingsUpdate(@ToolParam(description = "WebrtcSettingsRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete webrtc_settings by uid. This tool returns structured data for AI tool invocation.")
    public Object webrtc_settingsDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
