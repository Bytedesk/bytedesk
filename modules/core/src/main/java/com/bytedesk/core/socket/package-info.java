/**
 * socket package for coordinating connection presence, MQTT transport, and STOMP-over-WebSocket messaging runtimes.
 * socket 包，负责协调连接在线状态、MQTT 传输与 STOMP over WebSocket 消息运行时。
 *
 * <p>The package acts as the realtime transport boundary that groups connection registry and presence synchronization,
 * MQTT protocol/server/runtime support, and STOMP configuration, handlers, interceptors, and listeners.
 * 该包作为实时传输边界，聚合了连接注册与在线状态同步、MQTT 协议与服务端运行支持，以及 STOMP 配置、处理器、拦截器和监听器。
 *
 * @author bytedesk.com
 */
@NullMarked
package com.bytedesk.core.socket;

import org.jspecify.annotations.NullMarked;