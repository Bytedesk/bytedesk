package com.bytedesk.ticket.ticket_sla_rule;

import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TicketSlaRuleResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private String slaType;
    private String priority;
    private String categoryUid;
    private Long durationMinutes;
    private Long warningMinutes;
    private Boolean enabled;
    private Integer orderIndex;
}