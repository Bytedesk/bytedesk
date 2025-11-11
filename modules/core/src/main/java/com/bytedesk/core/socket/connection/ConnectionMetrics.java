package com.bytedesk.core.socket.connection;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

/**
 * Metrics for connection heartbeat & presence performance.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnClass(MeterRegistry.class)
public class ConnectionMetrics {

    private final MeterRegistry meterRegistry;

    private Counter heartbeatCalls;
    private Counter heartbeatDbWrites;
    private Counter heartbeatSkipped;
    private Counter heartbeatCreated;
    private Timer flushTimer;
    private DistributionSummary batchSizeSummary;

    @PostConstruct
    public void init() {
        heartbeatCalls = meterRegistry.counter("conn.hb.calls");
        heartbeatDbWrites = meterRegistry.counter("conn.hb.dbwrites");
        heartbeatSkipped = meterRegistry.counter("conn.hb.skipped");
        heartbeatCreated = meterRegistry.counter("conn.hb.created");
        flushTimer = meterRegistry.timer("conn.hb.flush.timer");
        batchSizeSummary = DistributionSummary.builder("conn.hb.flush.batch.size").register(meterRegistry);
    }

    public void incHeartbeatCall() { heartbeatCalls.increment(); }
    public void incDbWrite() { heartbeatDbWrites.increment(); }
    public void incSkipped() { heartbeatSkipped.increment(); }
    public void incCreated() { heartbeatCreated.increment(); }
    public void recordFlush(long nanos, int batchSize) {
        flushTimer.record(nanos, java.util.concurrent.TimeUnit.NANOSECONDS);
        batchSizeSummary.record(batchSize);
    }
}
