package com.bytedesk.remote.device;

import jakarta.validation.constraints.*;

public record RemoteDeviceRegisterRequest(
        @NotBlank @Size(max = 64) String invitationToken,
        @NotBlank @Size(max = 128) String deviceName,
        @NotNull @Pattern(regexp = "win32|darwin|linux|android|ios") String osType) {
    @Override public String toString() { return "RemoteDeviceRegisterRequest[invitationToken=REDACTED]"; }
}
