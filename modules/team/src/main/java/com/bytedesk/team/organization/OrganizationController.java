package com.bytedesk.team.organization;

import java.util.Optional;

import org.springframework.data.domain.Page;
// import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

/**
 * 
 * http://localhost:9003/swagger-ui/index.html
 */
// @Slf4j
@AllArgsConstructor
// @RepositoryRestController("/v1/org")
@RestController
@RequestMapping("/api/v1/org")
public class OrganizationController {

    private final OrganizationService organizationService;

    /**
     * query user organizations
     *
     * @return json
     */
    @GetMapping("/query")
    public ResponseEntity<JsonResult<?>> query(OrganizationRequest pageParam) {
        //
        Page<Organization> orgPage = organizationService.queryMyOrgs(pageParam);
        // //
        return ResponseEntity.ok().body(new JsonResult<>("get my orgs success", 200, orgPage));
    }

    /**
     * 
     * @param request
     * @return
     */
    @GetMapping("/query/oid")
    public ResponseEntity<JsonResult<?>> queryByOid(OrganizationRequest request) {
        //
        Optional<Organization> organizations = organizationService.findByOid(request.getOid());
        //
        return ResponseEntity.ok().body(new JsonResult<>("get or by oid success", 200, organizations));
    }

    // /**
    // * 创建
    // *
    // * @param roleParam role
    // * @return json
    // */
    // @PostMapping("/create")
    // public Callable<JsonResult<?>> create(@RequestBody RoleParam roleParam) {

    // return () -> {

    // // RoleDTO roleDTO = roleService.create(roleParam);

    // return new JsonResult<>("创建成功", 200, false);
    // };
    // }

    // /**
    // * 更新
    // *
    // * @param roleParam role
    // * @return json
    // */
    // @PostMapping("/update")
    // public Callable<JsonResult<?>> update(@RequestBody RoleParam roleParam) {

    // return () -> {

    // // RoleDTO roleDTO = roleService.update(roleParam);
    // //
    // return new JsonResult<>("更新成功", 200, false);
    // };
    // }

    // /**
    // * 删除
    // *
    // * @param roleParam role
    // * @return json
    // */
    // @PostMapping("/delete")
    // public Callable<JsonResult<?>> delete(@RequestBody RoleParam roleParam) {

    // return () -> {
    // //
    // // roleService.deleteById(roleParam.getId());

    // return new JsonResult<>("删除成功", 200, roleParam.getId());
    // };
    // }

    // /**
    // * 搜索
    // *
    // * @return json
    // */
    // @GetMapping("/filter")
    // public Callable<JsonResult<?>> filter(FilterParam filterParam) {

    // return () -> {
    // //
    // // Page<RoleDTO> roleDTOPage =
    // // roleService.findByNameContainingOrValueContainingAndUser(filterParam);
    // //
    // return new JsonResult<>("搜索成功", 200, false);
    // };
    // }

}
