package com.bytedesk.team.department;

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
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dep")
@Tag(name = "department - 部门", description = "department apis")
public class DepartmentController {

    private final DepartmentService departmentService;

    // @GetMapping("/query/all")
    // public ResponseEntity<?> queryAll(DepartmentRequest departmentRequest) {
    // //
    // List<DepartmentResponse> departmentList =
    // departmentService.queryAll(departmentRequest);
    // //
    // return ResponseEntity.ok(JsonResult.success(departmentList));
    // }

    /**
     * query org departments
     *
     * @return json
     */
    @GetMapping("/query/org")
    public ResponseEntity<?> query(DepartmentRequest departmentRequest) {
        //
        Page<DepartmentResponse> departmentPage = departmentService.queryByOrg(departmentRequest);
        //
        return ResponseEntity.ok(JsonResult.success(departmentPage));
    }

    /**
     * 创建
     *
     * @param departmentRequest department
     * @return json
     */
    @ActionAnnotation(title = "department", action = "create", description = "create department")
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody DepartmentRequest departmentRequest) {

        DepartmentResponse department = departmentService.create(departmentRequest);
        if (department == null) {
            return ResponseEntity.ok().body(JsonResult.error("create dep failed", -1));
        }
        return ResponseEntity.ok().body(JsonResult.success(department));
    }

    /**
     * 更新
     *
     * @param departmentRequest department
     * @return json
     */
    @ActionAnnotation(title = "department", action = "update", description = "update department")
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody DepartmentRequest departmentRequest) {

        DepartmentResponse department = departmentService.update(departmentRequest);
        if (department == null) {
            return ResponseEntity.ok().body(JsonResult.error("update dep failed", -1));
        }
        //
        return ResponseEntity.ok().body(JsonResult.success(department));
    }

    /**
     * 删除
     *
     * @param departmentRequest department
     * @return json
     */
    @ActionAnnotation(title = "department", action = "delete", description = "delete department")
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody DepartmentRequest departmentRequest) {

        departmentService.deleteByUid(departmentRequest);

        return ResponseEntity.ok().body(JsonResult.success("delete dep success"));
    }

}
