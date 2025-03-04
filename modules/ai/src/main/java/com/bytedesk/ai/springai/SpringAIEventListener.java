/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-24 09:34:56
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-04 17:40:12
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

import com.bytedesk.ai.springai.event.VectorSplitEvent;
import com.bytedesk.ai.springai.zhipuai.SpringAIZhipuaiService;
import com.bytedesk.core.redis.pubsub.RedisPubsubParseFileErrorEvent;
import com.bytedesk.core.redis.pubsub.RedisPubsubParseFileSuccessEvent;
import com.bytedesk.core.redis.pubsub.message.RedisPubsubMessageFile;
import com.bytedesk.kbase.faq.FaqEntity;
import com.bytedesk.kbase.faq.FaqRestService;
import com.bytedesk.kbase.faq.event.FaqCreateEvent;
import com.bytedesk.kbase.faq.event.FaqUpdateEvent;
import com.bytedesk.kbase.file.FileEntity;
import com.bytedesk.kbase.file.event.FileCreateEvent;
import com.bytedesk.kbase.file.event.FileUpdateEvent;
import com.bytedesk.kbase.split.SplitEntity;
import com.bytedesk.kbase.split.event.SplitCreateEvent;
import com.bytedesk.kbase.split.event.SplitUpdateEvent;
// import com.bytedesk.kbase.qa.FaqEntity;
// import com.bytedesk.kbase.qa.event.FaqCreateEvent;
// import com.bytedesk.kbase.qa.event.FaqUpdateEvent;
import com.bytedesk.kbase.text.TextEntity;
import com.bytedesk.kbase.text.event.TextCreateEvent;
import com.bytedesk.kbase.text.event.TextUpdateEvent;
import com.bytedesk.kbase.website.WebsiteEntity;
import com.bytedesk.kbase.website.event.WebsiteCreateEvent;
import com.bytedesk.kbase.website.event.WebsiteUpdateEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpringAIEventListener {
    
    private final Optional<SpringAIVectorService> springAiVectorService;

    private final Optional<SpringAIZhipuaiService> springAIZhipuaiChatService;

    // private final Optional<OllamaChatService> ollamaChatService;

    private final FaqRestService faqRestService;

    @EventListener
    public void onFileCreateEvent(FileCreateEvent event) throws IOException {
        FileEntity file = event.getFile();
        log.info("SpringAIEventListener onFileCreateEvent: {}", file.getFileName());
        // etl分块处理
        springAiVectorService.ifPresent(service -> {
            service.readSplitWriteToVectorStore(file);
        });
    }

    @EventListener
    public void onFileUpdateEvent(FileUpdateEvent event) {
        FileEntity file = event.getFile();
        log.info("SpringAIEventListener onFileUpdateEvent: {}", file.getFileName());
        // 后台删除文件记录
        if (file.isDeleted()) {
            // 删除文件对应的document
            springAiVectorService.ifPresent(service -> {
                service.deleteDoc(file.getDocIdList());
            });
        }
    }

    @EventListener
    public void onTextCreateEvent(TextCreateEvent event) {
        TextEntity text = event.getText();
        log.info("SpringAIEventListener onTextCreateEvent: {}", text.getName());
        // 生成document
        springAiVectorService.ifPresent(service -> {
            service.readText(text);
        });
    }

    @EventListener
    public void onTextUpdateEvent(TextUpdateEvent event) {
        TextEntity text = event.getText();
        log.info("SpringAIEventListener onTextUpdateEvent: {}", text.getName());
        // 首先删除text对应的document，以及redis中缓存的document
        springAiVectorService.ifPresent(service -> {
            service.deleteDoc(text.getDocIdList());
        });
        // 然后重新生成document
        springAiVectorService.ifPresent(service -> {
            service.readText(text);
        });
    }

    @EventListener
    public void onFaqCreateEvent(FaqCreateEvent event) {
        FaqEntity qa = event.getFaq();
        log.info("SpringAIEventListener onFaqCreateEvent: {}", qa.getQuestion());
        // 生成document
        springAiVectorService.ifPresent(service -> {
            service.readFaq(qa);
        });
    }

    @EventListener
    public void onFaqUpdateEvent(FaqUpdateEvent event) {
        FaqEntity qa = event.getFaq();
        log.info("SpringAIEventListener onFaqUpdateEvent: {}", qa.getQuestion());
        // 首先删除text对应的document，以及redis中缓存的document
        // springAiVectorService.deleteDoc(qa.getDocIdList());
        // 然后重新生成document
        // springAiVectorService.readFaq(qa);
    }

    @EventListener
    public void onWebsiteCreateEvent(WebsiteCreateEvent event) {
        WebsiteEntity website = event.getWebsite();
        log.info("SpringAIEventListener onWebsiteCreateEvent: {}", website.getName());
        // 生成document
        springAiVectorService.ifPresent(service -> {
            service.readWebsite(website);
        });
    }

    @EventListener
    public void onWebsiteUpdateEvent(WebsiteUpdateEvent event) {
        WebsiteEntity website = event.getWebsite();
        log.info("SpringAIEventListener onWebsiteUpdateEvent: {}", website.getName());
        // 首先删除text对应的document，以及redis中缓存的document
        springAiVectorService.ifPresent(service -> {
            service.deleteDoc(website.getDocIdList());
        });
        // 然后重新生成document
        springAiVectorService.ifPresent(service -> {
            service.readWebsite(website);
        });
    }

    @EventListener
    public void onSplitCreateEvent(SplitCreateEvent event) {
        SplitEntity split = event.getSplit();
        log.info("SpringAIEventListener onSplitCreateEvent: {}", split.getName());
    }

    @EventListener
    public void onSplitUpdateEvent(SplitUpdateEvent event) {
        SplitEntity split = event.getSplit();
        log.info("SpringAIEventListener onSplitUpdateEvent: {}", split.getName());
        if (split.isDeleted()) {
            // 删除向量库
            springAiVectorService.ifPresent(service -> {
                service.deleteDoc(split.getDocId());
            });
        } else {
            // 更新向量库
            springAiVectorService.ifPresent(service -> {
                service.updateDoc(split.getDocId(), split.getContent(), split.getKbUid());
            });
        }
    }

    @EventListener
    public void onVectorSplitEvent(VectorSplitEvent event) {
        log.info("SpringAIEventListener onVectorSplitEvent: {}", event.getKbUid());
        // List<Document> docList = event.getDocuments();
        // String kbUid = event.getKbUid();
        // String orgUid = event.getOrgUid();
        // // 生成问答对
		// for (Document document : docList) {
        //     // 调用模型生成问答对
        //     // springAIZhipuaiChatService.ifPresent(service -> {
        //     //     String qaPairs = service.generateFaqPairsAsync(document.getText());
        //     //     // log.info("zhipuaiChatService generateFaqPairsAsync qaPairs {}", qaPairs);
        //     //     faqRestService.saveFaqPairs(qaPairs, kbUid, orgUid, document.getId());
        //     // });
        //     // ollamaChatService.ifPresent(service -> {
        //     //     String qaPairs = service.generateFaqPairsAsync(document.getText());
        //     //     log.info("generateFaqPairsAsync qaPairs {}", qaPairs);
        //     //     faqRestService.saveFaqPairs(qaPairs, kbUid, orgUid, document.getId());
        //     // });
        // }
    }

    @EventListener
    public void onRedisPubsubParseFileSuccessEvent(RedisPubsubParseFileSuccessEvent event) {
        RedisPubsubMessageFile messageFile = event.getMessageFile();
        log.info("UploadEventListener RedisPubsubParseFileSuccessEvent: {}", messageFile.toString());
        //
        // UploadEntity upload = uploadService.findByUid(messageFile.getFileUid())
        //         .orElseThrow(() -> new RuntimeException("upload not found by uid: " + messageFile.getFileUid()));
        // upload.setDocIdList(messageFile.getDocIds());
        // upload.setStatus(UploadStatusEnum.PARSE_FILE_SUCCESS.name());
        // uploadService.save(upload);
        // //
        // String user = upload.getUser();
        // UserProtobuf uploadUser = JSON.parseObject(user, UserProtobuf.class);

        // // 通知前端
        // JSONObject contentObject = new JSONObject();
        // contentObject.put(I18Consts.I18N_NOTICE_TITLE, I18Consts.I18N_NOTICE_PARSE_FILE_SUCCESS);
        // //
        // MessageProtobuf message = MessageUtils.createNoticeMessage(uidUtils.getCacheSerialUid(), uploadUser.getUid(), upload.getOrgUid(),
        //         JSON.toJSONString(contentObject));
        // messageSendService.sendProtobufMessage(message);
    }

    @EventListener
    public void onRedisPubsubParseFileErrorEvent(RedisPubsubParseFileErrorEvent event) {
        RedisPubsubMessageFile messageFile = event.getMessageFile();
        log.info("UploadEventListener RedisPubsubParseFileErrorEvent: {}", messageFile.toString());
        // UploadEntity upload = uploadService.findByUid(messageFile.getFileUid())
        //         .orElseThrow(() -> new RuntimeException("upload not found by uid: " + messageFile.getFileUid()));
        // upload.setStatus(UploadStatusEnum.PARSE_FILE_ERROR.name());
        // uploadService.save(upload);
        // //
        // String user = upload.getUser();
        // UserProtobuf uploadUser = JSON.parseObject(user, UserProtobuf.class);
        // // 通知前端
        // JSONObject contentObject = new JSONObject();
        // contentObject.put(I18Consts.I18N_NOTICE_TITLE, I18Consts.I18N_NOTICE_PARSE_FILE_ERROR);
        // //
        // MessageProtobuf message = MessageUtils.createNoticeMessage(uidUtils.getCacheSerialUid(), uploadUser.getUid(), upload.getOrgUid(),
        //         JSON.toJSONString(contentObject));
        // messageSendService.sendProtobufMessage(message);
    }
    
}
