/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.xml_curl_trace;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.call.config.CallI18nConsts;
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/xml_curl_trace")
@AllArgsConstructor
@Tag(name = "XmlCurlTrace Management", description = "XmlCurlTrace management APIs for organizing and categorizing content with xml_curl_traces")
@Description("XmlCurlTrace Management Controller - Content xml_curl_traceging and categorization APIs")
public class XmlCurlTraceRestController extends BaseRestController<XmlCurlTraceRequest, XmlCurlTraceRestService> {

    private final XmlCurlTraceRestService xml_curl_traceRestService;

    @ActionAnnotation(title = CallI18nConsts.I18N_XML_CURL_TRACE, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query xml_curl_trace by org")
    @Operation(summary = "Query XmlCurlTraces by Organization", description = "Retrieve xml_curl_traces for the current organization")
    @PreAuthorize(XmlCurlTracePermissions.HAS_XML_CURL_TRACE_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(XmlCurlTraceRequest request) {
        
        Page<XmlCurlTraceResponse> xml_curl_traces = xml_curl_traceRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(xml_curl_traces));
    }

    @ActionAnnotation(title = CallI18nConsts.I18N_XML_CURL_TRACE, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query xml_curl_trace by user")
    @Operation(summary = "Query XmlCurlTraces by User", description = "Retrieve xml_curl_traces for the current user")
    @PreAuthorize(XmlCurlTracePermissions.HAS_XML_CURL_TRACE_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(XmlCurlTraceRequest request) {
        
        Page<XmlCurlTraceResponse> xml_curl_traces = xml_curl_traceRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(xml_curl_traces));
    }

    @ActionAnnotation(title = CallI18nConsts.I18N_XML_CURL_TRACE, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query xml_curl_trace by uid")
    @Operation(summary = "Query XmlCurlTrace by UID", description = "Retrieve a specific xml_curl_trace by its unique identifier")
    @PreAuthorize(XmlCurlTracePermissions.HAS_XML_CURL_TRACE_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(XmlCurlTraceRequest request) {
        
        XmlCurlTraceResponse xml_curl_trace = xml_curl_traceRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(xml_curl_trace));
    }

    @ActionAnnotation(title = CallI18nConsts.I18N_XML_CURL_TRACE, action = I18Consts.I18N_ACTION_CREATE, description = "create xml_curl_trace")
    @Operation(summary = "Create XmlCurlTrace", description = "Create a new xml_curl_trace")
    @Override
    @PreAuthorize(XmlCurlTracePermissions.HAS_XML_CURL_TRACE_CREATE)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody XmlCurlTraceRequest request) {
        
        XmlCurlTraceResponse xml_curl_trace = xml_curl_traceRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(xml_curl_trace));
    }

    @ActionAnnotation(title = CallI18nConsts.I18N_XML_CURL_TRACE, action = I18Consts.I18N_ACTION_UPDATE, description = "update xml_curl_trace")
    @Operation(summary = "Update XmlCurlTrace", description = "Update an existing xml_curl_trace")
    @Override
    @PreAuthorize(XmlCurlTracePermissions.HAS_XML_CURL_TRACE_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody XmlCurlTraceRequest request) {
        
        XmlCurlTraceResponse xml_curl_trace = xml_curl_traceRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(xml_curl_trace));
    }

    @ActionAnnotation(title = CallI18nConsts.I18N_XML_CURL_TRACE, action = I18Consts.I18N_ACTION_DELETE, description = "delete xml_curl_trace")
    @Operation(summary = "Delete XmlCurlTrace", description = "Delete a xml_curl_trace")
    @Override
    @PreAuthorize(XmlCurlTracePermissions.HAS_XML_CURL_TRACE_DELETE)
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody XmlCurlTraceRequest request) {
        
        xml_curl_traceRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = CallI18nConsts.I18N_XML_CURL_TRACE, action = I18Consts.I18N_ACTION_EXPORT, description = "export xml_curl_trace")
    @Operation(summary = "Export XmlCurlTraces", description = "Export xml_curl_traces to Excel format")
    @Override
    @PreAuthorize(XmlCurlTracePermissions.HAS_XML_CURL_TRACE_EXPORT)
    @GetMapping("/export")
    public Object export(XmlCurlTraceRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            xml_curl_traceRestService,
            XmlCurlTraceExcel.class,
            "XmlCurlTrace",
            "xml_curl_trace"
        );
    }

    
    
}