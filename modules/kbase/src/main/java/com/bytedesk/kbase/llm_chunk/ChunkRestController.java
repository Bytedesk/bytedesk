/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-13 14:11:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_chunk;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/llm/chunk")
@AllArgsConstructor
public class ChunkRestController extends BaseRestController<ChunkRequest> {

    private final ChunkRestService chunkRestService;

    // @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByOrg(ChunkRequest request) {
        
        Page<ChunkResponse> chunks = chunkRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(chunks));
    }

    // @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByUser(ChunkRequest request) {
        
        Page<ChunkResponse> chunks = chunkRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(chunks));
    }

    // @PreAuthorize("hasAuthority('KBASE_READ')")
    @Override
    public ResponseEntity<?> queryByUid(ChunkRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    // @PreAuthorize("hasAuthority('KBASE_CREATE')")
    @Override
    public ResponseEntity<?> create(ChunkRequest request) {
        
        ChunkResponse chunk = chunkRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(chunk));
    }

    // @PreAuthorize("hasAuthority('KBASE_UPDATE')")
    @Override
    public ResponseEntity<?> update(ChunkRequest request) {
        
        ChunkResponse chunk = chunkRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(chunk));
    }

    // @PreAuthorize("hasAuthority('KBASE_DELETE')")
    @Override
    public ResponseEntity<?> delete(ChunkRequest request) {
        
        chunkRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // @PreAuthorize("hasAuthority('KBASE_EXPORT')")
    @Override
    public Object export(ChunkRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            chunkRestService,
            ChunkExcel.class,
            "分词",
            "chunk"
        );
    }

    // update elasticsearch index
    @PostMapping("/updateIndex")
    public ResponseEntity<?> updateIndex(ChunkRequest request) {
        // chunkRestService.updateIndex(request);
        return ResponseEntity.ok(JsonResult.success());
    }

    // update elasticsearch vector index
    @PostMapping("/updateVectorIndex")
    public ResponseEntity<?> updateVectorIndex(ChunkRequest request) {
        // chunkRestService.updateVectorIndex(request);
        return ResponseEntity.ok(JsonResult.success());
    }

    // update elasticsearch all index
    @PostMapping("/updateAllIndex")
    public ResponseEntity<?> updateAllIndex(ChunkRequest request) {
        // chunkRestService.updateAllIndex(request);
        return ResponseEntity.ok(JsonResult.success());
    }

    // update elasticsearch all vector index
    @PostMapping("/updateAllVectorIndex")
    public ResponseEntity<?> updateAllVectorIndex(ChunkRequest request) {
        // chunkRestService.updateAllVectorIndex(request);
        return ResponseEntity.ok(JsonResult.success());
    }
}