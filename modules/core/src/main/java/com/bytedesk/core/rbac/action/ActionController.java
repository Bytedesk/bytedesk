package com.bytedesk.core.rbac.action;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作：查询、插入、删除
 */
// @Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/action")
public class ActionController {

    // private final ActionService actionService;

    /**
     * 
     *
     * @return json
     */
    // @GetMapping("/list")
    // public JsonResult<?> list(@RequestParam(value = "client") final String client) {
        // //
        // final List<ActionDTO> actionDTOS = actionService.findAllDTO();

        // return new JsonResult<>("获取操作列表成功", 200, actionDTOS);
    // }

}
