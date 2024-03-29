package com.bytedesk.team.group;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

/**
 * 
 * http://localhost:9003/swagger-ui/index.html
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/group")
public class GroupController {

    private final GroupService groupService;

    /**
     * query all groups
     *
     * @return json
     */
    @GetMapping("/all")
    public ResponseEntity<JsonResult<?>> all() {

        List<Group> groupList = groupService.findAll();
        //
        return ResponseEntity.ok().body(new JsonResult<>("get all group success", 200, groupList));
    }

    /**
     * query by page
     *
     * @return json
     */
    @GetMapping("/query")
    public ResponseEntity<JsonResult<?>> query(GroupRequest groupRequest) {

        Page<Group> groupPage = groupService.query(groupRequest);
        //
        return ResponseEntity.ok().body(new JsonResult<>("query group by page success", 200, groupPage));
    }

    // /**
    // * 创建
    // *
    // * @param roleParam role
    // * @return json
    // */
    // @PostMapping("/create")
    // public ResponseEntity<JsonResult<?>> create(@RequestBody RoleParam roleParam)
    // {

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
    // public ResponseEntity<JsonResult<?>> update(@RequestBody RoleParam roleParam)
    // {

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
    // public ResponseEntity<JsonResult<?>> delete(@RequestBody RoleParam roleParam)
    // {

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
    // public ResponseEntity<JsonResult<?>> filter(FilterParam filterParam) {

    // return () -> {
    // //
    // // Page<RoleDTO> roleDTOPage =
    // // roleService.findByNameContainingOrValueContainingAndUser(filterParam);
    // //
    // return new JsonResult<>("搜索成功", 200, false);
    // };
    // }

}
