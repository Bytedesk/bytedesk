package com.bytedesk.call.xml_curl_trace;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class XmlCurlTraceTools extends BaseTools<XmlCurlTraceRequest, XmlCurlTraceResponse> {

    public XmlCurlTraceTools(XmlCurlTraceRestService restService, ObjectMapper objectMapper) {
        super("xml_curl_trace", XmlCurlTraceRequest.class, restService, objectMapper);
    }

    @Tool(name = "xml_curl_trace_query_by_uid", description = "Query xml_curl_trace by uid. This tool returns structured data for AI tool invocation.")
    public Object xml_curl_traceQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(name = "xml_curl_trace_query_by_org", description = "Query xml_curl_trace by org with request json. This tool returns structured data for AI tool invocation.")
    public Object xml_curl_traceQueryByOrg(@ToolParam(description = "XmlCurlTraceRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(name = "xml_curl_trace_query_by_user", description = "Query xml_curl_trace by user with request json. This tool returns structured data for AI tool invocation.")
    public Object xml_curl_traceQueryByUser(@ToolParam(description = "XmlCurlTraceRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(name = "xml_curl_trace_create", description = "Create xml_curl_trace with request json. This tool returns structured data for AI tool invocation.")
    public Object xml_curl_traceCreate(@ToolParam(description = "XmlCurlTraceRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(name = "xml_curl_trace_update", description = "Update xml_curl_trace with request json. This tool returns structured data for AI tool invocation.")
    public Object xml_curl_traceUpdate(@ToolParam(description = "XmlCurlTraceRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(name = "xml_curl_trace_delete_by_uid", description = "Delete xml_curl_trace by uid. This tool returns structured data for AI tool invocation.")
    public Object xml_curl_traceDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
