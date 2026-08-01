package com.bytedesk.core.thread;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.thread.enums.ThreadCloseTypeEnum;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Visitor-facing thread API (anonymous, no login required)
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/visitor/api/v1/thread")
@Tag(name = "访客会话管理", description = "访客会话相关匿名接口")
public class ThreadRestControllerVisitor {

    private final ThreadRestService threadRestService;

    /**
     * Get message count for a thread (used for rateMsgCount validation)
     * GET /visitor/api/v1/thread/message/count?uid=xxx
     */
    @GetMapping("/message/count")
    public ResponseEntity<?> getThreadMessageCount(@RequestParam("uid") String threadUid) {
        Optional<ThreadEntity> threadOptional = threadRestService.findByUid(threadUid);
        if (threadOptional.isEmpty()) {
            return ResponseEntity.ok(JsonResult.success(0));
        }
        int count = threadOptional.get().getAllMessageCount();
        return ResponseEntity.ok(JsonResult.success(count));
    }

    /**
     * 访客主动关闭会话
     * POST /visitor/api/v1/thread/close
     * Body: { "threadUid": "xxx", "channel": "WEB" }
     */
    @PostMapping("/close")
    @Operation(summary = "访客关闭会话", description = "访客主动结束当前会话，将会话状态设置为已关闭")
    public ResponseEntity<?> closeThread(@RequestBody Map<String, String> body) {
        String threadUid = body.get("threadUid");
        if (threadUid == null || threadUid.isBlank()) {
            return ResponseEntity.ok(JsonResult.error("threadUid is required"));
        }

        Optional<ThreadEntity> threadOptional = threadRestService.findByUid(threadUid);
        if (threadOptional.isEmpty()) {
            return ResponseEntity.ok(JsonResult.error("thread not found: " + threadUid));
        }

        ThreadRequest request = ThreadRequest.builder()
                .uid(threadUid)
                .topic(threadOptional.get().getTopic())
                .closeType(ThreadCloseTypeEnum.VISITOR.name())
                .build();
        ThreadResponse response = threadRestService.closeByUid(request);
        return ResponseEntity.ok(JsonResult.success("会话已关闭", response));
    }

    /**
     * 访客标记会话消息为已读
     * POST /visitor/api/v1/thread/markRead
     * Body: { "threadUid": "xxx" }
     */
    @PostMapping("/markRead")
    @Operation(summary = "标记会话已读", description = "访客标记当前会话中客服消息为已读，清除visitorUnreadCount")
    public ResponseEntity<?> markRead(@RequestBody Map<String, String> body) {
        String threadUid = body.get("threadUid");
        if (threadUid == null || threadUid.isBlank()) {
            return ResponseEntity.ok(JsonResult.error("threadUid is required"));
        }

        Optional<ThreadEntity> threadOptional = threadRestService.findByUid(threadUid);
        if (threadOptional.isEmpty()) {
            return ResponseEntity.ok(JsonResult.error("thread not found: " + threadUid));
        }

        threadRestService.markVisitorMessagesRead(threadUid);
        return ResponseEntity.ok(JsonResult.success("标记已读成功"));
    }
}
