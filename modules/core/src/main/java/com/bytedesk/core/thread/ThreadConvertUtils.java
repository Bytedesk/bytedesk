/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-01 17:20:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-27 11:55:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import org.modelmapper.ModelMapper;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.utils.ApplicationContextHolder;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ThreadConvertUtils {

    private static ModelMapper getModelMapper() {
        return ApplicationContextHolder.getBean(ModelMapper.class);
    }

    public static ThreadProtobuf convertToThreadProtobuf(ThreadEntity thread) {
        ThreadProtobuf threadProtobuf = getModelMapper().map(thread, ThreadProtobuf.class);
        //
        if (thread.getUser() != null) {
            UserProtobuf user = UserProtobuf.fromJson(thread.getUser());
            if (user != null) {
                if (user.getExtra() == null) {
                    user.setExtra(BytedeskConsts.EMPTY_JSON_STRING);
                }
                threadProtobuf.setUser(user);
            }
        }
        //
        return threadProtobuf;
    }

    public static ThreadResponse convertToThreadResponse(ThreadEntity thread) {
        // ensureThreadResponseTypeMapConfigured(modelMapper);
        ThreadResponse threadResponse = getModelMapper().map(thread, ThreadResponse.class);

        // 兼容：thread.content 可能已升级为 ThreadContent JSON；对外仍返回可读摘要
        ThreadContent tc = ThreadContent.fromStored(thread.getContent());
        if (tc != null) {
            threadResponse.setContentObject(tc);
            threadResponse.setContent(tc.getDisplayText());
        }
        // 用于更新robot-agent-llm配置，不能修改为UserProtobuf,
        // 否则会内容缺失，因为可能为RobotProtobuf类型, 其中含有llm字段
        // if (thread.getAgent() != null) {
        // UserProtobuf agent = JSON.parseObject(thread.getAgent(), UserProtobuf.class);
        // threadResponse.setAgent(agent);
        // }
        // agent
        // if (thread.getAgent() != null) {
        //     UserProtobuf agent = UserProtobuf.fromJson(thread.getAgent());
        //     threadResponse.setAgentProtobuf(agent);
        // }
        UserProtobuf user = UserProtobuf.fromJson(thread.getUser());
        if (user != null) {
            threadResponse.setUser(user);
        }
        // if (thread.getOwner() != null) {
        //     if (Hibernate.isInitialized(thread.getOwner())) {
        //         threadResponse.setOwner(convertToUserProtobuf(thread.getOwner()));
        //     } else {
        //         threadResponse.setOwner(null);
        //     }
        // }
        // robot
        // if (thread.getRobot() != null) {
        //     UserProtobuf robot = UserProtobuf.fromJson(thread.getRobot());
        //     threadResponse.setRobotProtobuf(robot);
        // }
        // if (thread.getWorkgroup() != null) {
        //     UserProtobuf workgroup = UserProtobuf.fromJson(thread.getWorkgroup());
        //     threadResponse.setWorkgroup(workgroup);
        // }
        if (thread.getInvites() != null) {
            // 清空列表，防止modelMapper自动映射产生的空对象
            threadResponse.getInvites().clear();
            // 将string[]为UserProtobuf[]，并存入threadResponse.setInvites()中
            for (String invite : thread.getInvites()) {
                UserProtobuf inviteUser = UserProtobuf.fromJson(invite);
                if (inviteUser != null) {
                    threadResponse.getInvites().add(inviteUser);
                }
            }
        }
        if (thread.getMonitors() != null) {
            // 清空列表，防止modelMapper自动映射产生的空对象
            threadResponse.getMonitors().clear();
            for (String monitor : thread.getMonitors()) {
                UserProtobuf monitorUser = UserProtobuf.fromJson(monitor);
                if (monitorUser != null) {
                    threadResponse.getMonitors().add(monitorUser);
                }
            }
        }
        if (thread.getAssistants() != null) {
            // 清空列表，防止modelMapper自动映射产生的空对象
            threadResponse.getAssistants().clear();
            for (String assistant : thread.getAssistants()) {
                UserProtobuf assistantUser = UserProtobuf.fromJson(assistant);
                if (assistantUser != null) {
                    threadResponse.getAssistants().add(assistantUser);
                }
            }
        }
        
        // 手动设置业务逻辑字段
        threadResponse.setRobotToAgent(thread.isRobotToAgent());
        // 关闭来源类型
        threadResponse.setCloseType(thread.getCloseType());
        //
        return threadResponse;
    }

    public static ThreadResponseSimple convertToThreadResponseSimple(ThreadEntity thread) {
        ThreadContent tc = ThreadContent.fromStored(thread.getContent());
        String displayContent = tc != null ? tc.getDisplayText() : thread.getContent();
        ThreadResponseSimple threadResponse = ThreadResponseSimple.builder()
            .topic(thread.getTopic())
            .content(displayContent)
            .type(thread.getType())
            .status(thread.getStatus())
            .top(thread.getTop())
            .unread(thread.getUnread())
            .mute(thread.getMute())
            .hide(thread.getHide())
            .star(thread.getStar())
            .fold(thread.getFold())
            .closeType(thread.getCloseType())
            .note(thread.getNote())
            // .offline(thread.getOffline())
            .channel(thread.getChannel())
            .extra(thread.getExtra())
            .agent(thread.getAgent())
            .workgroup(thread.getWorkgroup())
        .build();
        if (tc != null) {
            threadResponse.setContentObject(tc);
        }
        // ThreadResponseSimple threadResponse = getModelMapper().map(thread, ThreadResponseSimple.class);
        threadResponse.setUser(thread.getUserProtobuf());
        // 
        return threadResponse;
    }
    

}
