package com.bytedesk.kbase.trigger;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TriggerRequestSimple implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Existing trigger uid (optional). If present, backend updates the trigger. */
    private String uid;

    /** Stable runtime key identifying trigger behavior */
    private String triggerKey;

    /** Whether enabled */
    @Builder.Default
    private Boolean enabled = true;

    /** Trigger type (THREAD/CUSTOMER/TICKET etc.) */
    private String type;

    /** Display name */
    private String name;

    /** Display description */
    private String description;

    /** JSON config string */
    @Builder.Default
    private String config = "{}";
}
