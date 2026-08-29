package com.bytedesk.core.connection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PresenceResponse {
    private boolean online;
    private int activeCount;
}
