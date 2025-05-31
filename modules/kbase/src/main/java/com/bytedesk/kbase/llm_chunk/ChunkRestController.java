/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-14 16:19:55
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.kbase.llm_chunk.elastic.ChunkElasticService;
import com.bytedesk.kbase.llm_chunk.vector.ChunkVectorService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/llm/chunk")
@AllArgsConstructor
public class ChunkRestController extends BaseRestController<ChunkRequest> {

    private final ChunkRestService chunkRestService;
    
    private final ChunkElasticService chunkElasticService;

    private final ChunkVectorService chunkVectorService;

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

    // deleteAll
    @PostMapping("/deleteAll")
    public ResponseEntity<?> deleteAll(@RequestBody ChunkRequest request) {

        chunkRestService.deleteAll(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // enable/disable chunk
    @PostMapping("/enable")
    public ResponseEntity<?> enable(@RequestBody ChunkRequest request) {

        ChunkResponse chunk = chunkRestService.enable(request);

        return ResponseEntity.ok(JsonResult.success(chunk));
    }

    // @PreAuthorize("hasAuthority('KBASE_EXPORT')")
    @Override
    public Object export(ChunkRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            chunkRestService,
            ChunkExcel.class,
            "文件分块",
            "chunk"
        );
    }

    // update elasticsearch index
    @ActionAnnotation(title = "文件分块", action = "更新索引", description = "update chunk index")
    @PostMapping("/updateIndex")
    public ResponseEntity<?> updateIndex(@RequestBody ChunkRequest request) {

        chunkElasticService.updateIndex(request);

        return ResponseEntity.ok(JsonResult.success("update index success", request.getUid()));
    }

    // update elasticsearch vector index
    @ActionAnnotation(title = "文件分块", action = "更新向量索引", description = "update chunk vector index")
    @PostMapping("/updateVectorIndex")
    public ResponseEntity<?> updateVectorIndex(@RequestBody ChunkRequest request) {

        chunkVectorService.updateVectorIndex(request);

        return ResponseEntity.ok(JsonResult.success("update vector index success", request.getUid()));
    }

    // update elasticsearch all index
    @ActionAnnotation(title = "文件分块", action = "更新所有索引", description = "update all chunk index")
    @PostMapping("/updateAllIndex")
    public ResponseEntity<?> updateAllIndex(@RequestBody ChunkRequest request) {

        chunkElasticService.updateAllIndex(request);

        return ResponseEntity.ok(JsonResult.success("update all index success", request.getKbUid()));
    }

    // update elasticsearch all vector index
    @ActionAnnotation(title = "文件分块", action = "更新所有向量索引", description = "update all chunk vector index")
    @PostMapping("/updateAllVectorIndex")
    public ResponseEntity<?> updateAllVectorIndex(@RequestBody ChunkRequest request) {

        chunkVectorService.updateAllVectorIndex(request);

        return ResponseEntity.ok(JsonResult.success("update all vector index success", request.getKbUid()));
    }
}