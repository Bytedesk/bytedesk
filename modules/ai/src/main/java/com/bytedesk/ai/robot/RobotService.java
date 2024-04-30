/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 16:44:41
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-17 23:57:32
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
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bytedesk.ai.kb.KbService;
import com.bytedesk.ai.llm.LlmService;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RobotService {

    private final RobotRepository robotRepository;

    private final AuthService authService;

    private final LlmService llmService;

    private final KbService kbService;

    private final UserService userService;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    public Page<RobotResponse> query(RobotRequest robotRequest) {

        User user = authService.getCurrentUser();

        Pageable pageable = PageRequest.of(robotRequest.getPageNumber(), robotRequest.getPageSize(), Sort.Direction.DESC,
                "id");
        // 
        Page<Robot> robotPage = robotRepository.findByUser(user, pageable);

        return robotPage.map(robot -> modelMapper.map(robot, RobotResponse.class));
    }

    public JsonResult<?> create(RobotRequest robotRequest) {

        User user = authService.getCurrentUser();

        if (robotRepository.existsByUserAndName(user, robotRequest.getName())) {
            return JsonResult.error("机器人名称已存在");
        }
        // 
        Robot robot = modelMapper.map(robotRequest, Robot.class);
        // 
        String rid = uidUtils.getCacheSerialUid();
        robot.setRid(rid);

        robot.setAvatar(AvatarConsts.DEFAULT_AVATAR_URL);
        robot.setDescription("default robot description");
        robot.setWelcome("您好，有什么可以帮您的？");
        // robot.setType(null)
        robot.setPublished(false);

        robot.setLlm(llmService.getLlm("user"));
        robot.setKb(kbService.getKb(rid));
        robot.setUser(user);

        Robot result = robotRepository.save(robot);

        return JsonResult.success(result);
    }


    // @SuppressWarnings("null")
    public void initData() {
        
        if (robotRepository.count() > 0) {
            return;
        }

        // 
        Optional<User> adminOptional = userService.getAdmin();
        if (adminOptional.isPresent()) {
            // 
            String rid = uidUtils.getCacheSerialUid();
            Robot robot = Robot.builder()
                    .rid(rid)
                    .name("客服机器人")
                    .avatar(AvatarConsts.DEFAULT_AVATAR_URL)
                    .description("客服机器人")
                    .welcome("欢迎使用客服机器人")
                    .published(false)
                    .type("service")
                    .llm(llmService.getLlm("user"))
                    .kb(kbService.getKb(rid))
                    .user(adminOptional.get())
                    .build();
            robotRepository.save(robot);
        }
        
        
        
    }
    
}
