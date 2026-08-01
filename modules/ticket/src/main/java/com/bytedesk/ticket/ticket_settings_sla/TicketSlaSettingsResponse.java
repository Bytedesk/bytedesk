package com.bytedesk.ticket.ticket_settings_sla;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.ticket.ticket_sla_rule.TicketSlaRuleResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
public class TicketSlaSettingsResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private Boolean enabled;
    private Boolean businessHoursEnabled;
    private String businessHoursStartTime;
    private String businessHoursEndTime;
    private String businessHoursTimezone;
    private String businessHoursCountryCode;
    private Boolean pauseOnHold;
    private Boolean notifyOnBreach;
    private Boolean autoEscalateEnabled;
    private String escalateAssigneeUid;
    private Boolean autoCloseCustomerPendingEnabled;
    private Integer customerVerifyAutoCloseHours;
    private Integer warningPercent;

    @Builder.Default
    private List<TicketSlaRuleResponse> rules = new ArrayList<>();
}