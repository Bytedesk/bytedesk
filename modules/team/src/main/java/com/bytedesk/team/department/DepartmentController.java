package com.bytedesk.team.department;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.BaseRequest;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 
 * http://localhost:9003/swagger-ui/index.html
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dep")
@Tag(name = "department - 部门", description = "department apis")
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * query org departments
     *
     * @return json
     */
    @GetMapping("/query")
    public ResponseEntity<?> query(DepartmentRequest departmentRequest) {
        //
        Page<DepartmentResponse> departmentPage = departmentService.query(departmentRequest);
        //
        return ResponseEntity.ok(JsonResult.success(departmentPage));
    }

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

        return ResponseEntity.ok().body(new JsonResult<>("delete dep success", 200, departmentRequest.getUid()));
    }

    /**
     * 搜索
     *
     * @return json
     */
    @GetMapping("/filter")
    public ResponseEntity<JsonResult<?>> filter(BaseRequest filterParam) {

        // Page<RoleDTO> departmentDTOPage =
        // departmentService.findByNameContainingOrValueContainingAndUser(filterParam);
        //
        return ResponseEntity.ok().body(new JsonResult<>("filter dep success", 200, false));
    }

}
