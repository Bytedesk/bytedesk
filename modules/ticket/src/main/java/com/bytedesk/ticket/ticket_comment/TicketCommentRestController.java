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
package com.bytedesk.ticket.ticket_comment;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/v1/ticket_comment")
@AllArgsConstructor
@Tag(name = "TicketComment Management", description = "TicketComment management APIs for organizing and categorizing content with ticket_comments")
@Description("TicketComment Management Controller - Content ticket_commentging and categorization APIs")
public class TicketCommentRestController extends BaseRestController<TicketCommentRequest, TicketCommentRestService> {

    private final TicketCommentRestService ticket_commentRestService;

    @ActionAnnotation(title = I18Consts.I18N_TICKET_COMMENT, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query ticket_comment by org")
    @Operation(summary = "Query TicketComments by Organization", description = "Retrieve ticket_comments for the current organization")
    @PreAuthorize(TicketCommentPermissions.HAS_TICKET_COMMENT_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(TicketCommentRequest request) {
        
        Page<TicketCommentResponse> ticket_comments = ticket_commentRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(ticket_comments));
    }

    @ActionAnnotation(title = I18Consts.I18N_TICKET_COMMENT, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query ticket_comment by user")
    @Operation(summary = "Query TicketComments by User", description = "Retrieve ticket_comments for the current user")
    @PreAuthorize(TicketCommentPermissions.HAS_TICKET_COMMENT_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(TicketCommentRequest request) {
        
        Page<TicketCommentResponse> ticket_comments = ticket_commentRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(ticket_comments));
    }

    @ActionAnnotation(title = I18Consts.I18N_TICKET_COMMENT, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query ticket_comment by uid")
    @Operation(summary = "Query TicketComment by UID", description = "Retrieve a specific ticket_comment by its unique identifier")
    @PreAuthorize(TicketCommentPermissions.HAS_TICKET_COMMENT_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(TicketCommentRequest request) {
        
        TicketCommentResponse ticket_comment = ticket_commentRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(ticket_comment));
    }

    @ActionAnnotation(title = I18Consts.I18N_TICKET_COMMENT, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query ticket_comment by ticket uid")
    @Operation(summary = "Query TicketComments by Ticket UID", description = "Retrieve comments/remarks for a specific ticket, ordered by creation time desc")
    @PreAuthorize(TicketCommentPermissions.HAS_TICKET_COMMENT_READ)
    @GetMapping("/query/ticket")
    public ResponseEntity<?> queryByTicket(TicketCommentRequest request) {
        
        Page<TicketCommentResponse> ticket_comments = ticket_commentRestService.queryByTicketUid(request);

        return ResponseEntity.ok(JsonResult.success(ticket_comments));
    }

    @ActionAnnotation(title = I18Consts.I18N_TICKET_COMMENT, action = I18Consts.I18N_ACTION_CREATE, description = "create ticket_comment")
    @Operation(summary = "Create TicketComment", description = "Create a new ticket_comment")
    @Override
    @PreAuthorize(TicketCommentPermissions.HAS_TICKET_COMMENT_CREATE)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody TicketCommentRequest request) {
        
        TicketCommentResponse ticket_comment = ticket_commentRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(ticket_comment));
    }

    @ActionAnnotation(title = I18Consts.I18N_TICKET_COMMENT, action = I18Consts.I18N_ACTION_UPDATE, description = "update ticket_comment")
    @Operation(summary = "Update TicketComment", description = "Update an existing ticket_comment")
    @Override
    @PreAuthorize(TicketCommentPermissions.HAS_TICKET_COMMENT_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody TicketCommentRequest request) {
        
        TicketCommentResponse ticket_comment = ticket_commentRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(ticket_comment));
    }

    @ActionAnnotation(title = I18Consts.I18N_TICKET_COMMENT, action = I18Consts.I18N_ACTION_DELETE, description = "delete ticket_comment")
    @Operation(summary = "Delete TicketComment", description = "Delete a ticket_comment")
    @Override
    @PreAuthorize(TicketCommentPermissions.HAS_TICKET_COMMENT_DELETE)
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody TicketCommentRequest request) {
        
        ticket_commentRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_TICKET_COMMENT, action = I18Consts.I18N_ACTION_EXPORT, description = "export ticket_comment")
    @Operation(summary = "Export TicketComments", description = "Export ticket_comments to Excel format")
    @Override
    @PreAuthorize(TicketCommentPermissions.HAS_TICKET_COMMENT_EXPORT)
    @GetMapping("/export")
    public Object export(TicketCommentRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            ticket_commentRestService,
            TicketCommentExcel.class,
            "TicketComment",
            "ticket_comment"
        );
    }

    
    
}