/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-01 06:47:27
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

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.action.ActionAnnotation;
import com.bytedesk.core.base.BaseController;
import com.bytedesk.core.utils.DateUtils;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
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
public class MemberController extends BaseController<MemberRequest> {

    private final MemberService memberService;

    /**
     * query by orgUid
     * 
     * @param memberRequest
     * @return
     */
    @Override
    public ResponseEntity<?> queryByOrg(MemberRequest memberRequest) {
        //
        Page<MemberResponse> memberResponse = memberService.queryByOrg(memberRequest);
        //
        return ResponseEntity.ok(JsonResult.success(memberResponse));
    }

    @Override
    public ResponseEntity<?> query(MemberRequest memberRequest) {
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

    /**
     * create
     *
     * @param memberRequest role
     * @return json
     */
    @ActionAnnotation(title = "member", action = "create", description = "create member")
    @Override
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
    @Override
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
    @Override
    public ResponseEntity<?> delete(@RequestBody MemberRequest memberRequest) {

        memberService.deleteByUid(memberRequest.getUid());

        return ResponseEntity.ok(JsonResult.success());
    }

    // https://github.com/alibaba/easyexcel
    // https://easyexcel.opensource.alibaba.com/docs/current/
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
            String fileName = "Member-" + DateUtils.formatDatetimeUid() + ".xlsx";
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

            // 转换数据
            List<MemberExcel> excelList = memberPage.getContent().stream().map(memberResponse -> memberService.convertToExcel(memberResponse)).toList();

            // write to excel
            EasyExcel.write(response.getOutputStream(), MemberExcel.class)
                    .autoCloseStream(Boolean.FALSE)
                    .sheet("Member")
                    .doWrite(excelList);

        } catch (Exception e) {
            // reset response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            //
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", "failure");
            jsonObject.put("message", "download faied " + e.getMessage());
            try {
                response.getWriter().println(JSON.toJSONString(jsonObject));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        return "";
    }

    
}
