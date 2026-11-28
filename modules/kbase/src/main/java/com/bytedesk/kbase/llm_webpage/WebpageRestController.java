/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-18 10:12:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_webpage;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.kbase.llm_webpage.elastic.WebpageElasticService;
import com.bytedesk.kbase.llm_webpage.vector.WebpageVectorService;
import jakarta.servlet.http.HttpServletResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/llm/webpage")
public class WebpageRestController extends BaseRestController<WebpageRequest, WebpageRestService> {

    private final WebpageRestService webpageRestService;

    private final WebpageElasticService webpageElasticService;

    private final WebpageVectorService webpageVectorService;

    public WebpageRestController(WebpageRestService webpageRestService, WebpageElasticService webpageElasticService,
            ObjectProvider<WebpageVectorService> webpageVectorServiceProvider) {
        this.webpageVectorService = webpageVectorServiceProvider.getIfAvailable();
        this.webpageRestService = webpageRestService;
        this.webpageElasticService = webpageElasticService;
    }

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Operation(summary = "Query Webpages by Organization", description = "Retrieve webpages for the current organization")
    @ActionAnnotation(title = I18Consts.I18N_WEBPAGE, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query webpage")
    @GetMapping("/query/org")
    @Override
    public ResponseEntity<?> queryByOrg(WebpageRequest request) {
        
        Page<WebpageResponse> webpages = webpageRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(webpages));
    }

    @PreAuthorize(WebpagePermissions.HAS_WEBPAGE_READ)
    @Operation(summary = "Query Webpages by User", description = "Retrieve webpages for the current user")
    @ActionAnnotation(title = I18Consts.I18N_WEBPAGE, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query webpage")
    @GetMapping({ "/query", "/query/user" })
    @Override
    public ResponseEntity<?> queryByUser(WebpageRequest request) {
        
        Page<WebpageResponse> webpages = webpageRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(webpages));
    }

    // @Override
    // public ResponseEntity<?> queryByUid(WebpageRequest request) {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    // }

