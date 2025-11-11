package com.bytedesk.ticket.ticket_settings.sub;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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

    @Builder.Default
    private String workingDays = "[1,2,3,4,5]"; // JSON Array

    @Builder.Default
    private Integer maxConcurrentTickets = 10;
}
