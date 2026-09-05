package com.bytedesk.kbase.llm_embedding;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class LlmEmbeddingTools extends BaseTools<LlmEmbeddingRequest, LlmEmbeddingResponse> {

    public LlmEmbeddingTools(LlmEmbeddingRestService restService, ObjectMapper objectMapper) {
        super("llm_embedding", LlmEmbeddingRequest.class, restService, objectMapper);
    }

    @Tool(name = "llm_embedding_query_by_uid", description = "Query llm_embedding by uid. This tool returns structured data for AI tool invocation.")
    public Object llm_embeddingQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(name = "llm_embedding_query_by_org", description = "Query llm_embedding by org with request json. This tool returns structured data for AI tool invocation.")
    public Object llm_embeddingQueryByOrg(@ToolParam(description = "LlmEmbeddingRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(name = "llm_embedding_query_by_user", description = "Query llm_embedding by user with request json. This tool returns structured data for AI tool invocation.")
    public Object llm_embeddingQueryByUser(@ToolParam(description = "LlmEmbeddingRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(name = "llm_embedding_create", description = "Create llm_embedding with request json. This tool returns structured data for AI tool invocation.")
    public Object llm_embeddingCreate(@ToolParam(description = "LlmEmbeddingRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(name = "llm_embedding_update", description = "Update llm_embedding with request json. This tool returns structured data for AI tool invocation.")
    public Object llm_embeddingUpdate(@ToolParam(description = "LlmEmbeddingRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(name = "llm_embedding_delete_by_uid", description = "Delete llm_embedding by uid. This tool returns structured data for AI tool invocation.")
    public Object llm_embeddingDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
