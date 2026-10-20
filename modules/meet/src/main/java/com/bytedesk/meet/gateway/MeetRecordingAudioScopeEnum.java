package com.bytedesk.meet.gateway;

/**
 * 录制音轨覆盖范围（2026-10-19 规划 §3.2）
 *
 * 客户端上报仅用于标识与排障，不作为完整性证明；历史记录为 UNKNOWN。
 */
public enum MeetRecordingAudioScopeEnum {

    /** 仅主持人本地麦克风（历史录制默认） */
    HOST_ONLY,

    /** 主持人本地 + 已订阅远端音频混音 */
    MIXED_SUBSCRIBED,

    /** 未知（历史记录/未上报） */
    UNKNOWN
}
