package com.bytedesk.ticket.assignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.bytedesk.ticket.assignment.dto.AssignmentRuleRequest;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets/assignment/rules")
@PreAuthorize("hasRole('ADMIN')")
public class AssignmentRuleController {

    @Autowired
    private AssignmentRuleService ruleService;

    @GetMapping
    public ResponseEntity<Page<AssignmentRuleEntity>> getRules(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String priorityLevel,
            @RequestParam(required = false) Boolean enabled,
            Pageable pageable) {
        return ResponseEntity.ok(ruleService.searchRules(keyword, categoryId, priorityLevel, enabled, pageable));
    }

    @GetMapping("/{ruleId}")
    public ResponseEntity<AssignmentRuleEntity> getRule(@PathVariable Long ruleId) {
        return ResponseEntity.ok(ruleService.getRule(ruleId));
    }

    @PostMapping
    public ResponseEntity<AssignmentRuleEntity> createRule(
            @Valid @RequestBody AssignmentRuleRequest request) {
        return ResponseEntity.ok(ruleService.createRule(request));
    }

    @PutMapping("/{ruleId}")
    public ResponseEntity<AssignmentRuleEntity> updateRule(
            @PathVariable Long ruleId,
            @Valid @RequestBody AssignmentRuleRequest request) {
        return ResponseEntity.ok(ruleService.updateRule(ruleId, request));
    }

    @DeleteMapping("/{ruleId}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long ruleId) {
        ruleService.deleteRule(ruleId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{ruleId}/enable")
    public ResponseEntity<Void> enableRule(@PathVariable Long ruleId) {
        ruleService.enableRule(ruleId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{ruleId}/disable")
    public ResponseEntity<Void> disableRule(@PathVariable Long ruleId) {
        ruleService.disableRule(ruleId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/reorder")
    public ResponseEntity<Void> reorderRules(@RequestBody List<Long> ruleIds) {
        ruleService.reorderRules(ruleIds);
        return ResponseEntity.ok().build();
    }
} 