package com.bytedesk.remote.session;

import jakarta.validation.constraints.*;

public record DesktopSignalRequest(
        @NotBlank @Size(max = 64) String id,
        @NotNull @Pattern(regexp = "offer|answer|candidate") String type,
        @NotBlank @Size(max = 65536) String payload) {}
