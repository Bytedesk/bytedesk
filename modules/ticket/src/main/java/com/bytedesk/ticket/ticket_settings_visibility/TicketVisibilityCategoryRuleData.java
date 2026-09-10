package com.bytedesk.ticket.ticket_settings_visibility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketVisibilityCategoryRuleData implements Serializable {

    private static final long serialVersionUID = 1L;

    private String categoryUid;

    @Builder.Default
    private String visibility = TicketVisibilityModeEnum.ORG_WIDE.name();

    @Builder.Default
    private List<String> departmentUids = new ArrayList<>();
}