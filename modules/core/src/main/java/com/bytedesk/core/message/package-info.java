/**
 * message package for message persistence, delivery orchestration, message-type modeling, and realtime message services.
 * message 包，负责消息持久化、投递编排、消息类型建模与实时消息服务。
 *
 * <p>The package combines message entities, request and response models, repository and specification queries,
 * persist and queue services, socket delivery support, protobuf conversion, lifecycle listeners, initialization hooks,
 * and specialized subpackages for content, preview, playback, reaction, extra data, and utilities.
 * 该包组合了消息实体、请求响应模型、仓库与 Specification 查询、持久化与队列服务、Socket 投递支持、protobuf 转换、生命周期监听器、初始化钩子，
 * 以及 content、preview、playback、reaction、extra、utils 等专门子包。
 *
 * @author bytedesk.com
 */
@NonNullApi
package com.bytedesk.core.message;

import org.springframework.lang.NonNullApi;