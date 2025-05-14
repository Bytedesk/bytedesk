/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-14 16:20:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_text;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/llm/text")
@AllArgsConstructor
public class TextRestController extends BaseRestController<TextRequest> {

    private final TextRestService textRestService;

    private final TextElasticService textElasticService;

    private final TextVectorService textVectorService;

    // @PreAuthorize("hasAnyRole('SUPER', 'ADMIN')")
    @Override
    public ResponseEntity<?> queryByOrg(TextRequest request) {
        
        Page<TextResponse> texts = textRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(texts));
    }

    // @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    @Override
    public ResponseEntity<?> queryByUser(TextRequest request) {
        
        Page<TextResponse> texts = textRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(texts));
    }

    // @PreAuthorize("hasAuthority('KBASE_CREATE')")
    @Override
    public ResponseEntity<?> create(TextRequest request) {
        
        TextResponse text = textRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(text));
    }

    // @PreAuthorize("hasAuthority('KBASE_UPDATE')")
    @Override
    public ResponseEntity<?> update(TextRequest request) {
        
        TextResponse text = textRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(text));
    }

    // @PreAuthorize("hasAuthority('KBASE_DELETE')")
    @Override
    public ResponseEntity<?> delete(TextRequest request) {
        
        textRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @PostMapping("/deleteAll")
    public ResponseEntity<?> deleteAll(@RequestBody TextRequest request) {

        textRestService.deleteAll(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // enable/disable text
    @PostMapping("/enable")
    public ResponseEntity<?> enable(@RequestBody TextRequest request) {

        TextResponse textResponse = textRestService.enable(request);
        
        return ResponseEntity.ok(JsonResult.success(textResponse));
    }

    // @PreAuthorize("hasAuthority('KBASE_EXPORT')")
    @Override
    public Object export(TextRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            textRestService,
            TextExcel.class,
            "知识库文本",
            "text"
        );
    }

    // @PreAuthorize("hasAnyRole('SUPER', 'ADMIN', 'MEMBER', 'AGENT')")
    @Override
    public ResponseEntity<?> queryByUid(TextRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    // update elasticsearch index
    @ActionAnnotation(title = "知识库文本", action = "更新索引", description = "update text index")
    @PostMapping("/updateIndex")
    public ResponseEntity<?> updateIndex(@RequestBody TextRequest request) {

        textElasticService.updateIndex(request);
        
        return ResponseEntity.ok(JsonResult.success("update index success", request.getUid()));
    }

    // update elasticsearch vector index
    @ActionAnnotation(title = "知识库文本", action = "更新向量索引", description = "update text vector index")
    @PostMapping("/updateVectorIndex")
    public ResponseEntity<?> updateVectorIndex(@RequestBody TextRequest request) {

        textVectorService.updateVectorIndex(request);

        return ResponseEntity.ok(JsonResult.success("update vector index success", request.getUid()));
    }

    // update elasticsearch all index
    @ActionAnnotation(title = "知识库文本", action = "更新所有索引", description = "update all text index")
    @PostMapping("/updateAllIndex")
    public ResponseEntity<?> updateAllIndex(@RequestBody TextRequest request) {

        textElasticService.updateAllIndex(request);

        return ResponseEntity.ok(JsonResult.success("update all index success", request.getKbUid()));
    }

    // update elasticsearch all vector index
    @ActionAnnotation(title = "知识库文本", action = "更新所有向量索引", description = "update all text vector index")
    @PostMapping("/updateAllVectorIndex")
    public ResponseEntity<?> updateAllVectorIndex(@RequestBody TextRequest request) {

        textVectorService.updateAllVectorIndex(request);

        return ResponseEntity.ok(JsonResult.success("update all vector index success", request.getKbUid()));
    }
    
}