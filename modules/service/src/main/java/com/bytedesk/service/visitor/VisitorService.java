/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-04 10:48:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.ClientConsts;
import com.bytedesk.core.constant.MessageTypeConsts;
import com.bytedesk.core.constant.StatusConsts;
import com.bytedesk.core.constant.ThreadTypeConsts;
import com.bytedesk.core.event.BytedeskEventPublisher;
import com.bytedesk.core.ip.IpService;
import com.bytedesk.core.message.Message;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.message.MessageService;
import com.bytedesk.core.rbac.user.UserResponseSimple;
import com.bytedesk.core.thread.Thread;
import com.bytedesk.core.thread.ThreadService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.agent.Agent;
import com.bytedesk.service.agent.AgentResponseSimple;
import com.bytedesk.service.agent.AgentService;
import com.bytedesk.service.workgroup.Workgroup;
import com.bytedesk.service.workgroup.WorkgroupService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class VisitorService {

    private final VisitorRepository visitorRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final IpService ipService;

    private final ThreadService threadService;

    private final AgentService agentService;

    private final WorkgroupService workgroupService;

    private final MessageService messageService;

    private final BytedeskEventPublisher bytedeskEventPublisher;
    
    @Cacheable(value = "visitor", key = "#uid", unless="#result == null")
    public Optional<Visitor> findByUid(String uid) {
        return visitorRepository.findByUid(uid);
    }

    /**
     * create visitor record
     * 
     * @param visitorRequest
     * @return
     */
    public Visitor create(VisitorRequest visitorRequest, HttpServletRequest request) {

        String uid = visitorRequest.getUid();
        log.info("visitor init, uid: {}", uid);
        // 
        Visitor visitor = findByUid(uid).orElse(null);
        if (visitor != null) {
            return visitor;
        }
        // 
        if (!StringUtils.hasText(visitorRequest.getNickname())) {
            visitorRequest.setNickname(createNickname(request));
        }
        if (!StringUtils.hasText(visitorRequest.getAvatar())) {
            visitorRequest.setAvatar(AvatarConsts.DEFAULT_VISITOR_AVATAR_URL);
        }
        // 
        String ip = ipService.getIp(request);
        if (ip != null) {
            visitorRequest.setIp(ip);
            visitorRequest.setIpLocation(ipService.getIpLocation(ip));
        }

        visitor = modelMapper.map(visitorRequest, Visitor.class);
        if (visitor != null) {
            visitor.setUid(uidUtils.getCacheSerialUid());
            return save(visitor);
        }

        return null;
    }
    
    /** */
    public MessageResponse createCustomerServiceThread(VisitorRequest visitorRequest) {
        // 
        String topic = visitorRequest.formatTopic(visitorRequest.getUid());
        // 
        Thread thread = getThread(visitorRequest, topic);
        // 
        if (thread == null) {
            return null;
        }
        
        // TODO: check is agent is online

        // TODO: check push token, if offline & has token, push offline message

        // 
        Message lastMessage = findOrCreateThreadMessage(visitorRequest, thread);
        // 
        MessageResponse messageResponse = convertToMessageResponse(lastMessage, thread);

        // notify agent - 通知客服
        notifyAgent(thread, messageResponse);
        
        return messageResponse;
    }

    private Thread getThread(VisitorRequest visitorRequest, String topic) {
        if (visitorRequest == null) {
            throw new IllegalArgumentException("visitorRequest cannot be null");
        }

        Optional<Thread> threadOptional = threadService.findByTopic(topic);
        Thread thread = threadOptional.orElseGet(() -> {
            // 
            String type = visitorRequest.formatType();
            // 
            Thread newThread = new Thread();
            newThread.setUid(uidUtils.getCacheSerialUid());
            newThread.setTopic(topic);
            newThread.setType(type);
            newThread.setClient(visitorRequest.getClient());
            // 
            VisitorResponseSimple visitor = convertToVisitorResponseSimple(visitorRequest);
            newThread.setUser(JSON.toJSONString(visitor));
            // 
            VisitorExtra extra = new VisitorExtra();
            extra.setVisitor(visitor);        
            // 
            if (type.equals(ThreadTypeConsts.APPOINTED)) {
                // 一对一
                String aid = visitorRequest.getSid();
                Optional<Agent> agentOptional = agentService.findByUid(aid);
                if (agentOptional.isPresent()) {
                    // 
                    Agent agent = agentOptional.get();
                    extra.setWelcomeTip(agent.getWelcomeTip());
                    extra.setAgent(agentService.convertToAgentResponseSimple(agent));
                    extra.setAutoCloseMin(agent.getAutoCloseMin());
                    // 
                    newThread.setOwner(agent.getUser());
                    newThread.setContent(agent.getWelcomeTip());
                    newThread.setOrgUid(agent.getOrgUid());
                } else {
                    log.error("agent aid {} not exist", aid);
                    return null;
                }
            } else {
                // 技能组
                log.debug("workgroup {}", topic);
                String aid = visitorRequest.getSid();
                Optional<Workgroup> workgroupOptional = workgroupService.findByWid(aid);
                if (workgroupOptional.isPresent()) {
                    Workgroup workgroup = workgroupOptional.get();
                    if (!workgroup.getAgents().isEmpty()) {
                        // 获取workgroup的第一个agent
                        // TODO: 根据算法选择一个agent
                        Agent agent = workgroup.getAgents().iterator().next();
                        extra.setWelcomeTip(workgroup.getWelcomeTip());
                        extra.setAgent(agentService.convertToAgentResponseSimple(agent));
                        extra.setAutoCloseMin(agent.getAutoCloseMin());
                        // 
                        newThread.setOwner(agent.getUser());
                        newThread.setContent(workgroup.getWelcomeTip());
                        newThread.setOrgUid(agent.getOrgUid());
                    } else {
                        log.error("No agents found in workgroup with wid {}", aid);
                        return null;
                    }
                } else {
                    log.error("workgroup wid {} not exist", aid);
                    return null;
                }
            }
            // 
            newThread.setExtra(JSON.toJSONString(extra));
            return threadService.save(newThread);
        });
        // 
        return thread;
    }
    
    private Message findOrCreateThreadMessage(VisitorRequest visitorRequest, Thread thread) {
        if (thread == null) {
            throw new IllegalArgumentException("Thread cannot be null");
        }

        // if thread is closed, reopen it and then create a new message
        if (threadService.isClosed(thread)) {
            VisitorExtra extra = JSON.parseObject(thread.getExtra(), VisitorExtra.class);
            thread.setContent(extra.getWelcomeTip());
            thread = threadService.reopen(thread);
            // 
            Message newMessage = createDefaultMessageForThread(thread);
            return messageService.save(newMessage);
        }

        // find the last message
        Optional<Message> messageOptional = messageService.findByThreadsUidInOrderByCreatedAtDesc(thread.getUid());
        if (messageOptional.isPresent()) {
            return messageOptional.get();
        }

        // create new message
        Message newMessage = createDefaultMessageForThread(thread);
        return messageService.save(newMessage);
    }

    private Message createDefaultMessageForThread(Thread thread) {
        VisitorExtra extra = JSON.parseObject(thread.getExtra(), VisitorExtra.class);
        UserResponseSimple user = convertToUserResponseSimple(extra.getAgent());

        Message message = Message.builder()
                // .mid(uidUtils.getCacheSerialUid())
                .type(MessageTypeConsts.NOTIFICATION_THREAD)
                .content(extra.getWelcomeTip())
                .status(StatusConsts.MESSAGE_STATUS_READ)
                .client(ClientConsts.CLIENT_SYSTEM)
                .user(JSON.toJSONString(user))
                .orgUid(thread.getOrgUid())
                .build();
        message.setUid(uidUtils.getCacheSerialUid());

        message.getThreads().add(thread);
        return message;
    }

    @Caching(put = {
        @CachePut(value = "visitor", key = "#visitor.uid"),
    })
    private Visitor save(Visitor visitor) {
        return visitorRepository.save(visitor);
    }

    public String createNickname(HttpServletRequest request) {

        String location = ipService.getIpLocation(request);
        // TODO: 修改昵称后缀数字为从1~递增
        String randomId = uidUtils.getCacheSerialUid().substring(11, 15);

        // location: "国家|区域|省份|城市|ISP"
        // location: "中国|0|湖北省|武汉市|联通"
        // 0|0|0|内网IP|内网IP
        String[] locals = location.split("|");
        // log.info("locals {}", location);
        if (locals.length > 2) {
            if (locals[2].equals("0")) {
                return "LOCAL" + randomId;
            }
            return locals[2] + randomId;
        }
        
        return "Visitor";
    }
    
    public void notifyAgent(Thread thread, MessageResponse messageResponse) {
        try {
            // 克隆MessageResponse对象
            MessageResponse agentMessageResponse = SerializationUtils.clone(messageResponse);
            
            // 验证并解析extra为VisitorExtra对象
            String extraJson = thread.getExtra();
            if (StringUtils.hasText(extraJson) && JSON.isValid(extraJson)) {
                VisitorExtra extra = JSON.parseObject(extraJson, VisitorExtra.class);
                
                // 验证Visitor对象并转换
                VisitorResponseSimple visitor = extra.getVisitor();
                if (visitor != null) {
                    // user替换成访客，否则客服端会显示客服自己的头像
                    UserResponseSimple user = convertToUserResponseSimple(visitor);
                    agentMessageResponse.setUser(user);

                    // 发布消息事件
                    String json = JSON.toJSONString(agentMessageResponse);
                    bytedeskEventPublisher.publishMessageJsonEvent(json);
                } else {
                    // 处理visitor为空的情况
                }
            } else {
                // 处理extraJson为空或无效的情况
            }
        } catch (Exception e) {
            // 处理其他异常
        }
    }

    public VisitorResponse convertToVisitorResponse(Visitor visitor) {
        return modelMapper.map(visitor, VisitorResponse.class);
    }

    public VisitorResponseSimple convertToVisitorResponseSimple(Visitor visitor) {
        return modelMapper.map(visitor, VisitorResponseSimple.class);
    }

    public VisitorResponseSimple convertToVisitorResponseSimple(VisitorRequest visitorRequest) {
        return modelMapper.map(visitorRequest, VisitorResponseSimple.class);
    }

    public UserResponseSimple convertToUserResponseSimple(AgentResponseSimple agentResponseSimple) {
        return modelMapper.map(agentResponseSimple, UserResponseSimple.class);
    }

    public UserResponseSimple convertToUserResponseSimple(VisitorResponseSimple visitorResponseSimple) {
        return modelMapper.map(visitorResponseSimple, UserResponseSimple.class);
    }

    public MessageResponse convertToMessageResponse(Message lastMessage, Thread thread) {
        // 
        MessageResponse messageResponse = modelMapper.map(lastMessage, MessageResponse.class);
        messageResponse.setThread(threadService.convertToThreadResponseSimple(thread));
        // 
        UserResponseSimple user = JSON.parseObject(lastMessage.getUser(), UserResponseSimple.class);
        messageResponse.setUser(user);

        return messageResponse;
    }
    
}
