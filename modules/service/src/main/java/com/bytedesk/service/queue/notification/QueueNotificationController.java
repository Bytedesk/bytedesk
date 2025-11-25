package com.bytedesk.service.queue.notification;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "队列通知", description = "客服队列通知接口")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/queue")
public class QueueNotificationController {

    // private final QueueNotificationService queueNotificationService;

    // @PostMapping("/agent/{agentUid}/notifications")
    // @Operation(summary = "发送客服队列通知", description = "向指定客服的队列线程推送队列变化通知")
    // @ApiResponse(responseCode = "202", description = "通知已接受",
    //         content = @Content(schema = @Schema(implementation = JsonResult.class)))
    // public ResponseEntity<?> publishAgentNotification(
    //         @PathVariable String agentUid,
    //         @Valid @RequestBody QueueNotificationRequest request) {
    //     queueNotificationService.publishAgentNotification(agentUid, request);
    //     return ResponseEntity.status(HttpStatus.ACCEPTED).body(JsonResult.success());
    // }
}
