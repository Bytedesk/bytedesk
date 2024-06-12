package com.bytedesk.team.group;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

/**
 * 
 * http://localhost:9003/swagger-ui/index.html
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/group")
@Tag(name = "group - 群组", description = "group apis")
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


}
