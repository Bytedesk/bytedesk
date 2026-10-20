package com.bytedesk.meet.transcript;

/**
 * 会议转写任务执行阶段（2026-10-19 规划 §6.1）
 *
 * PREPARING: ffmpeg 预处理/分块；TRANSCRIBING: 供应商识别中；SAVING: 分段落库/发布
 */
public enum MeetTranscriptStageEnum {

    PREPARING,

    TRANSCRIBING,

    SAVING
}
