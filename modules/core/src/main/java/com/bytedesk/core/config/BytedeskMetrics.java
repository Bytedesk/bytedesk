package com.bytedesk.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;

/**
 * 统一管理监控指标
 */
@Slf4j
@Component
public class BytedeskMetrics {

    private final MeterRegistry registry;

    // 系统指标
    private final Counter totalRequestCounter;
    private final Timer httpRequestTimer;
    private final Gauge jvmMemoryGauge;

    // 用户相关指标
    private final Counter onlineUsersCounter;
    private final Counter totalUsersCounter;
    private final Counter activeUsersCounter;

    // 消息相关指标
    private final Counter messageCounter;
    private final Counter messageErrorCounter;
    private final Timer messageProcessTimer;

    // AI相关指标
    private final Counter aiRequestCounter;
    private final Counter aiErrorCounter;
    private final Timer aiResponseTimer;

    // WebSocket相关指标
    private final Counter wsConnectionCounter;
    private final Counter wsMessageCounter;
    private final Gauge wsActiveConnectionGauge;

    @Autowired
    public BytedeskMetrics(MeterRegistry registry) {
        this.registry = registry;

        // 初始化系统指标
        this.totalRequestCounter = Counter.builder("bytedesk.requests.total")
                .description("Total number of requests")
                .register(registry);

        this.httpRequestTimer = Timer.builder("bytedesk.http.request.time")
                .description("HTTP request processing time")
                .register(registry);

        this.jvmMemoryGauge = Gauge.builder("bytedesk.jvm.memory", 
                Runtime.getRuntime(),
                runtime -> runtime.totalMemory() - runtime.freeMemory())
                .description("JVM memory usage")
                .register(registry);

        // 初始化用户相关指标
        this.onlineUsersCounter = Counter.builder("bytedesk.users.online")
                .description("Number of online users")
                .register(registry);

        this.totalUsersCounter = Counter.builder("bytedesk.users.total")
                .description("Total number of registered users")
                .register(registry);

        this.activeUsersCounter = Counter.builder("bytedesk.users.active")
                .description("Number of active users")
                .register(registry);

        // 初始化消息相关指标
        this.messageCounter = Counter.builder("bytedesk.messages.total")
                .description("Total number of messages")
                .register(registry);

        this.messageErrorCounter = Counter.builder("bytedesk.messages.errors")
                .description("Number of message errors")
                .register(registry);

        this.messageProcessTimer = Timer.builder("bytedesk.message.process.time")
                .description("Message processing time")
                .register(registry);

        // 初始化AI相关指标
        this.aiRequestCounter = Counter.builder("bytedesk.ai.requests")
                .description("Number of AI requests")
                .register(registry);

        this.aiErrorCounter = Counter.builder("bytedesk.ai.errors")
                .description("Number of AI errors")
                .register(registry);

        this.aiResponseTimer = Timer.builder("bytedesk.ai.response.time")
                .description("AI response time")
                .register(registry);

        // 初始化WebSocket相关指标
        this.wsConnectionCounter = Counter.builder("bytedesk.ws.connections")
                .description("Number of WebSocket connections")
                .register(registry);

        this.wsMessageCounter = Counter.builder("bytedesk.ws.messages")
                .description("Number of WebSocket messages")
                .register(registry);

        this.wsActiveConnectionGauge = Gauge.builder("bytedesk.ws.active.connections", 
                () -> 0D)  // 初始值为0
                .description("Number of active WebSocket connections")
                .register(registry);
    }

    // 系统指标操作方法
    public void incrementRequestCount() {
        totalRequestCounter.increment();
    }

    public Timer.Sample startHttpRequestTimer() {
        return Timer.start(registry);
    }

    public void stopHttpRequestTimer(Timer.Sample sample) {
        sample.stop(httpRequestTimer);
    }

    // 用户相关指标操作方法
    public void userConnected() {
        onlineUsersCounter.increment();
    }

    public void userDisconnected() {
        onlineUsersCounter.increment(-1);
    }

    public void newUserRegistered() {
        totalUsersCounter.increment();
    }

    public void userBecameActive() {
        activeUsersCounter.increment();
    }

    public void userBecameInactive() {
        activeUsersCounter.increment(-1);
    }

    // 消息相关指标操作方法
    public void messageReceived() {
        messageCounter.increment();
    }

    public void messageError() {
        messageErrorCounter.increment();
    }

    public Timer.Sample startMessageProcessTimer() {
        return Timer.start(registry);
    }

    public void stopMessageProcessTimer(Timer.Sample sample) {
        sample.stop(messageProcessTimer);
    }

    // AI相关指标操作方法
    public void aiRequestMade() {
        aiRequestCounter.increment();
    }

    public void aiError() {
        aiErrorCounter.increment();
    }

    public Timer.Sample startAiResponseTimer() {
        return Timer.start(registry);
    }

    public void stopAiResponseTimer(Timer.Sample sample) {
        sample.stop(aiResponseTimer);
    }

    // WebSocket相关指标操作方法
    public void wsConnectionOpened() {
        wsConnectionCounter.increment();
    }

    public void wsConnectionClosed() {
        wsConnectionCounter.increment(-1);
    }

    public void wsMessageSent() {
        wsMessageCounter.increment();
    }

    public void updateWsActiveConnections(int count) {
        registry.gauge("bytedesk.ws.active.connections", count);
    }

    // 获取指标值的方法
    public double getOnlineUsersCount() {
        return onlineUsersCounter.count();
    }

    public double getTotalMessagesCount() {
        return messageCounter.count();
    }

    public double getAiRequestsCount() {
        return aiRequestCounter.count();
    }

    public double getWsConnectionsCount() {
        return wsConnectionCounter.count();
    }
} 