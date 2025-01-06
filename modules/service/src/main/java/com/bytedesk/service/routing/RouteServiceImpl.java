/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-19 18:56:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-03 13:33:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.routing;

// import org.springframework.stereotype.Service;

// import com.alibaba.fastjson2.JSON;
// import com.bytedesk.ai.robot.RobotEntity;
// import com.bytedesk.ai.utils.ConvertAiUtils;
// import com.bytedesk.core.message.IMessageSendService;
// import com.bytedesk.core.message.MessageProtobuf;
// import com.bytedesk.core.rbac.user.UserProtobuf;
// import com.bytedesk.service.agent.AgentEntity;
// import com.bytedesk.service.agent.AgentRestService;
// import com.bytedesk.service.strategy.ThreadMessageUtil;
// import com.bytedesk.service.utils.ConvertServiceUtils;
// import com.bytedesk.core.thread.ThreadEntity;
// import com.bytedesk.core.thread.ThreadRestService;
// import com.bytedesk.service.visitor.VisitorRequest;
// import com.bytedesk.service.workgroup.WorkgroupEntity;

// import jakarta.annotation.Nonnull;
// import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// @Service
// @AllArgsConstructor
// public class RouteServiceImpl implements IRouteService {

//     private final ThreadRestService threadService;

//     private final IMessageSendService messageSendService;

//     private final AgentRestService agentRestService;

//     @Override
//     public MessageProtobuf routeRobot(VisitorRequest request, @Nonnull ThreadEntity thread, @Nonnull RobotEntity robot) {
//         thread.setContent(robot.getServiceSettings().getWelcomeTip());
//         // 使用agent的serviceSettings配置
//         UserProtobuf agentProtobuf = ConvertAiUtils.convertToUserProtobuf(robot);
//         thread.setAgent(JSON.toJSONString(agentProtobuf));
//         //
//         thread.setRobot(true);
//         thread.setUnreadCount(0);
//         threadService.save(thread);
//         //
//         MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadWelcomeMessage(agentProtobuf, thread);
//         // 广播消息，由消息通道统一处理
//         // messageService.notifyUser(messageProtobuf);
//         return messageProtobuf;
//     }

//     @Override
//     public MessageProtobuf routeAgent(VisitorRequest visitorRequest, @Nonnull ThreadEntity thread, @Nonnull AgentEntity agent) {
//         log.info("RouteServiceImpl routeAgent: " + agent.getUid());
//         if (agent.isConnectedAndAvailable()) {
//             // 排队：在vip模块中处理
//             // 客服在线 且 接待状态
//             thread.setUnreadCount(1);
//             thread.setContent(agent.getServiceSettings().getWelcomeTip());
//             // 增加接待数量，待优化
//             agent.increaseThreadCount();
//             agentRestService.save(agent);
//         } else {
//             // 离线状态永远显示离线提示语，不显示“继续会话”
//             // 客服离线 或 非接待状态
//             thread.setOffline();
//             thread.setContent(agent.getServiceSettings().getLeavemsgTip());
//         }
//         threadService.save(thread);
//         //
//         UserProtobuf user = ConvertServiceUtils.convertToUserProtobuf(agent);
//         //
//         MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadWelcomeMessage(user, thread);
//         // 广播消息，由消息通道统一处理
//         // MessageUtils.notifyUser(messageProtobuf);
//         messageSendService.sendProtobufMessage(messageProtobuf);

//         return messageProtobuf;
//     }

//     @Override
//     public MessageProtobuf routeWorkgroup(VisitorRequest visitorRequest, ThreadEntity thread, WorkgroupEntity workgroup) {
//         log.info("RouteServiceImpl routeWorkgroup: " + workgroup.getUid());
//         // 排队在vip模块中处理
//         // 高级路由逻辑在vip模块中处理
//         if (workgroup.getAgents().isEmpty()) {
//             throw new RuntimeException("No agents found in workgroup with uid " + workgroup.getUid());
//         }
//         // 下面人工接待
//         AgentEntity agent = workgroup.nextAgent();
//         if (agent == null) {
//             throw new RuntimeException("No available agent found in workgroup with uid " + workgroup.getUid());
//         }
//         if (agent.isConnectedAndAvailable()) {
//             // 客服在线 且 接待状态
//             thread.setUnreadCount(1);
//             thread.setContent(workgroup.getServiceSettings().getWelcomeTip());
            
//         } else {
//             // 离线状态永远显示离线提示语，不显示“继续会话”
//             // 客服离线 或 非接待状态
//             thread.setOffline();
//             thread.setContent(workgroup.getServiceSettings().getLeavemsgTip());
//             // thread.setStatus(ThreadStateEnum.OFFLINE.name());
//         }
//         //
//         thread.setOwner(agent.getMember().getUser());
//         UserProtobuf agentProtobuf = ConvertServiceUtils.convertToUserProtobuf(agent);
//         thread.setAgent(JSON.toJSONString(agentProtobuf));
//         // return getWorkgroupMessage(visitorRequest, thread, agent, workgroup);
//         threadService.save(thread);
//         //
//         UserProtobuf user = ConvertServiceUtils.convertToUserProtobuf(agent);
//         //
//         MessageProtobuf messageProtobuf = ThreadMessageUtil.getThreadWelcomeMessage(user, thread);
//         // 广播消息，由消息通道统一处理
//         // MessageUtils.notifyUser(messageProtobuf);
//         messageSendService.sendProtobufMessage(messageProtobuf);

//         return messageProtobuf;
//     }

// }
