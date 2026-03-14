package com.bytedesk.meet.conference;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/meet/conference")
public class ConferenceRestController {

    private final ConferenceRoomRegistry conferenceRoomRegistry;

    private final SimpMessagingTemplate messagingTemplate;

    private final ConferenceRuntimeProperties conferenceRuntimeProperties;

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody ConferenceJoinRequest request) {
        try {
            ConferenceRoomSnapshot snapshot = conferenceRoomRegistry.join(
                    new ConferenceJoinCommand(
                            request.roomId(),
                            request.participantId(),
                            request.displayName(),
                            request.audioEnabled() == null ? true : request.audioEnabled(),
                            request.videoEnabled() == null ? true : request.videoEnabled(),
                            request.password()));
            publishRoomState(snapshot);
            return ResponseEntity.ok(JsonResult.success(snapshot));
        } catch (ConferenceAccessDeniedException exception) {
            return ResponseEntity.status(403).body(JsonResult.error(exception.getMessage(), 403));
        }
    }

    @PostMapping("/lock")
    public ResponseEntity<?> lockRoom(@RequestBody ConferenceRoomLockRequest request) {
        ConferenceRoomSnapshot snapshot = conferenceRoomRegistry.lockRoom(
                request.roomId(), request.participantId(), request.displayName(), request.password());
        publishRoomState(snapshot);
        return ResponseEntity.ok(JsonResult.success(snapshot));
    }

    @PostMapping("/unlock")
    public ResponseEntity<?> unlockRoom(@RequestBody ConferenceRoomUnlockRequest request) {
        ConferenceRoomSnapshot snapshot = conferenceRoomRegistry.unlockRoom(
                request.roomId(), request.participantId());
        publishRoomState(snapshot);
        return ResponseEntity.ok(JsonResult.success(snapshot));
    }

    @PostMapping("/leave")
    public ResponseEntity<?> leave(@RequestBody ConferenceLeaveRequest request) {
        ConferenceRoomSnapshot snapshot = conferenceRoomRegistry.leave(request.roomId(), request.participantId());
        publishRoomState(snapshot);
        return ResponseEntity.ok(JsonResult.success(snapshot));
    }

    @PostMapping("/media")
    public ResponseEntity<?> updateMediaState(@RequestBody ConferenceMediaStateRequest request) {
        ConferenceRoomSnapshot snapshot = conferenceRoomRegistry.updateMediaState(
                request.roomId(), request.participantId(), request.audioEnabled(), request.videoEnabled());
        publishRoomState(snapshot);
        return ResponseEntity.ok(JsonResult.success(snapshot));
    }

    @PostMapping("/interaction")
    public ResponseEntity<?> updateInteractionState(@RequestBody ConferenceInteractionStateRequest request) {
        ConferenceRoomSnapshot snapshot = conferenceRoomRegistry.updateInteractionState(
                request.roomId(),
                request.participantId(),
                request.screenSharing(),
                request.handRaised());
        publishRoomState(snapshot);
        return ResponseEntity.ok(JsonResult.success(snapshot));
    }

    @PostMapping("/heartbeat")
    public ResponseEntity<?> heartbeat(@RequestBody ConferenceHeartbeatRequest request) {
        ConferenceRoomSnapshot snapshot = conferenceRoomRegistry.touch(request.roomId(), request.participantId());
        return ResponseEntity.ok(JsonResult.success(snapshot));
    }

    @GetMapping("/ice-servers")
    public ResponseEntity<?> getIceServers() {
        return ResponseEntity.ok(JsonResult.success(conferenceRuntimeProperties.toIceServerResponse()));
    }

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<?> getRoom(@PathVariable String roomId) {
        return ResponseEntity.ok(JsonResult.success(conferenceRoomRegistry.getSnapshot(roomId)));
    }

    private void publishRoomState(ConferenceRoomSnapshot snapshot) {
        messagingTemplate.convertAndSend(topic(snapshot.roomId()), ConferenceSignalMessage.roomState(snapshot.roomId(), snapshot));
    }

    private String topic(String roomId) {
        return "/topic/meet/rooms/" + roomId;
    }
}