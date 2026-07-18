package com.bytedesk.call.mrcp.rtp;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Minimal runtime wrapper that drives a UDP RTP listener on a background thread.
 */
public class RtpCaptureRuntime implements AutoCloseable {

    private static final long DEFAULT_IDLE_COMPLETE_THRESHOLD_MS = 30_000L;
    private static final long IDLE_POLL_INTERVAL_MS = 2_000L;

    private final UdpRtpCaptureListener listener;
    private final long idleCompleteThresholdMs;
    private final AtomicBoolean running = new AtomicBoolean(false);

    private volatile Thread workerThread;
    private volatile boolean receivedAnyData;

    public RtpCaptureRuntime(UdpRtpCaptureListener listener) {
        this(listener, DEFAULT_IDLE_COMPLETE_THRESHOLD_MS);
    }

    public RtpCaptureRuntime(UdpRtpCaptureListener listener, long idleCompleteThresholdMs) {
        if (listener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        if (idleCompleteThresholdMs < IDLE_POLL_INTERVAL_MS) {
            throw new IllegalArgumentException("idleCompleteThresholdMs must be at least " + IDLE_POLL_INTERVAL_MS);
        }
        this.listener = listener;
        this.idleCompleteThresholdMs = idleCompleteThresholdMs;
    }

    public void start() {
        if (!running.compareAndSet(false, true)) {
            return;
        }
        Thread thread = new Thread(this::runLoop, "mrcp-rtp-capture-" + listener.session().port());
        thread.setDaemon(true);
        workerThread = thread;
        thread.start();
    }

    public void stop(boolean completeCapture) {
        if (!running.compareAndSet(true, false)) {
            if (completeCapture) {
                listener.complete();
            }
            listener.close();
            return;
        }

        if (completeCapture) {
            listener.complete();
        }
        listener.close();
    }

    public boolean isRunning() {
        return running.get();
    }

    public String workerThreadName() {
        Thread thread = workerThread;
        return thread == null ? null : thread.getName();
    }

    public InetSocketAddress localAddress() {
        return listener.localAddress();
    }

    public BytedeskRtpSessionFactory.RtpSession session() {
        return listener.session();
    }

    @Override
    public void close() {
        stop(false);
    }

    private void runLoop() {
        long lastDataTimestamp = System.currentTimeMillis();
        try {
            while (running.get()) {
                int received = listener.receiveNext();
                if (received < 0) {
                    break;
                }
                if (received > 0) {
                    receivedAnyData = true;
                    lastDataTimestamp = System.currentTimeMillis();
                } else if (receivedAnyData && System.currentTimeMillis() - lastDataTimestamp >= idleCompleteThresholdMs) {
                    listener.complete();
                    break;
                }
            }
        } finally {
            running.set(false);
        }
    }
}