package com.bytedesk.meet.minutes;

/**
 * 会议纪要任务状态（2026-10-19 规划 §7）
 *
 * 与转写任务恢复原则一致：PENDING/RUNNING/SUCCEEDED/FAILED/CANCELED/NEEDS_REVIEW
 */
public enum MeetMinutesStatusEnum {

    PENDING,

    RUNNING,

    SUCCEEDED,

    NEEDS_REVIEW,

    FAILED,

    CANCELED
}
