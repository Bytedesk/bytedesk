/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-24 09:34:56
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-22 14:52:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.listener;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.ai.springai.service.SpringAIVectorService;
import com.bytedesk.core.quartz.event.QuartzOneMinEvent;
import com.bytedesk.core.redis.pubsub.RedisPubsubParseFileErrorEvent;
import com.bytedesk.core.redis.pubsub.RedisPubsubParseFileSuccessEvent;
import com.bytedesk.core.redis.pubsub.message.RedisPubsubMessageFile;
import com.bytedesk.kbase.faq.FaqEntity;
import com.bytedesk.kbase.faq.event.FaqDeleteEvent;
import com.bytedesk.kbase.llm.file.FileEntity;
import com.bytedesk.kbase.llm.file.event.FileCreateEvent;
import com.bytedesk.kbase.llm.file.event.FileDeleteEvent;
import com.bytedesk.kbase.llm.qa.QaEntity;
import com.bytedesk.kbase.llm.qa.event.QaCreateEvent;
import com.bytedesk.kbase.llm.qa.event.QaDeleteEvent;
import com.bytedesk.kbase.llm.qa.event.QaUpdateDocEvent;
import com.bytedesk.kbase.llm.text.TextEntity;
import com.bytedesk.kbase.llm.text.event.TextCreateEvent;
import com.bytedesk.kbase.llm.text.event.TextDeleteEvent;
import com.bytedesk.kbase.llm.text.event.TextUpdateDocEvent;
import com.bytedesk.kbase.llm.website.WebsiteEntity;
import com.bytedesk.kbase.llm.website.event.WebsiteCreateEvent;
import com.bytedesk.kbase.llm.website.event.WebsiteDeleteEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpringAIEventListener {

    private final SpringAIVectorService springAiVectorService;

    // 存储收集到的FAQ实体，用于批量处理
    private final ConcurrentHashMap<String, FaqEntity> faqCreateMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, FaqEntity> faqUpdateMap = new ConcurrentHashMap<>();

    // 存储收集到的QA实体，用于批量处理
    private final ConcurrentHashMap<String, QaEntity> qaCreateMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, QaEntity> qaUpdateMap = new ConcurrentHashMap<>();

    // 存储收集到的Text实体，用于批量处理
    private final ConcurrentHashMap<String, TextEntity> textCreateMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, TextEntity> textUpdateMap = new ConcurrentHashMap<>();

    // 存储收集到的File实体，用于批量处理
    private final ConcurrentHashMap<String, FileEntity> fileCreateMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, FileEntity> fileDeleteMap = new ConcurrentHashMap<>();

    @EventListener
    public void onFileCreateEvent(FileCreateEvent event) {
        FileEntity file = event.getFile();
        log.info("SpringAIEventListener onFileCreateEvent: {}", file.getFileName());
        // 将File实体添加到创建缓存中，而不是立即处理
        fileCreateMap.put(file.getUid(), file);
    }

    @EventListener
    public void onFileDeleteEvent(FileDeleteEvent event) {
        FileEntity file = event.getFile();
        log.info("SpringAIEventListener onFileDeleteEvent: {}", file.getFileName());
        // 将File实体添加到删除缓存中，而不是立即处理
        fileDeleteMap.put(file.getUid(), file);
    }

    @EventListener
    public void onTextCreateEvent(TextCreateEvent event) {
        TextEntity text = event.getText();
        log.info("SpringAIEventListener onTextCreateEvent: {}", text.getName());
        // 将Text实体添加到创建缓存中
        textCreateMap.put(text.getUid(), text);
    }

    @EventListener
    public void onTextUpdateDocEvent(TextUpdateDocEvent event) {
        TextEntity text = event.getText();
        log.info("SpringAIEventListener onTextUpdateEvent: {}", text.getName());
        if (!text.isDeleted()) {
            // 将Text实体添加到更新缓存中
            textUpdateMap.put(text.getUid(), text);
        }
    }

    @EventListener
    public void onTextDeleteEvent(TextDeleteEvent event) {
        TextEntity text = event.getText();
        log.info("SpringAIEventListener onTextDeleteEvent: {}", text.getName());
        // 删除text对应的document，以及redis中缓存的document
        springAiVectorService.deleteDocs(text.getDocIdList());
        // 从缓存中移除
        textCreateMap.remove(text.getUid());
        textUpdateMap.remove(text.getUid());
    }

    @EventListener
    public void onQaCreateEvent(QaCreateEvent event) {
        QaEntity qa = event.getQa();
        log.info("SpringAIEventListener onQaCreateEvent: {}", qa.getQuestion());
        // 将QA实体添加到创建缓存中
        qaCreateMap.put(qa.getUid(), qa);
    }

    @EventListener
    public void onQaQaUpdateDocEvent(QaUpdateDocEvent event) {
        QaEntity qa = event.getQa();
        log.info("SpringAIEventListener QaUpdateDocEvent: {}", qa.getQuestion());
        if (!qa.isDeleted()) {
            // 将QA实体添加到更新缓存中
            qaUpdateMap.put(qa.getUid(), qa);
        }
    }

    @EventListener
    public void onQaDeleteEvent(QaDeleteEvent event) {
        QaEntity qa = event.getQa();
        log.info("SpringAIEventListener onQaDeleteEvent: {}", qa.getQuestion());
        // 删除qa对应的document，以及redis中缓存的document
        springAiVectorService.deleteDocs(qa.getDocIdList());
        // 从缓存中移除
        qaCreateMap.remove(qa.getUid());
        qaUpdateMap.remove(qa.getUid());
    }

    // @EventListener
    // public void onFaqCreateEvent(FaqCreateEvent event) {
    // FaqEntity faq = event.getFaq();
    // log.info("SpringAIEventListener onFaqCreateEvent: {}", faq.getQuestion());
    // // 将FAQ实体添加到创建缓存中，而不是立即处理
    // faqCreateMap.put(faq.getUid(), faq);
    // }

    // @EventListener
    // public void onFaqUpdateDocEvent(FaqUpdateDocEvent event) {
    // FaqEntity faq = event.getFaq();
    // log.info("SpringAIEventListener onFaqUpdateDocEvent: {}", faq.getQuestion());
    // if (!faq.isDeleted()) {
    // // 将FAQ实体添加到更新缓存中，而不是立即处理
    // faqUpdateMap.put(faq.getUid(), faq);
    // }
    // }

    @EventListener
    public void onFaqDeleteEvent(FaqDeleteEvent event) {
        FaqEntity faq = event.getFaq();
        log.info("SpringAIEventListener onFaqDeleteEvent: {}", faq.getQuestion());
        // 删除faq对应的document，以及redis中缓存的document
        springAiVectorService.deleteDocs(faq.getDocIdList());
        // 从缓存中移除
        faqCreateMap.remove(faq.getUid());
        faqUpdateMap.remove(faq.getUid());
    }

    @EventListener
    public void onWebsiteCreateEvent(WebsiteCreateEvent event) {
        WebsiteEntity website = event.getWebsite();
        log.info("SpringAIEventListener onWebsiteCreateEvent: {}", website.getName());
        // 生成document
        springAiVectorService.readWebsite(website);
    }

    @EventListener
    public void onWebsiteDeleteEvent(WebsiteDeleteEvent event) {
        WebsiteEntity website = event.getWebsite();
        log.info("SpringAIEventListener onWebsiteDeleteEvent: {}", website.getName());
        // 删除text对应的document，以及redis中缓存的document
        springAiVectorService.deleteDocs(website.getDocIdList());
    }

    @EventListener
    public void onQuartzOneMinEvent(QuartzOneMinEvent event) {
        // 批量处理FAQ创建
        processFaqCreations();

        // 批量处理FAQ更新
        processFaqUpdates();

        // 批量处理QA创建
        processQaCreations();

        // 批量处理QA更新
        processQaUpdates();

        // 批量处理Text创建
        processTextCreations();

        // 批量处理Text更新
        processTextUpdates();

        // 批量处理File创建
        processFileCreations();

        // 批量处理File删除
        processFileDeletions();
    }

    private void processFaqCreations() {
        if (!faqCreateMap.isEmpty()) {
            log.info("处理FAQ创建: 数量: {}", faqCreateMap.size());
            Set<String> processedKeys = new HashSet<>();

            faqCreateMap.forEach((uid, faq) -> {
                try {
                    springAiVectorService.readFaq(faq);
                    processedKeys.add(uid);
                } catch (Exception e) {
                    log.error("处理FAQ创建失败: {} - {}", uid, e.getMessage());
                }
            });

            // 移除已处理的实体
            processedKeys.forEach(faqCreateMap::remove);
        }
    }

    private void processFaqUpdates() {
        if (!faqUpdateMap.isEmpty()) {
            log.info("处理FAQ更新: 数量: {}", faqUpdateMap.size());
            Set<String> processedKeys = new HashSet<>();

            faqUpdateMap.forEach((uid, faq) -> {
                try {
                    springAiVectorService.deleteDocs(faq.getDocIdList());
                    springAiVectorService.readFaq(faq);
                    processedKeys.add(uid);
                } catch (Exception e) {
                    log.error("处理FAQ更新失败: {} - {}", uid, e.getMessage());
                }
            });

            // 移除已处理的实体
            processedKeys.forEach(faqUpdateMap::remove);
        }
    }

    private void processQaCreations() {
        if (!qaCreateMap.isEmpty()) {
            log.info("处理QA创建: 数量: {}", qaCreateMap.size());
            Set<String> processedKeys = new HashSet<>();

            qaCreateMap.forEach((uid, qa) -> {
                try {
                    springAiVectorService.readQa(qa);
                    processedKeys.add(uid);
                } catch (Exception e) {
                    log.error("处理QA创建失败: {} - {}", uid, e.getMessage());
                }
            });

            // 移除已处理的实体
            processedKeys.forEach(qaCreateMap::remove);
        }
    }

    private void processQaUpdates() {
        if (!qaUpdateMap.isEmpty()) {
            log.info("处理QA更新: 数量: {}", qaUpdateMap.size());
            Set<String> processedKeys = new HashSet<>();

            qaUpdateMap.forEach((uid, qa) -> {
                try {
                    springAiVectorService.deleteDocs(qa.getDocIdList());
                    springAiVectorService.readQa(qa);
                    processedKeys.add(uid);
                } catch (Exception e) {
                    log.error("处理QA更新失败: {} - {}", uid, e.getMessage());
                }
            });

            // 移除已处理的实体
            processedKeys.forEach(qaUpdateMap::remove);
        }
    }

    private void processTextCreations() {
        if (!textCreateMap.isEmpty()) {
            log.info("处理Text创建: 数量: {}", textCreateMap.size());
            Set<String> processedKeys = new HashSet<>();

            textCreateMap.forEach((uid, text) -> {
                try {
                    springAiVectorService.readText(text);
                    processedKeys.add(uid);
                } catch (Exception e) {
                    log.error("处理Text创建失败: {} - {}", uid, e.getMessage());
                }
            });

            // 移除已处理的实体
            processedKeys.forEach(textCreateMap::remove);
        }
    }

    private void processTextUpdates() {
        if (!textUpdateMap.isEmpty()) {
            log.info("处理Text更新: 数量: {}", textUpdateMap.size());
            Set<String> processedKeys = new HashSet<>();

            textUpdateMap.forEach((uid, text) -> {
                try {
                    springAiVectorService.deleteDocs(text.getDocIdList());
                    springAiVectorService.readText(text);
                    processedKeys.add(uid);
                } catch (Exception e) {
                    log.error("处理Text更新失败: {} - {}", uid, e.getMessage());
                }
            });

            // 移除已处理的实体
            processedKeys.forEach(textUpdateMap::remove);
        }
    }

    private void processFileCreations() {
        if (!fileCreateMap.isEmpty()) {
            log.info("处理File创建: 数量: {}", fileCreateMap.size());
            Set<String> processedKeys = new HashSet<>();

            fileCreateMap.forEach((uid, file) -> {
                try {
                    springAiVectorService.readSplitWriteToVectorStore(file);
                    processedKeys.add(uid);
                } catch (Exception e) {
                    log.error("处理File创建失败: {} - {}", uid, e.getMessage());
                }
            });

            // 移除已处理的实体
            processedKeys.forEach(fileCreateMap::remove);
        }
    }

    private void processFileDeletions() {
        if (!fileDeleteMap.isEmpty()) {
            log.info("处理File删除: 数量: {}", fileDeleteMap.size());
            Set<String> processedKeys = new HashSet<>();

            fileDeleteMap.forEach((uid, file) -> {
                try {
                    springAiVectorService.deleteDocs(file.getDocIdList());
                    processedKeys.add(uid);
                } catch (Exception e) {
                    log.error("处理File删除失败: {} - {}", uid, e.getMessage());
                }
            });

            // 移除已处理的实体
            processedKeys.forEach(fileDeleteMap::remove);
        }
    }

    @EventListener
    public void onRedisPubsubParseFileSuccessEvent(RedisPubsubParseFileSuccessEvent event) {
        RedisPubsubMessageFile messageFile = event.getMessageFile();
        log.info("UploadEventListener RedisPubsubParseFileSuccessEvent: {}", messageFile.toString());
        //
        // UploadEntity upload = uploadService.findByUid(messageFile.getFileUid())
        // .orElseThrow(() -> new RuntimeException("upload not found by uid: " +
        // messageFile.getFileUid()));
        // upload.setDocIdList(messageFile.getDocIds());
        // upload.setStatus(UploadStatusEnum.PARSE_FILE_SUCCESS.name());
        // uploadService.save(upload);
        // //
        // String user = upload.getUser();
        // UserProtobuf uploadUser = JSON.parseObject(user, UserProtobuf.class);

        // // 通知前端
        // JSONObject contentObject = new JSONObject();
        // contentObject.put(I18Consts.I18N_NOTICE_TITLE,
        // I18Consts.I18N_NOTICE_PARSE_FILE_SUCCESS);
        // //
        // MessageProtobuf message =
        // MessageUtils.createNoticeMessage(uidUtils.getCacheSerialUid(),
        // uploadUser.getUid(), upload.getOrgUid(),
        // JSON.toJSONString(contentObject));
        // messageSendService.sendProtobufMessage(message);
    }

    @EventListener
    public void onRedisPubsubParseFileErrorEvent(RedisPubsubParseFileErrorEvent event) {
        RedisPubsubMessageFile messageFile = event.getMessageFile();
        log.info("UploadEventListener RedisPubsubParseFileErrorEvent: {}", messageFile.toString());
        // UploadEntity upload = uploadService.findByUid(messageFile.getFileUid())
        // .orElseThrow(() -> new RuntimeException("upload not found by uid: " +
        // messageFile.getFileUid()));
        // upload.setStatus(UploadStatusEnum.PARSE_FILE_ERROR.name());
        // uploadService.save(upload);
        // //
        // String user = upload.getUser();
        // UserProtobuf uploadUser = JSON.parseObject(user, UserProtobuf.class);
        // // 通知前端
        // JSONObject contentObject = new JSONObject();
        // contentObject.put(I18Consts.I18N_NOTICE_TITLE,
        // I18Consts.I18N_NOTICE_PARSE_FILE_ERROR);
        // //
        // MessageProtobuf message =
        // MessageUtils.createNoticeMessage(uidUtils.getCacheSerialUid(),
        // uploadUser.getUid(), upload.getOrgUid(),
        // JSON.toJSONString(contentObject));
        // messageSendService.sendProtobufMessage(message);
    }

}
