/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-22 16:03:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.member;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.action.ActionAnnotation;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 
 * http://127.0.0.1:9003/swagger-ui/index.html
 * https://www.bezkoder.com/swagger-3-annotations/#Swagger_3_annotations
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mem")
@Tag(name = "member", description = "member apis")
public class MemberController {

    private final MemberService memberService;

    /**
     * query by orgUid
     * 
     * @param memberRequest
     * @return
     */
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(MemberRequest memberRequest) {
        //
        Page<MemberResponse> memberResponse = memberService.queryByOrg(memberRequest);
        //
        return ResponseEntity.ok(JsonResult.success(memberResponse));
    }

    
    @GetMapping("/query")
    public ResponseEntity<?> query(MemberRequest memberRequest) {
        //
        Optional<MemberResponse> memberResponse = memberService.query(memberRequest);
        if (memberResponse.isEmpty()) {
            return ResponseEntity.ok(JsonResult.error("member not found", -1));
        }
        //
        return ResponseEntity.ok(JsonResult.success(memberResponse));
    }

    /**
     * create
     *
     * @param memberRequest role
     * @return json
     */
    @ActionAnnotation(title = "member", action = "create", description = "create member")
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody MemberRequest memberRequest) {

        MemberResponse member = memberService.create(memberRequest);

        return ResponseEntity.ok(JsonResult.success(member));
    }

    /**
     * update
     *
     * @param memberRequest role
     * @return json
     */
    @ActionAnnotation(title = "member", action = "update", description = "update member")
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody MemberRequest memberRequest) {

        MemberResponse member = memberService.update(memberRequest);
        //
        return ResponseEntity.ok(JsonResult.success(member));
    }

    /**
     * delete
     *
     * @param memberRequest role
     * @return json
     */
    @ActionAnnotation(title = "member", action = "delete", description = "delete member")
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody MemberRequest memberRequest) {

        memberService.deleteByUid(memberRequest.getUid());

        return ResponseEntity.ok(JsonResult.success());
    }

    /**
     * filter
     *
     * @return json
     */
    @GetMapping("/filter")
    public ResponseEntity<?> filter(MemberRequest filterParam) {

        //
        return ResponseEntity.ok(JsonResult.success());
    }

}
