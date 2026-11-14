package com.bytedesk.remote.session;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DesktopSessionExpiry {
    private final DesktopSessionRepository sessions;
    private final DesktopSessionService service;

    @Scheduled(fixedDelay = 10000)
    public void expire() {
        for (var session : sessions.findByStatusIn(List.of(DesktopSessionStatusEnum.INITIALIZED,
                DesktopSessionStatusEnum.CONNECTING, DesktopSessionStatusEnum.CONNECTED))) {
            service.expireByUid(session.getUid());
        }
    }
}
