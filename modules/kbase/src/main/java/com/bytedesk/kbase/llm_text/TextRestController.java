/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-08 19:46:56
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.kbase.llm_text.elastic.TextElasticService;
import com.bytedesk.kbase.llm_text.vector.TextVectorService;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;

@RestController
@RequestMapping("/api/v1/llm/text")
@Description("Text Management Controller - LLM text content management and vector processing APIs")
public class TextRestController extends BaseRestController<TextRequest, TextRestService> {

    private final TextRestService textRestService;

    private final TextElasticService textElasticService;

    @Autowired(required = false)
    private TextVectorService textVectorService;

    public TextRestController(TextRestService textRestService, TextElasticService textElasticService) {
        this.textRestService = textRestService;
        this.textElasticService = textElasticService;
    }

    @PreAuthorize(TextPermissions.HAS_TEXT_READ)
    @ActionAnnotation(title = "知识库文本", action = "组织查询", description = "query text by org")
    @Override
    public ResponseEntity<?> queryByOrg(TextRequest request) {
        
        Page<TextResponse> texts = textRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(texts));
    }

    @PreAuthorize(TextPermissions.HAS_TEXT_READ)
    @ActionAnnotation(title = "知识库文本", action = "用户查询", description = "query text by user")
    @Override
    public ResponseEntity<?> queryByUser(TextRequest request) {
        
        Page<TextResponse> texts = textRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(texts));
    }

    @PreAuthorize(TextPermissions.HAS_TEXT_CREATE)
    @ActionAnnotation(title = "知识库文本", action = "新建", description = "create text")
    @Override
    public ResponseEntity<?> create(TextRequest request) {
        
        TextResponse text = textRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(text));
    }

    @PreAuthorize(TextPermissions.HAS_TEXT_UPDATE)
    @ActionAnnotation(title = "知识库文本", action = "更新", description = "update text")
    @Override
    public ResponseEntity<?> update(TextRequest request) {
        
        TextResponse text = textRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(text));
    }

    @PreAuthorize(TextPermissions.HAS_TEXT_DELETE)
    @ActionAnnotation(title = "知识库文本", action = "删除", description = "delete text")
    @Override
    public ResponseEntity<?> delete(TextRequest request) {
        
        textRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @PreAuthorize(TextPermissions.HAS_TEXT_DELETE)
    @ActionAnnotation(title = "知识库文本", action = "删除所有", description = "delete all text")
    @PostMapping("/deleteAll")
    public ResponseEntity<?> deleteAll(@RequestBody TextRequest request) {

        textRestService.deleteAll(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // enable/disable text
    @PreAuthorize(TextPermissions.HAS_TEXT_UPDATE)
    @ActionAnnotation(title = "知识库文本", action = "启用", description = "enable text")
    @PostMapping("/enable")
    public ResponseEntity<?> enable(@RequestBody TextRequest request) {

        TextResponse textResponse = textRestService.enable(request);
        
        return ResponseEntity.ok(JsonResult.success(textResponse));
    }

    @PreAuthorize(TextPermissions.HAS_TEXT_EXPORT)
    @ActionAnnotation(title = "知识库文本", action = "导出", description = "export text")
    @GetMapping("/export")
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

    @PreAuthorize(TextPermissions.HAS_TEXT_READ)
    @Override
    public ResponseEntity<?> queryByUid(TextRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    // update elasticsearch index
    @PreAuthorize(TextPermissions.HAS_TEXT_UPDATE)
    @ActionAnnotation(title = "知识库文本", action = "更新索引", description = "update text index")
    @PostMapping("/updateIndex")
    public ResponseEntity<?> updateIndex(@RequestBody TextRequest request) {

        textElasticService.updateIndex(request);
        
        return ResponseEntity.ok(JsonResult.success("update index success", request.getUid()));
    }

    // delete elasticsearch index
    @PreAuthorize(TextPermissions.HAS_TEXT_UPDATE)
    @ActionAnnotation(title = "知识库文本", action = "删除索引", description = "delete text elastic index")
    @PostMapping("/deleteIndex")
    public ResponseEntity<?> deleteIndex(@RequestBody TextRequest request) {
        Boolean deleted = textElasticService.deleteIndexAndSyncStatus(request);
        return ResponseEntity.ok(JsonResult.success(deleted));
    }

    // sync elasticsearch index status
    @PreAuthorize(TextPermissions.HAS_TEXT_UPDATE)
    @ActionAnnotation(title = "知识库文本", action = "同步索引状态", description = "sync text elastic status")
    @PostMapping("/syncIndexStatus")
    public ResponseEntity<?> syncIndexStatus(@RequestBody TextRequest request) {
        var text = textElasticService.syncElasticStatus(request);
        return ResponseEntity.ok(JsonResult.success(text.getElasticStatus()));
    }

    // sync elasticsearch index status by kbUid
    @PreAuthorize(TextPermissions.HAS_TEXT_UPDATE)
    @ActionAnnotation(title = "知识库文本", action = "批量同步索引状态", description = "sync text elastic status by kb")
    @PostMapping("/syncIndexStatusByKbUid")
    public ResponseEntity<?> syncIndexStatusByKbUid(@RequestBody TextRequest request) {
        var result = textElasticService.syncElasticStatusByKbUid(request);
        return ResponseEntity.ok(JsonResult.success(result));
    }

    // delete all elasticsearch index by kbUid
    @PreAuthorize(TextPermissions.HAS_TEXT_UPDATE)
    @ActionAnnotation(title = "知识库文本", action = "知识库删除索引", description = "delete text elastic index by kb")
    @PostMapping("/deleteAllIndexByKbUid")
    public ResponseEntity<?> deleteAllIndexByKbUid(@RequestBody TextRequest request) {
        var result = textElasticService.deleteAllIndexByKbUidAndSyncStatus(request);
        return ResponseEntity.ok(JsonResult.success(result));
    }

    // update elasticsearch vector index
    @PreAuthorize(TextPermissions.HAS_TEXT_UPDATE)
    @ActionAnnotation(title = "知识库文本", action = "更新向量索引", description = "update text vector index")
    @PostMapping("/updateVectorIndex")
    public ResponseEntity<?> updateVectorIndex(@RequestBody TextRequest request) {

        if (textVectorService != null) {
            textVectorService.updateVectorIndex(request);
        }

        return ResponseEntity.ok(JsonResult.success("update vector index success", request.getUid()));
    }

    // delete vector index
    @PreAuthorize(TextPermissions.HAS_TEXT_UPDATE)
    @ActionAnnotation(title = "知识库文本", action = "删除向量索引", description = "delete text vector index")
    @PostMapping("/deleteVectorIndex")
    public ResponseEntity<?> deleteVectorIndex(@RequestBody TextRequest request) {
        if (textVectorService != null) {
            Boolean deleted = textVectorService.deleteVectorIndexAndSyncStatus(request);
            return ResponseEntity.ok(JsonResult.success(deleted));
        }
        return ResponseEntity.ok(JsonResult.error("vector service not enabled"));
    }

    // sync vector status
    @PreAuthorize(TextPermissions.HAS_TEXT_UPDATE)
    @ActionAnnotation(title = "知识库文本", action = "同步向量状态", description = "sync text vector status")
    @PostMapping("/syncVectorStatus")
    public ResponseEntity<?> syncVectorStatus(@RequestBody TextRequest request) {
        if (textVectorService != null) {
            var text = textVectorService.syncVectorStatus(request);
            return ResponseEntity.ok(JsonResult.success(text.getVectorStatus()));
        }
        return ResponseEntity.ok(JsonResult.error("vector service not enabled"));
    }

    @PreAuthorize(TextPermissions.HAS_TEXT_READ)
    @ActionAnnotation(title = "知识库文本", action = "查询全文索引", description = "query text elastic by uid")
    @PostMapping("/queryElasticByUid")
    public ResponseEntity<?> queryElasticByUid(@RequestBody TextRequest request) {
        var result = textElasticService.queryElasticByUid(request);
        return ResponseEntity.ok(JsonResult.success(result));
    }

    @PreAuthorize(TextPermissions.HAS_TEXT_READ)
    @ActionAnnotation(title = "知识库文本", action = "查询向量索引", description = "query text vector by uid")
    @PostMapping("/queryVectorByUid")
    public ResponseEntity<?> queryVectorByUid(@RequestBody TextRequest request) {
        if (textVectorService != null) {
            var result = textVectorService.queryVectorByUid(request);
            return ResponseEntity.ok(JsonResult.success(result));
        }
        return ResponseEntity.ok(JsonResult.error("vector service not enabled"));
    }

    // sync vector status by kbUid
    @PreAuthorize(TextPermissions.HAS_TEXT_UPDATE)
    @ActionAnnotation(title = "知识库文本", action = "批量同步向量状态", description = "sync text vector status by kb")
    @PostMapping("/syncVectorStatusByKbUid")
    public ResponseEntity<?> syncVectorStatusByKbUid(@RequestBody TextRequest request) {
        if (textVectorService != null) {
            var result = textVectorService.syncVectorStatusByKbUid(request);
            return ResponseEntity.ok(JsonResult.success(result));
        }
        return ResponseEntity.ok(JsonResult.error("vector service not enabled"));
    }

    // update elasticsearch all index
    @PreAuthorize(TextPermissions.HAS_TEXT_UPDATE)
    @ActionAnnotation(title = "知识库文本", action = "更新所有索引", description = "update all text index")
    @PostMapping("/updateAllIndex")
    public ResponseEntity<?> updateAllIndex(@RequestBody TextRequest request) {

        textElasticService.updateAllIndex(request);

        return ResponseEntity.ok(JsonResult.success("update all index success", request.getKbUid()));
    }

    // update elasticsearch all vector index
    @PreAuthorize(TextPermissions.HAS_TEXT_UPDATE)
    @ActionAnnotation(title = "知识库文本", action = "更新所有向量索引", description = "update all text vector index")
    @PostMapping("/updateAllVectorIndex")
    public ResponseEntity<?> updateAllVectorIndex(@RequestBody TextRequest request) {

        if (textVectorService != null) {
            textVectorService.updateAllVectorIndex(request);
        }

        return ResponseEntity.ok(JsonResult.success("update all vector index success", request.getKbUid()));
    }

    // delete all vector index by kbUid
    @PreAuthorize(TextPermissions.HAS_TEXT_UPDATE)
    @ActionAnnotation(title = "知识库文本", action = "知识库删除向量索引", description = "delete text vector index by kb")
    @PostMapping("/deleteAllVectorIndexByKbUid")
    public ResponseEntity<?> deleteAllVectorIndexByKbUid(@RequestBody TextRequest request) {
        if (textVectorService != null) {
            var result = textVectorService.deleteAllVectorIndexByKbUidAndSyncStatus(request);
            return ResponseEntity.ok(JsonResult.success(result));
        }
        return ResponseEntity.ok(JsonResult.error("vector service not enabled"));
    }
    
}