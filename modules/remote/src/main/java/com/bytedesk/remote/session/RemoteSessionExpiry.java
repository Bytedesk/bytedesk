package com.bytedesk.remote.session;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoteSessionExpiry {
    private final RemoteSessionRepository sessions;
    private final RemoteSessionService service;

    @Scheduled(fixedDelay = 10000)
    public void expire() {
        for (var session : sessions.findByStatusIn(List.of(RemoteSessionStatusEnum.INITIALIZED,
                RemoteSessionStatusEnum.CONNECTING, RemoteSessionStatusEnum.CONNECTED))) {
            service.expireByUid(session.getUid());
        }
    }
}
