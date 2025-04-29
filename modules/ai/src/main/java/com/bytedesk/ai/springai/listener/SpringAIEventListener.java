/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-24 09:34:56
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-29 12:14:17
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

import com.bytedesk.ai.springai.service.SpringAIVectorStoreService;
import com.bytedesk.core.quartz.event.QuartzOneMinEvent;
import com.bytedesk.kbase.faq.FaqEntity;
import com.bytedesk.kbase.llm_file.FileEntity;
import com.bytedesk.kbase.llm_file.event.FileCreateEvent;
import com.bytedesk.kbase.llm_file.event.FileDeleteEvent;
import com.bytedesk.kbase.llm_text.TextEntity;
import com.bytedesk.kbase.llm_text.event.TextCreateEvent;
import com.bytedesk.kbase.llm_text.event.TextDeleteEvent;
import com.bytedesk.kbase.llm_text.event.TextUpdateDocEvent;
import com.bytedesk.kbase.llm_website.WebsiteEntity;
import com.bytedesk.kbase.llm_website.event.WebsiteCreateEvent;
import com.bytedesk.kbase.llm_website.event.WebsiteDeleteEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpringAIEventListener {

    private final SpringAIVectorStoreService springAiVectorService;
    
    // 存储收集到的FAQ实体，用于批量处理
    private final ConcurrentHashMap<String, FaqEntity> faqCreateMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, FaqEntity> faqUpdateMap = new ConcurrentHashMap<>();

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
        if (file.isAutoLlmSplit()) {
            // 仅当文件需要分割时，才添加到创建缓存中
            fileCreateMap.put(file.getUid(), file);
        } else if (file.isAutoGenerateLlmQa()) {
            // TODO: 仅当文件需要生成QA时，才添加到创建缓存中
        }
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
        if (text.isAutoDeleteLlmSplit()) {
            // 仅当文件需要分割时，才添加到创建缓存中
            textCreateMap.put(text.getUid(), text);
        } else if (text.isAutoGenerateLlmQa()) {
            // TODO: 仅当文件需要生成QA时，才添加到创建缓存中
        }
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
    public void onWebsiteCreateEvent(WebsiteCreateEvent event) {
        WebsiteEntity website = event.getWebsite();
        log.info("SpringAIEventListener onWebsiteCreateEvent: {}", website.getName());
        if (website.isAutoLlmSplit()) {
            // 仅当文件需要分割时，才添加到创建缓存中
            // websiteCreateMap.put(website.getUid(), website);
            // 生成document
            springAiVectorService.readWebsite(website);
        } else if (website.isAutoGenerateLlmQa()) {
            // TODO: 仅当文件需要生成QA时，才添加到创建缓存中
        }
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

    // @EventListener
    // public void onRedisPubsubParseFileSuccessEvent(RedisPubsubParseFileSuccessEvent event) {
    //     RedisPubsubMessageFile messageFile = event.getMessageFile();
    //     log.info("UploadEventListener RedisPubsubParseFileSuccessEvent: {}", messageFile.toString());
    // }

    // @EventListener
    // public void onRedisPubsubParseFileErrorEvent(RedisPubsubParseFileErrorEvent event) {
    //     RedisPubsubMessageFile messageFile = event.getMessageFile();
    //     log.info("UploadEventListener RedisPubsubParseFileErrorEvent: {}", messageFile.toString());
    // }

}
