/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:20:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-25 15:26:53
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

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.BaseRequest;
import com.bytedesk.core.utils.JsonResult;
// import com.bytedesk.core.utils.PageParam;

// import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 
 * http://localhost:9003/swagger-ui/index.html
 * https://www.bezkoder.com/swagger-3-annotations/#Swagger_3_annotations
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mem")
@Tag(name = "member - 成员", description = "member description")
public class MemberController {

    private final MemberService memberService;

    /**
     * create
     *
     * @param memberRequest role
     * @return json
     */
    @PostMapping("/create")
    public JsonResult<?> create(@RequestBody MemberRequest memberRequest) {

        return memberService.create(memberRequest);
    }

    /**
     * update
     *
     * @param memberRequest role
     * @return json
     */
    @PostMapping("/update")
    public JsonResult<?> update(@RequestBody MemberRequest memberRequest) {

        //
        return new JsonResult<>("update success", 200, false);
    }

    /**
     * delete
     *
     * @param memberRequest role
     * @return json
     */
    @PostMapping("/delete")
    public JsonResult<?> delete(@RequestBody MemberRequest memberRequest) {



        return new JsonResult<>("delete success", 200, memberRequest.getId());
    }

    /**
     * filter
     *
     * @return json
     */
    @GetMapping("/filter")
    public JsonResult<?> filter(BaseRequest filterParam) {

        //
        return new JsonResult<>("filter success", 200, false);
    }

}
