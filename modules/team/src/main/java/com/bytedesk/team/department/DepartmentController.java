/*
 *  bytedesk.com https://github.com/Bytedesk/bytedesk
 *
 *  Copyright (C)  2013-2024 bytedesk.com
 *
 *  License restrictions
 * 
 *      Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售
 *  
 *  Business Source License 1.1: 
 *  https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 * 
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 */
package com.bytedesk.team.department;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.BaseRequest;
import com.bytedesk.core.utils.JsonResult;
// import com.bytedesk.core.utils.PageParam;

import lombok.RequiredArgsConstructor;

/**
 * 
 * http://localhost:9003/swagger-ui/index.html
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dep")
public class DepartmentController {

    private final DepartmentService departmentService;

    // /**
    // * 列表
    // *
    // * @return json
    // */
    // @GetMapping("/list")
    // public ResponseEntity<JsonResult<?>> list() {

    // return () -> {

    // // 分页查询
    // // List<RoleDTO> departmentDTOList = departmentService.findByUser();
    // //
    // return new JsonResult<>("获取成功", 200, false);
    // };
    // }

    // /**
    // * 查询
    // *
    // * @return json
    // */
    // @GetMapping("/query")
    // public ResponseEntity<JsonResult<?>> query(PageParam pageParam) {

    // return () -> {

    // // 分页查询
    // // Page<RoleDTO> departmentDTOPage = departmentService.findByUser(pageParam);
    // //
    // return new JsonResult<>("获取成功", 200, false);
    // };
    // }

    /**
     * 创建
     *
     * @param departmentRequest department
     * @return json
     */
    @PostMapping("/create")
    public ResponseEntity<JsonResult<?>> create(@RequestBody DepartmentRequest departmentRequest) {

        Department department = departmentService.create(departmentRequest);
        if (department == null) {
            return ResponseEntity.ok().body(new JsonResult<>("create dep failed", -1, false));
        }
        return ResponseEntity.ok().body(new JsonResult<>("create dep success", 200, department));
    }

    /**
     * 更新
     *
     * @param departmentRequest department
     * @return json
     */
    @PostMapping("/update")
    public ResponseEntity<JsonResult<?>> update(@RequestBody DepartmentRequest departmentRequest) {

        // RoleDTO departmentDTO = departmentService.update(departmentRequest);
        //
        return ResponseEntity.ok().body(new JsonResult<>("update dep success", 200, false));
    }

    /**
     * 删除
     *
     * @param departmentRequest department
     * @return json
     */
    @PostMapping("/delete")
    public ResponseEntity<JsonResult<?>> delete(@RequestBody DepartmentRequest departmentRequest) {

        //
        // departmentService.deleteById(departmentRequest.getId());

        return ResponseEntity.ok().body(new JsonResult<>("delete dep success", 200, departmentRequest.getId()));
    }

    /**
     * 搜索
     *
     * @return json
     */
    @GetMapping("/filter")
    public ResponseEntity<JsonResult<?>> filter(BaseRequest filterParam) {

        //
        // Page<RoleDTO> departmentDTOPage =
        // departmentService.findByNameContainingOrValueContainingAndUser(filterParam);
        //
        return ResponseEntity.ok().body(new JsonResult<>("filter dep success", 200, false));
    }

}
