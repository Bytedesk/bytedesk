/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 22:59:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-08 19:46:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq;

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
import com.bytedesk.kbase.faq.elastic.FaqElasticService;
import com.bytedesk.kbase.faq.vector.FaqVectorService;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "常见问题管理", description = "常见问题管理相关接口")
@RestController
@RequestMapping("/api/v1/faq")
public class FaqRestController extends BaseRestController<FaqRequest, FaqRestService> {

    private final FaqRestService faqRestService;

    private final FaqElasticService faqElasticService;

    @Autowired(required = false)
    private FaqVectorService faqVectorService;

    public FaqRestController(FaqRestService faqRestService, FaqElasticService faqElasticService) {
        this.faqRestService = faqRestService;
        this.faqElasticService = faqElasticService;
    }

    @Operation(summary = "查询组织下的常见问题", description = "根据组织ID查询常见问题列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FaqResponse.class)))
    // @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByOrg(FaqRequest request) {

        Page<FaqResponse> page = faqRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "查询用户下的常见问题", description = "根据用户ID查询常见问题列表")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FaqResponse.class)))
    // @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByUser(FaqRequest request) {
        
        Page<FaqResponse> page = faqRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "查询指定常见问题", description = "根据UID查询常见问题详情")
    @ApiResponse(responseCode = "200", description = "查询成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FaqResponse.class)))
    // @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByUid(FaqRequest request) {
        
        FaqResponse faq = faqRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(faq));
    }

    @Operation(summary = "创建常见问题", description = "创建新的常见问题")
    @ApiResponse(responseCode = "200", description = "创建成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FaqResponse.class)))
    @ActionAnnotation(title = "常见问题", action = "新建", description = "create faq")
    @PreAuthorize("hasAuthority('KBASE_CREATE')")
    @Override
    public ResponseEntity<?> create(@RequestBody FaqRequest request) {

        FaqResponse Faq = faqRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(Faq));
    }

    @Operation(summary = "更新常见问题", description = "更新常见问题信息")
    @ApiResponse(responseCode = "200", description = "更新成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FaqResponse.class)))
    @ActionAnnotation(title = "常见问题", action = "更新", description = "update faq")
    @PreAuthorize("hasAuthority('KBASE_UPDATE')")
    @Override
    public ResponseEntity<?> update(@RequestBody FaqRequest request) {

        FaqResponse Faq = faqRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(Faq));
    }

    @Operation(summary = "删除常见问题", description = "删除指定的常见问题")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @ActionAnnotation(title = "常见问题", action = "删除", description = "delete faq")
    @PreAuthorize("hasAuthority('KBASE_DELETE')")
    @Override
    public ResponseEntity<?> delete(@RequestBody FaqRequest request) {

        faqRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success("delete success", request.getUid()));
    }

    @Operation(summary = "删除所有常见问题", description = "删除所有常见问题")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @ActionAnnotation(title = "常见问题", action = "删除所有", description = "delete faq all")
    @PreAuthorize("hasAuthority('KBASE_DELETE')")
    @PostMapping("/deleteAll")
    public ResponseEntity<?> deleteAll(@RequestBody FaqRequest request) {

        faqRestService.delateAll(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "启用常见问题", description = "启用或禁用常见问题")
    @ApiResponse(responseCode = "200", description = "操作成功",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FaqResponse.class)))
    @ActionAnnotation(title = "常见问题", action = "启用", description = "enable faq")
    @PostMapping("/enable")
    public ResponseEntity<?> enable(@RequestBody FaqRequest request) {

        FaqResponse faqResponse = faqRestService.enable(request);

        return ResponseEntity.ok(JsonResult.success(faqResponse));
    }

    @Operation(summary = "导出常见问题", description = "导出常见问题数据")
    @ApiResponse(responseCode = "200", description = "导出成功")
    @ActionAnnotation(title = "常见问题", action = "导出", description = "export faq")
    @PreAuthorize("hasAuthority('KBASE_EXPORT')")
    @GetMapping("/export")
    public Object export(FaqRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            faqRestService,
            FaqExcel.class,
            "常见问题",
            "faq"
        );
    }

    @Operation(summary = "更新常见问题索引", description = "更新常见问题的Elasticsearch索引")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @ActionAnnotation(title = "常见问题", action = "更新索引", description = "update faq index")
    @PostMapping("/updateIndex")
    public ResponseEntity<?> updateIndex(@RequestBody FaqRequest request) {

        faqElasticService.updateIndex(request);

        return ResponseEntity.ok(JsonResult.success("update index success", request.getUid()));
    }

    @Operation(summary = "更新常见问题向量索引", description = "更新常见问题的向量索引")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @ActionAnnotation(title = "常见问题", action = "更新向量索引", description = "update faq vector index")
    @PostMapping("/updateVectorIndex")
    public ResponseEntity<?> updateVectorIndex(@RequestBody FaqRequest request) {

        if (faqVectorService != null) {
            faqVectorService.updateVectorIndex(request);
        }

        return ResponseEntity.ok(JsonResult.success("update vector index success", request.getUid()));
    }

    @Operation(summary = "更新所有常见问题索引", description = "更新所有常见问题的Elasticsearch索引")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @ActionAnnotation(title = "常见问题", action = "更新所有索引", description = "update all faq index")
    @PostMapping("/updateAllIndex")
    public ResponseEntity<?> updateAllIndex(@RequestBody FaqRequest request) {

        faqElasticService.updateAllIndex(request);

        return ResponseEntity.ok(JsonResult.success("update all index success", request.getUid()));
    }

    @Operation(summary = "更新所有常见问题向量索引", description = "更新所有常见问题的向量索引")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @ActionAnnotation(title = "常见问题", action = "更新所有向量索引", description = "update all faq vector index")
    @PostMapping("/updateAllVectorIndex")
    public ResponseEntity<?> updateAllVectorIndex(@RequestBody FaqRequest request) {

        if (faqVectorService != null) {
            faqVectorService.updateAllVectorIndex(request);
        }

        return ResponseEntity.ok(JsonResult.success("update all vector index success", request.getUid()));
    }

    // ai 依赖于 kbase, 不能在此调用 ai，所以迁移到 ai 模块
    // generateSimilarQuestions
    // @ActionAnnotation(title = "常见问题", action = "生成相似问题", description = "generate similar questions")
    // @PostMapping("/generateSimilarQuestions")
    // public ResponseEntity<?> generateSimilarQuestions(@RequestBody FaqRequest request) {

    //     List<String> similarQuestions = faqRestService.generateSimilarQuestions(request);

    //     return ResponseEntity.ok(JsonResult.success(similarQuestions));
    // }

}
