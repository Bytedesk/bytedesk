/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-24 09:34:56
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-25 12:45:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.ai.document.Document;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.ai.provider.vendors.zhipuai.ZhipuaiChatService;
import com.bytedesk.ai.springai.event.VectorSplitEvent;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageUtils;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.redis.pubsub.RedisPubsubParseFileErrorEvent;
import com.bytedesk.core.redis.pubsub.RedisPubsubParseFileSuccessEvent;
import com.bytedesk.core.redis.pubsub.message.RedisPubsubMessageFile;
import com.bytedesk.kbase.faq.FaqRestService;
import com.bytedesk.kbase.upload.UploadEntity;
import com.bytedesk.kbase.upload.UploadStatusEnum;
import com.bytedesk.kbase.upload.UploadTypeEnum;
import com.bytedesk.kbase.upload.event.UploadCreateEvent;
import com.bytedesk.kbase.upload.event.UploadUpdateEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpringAIEventListener {
    
    private final SpringAIVectorService springAiVectorService;

    private final Optional<ZhipuaiChatService> zhipuaiChatService;

    private final FaqRestService faqRestService;

    @EventListener
    public void onUploadCreateEvent(UploadCreateEvent event) throws IOException {
        UploadEntity upload = event.getUpload();
        log.info("SpringAIEventListener onUploadCreateEvent: {}", upload.toString());
        // etl分块处理
        if (upload.getType().equals(UploadTypeEnum.LLM.name())) {
            // 通知python ai模块处理
            springAiVectorService.readSplitWriteToVectorStore(upload);
            return;
        }
    }

    @EventListener
    public void onUploadUpdateEvent(UploadUpdateEvent event) {
        UploadEntity upload = event.getUpload();
        log.info("UploadEventListener update: {}", upload.toString());
        // 后台删除文件记录
        if (upload.isDeleted()) {
            // 通知python ai模块处理
            // redisPubsubService.sendDeleteFileMessage(upload.getUid(), upload.getDocIdList());
            // 删除redis中缓存的document
            // uploadVectorStore.deleteDoc(upload.getDocIdList());
        }
    }

    @EventListener
    public void onVectorSplitEvent(VectorSplitEvent event) {
        log.info("SpringAIEventListener onVectorSplitEvent: {}", event.toString());
        List<Document> docList = event.getDocuments();
        String kbUid = event.getKbUid();
        String orgUid = event.getOrgUid();
        // 生成问答对
		for (Document document : docList) {
            // 调用模型生成问答对
            zhipuaiChatService.ifPresent(service -> {
                String qaPairs = service.generateQaPairsAsync(document.getText());
                // log.info("generateQaPairsAsync qaPairs {}", qaPairs);
                faqRestService.saveQaPairs(qaPairs, kbUid, orgUid, document.getId());
            });
        }
    }

    @EventListener
    public void onRedisPubsubParseFileSuccessEvent(RedisPubsubParseFileSuccessEvent event) {
        RedisPubsubMessageFile messageFile = event.getMessageFile();
        log.info("UploadEventListener RedisPubsubParseFileSuccessEvent: {}", messageFile.toString());
        //
        UploadEntity upload = uploadService.findByUid(messageFile.getFileUid())
                .orElseThrow(() -> new RuntimeException("upload not found by uid: " + messageFile.getFileUid()));
        upload.setDocIdList(messageFile.getDocIds());
        upload.setStatus(UploadStatusEnum.PARSE_FILE_SUCCESS.name());
        uploadService.save(upload);
        //
        String user = upload.getUser();
        UserProtobuf uploadUser = JSON.parseObject(user, UserProtobuf.class);

        // 通知前端
        JSONObject contentObject = new JSONObject();
        contentObject.put(I18Consts.I18N_NOTICE_TITLE, I18Consts.I18N_NOTICE_PARSE_FILE_SUCCESS);
        //
        MessageProtobuf message = MessageUtils.createNoticeMessage(uidUtils.getCacheSerialUid(), uploadUser.getUid(), upload.getOrgUid(),
                JSON.toJSONString(contentObject));
        messageSendService.sendProtobufMessage(message);
    }

    @EventListener
    public void onRedisPubsubParseFileErrorEvent(RedisPubsubParseFileErrorEvent event) {
        RedisPubsubMessageFile messageFile = event.getMessageFile();
        log.info("UploadEventListener RedisPubsubParseFileErrorEvent: {}", messageFile.toString());
        UploadEntity upload = uploadService.findByUid(messageFile.getFileUid())
                .orElseThrow(() -> new RuntimeException("upload not found by uid: " + messageFile.getFileUid()));
        upload.setStatus(UploadStatusEnum.PARSE_FILE_ERROR.name());
        uploadService.save(upload);
        //
        String user = upload.getUser();
        UserProtobuf uploadUser = JSON.parseObject(user, UserProtobuf.class);
        // 通知前端
        JSONObject contentObject = new JSONObject();
        contentObject.put(I18Consts.I18N_NOTICE_TITLE, I18Consts.I18N_NOTICE_PARSE_FILE_ERROR);
        //
        MessageProtobuf message = MessageUtils.createNoticeMessage(uidUtils.getCacheSerialUid(), uploadUser.getUid(), upload.getOrgUid(),
                JSON.toJSONString(contentObject));
        messageSendService.sendProtobufMessage(message);
    }
    
}
