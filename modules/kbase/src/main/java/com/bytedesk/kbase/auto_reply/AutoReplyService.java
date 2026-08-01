package com.bytedesk.kbase.auto_reply;

import com.bytedesk.core.message.MessageProtobuf;

public interface AutoReplyService {

    /**
     * 访客发送机器人 SSE 消息前自动回复预检。
     * - hit=true: 前端直接展示自动回复并跳过 SSE
     * - hit=false: 前端继续走 sendSseMessage
     */
    VisitorAutoReplyCheckResult checkVisitorAutoReplyBeforeSse(
            String messageJson,
            String fallbackOrgUid,
            String autoReplySettingsUid);

    record VisitorAutoReplyCheckResult(boolean hit, MessageProtobuf answerMessage) {
    }
}
