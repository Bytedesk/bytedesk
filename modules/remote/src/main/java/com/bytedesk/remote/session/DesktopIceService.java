package com.bytedesk.remote.session;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DesktopIceService {
    @Value("${bytedesk.remote.ice-urls:}") private String urls;
    @Value("${bytedesk.remote.turn-secret:}") private String secret;

    public record IceServer(String urls, String username, String credential) {}

    public List<IceServer> servers(DesktopSessionResponse session) {
        if (session.status() == DesktopSessionStatusEnum.TERMINATED
                || session.status() == DesktopSessionStatusEnum.FAILED
                || session.expiresAt() <= Instant.now().toEpochMilli()) {
            throw new ResponseStatusException(HttpStatus.GONE, "Remote assistance session has ended");
        }
        String sessionUid = session.uid();
        var result = new ArrayList<IceServer>();
        for (String url : urls.split(",")) {
            url = url.trim();
            if (url.isEmpty()) continue;
            boolean turn = url.startsWith("turn:") || url.startsWith("turns:");
            if (!turn && !url.startsWith("stun:")) throw new IllegalStateException("Invalid remote ICE URL");
            if (turn && secret.isBlank()) throw new IllegalStateException("Remote TURN shared secret is required");
            // No ICE restart in MVP: credentials must cover allocation refreshes for the session.
            String username = turn ? (session.expiresAt() / 1000 + 300) + ":" + sessionUid : null;
            String credential = null;
            if (turn) {
                try {
                    Mac mac = Mac.getInstance("HmacSHA1");
                    mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
                    credential = Base64.getEncoder().encodeToString(mac.doFinal(Objects.requireNonNull(username).getBytes(StandardCharsets.UTF_8)));
                } catch (java.security.GeneralSecurityException e) {
                    throw new IllegalStateException("Cannot sign remote TURN credentials", e);
                }
            }
            result.add(new IceServer(url, username, credential));
        }
        return result;
    }
}
