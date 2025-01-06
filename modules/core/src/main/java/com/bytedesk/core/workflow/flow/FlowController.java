/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-10 12:15:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-14 16:25:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  技术/商务联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow.flow;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.JsonResult;

@RestController
// @RequestMapping("/api/v1/flow")
@RequestMapping("/flow") // for testing
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://127.0.0.1:9010"})
public class FlowController extends BaseRestController<FlowRequest> {

    private final FlowRestService flowService;

    // http://localhost:8080/api/v1/flow/query/org
    // http://localhost:9003/flow/query/org?pageNumber=0&pageSize=10
    @Override
    public ResponseEntity<?> queryByOrg(FlowRequest request) {

        Page<FlowResponse> page = flowService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> queryByUser(FlowRequest request) {

        Page<FlowResponse> page = flowService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @Override
    public ResponseEntity<?> create(FlowRequest request) {

        FlowResponse response = flowService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> update(FlowRequest request) {

        FlowResponse response = flowService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @Override
    public ResponseEntity<?> delete(FlowRequest request) {

        flowService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // @GetMapping
    // public List<FlowEntity> getFlows(@RequestParam String orgUid) {
    // return flowService.getFlowsByOrgUid(orgUid);
    // }

    // @GetMapping("/{id}")
    // public FlowEntity getFlow(@PathVariable Long id) {
    // return flowService.getFlowById(id);
    // }

    // @PostMapping
    // public FlowEntity createFlow(@RequestBody FlowEntity flow) {
    // return flowService.createFlow(flow);
    // }

    // @PutMapping("/{id}")
    // public FlowEntity updateFlow(@PathVariable Long id, @RequestBody FlowEntity
    // flow) {
    // return flowService.updateFlow(id, flow);
    // }

    // @DeleteMapping("/{id}")
    // public void deleteFlow(@PathVariable Long id) {
    // flowService.deleteFlow(id);
    // }

    // @GetMapping("/public/{publicId}")
    // public FlowEntity getPublicFlow(@PathVariable String publicId) {
    // return flowService.getFlowByPublicId(publicId);
    // }

    // @GetMapping("/domain/{customDomain}")
    // public FlowEntity getFlowByDomain(@PathVariable String customDomain) {
    // return flowService.getFlowByCustomDomain(customDomain);
    // }
}
