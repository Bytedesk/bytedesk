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
package com.bytedesk.kbase.llm_chunk;

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
import com.bytedesk.kbase.llm_chunk.elastic.ChunkElasticService;
import com.bytedesk.kbase.llm_chunk.vector.ChunkVectorService;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/v1/llm/chunk")
public class ChunkRestController extends BaseRestController<ChunkRequest, ChunkRestService> {

    private final ChunkRestService chunkRestService;
    
    private final ChunkElasticService chunkElasticService;

    @Autowired(required = false)
    private ChunkVectorService chunkVectorService;

    public ChunkRestController(ChunkRestService chunkRestService, ChunkElasticService chunkElasticService) {
        this.chunkRestService = chunkRestService;
        this.chunkElasticService = chunkElasticService;
    }

    @PreAuthorize(ChunkPermissions.HAS_CHUNK_READ)
    @GetMapping("/query/org")
    @Override
    public ResponseEntity<?> queryByOrg(ChunkRequest request) {
        
        Page<ChunkResponse> chunks = chunkRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(chunks));
    }

    @PreAuthorize(ChunkPermissions.HAS_CHUNK_READ)
    @GetMapping({ "/query", "/query/user" })
    @Override
    public ResponseEntity<?> queryByUser(ChunkRequest request) {
        
        Page<ChunkResponse> chunks = chunkRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(chunks));
    }

    @PreAuthorize(ChunkPermissions.HAS_CHUNK_READ)
    @Override
    public ResponseEntity<?> queryByUid(ChunkRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    @ActionAnnotation(title = I18Consts.I18N_CHUNK, action = I18Consts.I18N_ACTION_CREATE, description = "create chunk")
    @PreAuthorize(ChunkPermissions.HAS_CHUNK_CREATE)
    @PostMapping("/create")
    @Override
    public ResponseEntity<?> create(@RequestBody ChunkRequest request) {
        
        ChunkResponse chunk = chunkRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(chunk));
    }

    @ActionAnnotation(title = I18Consts.I18N_CHUNK, action = I18Consts.I18N_ACTION_UPDATE, description = "update chunk")
    @PreAuthorize(ChunkPermissions.HAS_CHUNK_UPDATE)
    @PostMapping("/update")
    @Override
    public ResponseEntity<?> update(@RequestBody ChunkRequest request) {
        
        ChunkResponse chunk = chunkRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(chunk));
    }

    @ActionAnnotation(title = I18Consts.I18N_CHUNK, action = I18Consts.I18N_ACTION_DELETE, description = "delete chunk")
    @PreAuthorize(ChunkPermissions.HAS_CHUNK_DELETE)
    @PostMapping("/delete")
    @Override
    public ResponseEntity<?> delete(@RequestBody ChunkRequest request) {
        
        chunkRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // deleteAll
    @PostMapping("/deleteAll")
    @PreAuthorize(ChunkPermissions.HAS_CHUNK_DELETE)
    public ResponseEntity<?> deleteAll(@RequestBody ChunkRequest request) {

        chunkRestService.deleteAll(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // enable/disable chunk
    @PostMapping("/enable")
    @PreAuthorize(ChunkPermissions.HAS_CHUNK_UPDATE)
    public ResponseEntity<?> enable(@RequestBody ChunkRequest request) {

        ChunkResponse chunk = chunkRestService.enable(request);

        return ResponseEntity.ok(JsonResult.success(chunk));
    }

    @ActionAnnotation(title = I18Consts.I18N_CHUNK, action = I18Consts.I18N_ACTION_EXPORT, description = "export chunk")
    @PreAuthorize(ChunkPermissions.HAS_CHUNK_EXPORT)
    @GetMapping("/export")
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
    @ActionAnnotation(title = I18Consts.I18N_CHUNK, action = I18Consts.I18N_ACTION_UPDATE_INDEX, description = "update chunk index")
    @PostMapping("/updateIndex")
    @PreAuthorize(ChunkPermissions.HAS_CHUNK_UPDATE)
    public ResponseEntity<?> updateIndex(@RequestBody ChunkRequest request) {

        chunkElasticService.updateIndex(request);

        return ResponseEntity.ok(JsonResult.success("update index success", request.getUid()));
    }

    // delete elasticsearch index
    @ActionAnnotation(title = I18Consts.I18N_CHUNK, action = I18Consts.I18N_ACTION_DELETE_INDEX, description = "delete chunk elastic index")
    @PostMapping("/deleteIndex")
    @PreAuthorize(ChunkPermissions.HAS_CHUNK_UPDATE)
    public ResponseEntity<?> deleteIndex(@RequestBody ChunkRequest request) {
        Boolean deleted = chunkElasticService.deleteIndexAndSyncStatus(request);
        return ResponseEntity.ok(JsonResult.success(deleted));
    }

    // sync elasticsearch index status
    @ActionAnnotation(title = I18Consts.I18N_CHUNK, action = I18Consts.I18N_ACTION_SYNC_INDEX_STATUS, description = "sync chunk elastic status")
    @PostMapping("/syncIndexStatus")
    @PreAuthorize(ChunkPermissions.HAS_CHUNK_UPDATE)
    public ResponseEntity<?> syncIndexStatus(@RequestBody ChunkRequest request) {
        var chunk = chunkElasticService.syncElasticStatus(request);
        return ResponseEntity.ok(JsonResult.success(chunk.getElasticStatus()));
    }

    // sync elasticsearch index status by kbUid
    @ActionAnnotation(title = I18Consts.I18N_CHUNK, action = I18Consts.I18N_ACTION_BATCH_SYNC_INDEX_STATUS, description = "sync chunk elastic status by kb")
    @PostMapping("/syncIndexStatusByKbUid")
    @PreAuthorize(ChunkPermissions.HAS_CHUNK_UPDATE)
    public ResponseEntity<?> syncIndexStatusByKbUid(@RequestBody ChunkRequest request) {
        var result = chunkElasticService.syncElasticStatusByKbUid(request);
        return ResponseEntity.ok(JsonResult.success(result));
    }

    // delete all elasticsearch index by kbUid
    @ActionAnnotation(title = I18Consts.I18N_CHUNK, action = I18Consts.I18N_ACTION_DELETE_INDEX_BY_KB, description = "delete chunk elastic index by kb")
    @PostMapping("/deleteAllIndexByKbUid")
    @PreAuthorize(ChunkPermissions.HAS_CHUNK_UPDATE)
    public ResponseEntity<?> deleteAllIndexByKbUid(@RequestBody ChunkRequest request) {
        var result = chunkElasticService.deleteAllIndexByKbUidAndSyncStatus(request);
        return ResponseEntity.ok(JsonResult.success(result));
    }

    // update elasticsearch vector index
    @ActionAnnotation(title = I18Consts.I18N_CHUNK, action = I18Consts.I18N_ACTION_UPDATE_VECTOR_INDEX, description = "update chunk vector index")
    @PostMapping("/updateVectorIndex")
    @PreAuthorize(ChunkPermissions.HAS_CHUNK_UPDATE)
    public ResponseEntity<?> updateVectorIndex(@RequestBody ChunkRequest request) {

        if (chunkVectorService != null) {
            chunkVectorService.updateVectorIndex(request);
        }

        return ResponseEntity.ok(JsonResult.success("update vector index success", request.getUid()));
    }

    // delete vector index
    @ActionAnnotation(title = I18Consts.I18N_CHUNK, action = I18Consts.I18N_ACTION_DELETE_VECTOR_INDEX, description = "delete chunk vector index")
    @PostMapping("/deleteVectorIndex")
    @PreAuthorize(ChunkPermissions.HAS_CHUNK_UPDATE)
    public ResponseEntity<?> deleteVectorIndex(@RequestBody ChunkRequest request) {
        if (chunkVectorService != null) {
            Boolean deleted = chunkVectorService.deleteVectorIndexAndSyncStatus(request);
            return ResponseEntity.ok(JsonResult.success(deleted));
        }
        return ResponseEntity.ok(JsonResult.error("vector service not enabled"));
    }

    // sync vector status
    @ActionAnnotation(title = I18Consts.I18N_CHUNK, action = I18Consts.I18N_ACTION_SYNC_VECTOR_STATUS, description = "sync chunk vector status")
    @PostMapping("/syncVectorStatus")
    @PreAuthorize(ChunkPermissions.HAS_CHUNK_UPDATE)
    public ResponseEntity<?> syncVectorStatus(@RequestBody ChunkRequest request) {
        if (chunkVectorService != null) {
            var chunk = chunkVectorService.syncVectorStatus(request);
            return ResponseEntity.ok(JsonResult.success(chunk.getVectorStatus()));
        }
        return ResponseEntity.ok(JsonResult.error("vector service not enabled"));
    }

    @PreAuthorize(ChunkPermissions.HAS_CHUNK_READ)
    @ActionAnnotation(title = I18Consts.I18N_CHUNK, action = I18Consts.I18N_ACTION_QUERY_ELASTIC_INDEX, description = "query chunk elastic by uid")
    @PostMapping("/queryElasticByUid")
    public ResponseEntity<?> queryElasticByUid(@RequestBody ChunkRequest request) {
        var result = chunkElasticService.queryElasticByUid(request);
        return ResponseEntity.ok(JsonResult.success(result));
    }

    @PreAuthorize(ChunkPermissions.HAS_CHUNK_READ)
    @ActionAnnotation(title = I18Consts.I18N_CHUNK, action = I18Consts.I18N_ACTION_QUERY_VECTOR_INDEX, description = "query chunk vector by uid")
    @PostMapping("/queryVectorByUid")
    public ResponseEntity<?> queryVectorByUid(@RequestBody ChunkRequest request) {
        if (chunkVectorService != null) {
            var result = chunkVectorService.queryVectorByUid(request);
            return ResponseEntity.ok(JsonResult.success(result));
        }
        return ResponseEntity.ok(JsonResult.error("vector service not enabled"));
    }

    // sync vector status by kbUid
    @ActionAnnotation(title = I18Consts.I18N_CHUNK, action = I18Consts.I18N_ACTION_BATCH_SYNC_VECTOR_STATUS, description = "sync chunk vector status by kb")
    @PostMapping("/syncVectorStatusByKbUid")
    @PreAuthorize(ChunkPermissions.HAS_CHUNK_UPDATE)
    public ResponseEntity<?> syncVectorStatusByKbUid(@RequestBody ChunkRequest request) {
        if (chunkVectorService != null) {
            var result = chunkVectorService.syncVectorStatusByKbUid(request);
            return ResponseEntity.ok(JsonResult.success(result));
        }
        return ResponseEntity.ok(JsonResult.error("vector service not enabled"));
    }

    // update elasticsearch all index
    @ActionAnnotation(title = I18Consts.I18N_CHUNK, action = I18Consts.I18N_ACTION_UPDATE_ALL_INDEX, description = "update all chunk index")
    @PostMapping("/updateAllIndex")
    @PreAuthorize(ChunkPermissions.HAS_CHUNK_UPDATE)
    public ResponseEntity<?> updateAllIndex(@RequestBody ChunkRequest request) {

        chunkElasticService.updateAllIndex(request);

        return ResponseEntity.ok(JsonResult.success("update all index success", request.getKbUid()));
    }

    // update elasticsearch all vector index
    @ActionAnnotation(title = I18Consts.I18N_CHUNK, action = I18Consts.I18N_ACTION_UPDATE_ALL_VECTOR_INDEX, description = "update all chunk vector index")
    @PostMapping("/updateAllVectorIndex")
    @PreAuthorize(ChunkPermissions.HAS_CHUNK_UPDATE)
    public ResponseEntity<?> updateAllVectorIndex(@RequestBody ChunkRequest request) {

        if (chunkVectorService != null) {
            var result = chunkVectorService.updateAllVectorIndex(request);
            return ResponseEntity.ok(JsonResult.success(result));
        }

        return ResponseEntity.ok(JsonResult.error("vector service not enabled"));
    }

    // delete all vector index by kbUid
    @ActionAnnotation(title = I18Consts.I18N_CHUNK, action = I18Consts.I18N_ACTION_DELETE_VECTOR_INDEX_BY_KB, description = "delete chunk vector index by kb")
    @PostMapping("/deleteAllVectorIndexByKbUid")
    @PreAuthorize(ChunkPermissions.HAS_CHUNK_UPDATE)
    public ResponseEntity<?> deleteAllVectorIndexByKbUid(@RequestBody ChunkRequest request) {
        if (chunkVectorService != null) {
            var result = chunkVectorService.deleteAllVectorIndexByKbUidAndSyncStatus(request);
            return ResponseEntity.ok(JsonResult.success(result));
        }
        return ResponseEntity.ok(JsonResult.error("vector service not enabled"));
    }
}