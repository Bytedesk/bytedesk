package com.bytedesk.remote.device;

import jakarta.validation.constraints.*;

public record DesktopDeviceRegisterRequest(
        @NotBlank @Size(max = 64) String invitationToken,
        @NotBlank @Size(max = 128) String deviceName,
        @NotNull @Pattern(regexp = "win32|darwin|linux") String osType) {
    @Override public String toString() { return "DesktopDeviceRegisterRequest[invitationToken=REDACTED]"; }
}
