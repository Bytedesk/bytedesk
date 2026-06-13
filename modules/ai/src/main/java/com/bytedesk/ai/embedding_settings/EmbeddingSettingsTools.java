package com.bytedesk.ai.embedding_settings;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class EmbeddingSettingsTools extends BaseTools<EmbeddingSettingsRequest, EmbeddingSettingsResponse> {

    public EmbeddingSettingsTools(EmbeddingSettingsRestService restService, ObjectMapper objectMapper) {
        super("embedding_settings", EmbeddingSettingsRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query embedding_settings by uid. This tool returns structured data for AI tool invocation.")
    public Object embedding_settingsQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query embedding_settings by org with request json. This tool returns structured data for AI tool invocation.")
    public Object embedding_settingsQueryByOrg(@ToolParam(description = "EmbeddingSettingsRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query embedding_settings by user with request json. This tool returns structured data for AI tool invocation.")
    public Object embedding_settingsQueryByUser(@ToolParam(description = "EmbeddingSettingsRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create embedding_settings with request json. This tool returns structured data for AI tool invocation.")
    public Object embedding_settingsCreate(@ToolParam(description = "EmbeddingSettingsRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update embedding_settings with request json. This tool returns structured data for AI tool invocation.")
    public Object embedding_settingsUpdate(@ToolParam(description = "EmbeddingSettingsRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete embedding_settings by uid. This tool returns structured data for AI tool invocation.")
    public Object embedding_settingsDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
