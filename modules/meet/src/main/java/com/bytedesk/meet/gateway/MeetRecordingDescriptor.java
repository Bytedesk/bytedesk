package com.bytedesk.meet.gateway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 受控的会议录制描述（2026-10-19 规划 §4.1）
 *
 * 由 enterprise/webrtc 的 Gateway 实现从既有 MeetingRecordingEntity 转换，
 * 不暴露真实文件路径给上层；文件读取走 {@link MeetRecordingSourceGateway#readAudio}.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetRecordingDescriptor {

    /** 录制 uid（MeetingRecordingEntity.uid） */
    private String recordingUid;

    /** 所属组织 */
    private String orgUid;

    /** 上传者（主持人）userUid */
    private String ownerUserUid;

    /** 关联房间 uid */
    private String roomUid;

    /** 录制来源：MEETING / WEBRTC（仅 MEETING 进入会议 AI） */
    private String source;

    /** 音轨覆盖范围，null 视为 UNKNOWN */
    private MeetRecordingAudioScopeEnum audioScope;

    /** 文件相对路径（Gateway 内部解析，不外发） */
    private String filePath;

    /** 文件 MIME 类型 */
    private String contentType;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 上报时长（秒，可空） */
    private Long durationSeconds;

    /** 临时落盘用的扩展名后缀（依据 contentType/常见容器），始终以点开头 */
    public String getFileNameSuffix() {
        String type = contentType == null ? "" : contentType.toLowerCase();
        if (type.contains("mp4") || type.contains("m4a")) {
            return ".mp4";
        }
        if (type.contains("ogg")) {
            return ".ogg";
        }
        if (type.contains("mpeg")) {
            return ".mpeg";
        }
        return ".webm";
    }
}
