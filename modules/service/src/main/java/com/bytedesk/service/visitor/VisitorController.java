/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-04 15:03:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.apilimit.ApiRateLimiter;
import com.bytedesk.core.base.BaseController;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.message_unread.MessageUnreadService;
import com.bytedesk.core.socket.service.MqService;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.service.leave_msg.LeaveMessageRequest;
import com.bytedesk.service.leave_msg.LeaveMessageResponse;
import com.bytedesk.service.leave_msg.LeaveMessageService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * anonymous api, no need to login
 * http://127.0.0.1:9003/swagger-ui/index.html
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/visitor/api/v1/")
public class VisitorController extends BaseController<VisitorRequest> {

    private final VisitorService visitorService;

    private final MqService stompMqService;

    private final MessageUnreadService messageUnreadService;

    private final LeaveMessageService leaveMessageService;

    @Override
    public ResponseEntity<?> queryByOrg(VisitorRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByOrg'");
    }

    /**
     * TODO: pre init use in web embeded button used for tracking & pre fetch
     * settings
     * 
     * @param request
     * @return
     */
    @GetMapping("/pre")
    public ResponseEntity<?> pre(HttpServletRequest request) {
        //
        return ResponseEntity.ok(JsonResult.success("pre"));
    }

    /**
     * init visitor cookies in browser & generate visitor in db
     * 
     * considering multi request from different clients, including ios/android/web,
     * apis should not use cookies which is specific to web browsers
     * http://127.0.0.1:9003/visitor/api/v1/init
     * 
     * @param visitorRequest
     * @return
     */
    @ApiRateLimiter(value = 10.0, timeout = 1)
    @GetMapping("/init")
    public ResponseEntity<?> init(VisitorRequest visitorRequest, HttpServletRequest request) {
        //
        VisitorProtobuf visitor = visitorService.create(visitorRequest, request);
        if (visitor == null) {
            return ResponseEntity.ok(JsonResult.error("init visitor failed", -1));
        }
        return ResponseEntity.ok(JsonResult.success(visitor));
    }

    /**
     * request thread
     * 
     * @param visitorRequest
     * @return
     */
    @VisitorAnnotation(title = "visitor", action = "requestThread", description = "request thread")
    @GetMapping("/thread")
    public ResponseEntity<?> requestThread(VisitorRequest visitorRequest, HttpServletRequest request) {
        //
        MessageProtobuf messageProtobuf = visitorService.createCsThread(visitorRequest);
        //
        return ResponseEntity.ok(JsonResult.success(messageProtobuf));
    }

    // query visitor info by uid
    @GetMapping("/query")
    public ResponseEntity<?> query(VisitorRequest visitorRequest) {
        //
        VisitorResponse visitorResponse = visitorService.query(visitorRequest);
        //
        return ResponseEntity.ok(JsonResult.success(visitorResponse));
    }

    /**
     * update
     *
     * @param visitorRequest visitor
     * @return json
     */
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody VisitorRequest visitorRequest) {
        //
        return ResponseEntity.ok(JsonResult.success("update success"));
    }

    /**
     * delete
     *
     * @param visitorRequest visitor
     * @return json
     */
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody VisitorRequest visitorRequest) {
        //
        return ResponseEntity.ok(JsonResult.success("delete success"));
    }

    /**
     * 客户端定期ping，返回未读消息数，如果大于0，则客户端拉取未读消息
     * 
     * @return
     */
    @GetMapping("/ping")
    public ResponseEntity<?> ping(VisitorRequest request) {

        int count = messageUnreadService.getUnreadCount(request.getUid());

        return ResponseEntity.ok(JsonResult.success("pong", count));
    }

    // 访客拉取未读消息
     @GetMapping("/message/unread")
    public ResponseEntity<?> getMessageUnread(VisitorRequest request) {
        //
        List<MessageResponse> messages = messageUnreadService.getMessages(request.getUid());

        return ResponseEntity.ok(JsonResult.success("get unread messages success", messages));
    }


    /**
     * send offline message
     * 
     * @param map map
     * @return json
     */
    @VisitorAnnotation(title = "visitor", action = "sendOfflineMessage", description = "in order to filter visitor")
    @PostMapping("/message/send")
    public ResponseEntity<?> sendOfflineMessage(@RequestBody Map<String, String> map) {
        //
        String json = (String) map.get("json");
        log.debug("json {}", json);
        stompMqService.sendJsonMessageToMq(json);
        //
        return ResponseEntity.ok(JsonResult.success(json));
    }

    
    // 访客留言
    @VisitorAnnotation(title = "visitor", action = "leaveMessage", description = "leave_msg")
    @PostMapping("/leavemsg")
    public ResponseEntity<?> leaveMessage(@RequestBody LeaveMessageRequest request) {

        LeaveMessageResponse response = leaveMessageService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @VisitorAnnotation(title = "visitor", action = "rateInitiative", description = "rateInitiative")
    // 访客主动发起评价
    @PostMapping("/rate/initiative")
    public ResponseEntity<?> rateInitiative(@RequestBody Map<String, String> map) {
        //
        return ResponseEntity.ok(JsonResult.success());
    }

    @VisitorAnnotation(title = "visitor", action = "rateDo", description = "rateDo")
    // 访客提交评价
    @PostMapping("/rate/do")
    public ResponseEntity<?> rateDo(@RequestBody Map<String, String> map) {
        //
        return ResponseEntity.ok(JsonResult.success());
    }

    @VisitorAnnotation(title = "visitor", action = "quickButtonMessage", description = "quickButtonMessage")
    @PostMapping("/quickbutton/send")
    public ResponseEntity<?> quickButtonMessage() {
        //

        return ResponseEntity.ok(JsonResult.success("test success"));
    }

    @VisitorAnnotation(title = "visitor", action = "faqMessage", description = "faqMessage")
    @PostMapping("/faq/send")
    public ResponseEntity<?> faqMessage() {

        return ResponseEntity.ok(JsonResult.success("test success"));
    }

    @Override
    public ResponseEntity<?> create(VisitorRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

}
