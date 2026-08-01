package com.bytedesk.call.esl;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bytedesk.call.config.CallSwitchEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "bytedesk.call.freeswitch", name = "enabled", havingValue = "true", matchIfMissing = false)
public class EslEventStreamService {

    private static final long SSE_TIMEOUT_MS = 0L;
    private static final int MAX_BUFFER_SIZE = 500;

    private final Object bufferLock = new Object();
    private final Deque<CallSwitchEvent> eventBuffer = new ArrayDeque<>();
    private final CopyOnWriteArrayList<EmitterRegistration> emitters = new CopyOnWriteArrayList<>();

    @EventListener
    public void onCallSwitchEvent(CallSwitchEvent event) {
        if (event == null) {
            return;
        }
        synchronized (bufferLock) {
            eventBuffer.addLast(event);
            while (eventBuffer.size() > MAX_BUFFER_SIZE) {
                eventBuffer.pollFirst();
            }
        }
        broadcast(event);
    }

    public SseEmitter subscribe(String eventName, String eventSubclass) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
        EmitterRegistration registration = new EmitterRegistration(
                emitter,
                normalize(eventName),
                normalize(eventSubclass));

        emitters.add(registration);
        emitter.onCompletion(() -> emitters.remove(registration));
        emitter.onTimeout(() -> emitters.remove(registration));
        emitter.onError(ex -> emitters.remove(registration));

        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data(Map.of("connected", true, "message", "esl event stream connected")));
        } catch (IOException ex) {
            emitters.remove(registration);
            throw new IllegalStateException("无法建立ESL事件SSE连接", ex);
        }
        return emitter;
    }

    public List<CallSwitchEvent> recentEvents(int limit, String eventName, String eventSubclass) {
        int safeLimit = Math.max(1, Math.min(limit, MAX_BUFFER_SIZE));
        String normalizedName = normalize(eventName);
        String normalizedSubclass = normalize(eventSubclass);

        List<CallSwitchEvent> snapshot;
        synchronized (bufferLock) {
            snapshot = new ArrayList<>(eventBuffer);
        }

        List<CallSwitchEvent> filtered = new ArrayList<>();
        for (int i = snapshot.size() - 1; i >= 0 && filtered.size() < safeLimit; i--) {
            CallSwitchEvent event = snapshot.get(i);
            if (matches(event, normalizedName, normalizedSubclass)) {
                filtered.add(event);
            }
        }
        return filtered;
    }

    public Map<String, Object> streamState() {
        int bufferSize;
        synchronized (bufferLock) {
            bufferSize = eventBuffer.size();
        }
        return Map.of(
                "emitters", emitters.size(),
                "bufferSize", bufferSize,
                "bufferLimit", MAX_BUFFER_SIZE
        );
    }

    private void broadcast(CallSwitchEvent event) {
        for (EmitterRegistration registration : emitters) {
            if (!matches(event, registration.eventName(), registration.eventSubclass())) {
                continue;
            }
            try {
                registration.emitter().send(
                        SseEmitter.event()
                                .name("esl-event")
                                .data(event)
                );
            } catch (IOException ex) {
                emitters.remove(registration);
                log.debug("移除失效SSE订阅: {}", ex.getMessage());
            }
        }
    }

    private boolean matches(CallSwitchEvent event, String eventName, String eventSubclass) {
        boolean nameMatches = eventName == null || Objects.equals(eventName, normalize(event.getEventName()));
        boolean subclassMatches = eventSubclass == null || Objects.equals(eventSubclass, normalize(event.getEventSubclass()));
        return nameMatches && subclassMatches;
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private record EmitterRegistration(SseEmitter emitter, String eventName, String eventSubclass) {
    }
}
