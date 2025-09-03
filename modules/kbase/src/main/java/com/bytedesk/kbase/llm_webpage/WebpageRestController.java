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

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.kbase.llm_webpage.elastic.WebpageElasticService;
import com.bytedesk.kbase.llm_webpage.vector.WebpageVectorService;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/v1/llm/webpage")
public class WebpageRestController extends BaseRestController<WebpageRequest, WebpageRestService> {

    private final WebpageRestService webpageRestService;

    private final WebpageElasticService webpageElasticService;

    @Autowired(required = false)
    private WebpageVectorService webpageVectorService;

    public WebpageRestController(WebpageRestService webpageRestService, WebpageElasticService webpageElasticService) {
        this.webpageRestService = webpageRestService;
        this.webpageElasticService = webpageElasticService;
    }

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Override
    public ResponseEntity<?> queryByOrg(WebpageRequest request) {
        
        Page<WebpageResponse> webpages = webpageRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(webpages));
    }

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

    @Override
    public ResponseEntity<?> create(WebpageRequest request) {
        
        WebpageResponse webpage = webpageRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(webpage));
    }

    @Override
    public ResponseEntity<?> update(WebpageRequest request) {
        
        WebpageResponse webpage = webpageRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(webpage));
    }

    @Override
    public ResponseEntity<?> delete(WebpageRequest request) {
        
        webpageRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // deleteAll
    @PostMapping("/deleteAll")
    public ResponseEntity<?> deleteAll(@RequestBody WebpageRequest request) {

        webpageRestService.deleteAll(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // enable/disable webpage
    @PostMapping("/enable")
    public ResponseEntity<?> enable(@RequestBody WebpageRequest request) {

        WebpageResponse webpage = webpageRestService.enable(request);
        
        return ResponseEntity.ok(JsonResult.success(webpage));
    }

    // crawl webpage content
    @PostMapping("/crawl")
    public ResponseEntity<?> crawlContent(@RequestBody WebpageRequest request) {

        WebpageResponse webpage = webpageRestService.crawlContent(request);
        
        return ResponseEntity.ok(JsonResult.success(webpage));
    }

    @Override
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
    @ActionAnnotation(title = "知识库网页", action = "更新索引", description = "update webpage index")
    @PostMapping("/updateIndex")
    public ResponseEntity<?> updateIndex(@RequestBody WebpageRequest request) {

        webpageElasticService.updateIndex(request);
        
        return ResponseEntity.ok(JsonResult.success("update index success", request.getUid()));
    }

    // update elasticsearch vector index
    @ActionAnnotation(title = "知识库网页", action = "更新向量索引", description = "update webpage vector index")
    @PostMapping("/updateVectorIndex")
    public ResponseEntity<?> updateVectorIndex(@RequestBody WebpageRequest request) {

        if (webpageVectorService != null) {
            webpageVectorService.updateVectorIndex(request);
        }

        return ResponseEntity.ok(JsonResult.success("update vector index success", request.getUid()));
    }

    // update elasticsearch all index
    @ActionAnnotation(title = "知识库网页", action = "更新所有索引", description = "update all webpage index")
    @PostMapping("/updateAllIndex")
    public ResponseEntity<?> updateAllIndex(@RequestBody WebpageRequest request) {

        webpageElasticService.updateAllIndex(request);

        return ResponseEntity.ok(JsonResult.success("update all index success", request.getKbUid()));
    }

    // update elasticsearch all vector index
    @ActionAnnotation(title = "知识库网页", action = "更新所有向量索引", description = "update all webpage vector index")
    @PostMapping("/updateAllVectorIndex")
    public ResponseEntity<?> updateAllVectorIndex(@RequestBody WebpageRequest request) {

        if (webpageVectorService != null) {
            webpageVectorService.updateAllVectorIndex(request);
        }

        return ResponseEntity.ok(JsonResult.success("update all vector index success", request.getKbUid()));
    }
    
}