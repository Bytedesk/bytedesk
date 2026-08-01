/**
 * connection package for connection-record persistence, presence-state tracking, and connection lifecycle services.
 * connection 包，负责连接记录持久化、在线状态跟踪与连接生命周期服务。
 *
 * <p>The package contains connection entities, request and response models, repository and specification queries,
 * heartbeat and metrics support, presence TTL resolution, initialization hooks, permission metadata,
 * and lifecycle events for tracked realtime connections.
 * 该包包含连接实体、请求响应模型、仓库与 Specification 查询、心跳与指标支持、在线 TTL 解析、初始化钩子、权限元数据以及被跟踪实时连接的生命周期事件。
 *
 * @author bytedesk.com
 */
@NonNullApi
package com.bytedesk.core.socket.connection;

import org.springframework.lang.NonNullApi;
