package com.bytedesk.remote.session;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RemoteSessionCreateRequest(@NotBlank @Size(max = 255) String threadUid) {}
