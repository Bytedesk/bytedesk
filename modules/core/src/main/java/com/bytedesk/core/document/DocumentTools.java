package com.bytedesk.core.document;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class DocumentTools extends BaseTools<DocumentRequest, DocumentResponse> {

    public DocumentTools(DocumentRestService restService, ObjectMapper objectMapper) {
        super("document", DocumentRequest.class, restService, objectMapper);
    }

    @Tool(name = "document_query_by_uid", description = "Query document by uid. This tool returns structured data for AI tool invocation.")
    public Object documentQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(name = "document_query_by_org", description = "Query document by org with request json. This tool returns structured data for AI tool invocation.")
    public Object documentQueryByOrg(@ToolParam(description = "DocumentRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(name = "document_query_by_user", description = "Query document by user with request json. This tool returns structured data for AI tool invocation.")
    public Object documentQueryByUser(@ToolParam(description = "DocumentRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(name = "document_create", description = "Create document with request json. This tool returns structured data for AI tool invocation.")
    public Object documentCreate(@ToolParam(description = "DocumentRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(name = "document_update", description = "Update document with request json. This tool returns structured data for AI tool invocation.")
    public Object documentUpdate(@ToolParam(description = "DocumentRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(name = "document_delete_by_uid", description = "Delete document by uid. This tool returns structured data for AI tool invocation.")
    public Object documentDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
