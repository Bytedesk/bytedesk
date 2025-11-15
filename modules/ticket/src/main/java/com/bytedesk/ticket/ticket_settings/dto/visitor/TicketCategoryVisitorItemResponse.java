package com.bytedesk.ticket.ticket_settings.dto.visitor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lightweight ticket category option exposed to visitor clients.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketCategoryVisitorItemResponse {

    /**
     * Unique identifier of the category option.
     */
    private String uid;

    /**
     * Display name presented to the visitor.
     */
    private String name;

    /**
     * Optional description or hint text for the category.
     */
    private String description;
}
