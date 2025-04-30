/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-30 10:44:16
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-30 12:55:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/visitor/api/v1/robot")
@AllArgsConstructor
public class RobotRestControllerVisitor {

    // private final RobotRestService robotRestService;

    // // rate message helpful
    // @PostMapping("/rate/message/helpful")
    // public ResponseEntity<?> rateMessageHelpful(@RequestBody FaqRequest request) {

    //     MessageResponse message = robotRestService.rateUp(request);

    //     return ResponseEntity.ok(JsonResult.success(message));
    // }

    // // rate message not helpful
    // @PostMapping("/rate/message/unhelpful")
    // public ResponseEntity<?> rateMessageNotHelpful(@RequestBody FaqRequest request) {
        
    //     MessageResponse message = robotRestService.rateDown(request);

    //     return ResponseEntity.ok(JsonResult.success(message));
    // }

    // // rate message feedback
    // @PostMapping("/rate/message/feedback")
    // public ResponseEntity<?> rateMessageFeedback(@RequestBody FaqRequest request) {

    //     MessageResponse message = robotRestService.rateFeedback(request);

    //     return ResponseEntity.ok(JsonResult.success(message));
    // }

    // // rate message transfer
    // @PostMapping("/rate/message/transfer")
    // public ResponseEntity<?> rateMessageTransfer(@RequestBody FaqRequest request) {

    //     MessageResponse message = robotRestService.rateTransfer(request);

    //     return ResponseEntity.ok(JsonResult.success(message));
    // }
    
}
