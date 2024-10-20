/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-27 13:53:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-16 15:21:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.knowledge_base;

import java.util.Date;
import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.config.BytedeskProperties;
import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageCache;
import com.bytedesk.core.message.MessageExtra;
import com.bytedesk.core.message.MessageJsonEvent;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.MessageUtils;
import com.bytedesk.core.rbac.organization.Organization;
import com.bytedesk.core.rbac.organization.OrganizationCreateEvent;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.redis.pubsub.RedisPubsubService;
import com.bytedesk.core.thread.ThreadProtobuf;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.core.uid.UidUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class KnowledgebaseEventListener {

        private final KnowledgebaseService knowledgebaseService;

        // private final ZhipuaiService zhipuaiService;

        // private final BytedeskEventPublisher bytedeskEventPublisher;

        private final UidUtils uidUtils;

        private final RedisPubsubService redisPubsubService;

        private final BytedeskProperties bytedeskProperties;

        private final MessageCache messageCache;

        private final IMessageSendService messageSendService;

        // BdConstants.DEFAULT_ORGANIZATION_UID
        @EventListener
        public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
                Organization organization = (Organization) event.getSource();
                String orgUid = organization.getUid();
                log.info("onOrganizationCreateEvent: orgUid {}", orgUid);
                //
                KnowledgebaseRequest kownledgebaseRequestHelpdoc = KnowledgebaseRequest.builder()
                                .name(KnowledgebaseConsts.KB_HELPDOC_NAME)
                                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                                .language(LanguageEnum.ZH_CN.name())
                                .build();
                kownledgebaseRequestHelpdoc.setType(KnowledgebaseTypeEnum.HELPDOC.name());
                kownledgebaseRequestHelpdoc.setOrgUid(orgUid);
                knowledgebaseService.create(kownledgebaseRequestHelpdoc);
                //
                KnowledgebaseRequest kownledgebaseRequestLlm = KnowledgebaseRequest.builder()
                                .name(KnowledgebaseConsts.KB_LLM_NAME)
                                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                                .language(LanguageEnum.ZH_CN.name())
                                .build();
                kownledgebaseRequestLlm.setType(KnowledgebaseTypeEnum.LLM.name());
                kownledgebaseRequestLlm.setOrgUid(orgUid);
                knowledgebaseService.create(kownledgebaseRequestLlm);
                //
                KnowledgebaseRequest kownledgebaseRequestKeyword = KnowledgebaseRequest.builder()
                                .name(KnowledgebaseConsts.KB_KEYWORD_NAME)
                                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                                .language(LanguageEnum.ZH_CN.name())
                                .build();
                kownledgebaseRequestKeyword.setType(KnowledgebaseTypeEnum.KEYWORD.name());
                kownledgebaseRequestKeyword.setOrgUid(orgUid);
                knowledgebaseService.create(kownledgebaseRequestKeyword);
                //
                KnowledgebaseRequest kownledgebaseRequeqstFaq = KnowledgebaseRequest.builder()
                                .name(KnowledgebaseConsts.KB_FAQ_NAME)
                                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                                .language(LanguageEnum.ZH_CN.name())
                                .build();
                kownledgebaseRequeqstFaq.setType(KnowledgebaseTypeEnum.FAQ.name());
                kownledgebaseRequeqstFaq.setOrgUid(orgUid);
                knowledgebaseService.create(kownledgebaseRequeqstFaq);
                //
                KnowledgebaseRequest kownledgebaseRequeqstAutoReply = KnowledgebaseRequest.builder()
                                .name(KnowledgebaseConsts.KB_AUTOREPLY_NAME)
                                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                                .language(LanguageEnum.ZH_CN.name())
                                .build();
                kownledgebaseRequeqstAutoReply.setType(KnowledgebaseTypeEnum.AUTOREPLY.name());
                kownledgebaseRequeqstAutoReply.setOrgUid(orgUid);
                knowledgebaseService.create(kownledgebaseRequeqstAutoReply);
                //
                KnowledgebaseRequest kownledgebaseRequeqstQuickReply = KnowledgebaseRequest.builder()
                                .name(KnowledgebaseConsts.KB_QUICKREPLY_NAME)
                                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                                .language(LanguageEnum.ZH_CN.name())
                                .build();
                kownledgebaseRequeqstQuickReply.setType(KnowledgebaseTypeEnum.QUICKREPLY.name());
                kownledgebaseRequeqstQuickReply.setOrgUid(orgUid);
                knowledgebaseService.create(kownledgebaseRequeqstQuickReply);
                //
                KnowledgebaseRequest kownledgebaseRequestTaboo = KnowledgebaseRequest.builder()
                                .name(KnowledgebaseConsts.KB_TABOO_NAME)
                                .descriptionHtml(KnowledgebaseConsts.KB_DESCRIPTION)
                                .language(LanguageEnum.ZH_CN.name())
                                .build();
                kownledgebaseRequestTaboo.setType(KnowledgebaseTypeEnum.TABOO.name());
                kownledgebaseRequestTaboo.setOrgUid(orgUid);
                knowledgebaseService.create(kownledgebaseRequestTaboo);
        }

        @EventListener
        public void onMessageJsonEvent(MessageJsonEvent event) {
                // log.info("MessageJsonEvent {}", event.getJson());
                String messageJson = event.getJson();
                //
                processMessage(messageJson);
        }

        // @EventListener
        // public void onMessageProtoEvent(MessageProtoEvent event) {
        //         // log.info("MessageProtoEvent");
        //         try {
        //                 MessageProto.Message messageProto = MessageProto.Message.parseFrom(event.getMessageBytes());
        //                 //
        //                 try {
        //                         String messageJson = MessageConvertUtils.toJson(messageProto);
        //                         //
        //                         processMessage(messageJson);

        //                 } catch (IOException e) {
        //                         e.printStackTrace();
        //                 }
        //         } catch (InvalidProtocolBufferException e) {
        //                 e.printStackTrace();
        //         }
        // }

        private void processMessage(String messageJson) {
                MessageProtobuf messageProtobuf = JSON.parseObject(messageJson, MessageProtobuf.class);
                MessageTypeEnum messageType = messageProtobuf.getType();
                if (messageType.equals(MessageTypeEnum.STREAM)) {
                        // ai回答暂不处理
                        return;
                }
                if (messageProtobuf.getUser().getUid().equals(BdConstants.DEFAULT_SYSTEM_UID)) {
                        // 系统消息不处理
                        return;
                }
                String query = messageProtobuf.getContent();
                // log.info("kb processMessage {}", query);
                //
                ThreadProtobuf thread = messageProtobuf.getThread();
                if (thread == null) {
                        throw new RuntimeException("thread is null");
                }
                // 仅针对文本类型自动回复
                if (!messageType.equals(MessageTypeEnum.TEXT)) {
                        return;
                }
                //
                String threadTopic = thread.getTopic();
                if (thread.getType().equals(ThreadTypeEnum.KB)) {
                        log.info("knowledge_base threadTopic {}, thread.type {}", threadTopic, thread.getType());
                        // 机器人客服消息 org/kb/default_kb_uid/1420995827073219
                        String[] splits = threadTopic.split("/");
                        if (splits.length < 4) {
                                throw new RuntimeException("kb topic format error");
                        }
                        String kbUid = splits[2];
                        if (!StringUtils.hasText(kbUid)) {
                                throw new RuntimeException("kbUid is null");
                        }
                        if (messageProtobuf.getUser().getUid().equals(kbUid)) {
                                return;
                        }
                        Optional<Knowledgebase> kbOptional = knowledgebaseService.findByUid(kbUid);
                        if (kbOptional.isPresent()) {
                                Knowledgebase kb = kbOptional.get();
                                //
                                UserProtobuf user = UserProtobuf.builder().build();
                                user.setUid(kbUid);
                                user.setNickname(kb.getName());
                                user.setAvatar(kb.getLogoUrl());
                                //
                                MessageExtra extra = MessageUtils.getMessageExtra(kb.getOrgUid());
                                //
                                String messageUid = uidUtils.getUid();
                                MessageProtobuf message = MessageProtobuf.builder()
                                                .uid(messageUid)
                                                .status(MessageStatusEnum.SUCCESS)
                                                .thread(thread)
                                                .user(user)
                                                .client(ClientEnum.SYSTEM_AUTO)
                                                .extra(JSONObject.toJSONString(extra))
                                                .createdAt(new Date())
                                                .build();

                                // 返回一个输入中消息，让访客端显示输入中
                                MessageProtobuf clonedMessage = SerializationUtils.clone(message);
                                clonedMessage.setUid(uidUtils.getUid());
                                clonedMessage.setType(MessageTypeEnum.PROCESSING);
                                // MessageUtils.notifyUser(clonedMessage);
                                messageSendService.sendMessage(messageProtobuf);
                                // 知识库
                                // if (bytedeskProperties.getJavaai()) {
                                //         zhipuaiService.sendWsRobotMessage(query, kb.getKbUid(), kb, message);
                                // }
                                // 通知python ai模块处理回答
                                if (bytedeskProperties.getPythonai()) {
                                        messageCache.put(messageUid, message);
                                        redisPubsubService.sendQuestionMessage(messageUid, threadTopic,
                                                        kb.getUid(),
                                                        query);
                                }
                        } else {
                                log.error("kb not found");
                        }
                }
        }

}
