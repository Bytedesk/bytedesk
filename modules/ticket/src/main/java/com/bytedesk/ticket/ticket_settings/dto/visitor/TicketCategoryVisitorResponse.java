package com.bytedesk.ticket.ticket_settings.dto.visitor;

import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Visitor-facing payload for ticket category configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketCategoryVisitorResponse {

    /**
     * Default category UID to preselect for visitors (may be {@code null}).
     */
    private String defaultCategoryUid;

    /**
     * Enabled category options available to the visitor.
     */
    @Builder.Default
    private List<TicketCategoryVisitorItemResponse> categories = Collections.emptyList();

    /**
     * Convenience factory for empty responses.
     */
    public static TicketCategoryVisitorResponse empty() {
        return TicketCategoryVisitorResponse.builder().categories(Collections.emptyList()).build();
    }
}
