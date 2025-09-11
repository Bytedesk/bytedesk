/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-08 19:46:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_image;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.kbase.llm_image.elastic.ImageElasticService;
import com.bytedesk.kbase.llm_image.vector.ImageVectorService;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/v1/llm/image")
public class ImageRestController extends BaseRestController<ImageRequest, ImageRestService> {

    private final ImageRestService imageRestService;
    
    private final ImageElasticService imageElasticService;

    @Autowired(required = false)
    private ImageVectorService imageVectorService;

    public ImageRestController(ImageRestService imageRestService, ImageElasticService imageElasticService) {
        this.imageRestService = imageRestService;
        this.imageElasticService = imageElasticService;
    }

    // @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByOrg(ImageRequest request) {
        
        Page<ImageResponse> images = imageRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(images));
    }

    // @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByUser(ImageRequest request) {
        
        Page<ImageResponse> images = imageRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(images));
    }

    // @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByUid(ImageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @ActionAnnotation(title = "文件分块", action = "新建", description = "create image")
    @Override
    public ResponseEntity<?> create(ImageRequest request) {
        
        ImageResponse image = imageRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(image));
    }

    @ActionAnnotation(title = "文件分块", action = "更新", description = "update image")
    @Override
    public ResponseEntity<?> update(ImageRequest request) {
        
        ImageResponse image = imageRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(image));
    }

    @ActionAnnotation(title = "文件分块", action = "删除", description = "delete image")
    @Override
    public ResponseEntity<?> delete(ImageRequest request) {
        
        imageRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // deleteAll
    @PostMapping("/deleteAll")
    public ResponseEntity<?> deleteAll(@RequestBody ImageRequest request) {

        imageRestService.deleteAll(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // enable/disable image
    @PostMapping("/enable")
    public ResponseEntity<?> enable(@RequestBody ImageRequest request) {

        ImageResponse image = imageRestService.enable(request);

        return ResponseEntity.ok(JsonResult.success(image));
    }

    @ActionAnnotation(title = "文件分块", action = "导出", description = "export image")
    @Override
    public Object export(ImageRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            imageRestService,
            ImageExcel.class,
            "文件分块",
            "image"
        );
    }

    // update elasticsearch index
    @ActionAnnotation(title = "文件分块", action = "更新索引", description = "update image index")
    @PostMapping("/updateIndex")
    public ResponseEntity<?> updateIndex(@RequestBody ImageRequest request) {

        imageElasticService.updateIndex(request);

        return ResponseEntity.ok(JsonResult.success("update index success", request.getUid()));
    }

    // update elasticsearch vector index
    @ActionAnnotation(title = "文件分块", action = "更新向量索引", description = "update image vector index")
    @PostMapping("/updateVectorIndex")
    public ResponseEntity<?> updateVectorIndex(@RequestBody ImageRequest request) {

        if (imageVectorService != null) {
            imageVectorService.updateVectorIndex(request);
        }

        return ResponseEntity.ok(JsonResult.success("update vector index success", request.getUid()));
    }

    // update elasticsearch all index
    @ActionAnnotation(title = "文件分块", action = "更新所有索引", description = "update all image index")
    @PostMapping("/updateAllIndex")
    public ResponseEntity<?> updateAllIndex(@RequestBody ImageRequest request) {

        imageElasticService.updateAllIndex(request);

        return ResponseEntity.ok(JsonResult.success("update all index success", request.getKbUid()));
    }

    // update elasticsearch all vector index
    @ActionAnnotation(title = "文件分块", action = "更新所有向量索引", description = "update all image vector index")
    @PostMapping("/updateAllVectorIndex")
    public ResponseEntity<?> updateAllVectorIndex(@RequestBody ImageRequest request) {

        if (imageVectorService != null) {
            imageVectorService.updateAllVectorIndex(request);
        }

        return ResponseEntity.ok(JsonResult.success("update all vector index success", request.getKbUid()));
    }
}