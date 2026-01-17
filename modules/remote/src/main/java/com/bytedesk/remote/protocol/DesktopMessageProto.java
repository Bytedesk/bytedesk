/*
 * @Author: bytedesk.com
 * @Date: 2025-01-16
 * @Description: Bytedesk Remote Desktop Protocol - Message Protobuf Definition
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *   contact: 270580156@qq.com
 *   Copyright (c) 2025 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.remote.protocol;

import java.io.Serializable;
import java.time.ZonedDateTime;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Desktop Remote Control Protocol Message
 * All fields correspond to desktop.proto message definition
 *
 * Message Flow:
 * 1. Viewer sends SESSION_INIT to establish connection
 * 2. Host responds with SESSION_INIT_ACK
 * 3. Host streams SCREEN_FRAME messages continuously
 * 4. Viewer sends CURSOR_MOVE, MOUSE_CLICK, KEY_EVENT messages
 * 5. Either side sends SESSION_TERMINATE to end session
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class DesktopMessageProto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Unique message identifier
     */
    private String uid;

    /**
     * Message type - see DesktopMessageTypeEnum
     */
    private DesktopMessageTypeEnum type;

    /**
     * Session identifier
     */
    private String sessionId;

    /**
     * Device identifier (sender)
     */
    private String deviceUid;

    /**
     * User identifier (sender)
     */
    private String userUid;

    /**
     * Message payload (JSON or binary data)
     * For SCREEN_FRAME: base64 encoded image data
     * For input events: JSON event data
     */
    private String content;

    /**
     * Sequence number for ordering
     */
    private Long sequence;

    /**
     * Timestamp when message was created
     */
    private ZonedDateTime createdAt;

    /**
     * Additional metadata (optional)
     */
    private String extra;

    /**
     * Quality level for screen frames (LOW, MEDIUM, HIGH)
     */
    private QualityLevelEnum quality;

    /**
     * Message status (for acknowledgments)
     */
    private DesktopMessageStatusEnum status;

    /**
     * Get formatted creation time
     */
    public String getCreatedAt() {
        return BdDateUtils.formatDatetimeToString(createdAt);
    }

    /**
     * Get original creation time
     */
    public ZonedDateTime getCreatedAtDateTime() {
        return createdAt;
    }

    /**
     * Get timestamp
     */
    public Long getTimestamp() {
        return BdDateUtils.toTimestamp(this.createdAt);
    }

    /**
     * Convert from JSON
     */
    public static DesktopMessageProto fromJson(String json) {
        return JSON.parseObject(json, DesktopMessageProto.class);
    }

    /**
     * Convert to JSON
     */
    public String toJson() {
        return JSON.toJSONString(this);
    }

    /**
     * Build screen frame message
     */
    public static DesktopMessageProto buildScreenFrame(
            String sessionId,
            String deviceUid,
            String base64ImageData,
            Long sequence,
            QualityLevelEnum quality) {
        return DesktopMessageProto.builder()
                .uid(java.util.UUID.randomUUID().toString())
                .type(DesktopMessageTypeEnum.SCREEN_FRAME)
                .sessionId(sessionId)
                .deviceUid(deviceUid)
                .content(base64ImageData)
                .sequence(sequence)
                .createdAt(ZonedDateTime.now())
                .quality(quality)
                .status(DesktopMessageStatusEnum.SENT)
                .build();
    }

    /**
     * Build mouse event message
     */
    public static DesktopMessageProto buildMouseEvent(
            String sessionId,
            String deviceUid,
            String eventData) {
        return DesktopMessageProto.builder()
                .uid(java.util.UUID.randomUUID().toString())
                .type(DesktopMessageTypeEnum.MOUSE_EVENT)
                .sessionId(sessionId)
                .deviceUid(deviceUid)
                .content(eventData)
                .sequence(System.currentTimeMillis())
                .createdAt(ZonedDateTime.now())
                .status(DesktopMessageStatusEnum.SENT)
                .build();
    }

    /**
     * Build keyboard event message
     */
    public static DesktopMessageProto buildKeyboardEvent(
            String sessionId,
            String deviceUid,
            String eventData) {
        return DesktopMessageProto.builder()
                .uid(java.util.UUID.randomUUID().toString())
                .type(DesktopMessageTypeEnum.KEY_EVENT)
                .sessionId(sessionId)
                .deviceUid(deviceUid)
                .content(eventData)
                .sequence(System.currentTimeMillis())
                .createdAt(ZonedDateTime.now())
                .status(DesktopMessageStatusEnum.SENT)
                .build();
    }

    /**
     * Build session init message
     */
    public static DesktopMessageProto buildSessionInit(
            String sessionId,
            String deviceUid,
            String userUid,
            String extra) {
        return DesktopMessageProto.builder()
                .uid(java.util.UUID.randomUUID().toString())
                .type(DesktopMessageTypeEnum.SESSION_INIT)
                .sessionId(sessionId)
                .deviceUid(deviceUid)
                .userUid(userUid)
                .extra(extra)
                .sequence(0L)
                .createdAt(ZonedDateTime.now())
                .status(DesktopMessageStatusEnum.SENT)
                .build();
    }

    /**
     * Build session terminate message
     */
    public static DesktopMessageProto buildSessionTerminate(
            String sessionId,
            String deviceUid,
            String reason) {
        return DesktopMessageProto.builder()
                .uid(java.util.UUID.randomUUID().toString())
                .type(DesktopMessageTypeEnum.SESSION_TERMINATE)
                .sessionId(sessionId)
                .deviceUid(deviceUid)
                .extra(reason)
                .sequence(System.currentTimeMillis())
                .createdAt(ZonedDateTime.now())
                .status(DesktopMessageStatusEnum.SENT)
                .build();
    }
}
