package com.bytedesk.remote.session;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DesktopSessionCreateRequest(@NotBlank @Size(max = 255) String threadUid) {}
