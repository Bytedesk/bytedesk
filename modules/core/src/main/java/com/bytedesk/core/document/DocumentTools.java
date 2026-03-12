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

    @Tool(description = "Query document by uid")
    public Object documentQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query document by org with request json")
    public Object documentQueryByOrg(@ToolParam(description = "DocumentRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query document by user with request json")
    public Object documentQueryByUser(@ToolParam(description = "DocumentRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create document with request json")
    public Object documentCreate(@ToolParam(description = "DocumentRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update document with request json")
    public Object documentUpdate(@ToolParam(description = "DocumentRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete document by uid")
    public Object documentDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
