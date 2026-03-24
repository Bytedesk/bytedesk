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
package com.bytedesk.kbase.llm_faq;

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
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.kbase.llm_faq.elastic.FaqElasticService;
import com.bytedesk.kbase.llm_faq.vector.FaqVectorService;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Tag(name = "FAQ Management", description = "FAQ management APIs")
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

    @Operation(summary = "Query FAQs by Organization", description = "Query the list of FAQs by organization ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FaqResponse.class)))
    @PreAuthorize(FaqPermissions.HAS_FAQ_READ)
    @ActionAnnotation(title = I18Consts.I18N_FAQ, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query faq by org")
    @GetMapping("/query/org")
    @Override
    public ResponseEntity<?> queryByOrg(FaqRequest request) {

        Page<FaqResponse> page = faqRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query FAQs by User", description = "Query the list of FAQs by user ID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FaqResponse.class)))
    @PreAuthorize(FaqPermissions.HAS_FAQ_READ)
    @ActionAnnotation(title = I18Consts.I18N_FAQ, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query faq by user")
    @GetMapping({ "/query", "/query/user" })
    @Override
    public ResponseEntity<?> queryByUser(FaqRequest request) {
        
        Page<FaqResponse> page = faqRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Operation(summary = "Query FAQ by UID", description = "Query FAQ details by UID")
    @ApiResponse(responseCode = "200", description = "Query successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FaqResponse.class)))
    @PreAuthorize(FaqPermissions.HAS_FAQ_READ)
    @ActionAnnotation(title = I18Consts.I18N_FAQ, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query faq by uid")
    @GetMapping("/query/uid")
    @Override
    public ResponseEntity<?> queryByUid(FaqRequest request) {
        
        FaqResponse faq = faqRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(faq));
    }

    @Operation(summary = "Create FAQ", description = "Create a new FAQ")
    @ApiResponse(responseCode = "200", description = "Creation successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FaqResponse.class)))
    @PreAuthorize(FaqPermissions.HAS_FAQ_CREATE)
    @ActionAnnotation(title = I18Consts.I18N_FAQ, action = I18Consts.I18N_ACTION_CREATE, description = "create faq")
    @PostMapping("/create")
    @Override
    public ResponseEntity<?> create(@RequestBody FaqRequest request) {

        FaqResponse Faq = faqRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(Faq));
    }

    @Operation(summary = "Update FAQ", description = "Update FAQ information")
    @ApiResponse(responseCode = "200", description = "Update successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FaqResponse.class)))
    @PreAuthorize(FaqPermissions.HAS_FAQ_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_FAQ, action = I18Consts.I18N_ACTION_UPDATE, description = "update faq")
    @PostMapping("/update")
    @Override
    public ResponseEntity<?> update(@RequestBody FaqRequest request) {

        FaqResponse Faq = faqRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(Faq));
    }

    @Operation(summary = "Delete FAQ", description = "Delete the specified FAQ")
    @ApiResponse(responseCode = "200", description = "Deletion successful")
    @PreAuthorize(FaqPermissions.HAS_FAQ_DELETE)
    @ActionAnnotation(title = I18Consts.I18N_FAQ, action = I18Consts.I18N_ACTION_DELETE, description = "delete faq")
    @PostMapping("/delete")
    @Override
    public ResponseEntity<?> delete(@RequestBody FaqRequest request) {

        // 删除FAQ的同时，同步删除全文索引与向量索引（向量服务可选）
        faqElasticService.deleteIndexAndSyncStatus(request);
        if (faqVectorService != null) {
            faqVectorService.deleteVectorIndexAndSyncStatus(request);
        }

        faqRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success("delete success", request.getUid()));
    }

    @Operation(summary = "Delete All FAQs", description = "Delete all FAQs")
    @ApiResponse(responseCode = "200", description = "Deletion successful")
    @PreAuthorize(FaqPermissions.HAS_FAQ_DELETE)
    @ActionAnnotation(title = I18Consts.I18N_FAQ, action = I18Consts.I18N_ACTION_DELETE_ALL, description = "delete faq all")
    @PostMapping("/deleteAll")
    public ResponseEntity<?> deleteAll(@RequestBody FaqRequest request) {

        // 删除全部FAQ前，同步删除全文索引与向量索引，并同步更新状态
        faqElasticService.deleteAllIndexByKbUidAndSyncStatus(request);
        if (faqVectorService != null) {
            faqVectorService.deleteAllVectorIndexByKbUidAndSyncStatus(request);
        }

        faqRestService.delateAll(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    @Operation(summary = "Delete All FAQ Vector Indexes by Knowledge Base", description = "Delete all FAQ vector indexes under the current knowledge base by kbUid and sync the FAQ entity vectorStatus")
    @ApiResponse(responseCode = "200", description = "Deletion successful")
    @PreAuthorize(FaqPermissions.HAS_FAQ_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_FAQ, action = I18Consts.I18N_ACTION_DELETE_VECTOR_INDEX_BY_KB, description = "delete faq vector index by kbUid")
    @PostMapping("/deleteAllVectorIndexByKbUid")
    public ResponseEntity<?> deleteAllVectorIndexByKbUid(@RequestBody FaqRequest request) {
        if (faqVectorService == null) {
            return ResponseEntity.ok(JsonResult.error("vector store not enabled"));
        }
        var result = faqVectorService.deleteAllVectorIndexByKbUidAndSyncStatus(request);
        return ResponseEntity.ok(JsonResult.success(result));
    }

    @Operation(summary = "Enable FAQ", description = "Enable or disable the FAQ")
    @ApiResponse(responseCode = "200", description = "Operation successful",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = FaqResponse.class)))
    @PreAuthorize(FaqPermissions.HAS_FAQ_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_FAQ, action = I18Consts.I18N_ACTION_ENABLE, description = "enable faq")
    @PostMapping("/enable")
    public ResponseEntity<?> enable(@RequestBody FaqRequest request) {

        FaqResponse faqResponse = faqRestService.enable(request);

        return ResponseEntity.ok(JsonResult.success(faqResponse));
    }

    @Operation(summary = "Export FAQs", description = "Export FAQ data")
    @ApiResponse(responseCode = "200", description = "Export successful")
    @PreAuthorize(FaqPermissions.HAS_FAQ_EXPORT)
    @ActionAnnotation(title = I18Consts.I18N_FAQ, action = I18Consts.I18N_ACTION_EXPORT, description = "export faq")
    @GetMapping("/export")
    public Object export(FaqRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            faqRestService,
            FaqExcel.class,
            "FAQ",
            "faq"
        );
    }

    @Operation(summary = "Update FAQ Index", description = "Update the Elasticsearch index for the FAQ")
    @ApiResponse(responseCode = "200", description = "Update successful")
    @PreAuthorize(FaqPermissions.HAS_FAQ_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_FAQ, action = I18Consts.I18N_ACTION_UPDATE_INDEX, description = "update faq index")
    @PostMapping("/updateIndex")
    public ResponseEntity<?> updateIndex(@RequestBody FaqRequest request) {

        faqElasticService.updateIndex(request);

        return ResponseEntity.ok(JsonResult.success("update index success", request.getUid()));
    }

    @Operation(summary = "Delete FAQ Index", description = "Delete the FAQ index in Elasticsearch and sync the FAQ entity index status")
    @ApiResponse(responseCode = "200", description = "Deletion successful")
    @PreAuthorize(FaqPermissions.HAS_FAQ_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_FAQ, action = I18Consts.I18N_ACTION_DELETE_INDEX, description = "delete faq elastic index")
    @PostMapping("/deleteIndex")
    public ResponseEntity<?> deleteIndex(@RequestBody FaqRequest request) {
        Boolean deleted = faqElasticService.deleteIndexAndSyncStatus(request);
        return ResponseEntity.ok(JsonResult.success(deleted));
    }

    @Operation(summary = "Sync FAQ Index Status", description = "Check whether the FAQ index exists in Elasticsearch and sync the FAQ entity elasticStatus")
    @ApiResponse(responseCode = "200", description = "Sync successful")
    @PreAuthorize(FaqPermissions.HAS_FAQ_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_FAQ, action = I18Consts.I18N_ACTION_SYNC_INDEX_STATUS, description = "sync faq elastic status")
    @PostMapping("/syncIndexStatus")
    public ResponseEntity<?> syncIndexStatus(@RequestBody FaqRequest request) {
        var faq = faqElasticService.syncElasticStatus(request);
        return ResponseEntity.ok(JsonResult.success(faq.getElasticStatus()));
    }

    @Operation(summary = "Batch Sync FAQ Index Status", description = "Batch check whether FAQ indexes exist in Elasticsearch by knowledge base kbUid and sync the FAQ entity elasticStatus")
    @ApiResponse(responseCode = "200", description = "Sync successful")
    @PreAuthorize(FaqPermissions.HAS_FAQ_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_FAQ, action = I18Consts.I18N_ACTION_BATCH_SYNC_INDEX_STATUS, description = "sync faq elastic status by kb")
    @PostMapping("/syncIndexStatusByKbUid")
    public ResponseEntity<?> syncIndexStatusByKbUid(@RequestBody FaqRequest request) {
        var result = faqElasticService.syncElasticStatusByKbUid(request);
        return ResponseEntity.ok(JsonResult.success(result));
    }

    @Operation(summary = "Delete All FAQ Indexes by Knowledge Base", description = "Delete FAQ indexes in Elasticsearch by knowledge base kbUid and sync the FAQ entity elasticStatus")
    @ApiResponse(responseCode = "200", description = "Deletion successful")
    @PreAuthorize(FaqPermissions.HAS_FAQ_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_FAQ, action = I18Consts.I18N_ACTION_DELETE_INDEX_BY_KB, description = "delete faq elastic index by kb")
    @PostMapping("/deleteAllIndexByKbUid")
    public ResponseEntity<?> deleteAllIndexByKbUid(@RequestBody FaqRequest request) {
        var result = faqElasticService.deleteAllIndexByKbUidAndSyncStatus(request);
        return ResponseEntity.ok(JsonResult.success(result));
    }

    @Operation(summary = "Update FAQ Vector Index", description = "Update the vector index for the FAQ")
    @ApiResponse(responseCode = "200", description = "Update successful")
    @PreAuthorize(FaqPermissions.HAS_FAQ_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_FAQ, action = I18Consts.I18N_ACTION_UPDATE_VECTOR_INDEX, description = "update faq vector index")
    @PostMapping("/updateVectorIndex")
    public ResponseEntity<?> updateVectorIndex(@RequestBody FaqRequest request) {

        if (faqVectorService != null) {
            faqVectorService.updateVectorIndex(request);
        } else {
            return ResponseEntity.ok(JsonResult.error("vector service not enabled"));
        }

        return ResponseEntity.ok(JsonResult.success("update vector index success", request.getUid()));
    }

    @Operation(summary = "Delete FAQ Vector Index", description = "Delete the FAQ index in vector storage and sync the FAQ entity vector index status")
    @ApiResponse(responseCode = "200", description = "Deletion successful")
    @PreAuthorize(FaqPermissions.HAS_FAQ_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_FAQ, action = I18Consts.I18N_ACTION_DELETE_VECTOR_INDEX, description = "delete faq vector index")
    @PostMapping("/deleteVectorIndex")
    public ResponseEntity<?> deleteVectorIndex(@RequestBody FaqRequest request) {

        if (faqVectorService != null) {
            Boolean deleted = faqVectorService.deleteVectorIndexAndSyncStatus(request);
            return ResponseEntity.ok(JsonResult.success(deleted));
        } else {
            return ResponseEntity.ok(JsonResult.error("vector service not enabled"));
        }
    }

    @Operation(summary = "Sync FAQ Vector Status", description = "Check whether the FAQ vector index exists in vector storage and sync the FAQ entity vectorStatus")
    @ApiResponse(responseCode = "200", description = "Sync successful")
    @PreAuthorize(FaqPermissions.HAS_FAQ_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_FAQ, action = I18Consts.I18N_ACTION_SYNC_VECTOR_STATUS, description = "sync faq vector status")
    @PostMapping("/syncVectorStatus")
    public ResponseEntity<?> syncVectorStatus(@RequestBody FaqRequest request) {
        
        if (faqVectorService != null) {
            var faq = faqVectorService.syncVectorStatus(request);
            return ResponseEntity.ok(JsonResult.success(faq.getVectorStatus()));
        } else {
            return ResponseEntity.ok(JsonResult.error("vector service not enabled"));
        }
    }

    @Operation(summary = "Query FAQ Full-Text Index", description = "Query the index document in Elasticsearch by FAQ UID")
    @ApiResponse(responseCode = "200", description = "Query successful")
    @PreAuthorize(FaqPermissions.HAS_FAQ_READ)
    @ActionAnnotation(title = I18Consts.I18N_FAQ, action = I18Consts.I18N_ACTION_QUERY_ELASTIC_INDEX, description = "query faq elastic by uid")
    @PostMapping("/queryElasticByUid")
    public ResponseEntity<?> queryElasticByUid(@RequestBody FaqRequest request) {
        var result = faqElasticService.queryElasticByUid(request);
        return ResponseEntity.ok(JsonResult.success(result));
    }

    @Operation(summary = "Query FAQ Vector Index", description = "Query the index document in vector storage by FAQ UID")
    @ApiResponse(responseCode = "200", description = "Query successful")
    @PreAuthorize(FaqPermissions.HAS_FAQ_READ)
    @ActionAnnotation(title = I18Consts.I18N_FAQ, action = I18Consts.I18N_ACTION_QUERY_VECTOR_INDEX, description = "query faq vector by uid")
    @PostMapping("/queryVectorByUid")
    public ResponseEntity<?> queryVectorByUid(@RequestBody FaqRequest request) {
        if (faqVectorService != null) {
            var result = faqVectorService.queryVectorByUid(request);
            return ResponseEntity.ok(JsonResult.success(result));
        }
        return ResponseEntity.ok(JsonResult.error("vector service not enabled"));
    }

    @Operation(summary = "Batch Sync FAQ Vector Status", description = "Batch check whether FAQ vector indexes exist in vector storage by knowledge base kbUid and sync the FAQ entity vectorStatus")
    @ApiResponse(responseCode = "200", description = "Sync successful")
    @PreAuthorize(FaqPermissions.HAS_FAQ_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_FAQ, action = I18Consts.I18N_ACTION_BATCH_SYNC_VECTOR_STATUS, description = "sync faq vector status by kb")
    @PostMapping("/syncVectorStatusByKbUid")
    public ResponseEntity<?> syncVectorStatusByKbUid(@RequestBody FaqRequest request) {

        if (faqVectorService != null) {
            var result = faqVectorService.syncVectorStatusByKbUid(request);
            return ResponseEntity.ok(JsonResult.success(result));
        } else {
            return ResponseEntity.ok(JsonResult.error("vector service not enabled"));
        }
    }

    @Operation(summary = "Update All FAQ Indexes", description = "Update Elasticsearch indexes for all FAQs")
    @ApiResponse(responseCode = "200", description = "Update successful")
    @PreAuthorize(FaqPermissions.HAS_FAQ_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_FAQ, action = I18Consts.I18N_ACTION_UPDATE_ALL_INDEX, description = "update all faq index")
    @PostMapping("/updateAllIndex")
    public ResponseEntity<?> updateAllIndex(@RequestBody FaqRequest request) {

        faqElasticService.updateAllIndex(request);

        return ResponseEntity.ok(JsonResult.success("update all index success", request.getUid()));
    }

    @Operation(summary = "Update All FAQ Vector Indexes", description = "Update vector indexes for all FAQs")
    @ApiResponse(responseCode = "200", description = "Update successful")
    @PreAuthorize(FaqPermissions.HAS_FAQ_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_FAQ, action = I18Consts.I18N_ACTION_UPDATE_ALL_VECTOR_INDEX, description = "update all faq vector index")
    @PostMapping("/updateAllVectorIndex")
    public ResponseEntity<?> updateAllVectorIndex(@RequestBody FaqRequest request) {

        if (faqVectorService != null) {
            faqVectorService.updateAllVectorIndex(request);
        } else {
            return ResponseEntity.ok(JsonResult.error("vector service not enabled"));
        }

        return ResponseEntity.ok(JsonResult.success("update all vector index success", request.getUid()));
    }

}
