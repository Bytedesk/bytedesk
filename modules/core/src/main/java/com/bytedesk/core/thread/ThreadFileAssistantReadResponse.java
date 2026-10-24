package com.bytedesk.core.thread;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * 文件助手会话“打开即已读”结果。
 *
 * <p>pendingMessageUids：客户端已观察到、但服务端尚未落库（异步持久化队列中）
 * 或归属异常的消息 uid。存在 pending 时客户端不得将未读数判定为“全部清零成功”，
 * 应做有限重试对账。
 */
@Data
@Builder
public class ThreadFileAssistantReadResponse {

    /** 会话 uid */
    private String threadUid;

    /** 本次实际置为 READ 的消息条数（幂等，无未读时为 0） */
    private Integer updatedCount;

    /** 操作完成后服务端权威未读数（按 ASSISTANT 口径） */
    private Integer unreadCount;

    /** 尚无法确认已读的消息 uid（未落库或归属不符） */
    @Builder.Default
    private List<String> pendingMessageUids = new ArrayList<>();
}
