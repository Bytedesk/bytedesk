/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bytedesk.core.rbac.role;

import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bytedesk.core.utils.JsonResult;

/**
 * 角色管理
 */
// @Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/role")
public class RoleController {

    private final RoleService roleService;
    
    /**
     * all
     *
     * @return json
     */
    @GetMapping("/all")
    public ResponseEntity<JsonResult<?>> all(RoleRequest roleRequest) {

        List<Role> roleList = roleService.findAll();
        //
        return ResponseEntity.ok().body(new JsonResult<>("get all roles success", 200, roleList));
    }

    /**
     * 创建
     *
     * @param roleParam role
     * @return json
     */
    // @PostMapping("/create")
    // public Callable<JsonResult<?>> create(@RequestBody RoleParam roleParam) {

    //     return () -> {

    //         RoleDTO roleDTO = roleService.create(roleParam);

    //         return new JsonResult<>("创建角色成功", 200, roleDTO);
    //     };
    // }

    /**
     * 更新
     *
     * @param roleParam role
     * @return json
     */
    // @PostMapping("/update")
    // public Callable<JsonResult<?>> update(@RequestBody RoleParam roleParam) {

    //     return () -> {

    //         RoleDTO roleDTO = roleService.update(roleParam);
    //         //
    //         return new JsonResult<>("更新角色成功", 200, roleDTO);
    //     };
    // }

    /**
     * 删除
     *
     * @param roleParam role
     * @return json
     */
    // @PostMapping("/delete")
    // public Callable<JsonResult<?>> delete(@RequestBody RoleParam roleParam) {

    //     return () -> {
    //         //
    //         roleService.deleteById(roleParam.getId());

    //         return new JsonResult<>("删除角色成功", 200, roleParam.getId());
    //     };
    // }

    /**
     * 搜索
     *
     * @return json
     */
    // @GetMapping("/filter")
    // public Callable<JsonResult<?>> filter(FilterParam filterParam) {

    //     return () -> {
    //         //
    //         Page<RoleDTO> roleDTOPage = roleService.findByNameContainingOrValueContainingAndUser(filterParam);
    //         //
    //         return new JsonResult<>("搜索角色成功", 200, roleDTOPage);
    //     };
    // }

}
