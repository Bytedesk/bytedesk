package com.bytedesk.remote.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record RemoteHeartbeatRequest(@Min(0) @Max(1000000000) long inputCount) {
}
