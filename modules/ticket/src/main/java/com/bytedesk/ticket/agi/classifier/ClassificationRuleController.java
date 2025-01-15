package com.bytedesk.ticket.agi.classifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets/classification/rules")
public class ClassificationRuleController {

    @Autowired
    private ClassificationRuleRepository ruleRepository;

    @GetMapping
    public ResponseEntity<List<ClassificationRuleEntity>> getAllRules() {
        return ResponseEntity.ok(ruleRepository.findAll());
    }

    @GetMapping("/{ruleId}")
    public ResponseEntity<ClassificationRuleEntity> getRule(@PathVariable Long ruleId) {
        return ResponseEntity.ok(ruleRepository.findById(ruleId)
            .orElseThrow(() -> new RuntimeException("Rule not found")));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClassificationRuleEntity> createRule(
            @Valid @RequestBody ClassificationRuleEntity rule) {
        return ResponseEntity.ok(ruleRepository.save(rule));
    }

    @PutMapping("/{ruleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClassificationRuleEntity> updateRule(
            @PathVariable Long ruleId,
            @Valid @RequestBody ClassificationRuleEntity rule) {
        if (!ruleRepository.existsById(ruleId)) {
            throw new RuntimeException("Rule not found");
        }
        rule.setId(ruleId);
        return ResponseEntity.ok(ruleRepository.save(rule));
    }

    @DeleteMapping("/{ruleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRule(@PathVariable Long ruleId) {
        ruleRepository.deleteById(ruleId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{ruleId}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> enableRule(@PathVariable Long ruleId) {
        ClassificationRuleEntity rule = ruleRepository.findById(ruleId)
            .orElseThrow(() -> new RuntimeException("Rule not found"));
        rule.setEnabled(true);
        ruleRepository.save(rule);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{ruleId}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> disableRule(@PathVariable Long ruleId) {
        ClassificationRuleEntity rule = ruleRepository.findById(ruleId)
            .orElseThrow(() -> new RuntimeException("Rule not found"));
        rule.setEnabled(false);
        ruleRepository.save(rule);
        return ResponseEntity.ok().build();
    }
} 