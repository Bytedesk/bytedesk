package com.bytedesk.remote.session;

import jakarta.validation.constraints.NotNull;

public record DesktopActionRequest(@NotNull Action action) {
    public enum Action { CONNECTED, REQUEST_CONTROL, GRANT_CONTROL, REVOKE_CONTROL, TERMINATE, REJECT }
}
