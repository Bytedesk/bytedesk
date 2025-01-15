package com.bytedesk.ticket.agi.skill;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets/skills")
public class AgentSkillController {

    @Autowired
    private AgentSkillService skillService;

    @PostMapping("/agents/{agentId}/categories/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AgentSkillEntity> addSkill(
            @PathVariable Long agentId,
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "1") Integer proficiency) {
        return ResponseEntity.ok(skillService.addSkill(agentId, categoryId, proficiency));
    }

    @DeleteMapping("/{skillId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeSkill(@PathVariable Long skillId) {
        skillService.removeSkill(skillId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{skillId}/proficiency")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateProficiency(
            @PathVariable Long skillId,
            @RequestParam Integer proficiency) {
        skillService.updateProficiency(skillId, proficiency);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{skillId}/max-tickets")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateMaxTickets(
            @PathVariable Long skillId,
            @RequestParam Integer maxTickets) {
        skillService.updateMaxTickets(skillId, maxTickets);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{skillId}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> enableSkill(@PathVariable Long skillId) {
        skillService.enableSkill(skillId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{skillId}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> disableSkill(@PathVariable Long skillId) {
        skillService.disableSkill(skillId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/agents/{agentId}")
    public ResponseEntity<List<AgentSkillEntity>> getAgentSkills(@PathVariable Long agentId) {
        return ResponseEntity.ok(skillService.getAgentSkills(agentId));
    }

    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<List<AgentSkillEntity>> getCategorySkills(@PathVariable Long categoryId) {
        return ResponseEntity.ok(skillService.getCategorySkills(categoryId));
    }

    @GetMapping("/categories/{categoryId}/best")
    public ResponseEntity<List<AgentSkillEntity>> getBestAgentsForCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(skillService.getBestAgentsForCategory(categoryId));
    }
} 