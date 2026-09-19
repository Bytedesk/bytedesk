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
package com.bytedesk.webrtc.participant;

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

import java.util.List;

@RestController
@RequestMapping("/api/v1/participant")
@AllArgsConstructor
@Tag(name = "Participant Management", description = "Participant management APIs for organizing and categorizing content with participants")
@Description("Participant Management Controller - Content participantging and categorization APIs")
public class ParticipantRestController extends BaseRestController<ParticipantRequest, ParticipantRestService> {

    private final ParticipantRestService participantRestService;

    @ActionAnnotation(title = I18Consts.I18N_PARTICIPANT, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query participant by org")
    @Operation(summary = "Query Participants by Organization", description = "Retrieve participants for the current organization")
    @PreAuthorize(ParticipantPermissions.HAS_PARTICIPANT_READ)
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(ParticipantRequest request) {
        
        Page<ParticipantResponse> participants = participantRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(participants));
    }

    @ActionAnnotation(title = I18Consts.I18N_PARTICIPANT, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query participant by user")
    @Operation(summary = "Query Participants by User", description = "Retrieve participants for the current user")
    @PreAuthorize(ParticipantPermissions.HAS_PARTICIPANT_READ)
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(ParticipantRequest request) {
        
        Page<ParticipantResponse> participants = participantRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(participants));
    }

    @ActionAnnotation(title = I18Consts.I18N_PARTICIPANT, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query participant by uid")
    @Operation(summary = "Query Participant by UID", description = "Retrieve a specific participant by its unique identifier")
    @PreAuthorize(ParticipantPermissions.HAS_PARTICIPANT_READ)
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(ParticipantRequest request) {
        
        ParticipantResponse participant = participantRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(participant));
    }

    @ActionAnnotation(title = I18Consts.I18N_PARTICIPANT, action = I18Consts.I18N_ACTION_CREATE, description = "create participant")
    @Operation(summary = "Create Participant", description = "Create a new participant")
    @Override
    @PreAuthorize(ParticipantPermissions.HAS_PARTICIPANT_CREATE)
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ParticipantRequest request) {
        
        ParticipantResponse participant = participantRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(participant));
    }

    @ActionAnnotation(title = I18Consts.I18N_PARTICIPANT, action = I18Consts.I18N_ACTION_CREATE, description = "record joining a meeting")
    @Operation(summary = "Join Meeting", description = "Record current user joining a meeting room (creates a participant session row with joinedAt)")
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody ParticipantRequest request) {
        
        ParticipantResponse participant = participantRestService.joinMeeting(request);

        return ResponseEntity.ok(JsonResult.success(participant));
    }

    @ActionAnnotation(title = I18Consts.I18N_PARTICIPANT, action = I18Consts.I18N_ACTION_UPDATE, description = "record leaving a meeting")
    @Operation(summary = "Leave Meeting", description = "Record current user leaving a meeting room (sets leftAt, duration and status=LEFT; idempotent)")
    @PostMapping("/leave")
    public ResponseEntity<?> leave(@RequestBody ParticipantRequest request) {
        
        ParticipantResponse participant = participantRestService.leaveMeeting(request);

        return ResponseEntity.ok(JsonResult.success(participant));
    }

    @ActionAnnotation(title = I18Consts.I18N_PARTICIPANT, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query participants by roomUid")
    @Operation(summary = "Query Participants by Room", description = "Retrieve all participant session records of a meeting room")
    @GetMapping("/query/room")
    public ResponseEntity<?> queryByRoom(@org.springframework.web.bind.annotation.RequestParam("roomUid") String roomUid) {
        
        List<ParticipantResponse> participants = participantRestService.queryByRoomUid(roomUid);

        return ResponseEntity.ok(JsonResult.success(participants));
    }

    @ActionAnnotation(title = I18Consts.I18N_PARTICIPANT, action = I18Consts.I18N_ACTION_UPDATE, description = "update participant")
    @Operation(summary = "Update Participant", description = "Update an existing participant")
    @Override
    @PreAuthorize(ParticipantPermissions.HAS_PARTICIPANT_UPDATE)
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody ParticipantRequest request) {
        
        ParticipantResponse participant = participantRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(participant));
    }

    @ActionAnnotation(title = I18Consts.I18N_PARTICIPANT, action = I18Consts.I18N_ACTION_DELETE, description = "delete participant")
    @Operation(summary = "Delete Participant", description = "Delete a participant")
    @Override
    @PreAuthorize(ParticipantPermissions.HAS_PARTICIPANT_DELETE)
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody ParticipantRequest request) {
        
        participantRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = I18Consts.I18N_PARTICIPANT, action = I18Consts.I18N_ACTION_EXPORT, description = "export participant")
    @Operation(summary = "Export Participants", description = "Export participants to Excel format")
    @Override
    @PreAuthorize(ParticipantPermissions.HAS_PARTICIPANT_EXPORT)
    @GetMapping("/export")
    public Object export(ParticipantRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            participantRestService,
            ParticipantExcel.class,
            "Participant",
            "participant"
        );
    }

    
    
}