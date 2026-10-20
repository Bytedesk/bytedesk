package com.bytedesk.meet.transcript;

/**
 * 会议转写任务状态（2026-10-19 规划 §6.1）
 *
 * PENDING -> RUNNING -> SUCCEEDED / FAILED / NEEDS_REVIEW
 * PENDING/RUNNING -> CANCELED
 */
public enum MeetTranscriptStatusEnum {

    PENDING,

    RUNNING,

    SUCCEEDED,

    /** 供应商提交结果不明（提交成功但 taskId 未落库等不确定窗口），不能安全重放 */
    NEEDS_REVIEW,

    FAILED,

    CANCELED
}
