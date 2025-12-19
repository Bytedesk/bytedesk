package com.bytedesk.core.thread;

import lombok.Builder;
import lombok.Data;

/**
 * Metadata returned when visitors request server allocated message information.
 */
@Data
@Builder
public class ThreadSequenceResponse {

    /** Thread identifier provided by the client. */
    private String threadUid;

    /** Server generated unique identifier for the upcoming message. */
    private String messageUid;

    /** Server side timestamp (epoch millis) when the metadata is generated. */
    private Long timestamp;
}
