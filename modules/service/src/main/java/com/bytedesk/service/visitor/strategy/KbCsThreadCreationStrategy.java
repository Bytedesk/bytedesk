/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-29 22:59:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-07 09:45:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor.strategy;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadService;
import com.bytedesk.core.thread.ThreadStatusEnum;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.knowledge_base.Knowledgebase;
import com.bytedesk.kbase.knowledge_base.KnowledgebaseService;
import com.bytedesk.service.utils.ConvertServiceUtils;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.core.thread.Thread;

import lombok.AllArgsConstructor;

// 知识库对话
@Component("kbCsThreadStrategy")
@AllArgsConstructor
public class KbCsThreadCreationStrategy implements CsThreadCreationStrategy {

    private final KnowledgebaseService knowledgebaseService;

    private final ThreadService threadService;

    private final UidUtils uidUtils;

    @Override
    public MessageProtobuf createCsThread(VisitorRequest visitorRequest) {
        return createKbCsThread(visitorRequest);
    }

    public MessageProtobuf createKbCsThread(VisitorRequest visitorRequest) {

        String kbUid = visitorRequest.getSid();
        Knowledgebase knowledgebase = knowledgebaseService.findByUid(kbUid)
                .orElseThrow(() -> new RuntimeException("Knowledgebase " + kbUid + " not found"));
        //
        Thread thread = getKbThread(visitorRequest, knowledgebase);
        //
        return getKbMessage(visitorRequest, thread, knowledgebase);
    }

    private Thread getKbThread(VisitorRequest visitorRequest, Knowledgebase kb) {
        if (kb == null) {
            throw new RuntimeException("Knowledgebase cannot be null");
        }
        //
        String topic = TopicUtils.formatOrgKbThreadTopic(kb.getUid(), visitorRequest.getUid());
        Optional<Thread> threadOptional = threadService.findByTopic(topic);
        if (threadOptional.isPresent()) {
            return threadOptional.get();
        }
        //
        Thread thread = Thread.builder().build();
        thread.setUid(uidUtils.getCacheSerialUid());
        thread.setTopic(topic);
        thread.setType(ThreadTypeEnum.KB.name());
        thread.setUnreadCount(0);
        thread.setClient(ClientEnum.fromValue(visitorRequest.getClient()).name());
        //
        UserProtobuf visitor = ConvertServiceUtils.convertToUserProtobuf(visitorRequest);
        thread.setUser(JSON.toJSONString(visitor));
        //
        thread.setOrgUid(kb.getOrgUid());
        // thread.setExtra(JSON.toJSONString(ConvertAiUtils.convertToServiceSettingsResponseVisitor(
        // robot.getServiceSettings())));
        // thread.setAgent(JSON.toJSONString(ConvertAiUtils.convertToRobotProtobuf(robot)));
        //
        return thread;
    }

    private MessageProtobuf getKbMessage(VisitorRequest visitorRequest, Thread thread, Knowledgebase kb) {
        if (thread == null) {
            throw new RuntimeException("Thread cannot be null");
        }
        if (kb == null) {
            throw new RuntimeException("Knowledgebase cannot be null");
        }
        thread.setContent(kb.getName());
        //
        boolean isReenter = true;
        if (thread.getStatus() == ThreadStatusEnum.NORMAL.name()) {
            isReenter = false;
        }
        // 更新机器人配置+大模型相关信息
        // thread.setExtra(JSON.toJSONString(ConvertAiUtils.convertToServiceSettingsResponseVisitor(
        // robot.getServiceSettings())));
        // thread.setAgent(JSON.toJSONString(ConvertAiUtils.convertToRobotProtobuf(robot)));
        //
        // if thread is closed, reopen it and then create a new message
        if (thread.isClosed()) {
            isReenter = false;
            thread.setStatus(ThreadStatusEnum.REOPEN.name());
        } else {
            thread.setStatus(isReenter ? ThreadStatusEnum.CONTINUE.name() : ThreadStatusEnum.NORMAL.name());
        }
        threadService.save(thread);
        //
        UserProtobuf user = UserProtobuf.builder()
                .nickname(kb.getName())
                .avatar(kb.getLogoUrl())
                .build();
        user.setUid(kb.getUid());
        //
        // JSONObject userExtra = new JSONObject();
        // userExtra.put("llm", robot.getLlm().isEnabled());
        // userExtra.put("defaultReply", robot.getDefaultReply());
        // user.setExtra(JSON.toJSONString(userExtra));
        //
        return ThreadMessageUtil.getThreadMessage(user, thread, isReenter);
    }

}