    @PreAuthorize(WebpagePermissions.HAS_WEBPAGE_CREATE)
    @Operation(summary = "Create Webpages", description = "Create a new webpage entry")
    @PostMapping("/create")
    @Override
    @ActionAnnotation(title = I18Consts.I18N_WEBPAGE, action = I18Consts.I18N_ACTION_CREATE, description = "create webpage")
    public ResponseEntity<?> create(@RequestBody WebpageRequest request) {
        
        WebpageResponse webpage = webpageRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(webpage));
    }

    @PreAuthorize(WebpagePermissions.HAS_WEBPAGE_UPDATE)
    @Operation(summary = "Update Webpages", description = "Update an existing webpage entry")
    @PostMapping("/update")
    @Override
    @ActionAnnotation(title = I18Consts.I18N_WEBPAGE, action = I18Consts.I18N_ACTION_UPDATE, description = "update webpage")
    public ResponseEntity<?> update(@RequestBody WebpageRequest request) {
        
        WebpageResponse webpage = webpageRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(webpage));
    }

    @PreAuthorize(WebpagePermissions.HAS_WEBPAGE_DELETE)
    @Operation(summary = "Delete Webpages", description = "Delete a webpage entry")
    @PostMapping("/delete")
    @Override
    @ActionAnnotation(title = I18Consts.I18N_WEBPAGE, action = I18Consts.I18N_ACTION_DELETE, description = "delete webpage")
    public ResponseEntity<?> delete(@RequestBody WebpageRequest request) {
        
        webpageRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // deleteAll
    @PreAuthorize(WebpagePermissions.HAS_WEBPAGE_DELETE)
    @Operation(summary = "Delete All", description = "Delete all webpages entries")
    @ActionAnnotation(title = I18Consts.I18N_WEBPAGE, action = I18Consts.I18N_ACTION_DELETE_ALL, description = "delete all webpage")
    @PostMapping("/deleteAll")
    public ResponseEntity<?> deleteAll(@RequestBody WebpageRequest request) {

        webpageRestService.deleteAll(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // enable/disable webpage
    @PreAuthorize(WebpagePermissions.HAS_WEBPAGE_UPDATE)
    @Operation(summary = "Enable / Disable", description = "Enable or disable webpage entries")
    @ActionAnnotation(title = I18Consts.I18N_WEBPAGE, action = I18Consts.I18N_ACTION_ENABLE, description = "enable webpage")
    @PostMapping("/enable")
    public ResponseEntity<?> enable(@RequestBody WebpageRequest request) {

        WebpageResponse webpage = webpageRestService.enable(request);
        
        return ResponseEntity.ok(JsonResult.success(webpage));
    }

    // crawl webpage content
    @PreAuthorize(WebpagePermissions.HAS_WEBPAGE_UPDATE)
    @Operation(summary = "Crawl Content", description = "Crawl the content of a webpage")
    @ActionAnnotation(title = I18Consts.I18N_WEBPAGE, action = I18Consts.I18N_ACTION_SYNC, description = "sync webpage")
    @PostMapping("/crawl")
    public ResponseEntity<?> crawlContent(@RequestBody WebpageRequest request) {

        WebpageResponse webpage = webpageRestService.crawlContent(request);
        
        return ResponseEntity.ok(JsonResult.success(webpage));
    }

    @PreAuthorize(WebpagePermissions.HAS_WEBPAGE_EXPORT)
    @Operation(summary = "Export Webpages", description = "Export webpages to Excel format")
    @GetMapping("/export")
    @Override
    @ActionAnnotation(title = I18Consts.I18N_WEBPAGE, action = I18Consts.I18N_ACTION_EXPORT, description = "export webpage")
    public Object export(WebpageRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            webpageRestService,
            WebpageExcel.class,
            "知识库网站",
            "webpage"
        );
    }

    // update elasticsearch index
    @PreAuthorize(WebpagePermissions.HAS_WEBPAGE_UPDATE)
    @Operation(summary = "Update Elastic Index", description = "Update the elastic search index")
    @ActionAnnotation(title = I18Consts.I18N_WEBPAGE, action = I18Consts.I18N_ACTION_UPDATE_INDEX, description = "update webpage index")
    @PostMapping("/updateIndex")
    public ResponseEntity<?> updateIndex(@RequestBody WebpageRequest request) {

        webpageElasticService.updateIndex(request);
        
        return ResponseEntity.ok(JsonResult.success("update index success", request.getUid()));
    }

    // delete elasticsearch index
    @PreAuthorize(WebpagePermissions.HAS_WEBPAGE_DELETE)
    @Operation(summary = "Delete Elastic Index", description = "Delete the elastic search index")
    @ActionAnnotation(title = I18Consts.I18N_WEBPAGE, action = I18Consts.I18N_ACTION_DELETE_INDEX, description = "delete webpage elastic index")
    @PostMapping("/deleteIndex")
    public ResponseEntity<?> deleteIndex(@RequestBody WebpageRequest request) {
        Boolean deleted = webpageElasticService.deleteIndexAndSyncStatus(request);
        return ResponseEntity.ok(JsonResult.success(deleted));
    }

    // sync elasticsearch index status
    @PreAuthorize(WebpagePermissions.HAS_WEBPAGE_UPDATE)
    @Operation(summary = "Sync Elastic Index Status", description = "Synchronize elastic index status")
    @ActionAnnotation(title = I18Consts.I18N_WEBPAGE, action = I18Consts.I18N_ACTION_SYNC_INDEX_STATUS, description = "sync webpage elastic status")
    @PostMapping("/syncIndexStatus")
    public ResponseEntity<?> syncIndexStatus(@RequestBody WebpageRequest request) {
        var webpage = webpageElasticService.syncElasticStatus(request);
        return ResponseEntity.ok(JsonResult.success(webpage.getElasticStatus()));
    }

    // sync elasticsearch index status by kbUid
    @PreAuthorize(WebpagePermissions.HAS_WEBPAGE_UPDATE)
    @Operation(summary = "Sync Elastic Index Status by KB", description = "Synchronize elastic index status by knowledge base")
    @ActionAnnotation(title = I18Consts.I18N_WEBPAGE, action = I18Consts.I18N_ACTION_BATCH_SYNC_INDEX_STATUS, description = "sync webpage elastic status by kb")
    @PostMapping("/syncIndexStatusByKbUid")
    public ResponseEntity<?> syncIndexStatusByKbUid(@RequestBody WebpageRequest request) {
        var result = webpageElasticService.syncElasticStatusByKbUid(request);
        return ResponseEntity.ok(JsonResult.success(result));
    }

    // delete all elasticsearch index by kbUid
    @PreAuthorize(WebpagePermissions.HAS_WEBPAGE_DELETE)
    @Operation(summary = "Delete All Elastic Index by KB", description = "Delete all elastic indexes of a knowledge base")
    @ActionAnnotation(title = I18Consts.I18N_WEBPAGE, action = I18Consts.I18N_ACTION_DELETE_INDEX_BY_KB, description = "delete webpage elastic index by kb")
    @PostMapping("/deleteAllIndexByKbUid")
    public ResponseEntity<?> deleteAllIndexByKbUid(@RequestBody WebpageRequest request) {
        var result = webpageElasticService.deleteAllIndexByKbUidAndSyncStatus(request);
        return ResponseEntity.ok(JsonResult.success(result));
    }

    // update elasticsearch vector index
    @PreAuthorize(WebpagePermissions.HAS_WEBPAGE_UPDATE)
    @Operation(summary = "Update Vector Index", description = "Update the vector index")
    @ActionAnnotation(title = I18Consts.I18N_WEBPAGE, action = I18Consts.I18N_ACTION_UPDATE_VECTOR_INDEX, description = "update webpage vector index")
    @PostMapping("/updateVectorIndex")
    public ResponseEntity<?> updateVectorIndex(@RequestBody WebpageRequest request) {

        if (webpageVectorService != null) {
            webpageVectorService.updateVectorIndex(request);
        }

        return ResponseEntity.ok(JsonResult.success("update vector index success", request.getUid()));
    }

    // delete vector index
    @PreAuthorize(WebpagePermissions.HAS_WEBPAGE_DELETE)
    @Operation(summary = "Delete Vector Index", description = "Delete the vector index")
    @ActionAnnotation(title = I18Consts.I18N_WEBPAGE, action = I18Consts.I18N_ACTION_DELETE_VECTOR_INDEX, description = "delete webpage vector index")
    @PostMapping("/deleteVectorIndex")
    public ResponseEntity<?> deleteVectorIndex(@RequestBody WebpageRequest request) {
        if (webpageVectorService != null) {
            Boolean deleted = webpageVectorService.deleteVectorIndexAndSyncStatus(request);
            return ResponseEntity.ok(JsonResult.success(deleted));
        }
        return ResponseEntity.ok(JsonResult.error("vector service not enabled"));
    }

    // sync vector status
    @PreAuthorize(WebpagePermissions.HAS_WEBPAGE_UPDATE)
    @Operation(summary = "Sync Vector Index Status", description = "Synchronize vector index status")
    @ActionAnnotation(title = I18Consts.I18N_WEBPAGE, action = I18Consts.I18N_ACTION_SYNC_VECTOR_STATUS, description = "sync webpage vector status")
    @PostMapping("/syncVectorStatus")
    public ResponseEntity<?> syncVectorStatus(@RequestBody WebpageRequest request) {
        if (webpageVectorService != null) {
            var webpage = webpageVectorService.syncVectorStatus(request);
            return ResponseEntity.ok(JsonResult.success(webpage.getVectorStatus()));
        }
        return ResponseEntity.ok(JsonResult.error("vector service not enabled"));
    }

    @PreAuthorize(WebpagePermissions.HAS_WEBPAGE_READ)
    @Operation(summary = "Query Elastic Document by UID", description = "Retrieve the elastic document of an entry")
    @ActionAnnotation(title = I18Consts.I18N_WEBPAGE, action = I18Consts.I18N_ACTION_QUERY_ELASTIC_INDEX, description = "query webpage elastic by uid")
    @PostMapping("/queryElasticByUid")
    public ResponseEntity<?> queryElasticByUid(@RequestBody WebpageRequest request) {
        var result = webpageElasticService.queryElasticByUid(request);
        return ResponseEntity.ok(JsonResult.success(result));
    }

    @PreAuthorize(WebpagePermissions.HAS_WEBPAGE_READ)
    @Operation(summary = "Query Vector Document by UID", description = "Retrieve the vector document of an entry")
    @ActionAnnotation(title = I18Consts.I18N_WEBPAGE, action = I18Consts.I18N_ACTION_QUERY_VECTOR_INDEX, description = "query webpage vector by uid")
    @PostMapping("/queryVectorByUid")
    public ResponseEntity<?> queryVectorByUid(@RequestBody WebpageRequest request) {
        if (webpageVectorService != null) {
            var result = webpageVectorService.queryVectorByUid(request);
            return ResponseEntity.ok(JsonResult.success(result));
        }
        return ResponseEntity.ok(JsonResult.error("vector service not enabled"));
    }

    // sync vector status by kbUid
    @PreAuthorize(WebpagePermissions.HAS_WEBPAGE_UPDATE)
    @Operation(summary = "Sync Vector Index Status by KB", description = "Synchronize vector index status by knowledge base")
    @ActionAnnotation(title = I18Consts.I18N_WEBPAGE, action = I18Consts.I18N_ACTION_BATCH_SYNC_VECTOR_STATUS, description = "sync webpage vector status by kb")
    @PostMapping("/syncVectorStatusByKbUid")
    public ResponseEntity<?> syncVectorStatusByKbUid(@RequestBody WebpageRequest request) {
        if (webpageVectorService != null) {
            var result = webpageVectorService.syncVectorStatusByKbUid(request);
            return ResponseEntity.ok(JsonResult.success(result));
        }
        return ResponseEntity.ok(JsonResult.error("vector service not enabled"));
    }

    // update elasticsearch all index
    @PreAuthorize(WebpagePermissions.HAS_WEBPAGE_UPDATE)
    @Operation(summary = "Update All Elastic Indexes", description = "Update all elastic indexes")
    @ActionAnnotation(title = I18Consts.I18N_WEBPAGE, action = I18Consts.I18N_ACTION_UPDATE_ALL_INDEX, description = "update all webpage index")
    @PostMapping("/updateAllIndex")
    public ResponseEntity<?> updateAllIndex(@RequestBody WebpageRequest request) {

        webpageElasticService.updateAllIndex(request);

        return ResponseEntity.ok(JsonResult.success("update all index success", request.getKbUid()));
    }

    // update elasticsearch all vector index
    @PreAuthorize(WebpagePermissions.HAS_WEBPAGE_UPDATE)
    @Operation(summary = "Update All Vector Indexes", description = "Update all vector indexes")
    @ActionAnnotation(title = I18Consts.I18N_WEBPAGE, action = I18Consts.I18N_ACTION_UPDATE_ALL_VECTOR_INDEX, description = "update all webpage vector index")
    @PostMapping("/updateAllVectorIndex")
    public ResponseEntity<?> updateAllVectorIndex(@RequestBody WebpageRequest request) {

        if (webpageVectorService != null) {
            webpageVectorService.updateAllVectorIndex(request);
        }

        return ResponseEntity.ok(JsonResult.success("update all vector index success", request.getKbUid()));
    }

    // delete all vector index by kbUid
    @PreAuthorize(WebpagePermissions.HAS_WEBPAGE_DELETE)
    @Operation(summary = "Delete All Vector Indexes by KB", description = "Delete all vector indexes of a knowledge base")
    @ActionAnnotation(title = I18Consts.I18N_WEBPAGE, action = I18Consts.I18N_ACTION_DELETE_VECTOR_INDEX_BY_KB, description = "delete webpage vector index by kb")
    @PostMapping("/deleteAllVectorIndexByKbUid")
    public ResponseEntity<?> deleteAllVectorIndexByKbUid(@RequestBody WebpageRequest request) {
        if (webpageVectorService != null) {
            var result = webpageVectorService.deleteAllVectorIndexByKbUidAndSyncStatus(request);
            return ResponseEntity.ok(JsonResult.success(result));
        }
        return ResponseEntity.ok(JsonResult.error("vector service not enabled"));
    }
    
}
