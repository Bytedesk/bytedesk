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
 *  https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  
 */
package com.bytedesk.core.rbac.authority;

import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bytedesk.core.utils.JsonResult;

/**
 * 权限管理
 */
// @Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/authority")
public class AuthorityController {

    private final AuthorityService authorityService;

    /**
     * 全部
     *
     * @return json
     */
    // @GetMapping("/list")
    // public Callable<JsonResult<?>> list() {

    // return () -> {

    // // 全部查询
    // List<AuthorityDTO> authorityDTOPage = authorityService.findByUser();

    // return new JsonResult<>("获取全部权限成功", 200, authorityDTOPage);
    // };
    // }

    /**
     * 查询
     *
     * @return json
     */
    @GetMapping("/query")
    public ResponseEntity<JsonResult<?>> query(AuthorityRequest authorityRequest) {


        List<Authority> authorityList = authorityService.findAll();

        return ResponseEntity.ok().body(new JsonResult<>("get authority success", 200, authorityList)); 
    }

    /**
     * 创建
     *
     * @param authorityParam authority
     * @return
     */
    // @PostMapping("/create")
    // public Callable<JsonResult<?>> create(@RequestBody AuthorityParam authorityParam) {

    //     return () -> {
    //         //
    //         AuthorityDTO authorityDTO = authorityService.createDTO(authorityParam);

    //         return new JsonResult<>("创建权限成功", 200, authorityDTO);
    //     };
    // }

    /**
     * 更新
     *
     * @param authorityParam authority
     * @return json
     */
    // @PutMapping("/update")
    // public Callable<JsonResult<?>> update(@RequestBody AuthorityParam authorityParam) {

    //     return () -> {

    //         AuthorityDTO authorityDTO = authorityService.updateDTO(authorityParam);

    //         return new JsonResult<>("更新权限成功", 200, authorityDTO);
    //     };
    // }

    /**
     * 删除
     *
     * @param authorityParam authority
     * @return json
     */
    // @DeleteMapping("/delete")
    // public Callable<JsonResult<?>> delete(AuthorityParam authorityParam) {

    //     return () -> {

    //         authorityService.deleteById(authorityParam.getId());

    //         return new JsonResult<>("删除权限成功", 200, authorityParam.getId());
    //     };
    // }

    /**
     * 搜索
     *
     * @return json
     */
    // @GetMapping("/filter")
    // public Callable<JsonResult<?>> filter(FilterParam filterParam) {

    // return () -> {
    // // 分页查询
    // Pageable pageable = PageRequest.of(filterParam.getPage(),
    // filterParam.getSize(), Sort.Direction.DESC, "id");
    // //
    // Page<AuthorityDTO> authorityDTOPage =
    // authorityService.findByContent(filterParam.getContent(), pageable);
    // //
    // return new JsonResult<>("搜索权限成功", 200, authorityDTOPage);
    // };
    // }

}
