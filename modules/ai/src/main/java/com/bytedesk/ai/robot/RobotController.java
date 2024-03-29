/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 16:37:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-26 15:32:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.BaseRequest;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

/**
 * robot
 */
@RestController
@RequestMapping("/api/v1/robot")
@AllArgsConstructor
@Tag(name = "robot - 机器人", description = "robot description")
public class RobotController {

    private final RobotService robotService;

    /**
     * query
     * 
     * @param robotRequest
     * @return
     */
    @GetMapping("/query")
    public JsonResult<?> query(RobotRequest robotRequest) {

        return JsonResult.success(robotService.query(robotRequest));
    }

    /**
     * create
     *
     * @param robotRequest robot
     * @return json
     */
    @PostMapping("/create")
    public JsonResult<?> create(@RequestBody RobotRequest robotRequest) {

        return robotService.create(robotRequest);
    }

    /**
     * update
     *
     * @param robotRequest robot
     * @return json
     */
    @PostMapping("/update")
    public JsonResult<?> update(@RequestBody RobotRequest robotRequest) {

        //
        return new JsonResult<>("update success", 200, false);
    }

    /**
     * delete
     *
     * @param robotRequest robot
     * @return json
     */
    @PostMapping("/delete")
    public JsonResult<?> delete(@RequestBody RobotRequest robotRequest) {

        //

        return new JsonResult<>("delete success", 200, true);
    }

    /**
     * filter
     *
     * @return json
     */
    @GetMapping("/filter")
    public JsonResult<?> filter(BaseRequest filterParam) {

        //
        
        //
        return new JsonResult<>("filter success", 200, false);
    }

    
}
