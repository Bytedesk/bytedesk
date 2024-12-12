package com.bytedesk.ticket.group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets/groups")
public class GroupMemberController {

    @Autowired
    private GroupMemberService groupMemberService;

    @PostMapping("/{groupId}/members")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GroupMemberEntity> addMember(
            @PathVariable Long groupId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") Integer role,
            @RequestParam(defaultValue = "10") Integer maxTickets) {
        return ResponseEntity.ok(groupMemberService.addMember(groupId, userId, role, maxTickets));
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long groupId,
            @PathVariable Long userId) {
        groupMemberService.removeMember(groupId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMemberEntity>> getMembers(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupMemberService.getMembers(groupId));
    }

    @PutMapping("/{groupId}/members/{userId}/max-tickets")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateMaxTickets(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            @RequestParam Integer maxTickets) {
        groupMemberService.updateMaxTickets(groupId, userId, maxTickets);
        return ResponseEntity.ok().build();
    }
} 