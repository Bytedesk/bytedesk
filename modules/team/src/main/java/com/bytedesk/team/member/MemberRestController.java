/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-05 16:45:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.member;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.excel.EasyExcel;
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.core.utils.JsonResult;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * http://127.0.0.1:9003/swagger-ui/index.html
 * https://www.bezkoder.com/swagger-3-annotations/#Swagger_3_annotations
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
// @Tag(name = "member", description = "member apis")
public class MemberRestController extends BaseRestController<MemberRequest> {

    private final MemberRestService memberService;

    @Override
    public ResponseEntity<?> queryByOrg(MemberRequest memberRequest) {
        //
        Page<MemberResponse> memberResponse = memberService.queryByOrg(memberRequest);
        //
        return ResponseEntity.ok(JsonResult.success(memberResponse));
    }

    @Override
    public ResponseEntity<?> queryByUser(MemberRequest memberRequest) {
        //
        MemberResponse memberResponse = memberService.query(memberRequest);
        //
        return ResponseEntity.ok(JsonResult.success(memberResponse));
    }

    @GetMapping("/query/userUid")
    public ResponseEntity<?> queryByUserUid(MemberRequest memberRequest) {
        //
        MemberResponse memberResponse = memberService.queryByUserUid(memberRequest);
        //
        return ResponseEntity.ok(JsonResult.success(memberResponse));
    }

    @ActionAnnotation(title = "member", action = "create", description = "create member")
    @Override
    public ResponseEntity<?> create(@RequestBody MemberRequest memberRequest) {

        MemberResponse member = memberService.create(memberRequest);

        return ResponseEntity.ok(JsonResult.success(member));
    }

    @ActionAnnotation(title = "member", action = "update", description = "update member")
    @Override
    public ResponseEntity<?> update(@RequestBody MemberRequest memberRequest) {

        MemberResponse member = memberService.update(memberRequest);
        //
        return ResponseEntity.ok(JsonResult.success(member));
    }

    @ActionAnnotation(title = "member", action = "delete", description = "delete member")
    @Override
    public ResponseEntity<?> delete(@RequestBody MemberRequest memberRequest) {

        memberService.deleteByUid(memberRequest.getUid());

        return ResponseEntity.ok(JsonResult.success());
    }

    @ActionAnnotation(title = "member", action = "export", description = "export member")
    @GetMapping("/export")
    public Object export(MemberRequest request, HttpServletResponse response) {
        // query data to export
        Page<MemberResponse> memberPage = memberService.queryByOrg(request);
        // 
        try {
            //
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // download filename
            String fileName = "team-member-" + BdDateUtils.formatDatetimeUid() + ".xlsx";
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

            // 转换数据
            List<MemberExcel> excelList = memberPage.getContent().stream().map(memberResponse -> memberService.convertToExcel(memberResponse)).toList();
            // write to excel
            EasyExcel.write(response.getOutputStream(), MemberExcel.class)
                    .autoCloseStream(Boolean.FALSE)
                    .sheet("member")
                    .doWrite(excelList);

        } catch (Exception e) {
            // reset response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            //
            return JsonResult.error(e.getMessage());
        }

        return "";
    }

    @Override
    public ResponseEntity<?> queryByUid(MemberRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }

    
}
