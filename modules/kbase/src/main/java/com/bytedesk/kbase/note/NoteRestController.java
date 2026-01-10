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
package com.bytedesk.kbase.note;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Description;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/note")
@AllArgsConstructor
@Tag(name = "Note Management", description = "Note management APIs for organizing and categorizing content with notes")
@Description("Note Management Controller - Content noteging and categorization APIs")
public class NoteRestController extends BaseRestController<NoteRequest, NoteRestService> {

    private final NoteRestService noteRestService;

    @ActionAnnotation(title = "Note", action = "组织查询", description = "query note by org")
    @Operation(summary = "Query Notes by Organization", description = "Retrieve notes for the current organization")
    @PreAuthorize(NotePermissions.HAS_NOTE_READ)
    @Override
    public ResponseEntity<?> queryByOrg(NoteRequest request) {
        
        Page<NoteResponse> notes = noteRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(notes));
    }

    @ActionAnnotation(title = "Note", action = "用户查询", description = "query note by user")
    @Operation(summary = "Query Notes by User", description = "Retrieve notes for the current user")
    @PreAuthorize(NotePermissions.HAS_NOTE_READ)
    @Override
    public ResponseEntity<?> queryByUser(NoteRequest request) {
        
        Page<NoteResponse> notes = noteRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(notes));
    }

    @ActionAnnotation(title = "Note", action = "查询详情", description = "query note by uid")
    @Operation(summary = "Query Note by UID", description = "Retrieve a specific note by its unique identifier")
    @PreAuthorize(NotePermissions.HAS_NOTE_READ)
    @Override
    public ResponseEntity<?> queryByUid(NoteRequest request) {
        
        NoteResponse note = noteRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(note));
    }

    @ActionAnnotation(title = "Note", action = "新建", description = "create note")
    @Operation(summary = "Create Note", description = "Create a new note")
    @Override
    @PreAuthorize(NotePermissions.HAS_NOTE_CREATE)
    public ResponseEntity<?> create(NoteRequest request) {
        
        NoteResponse note = noteRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(note));
    }

    @ActionAnnotation(title = "Note", action = "更新", description = "update note")
    @Operation(summary = "Update Note", description = "Update an existing note")
    @Override
    @PreAuthorize(NotePermissions.HAS_NOTE_UPDATE)
    public ResponseEntity<?> update(NoteRequest request) {
        
        NoteResponse note = noteRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(note));
    }

    @ActionAnnotation(title = "Note", action = "删除", description = "delete note")
    @Operation(summary = "Delete Note", description = "Delete a note")
    @Override
    @PreAuthorize(NotePermissions.HAS_NOTE_DELETE)
    public ResponseEntity<?> delete(NoteRequest request) {
        
        noteRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "Note", action = "导出", description = "export note")
    @Operation(summary = "Export Notes", description = "Export notes to Excel format")
    @Override
    @PreAuthorize(NotePermissions.HAS_NOTE_EXPORT)
    @GetMapping("/export")
    public Object export(NoteRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            noteRestService,
            NoteExcel.class,
            "Note",
            "note"
        );
    }

    
    
}