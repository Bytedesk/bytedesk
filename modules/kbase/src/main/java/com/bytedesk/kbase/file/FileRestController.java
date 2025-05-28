/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-14 12:52:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.file;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/llm/file")
@AllArgsConstructor
public class FileRestController extends BaseRestController<FileRequest> {

    private final FileRestService fileRestService;

    // @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByOrg(FileRequest request) {
        
        Page<FileResponse> files = fileRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(files));
    }

    // @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByUser(FileRequest request) {
        
        Page<FileResponse> files = fileRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(files));
    }

    @Override
    public ResponseEntity<?> create(FileRequest request) {
        
        FileResponse file = fileRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(file));
    }

    @Override
    public ResponseEntity<?> update(FileRequest request) {
        
        FileResponse file = fileRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(file));
    }

    @Override
    public ResponseEntity<?> delete(FileRequest request) {
        
        fileRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // deleteAll
    @PostMapping("/deleteAll")
    public ResponseEntity<?> deleteAll(@RequestBody FileRequest request) {

        fileRestService.deleteAll(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // enable/disable file
    @PostMapping("/enable")
    public ResponseEntity<?> enable(@RequestBody FileRequest request) {

        FileResponse file = fileRestService.enable(request);
        
        return ResponseEntity.ok(JsonResult.success(file));
    }

    @Override
    public Object export(FileRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            fileRestService,
            FileExcel.class,
            "文件",
            "file"
        );
    }

    @Override
    public ResponseEntity<?> queryByUid(FileRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    // update elasticsearch index
    @PostMapping("/updateIndex")
    public ResponseEntity<?> updateIndex(@RequestBody FileRequest request) {
        // chunkRestService.updateIndex(request);
        return ResponseEntity.ok(JsonResult.success());
    }

    // update elasticsearch vector index
    @PostMapping("/updateVectorIndex")
    public ResponseEntity<?> updateVectorIndex(@RequestBody FileRequest request) {
        // chunkRestService.updateVectorIndex(request);
        return ResponseEntity.ok(JsonResult.success());
    }

    // update elasticsearch all index
    @PostMapping("/updateAllIndex")
    public ResponseEntity<?> updateAllIndex(@RequestBody FileRequest request) {
        // chunkRestService.updateAllIndex(request);
        return ResponseEntity.ok(JsonResult.success());
    }

    // update elasticsearch all vector index
    @PostMapping("/updateAllVectorIndex")
    public ResponseEntity<?> updateAllVectorIndex(@RequestBody FileRequest request) {
        // chunkRestService.updateAllVectorIndex(request);
        return ResponseEntity.ok(JsonResult.success());
    }
    
}