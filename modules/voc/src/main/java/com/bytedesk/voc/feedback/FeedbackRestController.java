/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-15 13:49:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.voc.feedback;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/feedback")
@AllArgsConstructor
@Tag(name = "Feedback Management", description = "Feedback management APIs for organizing and categorizing content with feedbacks")
@Description("Feedback Management Controller - Content tag and categorization APIs")
public class FeedbackRestController extends BaseRestController<FeedbackRequest, FeedbackRestService> {

    private final FeedbackRestService feedbackRestService;

    @ActionAnnotation(title = I18Consts.I18N_FEEDBACK, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query feedback by org")
    @Operation(summary = "Query Feedbacks by Organization", description = "Retrieve feedbacks for the current organization")
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(FeedbackRequest request) {
        
        Page<FeedbackResponse> feedbacks = feedbackRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(feedbacks));
    }

    @ActionAnnotation(title = I18Consts.I18N_FEEDBACK, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query feedback by user")
    @Operation(summary = "Query Feedbacks by User", description = "Retrieve feedbacks for the current user")
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(FeedbackRequest request) {
        
        Page<FeedbackResponse> feedbacks = feedbackRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(feedbacks));
    }

    @ActionAnnotation(title = I18Consts.I18N_FEEDBACK, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query feedback by uid")
    @Operation(summary = "Query Feedback by UID", description = "Retrieve a specific feedback by its unique identifier")
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(FeedbackRequest request) {
        
        FeedbackResponse feedback = feedbackRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(feedback));
    }

    @ActionAnnotation(title = I18Consts.I18N_FEEDBACK, action = I18Consts.I18N_ACTION_CREATE, description = "create feedback")
    @Operation(summary = "Create Feedback", description = "Create a new feedback")
    @Override
    // @PreAuthorize("hasAuthority('TAG_CREATE')")
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody FeedbackRequest request) {
        
        FeedbackResponse feedback = feedbackRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(feedback));
    }

    @ActionAnnotation(title = I18Consts.I18N_FEEDBACK, action = I18Consts.I18N_ACTION_UPDATE, description = "update feedback")
    @Operation(summary = "Update Feedback", description = "Update an existing feedback")
    @Override
    // @PreAuthorize("hasAuthority('TAG_UPDATE')")
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody FeedbackRequest request) {
        
        FeedbackResponse feedback = feedbackRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(feedback));
    }

    @ActionAnnotation(title = I18Consts.I18N_FEEDBACK, action = I18Consts.I18N_ACTION_DELETE, description = "delete feedback")
    @Operation(summary = "Delete Feedback", description = "Delete a feedback")
    @Override
    // @PreAuthorize("hasAuthority('TAG_DELETE')")
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody FeedbackRequest request) {
        
        feedbackRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_FEEDBACK, action = I18Consts.I18N_ACTION_EXPORT, description = "export feedback")
    @Operation(summary = "Export Feedbacks", description = "Export feedbacks to Excel format")
    @Override
    // @PreAuthorize("hasAuthority('TAG_EXPORT')")
    @GetMapping("/export")
    public Object export(FeedbackRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            feedbackRestService,
            FeedbackExcel.class,
            "feedback",
            "feedback"
        );
    }

    
    
}