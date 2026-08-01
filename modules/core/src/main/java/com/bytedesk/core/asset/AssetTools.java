package com.bytedesk.core.asset;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AssetTools extends BaseTools<AssetRequest, AssetResponse> {

    public AssetTools(AssetRestService restService, ObjectMapper objectMapper) {
        super("asset", AssetRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query asset by uid. This tool returns structured data for AI tool invocation.")
    public Object assetQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query asset by org with request json. This tool returns structured data for AI tool invocation.")
    public Object assetQueryByOrg(@ToolParam(description = "AssetRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query asset by user with request json. This tool returns structured data for AI tool invocation.")
    public Object assetQueryByUser(@ToolParam(description = "AssetRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create asset with request json. This tool returns structured data for AI tool invocation.")
    public Object assetCreate(@ToolParam(description = "AssetRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update asset with request json. This tool returns structured data for AI tool invocation.")
    public Object assetUpdate(@ToolParam(description = "AssetRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete asset by uid. This tool returns structured data for AI tool invocation.")
    public Object assetDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
