package com.bytedesk.core.notification;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/visitor/api/v1/notification")
@RequiredArgsConstructor
public class NotificationRestControllerVisitor {

    private final NotificationRestService notificationRestService;

    @GetMapping("/query")
    public ResponseEntity<?> queryByVisitor(NotificationRequest request) {
        // Use queryByOrg instead of queryByUser to preserve the userUid from the
        // request. queryByUser overwrites userUid with the authenticated user's UID,
        // which breaks visitor-notification queries where the visitor UID differs
        // from the authenticated user UID.
        Page<NotificationResponse> page = notificationRestService.queryByOrg(request);
        return ResponseEntity.ok(JsonResult.success(page));
    }

    @GetMapping("/query/unread/count")
    public ResponseEntity<?> countUnread(NotificationRequest request) {
        // 优先使用 visitorUid（前端自定义标识如 visitor_001）+ orgUid 查询，
        // 支持 TicketDemo 等外部页面直接用 visitorUid 获取未读通知数，
        // 无需先调用 visitor init 接口获取系统 uid。
        if (StringUtils.hasText(request.getVisitorUid())
                && StringUtils.hasText(request.getOrgUid())) {
            return ResponseEntity.ok(JsonResult.success(
                    notificationRestService.countUnreadByVisitorUid(request.getVisitorUid(), request.getOrgUid())));
        }
        return ResponseEntity.ok(JsonResult.success(notificationRestService.countUnread(request.getUserUid())));
    }

    @PostMapping("/read")
    public ResponseEntity<?> markRead(@RequestBody NotificationRequest request) {
        NotificationResponse response = notificationRestService.markRead(request.getUid(), request.getUserUid());
        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PostMapping("/read/all")
    public ResponseEntity<?> markAllRead(@RequestBody NotificationRequest request) {
        return ResponseEntity.ok(JsonResult.success(notificationRestService.markAllRead(request.getUserUid())));
    }
}