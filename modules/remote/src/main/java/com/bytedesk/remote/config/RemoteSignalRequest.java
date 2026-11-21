package com.bytedesk.remote.config;

import jakarta.validation.constraints.*;

public record RemoteSignalRequest(
        @NotBlank @Size(max = 64) String id,
        @NotNull @Pattern(regexp = "offer|answer|candidate") String type,
        @NotBlank @Size(max = 65536) String payload) {}
