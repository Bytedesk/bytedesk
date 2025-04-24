/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-13 11:16:32
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-24 09:46:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm.qa;

import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/visitor/api/v1/llm/qa")
@AllArgsConstructor
public class QaRestControllerVisitor {

    private final QaService qaService;

    private final QaRestService qaRestService;

    private final MessageRestService messageRestService;

    // 输入联想搜索qa
    @Deprecated
    @GetMapping("/search")
    public ResponseEntity<?> search(QaRequest request) {

        List<QaEntity> qaList = qaRestService.findByQuestionContains(request.getQuestion());
        
        return ResponseEntity.ok(JsonResult.success(qaList));
    }

    // suggest qa
    @GetMapping("/suggest")
    public ResponseEntity<?> suggest(QaRequest request) {

        List<QaElasticSearchResult> suggestList = qaService.suggestQa(request.getQuestion(), request.getKbUid(), request.getCategoryUid(), request.getOrgUid());

        return ResponseEntity.ok(JsonResult.success(suggestList));
    }

    // 换一换qa
    @GetMapping("/change")
    public ResponseEntity<?> change(QaRequest request) {

        Page<QaResponse> page = qaRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    // query by uid
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(QaRequest request) {
        
        QaResponse qa = qaRestService.queryByUid(request);
        if (qa == null) {
            return ResponseEntity.ok(JsonResult.error("qa not found"));
        }
        return ResponseEntity.ok(JsonResult.success(qa));
    }

    // rate up qa
    @PostMapping("/rate/up")
    public ResponseEntity<?> rateUp(@RequestBody QaRequest request) {

        QaResponse qa = qaRestService.rateUp(request.getUid());

        return ResponseEntity.ok(JsonResult.success(qa));
    }

    // rate down qa
    @PostMapping("/rate/down")
    public ResponseEntity<?> rateDown(@RequestBody QaRequest request) {

        QaResponse qa = qaRestService.rateDown(request.getUid());

        return ResponseEntity.ok(JsonResult.success(qa));
    }

    // rate message helpful
    @PostMapping("/rate/message/helpful")
    public ResponseEntity<?> rateMessageHelpful(@RequestBody QaRequest request) {

        MessageResponse message = messageRestService.rateUp(request.getUid());

        return ResponseEntity.ok(JsonResult.success(message));
    }

    // rate message not helpful
    @PostMapping("/rate/message/unhelpful")
    public ResponseEntity<?> rateMessageNotHelpful(@RequestBody QaRequest request) {
        
        MessageResponse message = messageRestService.rateDown(request.getUid());

        return ResponseEntity.ok(JsonResult.success(message));
    }

    // comment qa
    @PostMapping("/comment")
    public ResponseEntity<?> comment(@RequestBody QaRequest request) {

        // QaResponse qa = qaService.comment(request);

        return ResponseEntity.ok(JsonResult.success());
    }
    
}
