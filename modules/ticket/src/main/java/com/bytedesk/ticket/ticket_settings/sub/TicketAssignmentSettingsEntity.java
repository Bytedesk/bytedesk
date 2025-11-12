package com.bytedesk.ticket.ticket_settings.sub;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.ticket.ticket_settings.sub.dto.TicketAssignmentSettingsRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 指派与工作时间等设置。
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_ticket_assignment_settings")
public class TicketAssignmentSettingsEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Builder.Default
    private Boolean autoAssign = Boolean.TRUE;

    @Builder.Default
    private String assignmentType = "round_robin"; // round_robin | load_balancing | manual

    @Builder.Default
    private Boolean workingHoursEnabled = Boolean.TRUE;

    @Builder.Default
    private String workingHoursStart = "09:00";

    @Builder.Default
    private String workingHoursEnd = "18:00";

    /** 工作日集合（持久化 JSON） */
    @Builder.Default
    @Convert(converter = com.bytedesk.ticket.ticket_settings.sub.converter.WorkingDaysConverter.class)
    @Column(length = 128)
    private java.util.List<Integer> workingDays = java.util.Arrays.asList(1, 2, 3, 4, 5);

    @Builder.Default
    private Integer maxConcurrentTickets = 10;

    public static TicketAssignmentSettingsEntity fromRequest(TicketAssignmentSettingsRequest req) {
        TicketAssignmentSettingsEntity entity = new TicketAssignmentSettingsEntity();
        if (req == null) {
            return entity;
        }
        if (req.getAutoAssign() != null) entity.setAutoAssign(req.getAutoAssign());
        if (req.getAssignmentType() != null && !req.getAssignmentType().isEmpty()) entity.setAssignmentType(req.getAssignmentType());
        if (req.getWorkingHoursEnabled() != null) entity.setWorkingHoursEnabled(req.getWorkingHoursEnabled());
        if (req.getWorkingHoursStart() != null && !req.getWorkingHoursStart().isEmpty()) entity.setWorkingHoursStart(req.getWorkingHoursStart());
        if (req.getWorkingHoursEnd() != null && !req.getWorkingHoursEnd().isEmpty()) entity.setWorkingHoursEnd(req.getWorkingHoursEnd());
        if (req.getMaxConcurrentTickets() != null) entity.setMaxConcurrentTickets(req.getMaxConcurrentTickets());
        if (req.getWorkingDays() != null && !req.getWorkingDays().isEmpty()) {
            try {
                ObjectMapper om = new ObjectMapper();
                java.util.List<Integer> days = java.util.Arrays.asList(om.readValue(req.getWorkingDays(), Integer[].class));
                entity.setWorkingDays(days);
            } catch (Exception ignore) {}
        }
        return entity;
    }
}
