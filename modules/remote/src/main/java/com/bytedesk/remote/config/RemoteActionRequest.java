package com.bytedesk.remote.config;

import jakarta.validation.constraints.NotNull;

public record RemoteActionRequest(@NotNull Action action) {
    public enum Action { CONNECTED, REQUEST_CONTROL, GRANT_CONTROL, REVOKE_CONTROL, TERMINATE, REJECT }
}
