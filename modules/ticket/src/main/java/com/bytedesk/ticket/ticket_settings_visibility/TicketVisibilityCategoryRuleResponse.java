package com.bytedesk.ticket.ticket_settings_visibility;

import com.bytedesk.core.base.BaseResponse;

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
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TicketVisibilityCategoryRuleResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private String categoryUid;

    @Builder.Default
    private String visibility = TicketVisibilityModeEnum.ORG_WIDE.name();
}