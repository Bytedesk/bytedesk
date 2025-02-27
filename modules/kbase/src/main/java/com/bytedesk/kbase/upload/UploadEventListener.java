/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-28 06:48:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-25 12:57:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.upload;

import java.io.IOException;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.alibaba.excel.EasyExcel;
import com.bytedesk.kbase.auto_reply.fixed.AutoReplyFixedExcel;
import com.bytedesk.kbase.auto_reply.fixed.AutoReplyFixedExcelListener;
import com.bytedesk.kbase.auto_reply.fixed.AutoReplyFixedRestService;
import com.bytedesk.kbase.auto_reply.keyword.AutoReplyKeywordExcel;
import com.bytedesk.kbase.auto_reply.keyword.AutoReplyKeywordExcelListener;
import com.bytedesk.kbase.auto_reply.keyword.AutoReplyKeywordRestService;
import com.bytedesk.kbase.faq.FaqExcel;
import com.bytedesk.kbase.faq.FaqExcelListener;
import com.bytedesk.kbase.faq.FaqRestService;
import com.bytedesk.kbase.quick_reply.QuickReplyExcel;
import com.bytedesk.kbase.quick_reply.QuickReplyExcelListener;
import com.bytedesk.kbase.quick_reply.QuickReplyRestService;
import com.bytedesk.kbase.taboo.TabooExcel;
import com.bytedesk.kbase.taboo.TabooExcelListener;
import com.bytedesk.kbase.taboo.TabooService;
import com.bytedesk.kbase.upload.event.UploadCreateEvent;
import com.bytedesk.kbase.upload.event.UploadUpdateEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class UploadEventListener {

    private final UploadRestService uploadService;

    private final AutoReplyKeywordRestService keywordService;

    private final FaqRestService faqService;

    private final AutoReplyFixedRestService AutoReplyFixedService;

    private final QuickReplyRestService quickReplyService;

    private final TabooService tabooService;

    // private final RedisPubsubService redisPubsubService;

    // private final UidUtils uidUtils;

    // private final IMessageSendService messageSendService;

    @EventListener
    public void onUploadCreateEvent(UploadCreateEvent event) throws IOException {
        UploadEntity upload = event.getUpload();
        log.info("UploadEventListener create: {}", upload.toString());
        // etl分块处理
        if (upload.getType().equals(UploadTypeEnum.LLM.name())) {
            // ai 模块处理
            return;
        }
        // 导入Excel文件
        Resource resource = uploadService.loadAsResource(upload.getFileName());
        if (resource.exists()) {
            String filePath = resource.getFile().getAbsolutePath();
            log.info("UploadEventListener loadAsResource: {}", filePath);
            if (upload.getType().equals(UploadTypeEnum.KEYWORD.name())) {
                // 导入关键字
                // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
                // https://easyexcel.opensource.alibaba.com/docs/current/quickstart/read
                EasyExcel.read(filePath, AutoReplyKeywordExcel.class,
                        new AutoReplyKeywordExcelListener(keywordService,
                                // upload.getCategoryUid(),
                                upload.getKbUid(),
                                upload.getOrgUid()))
                        .sheet().doRead();
            } else if (upload.getType().equals(UploadTypeEnum.FAQ.name())) {
                // 导入FAQ
                // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
                // https://easyexcel.opensource.alibaba.com/docs/current/quickstart/read
                EasyExcel.read(filePath, FaqExcel.class, new FaqExcelListener(faqService,
                        // upload.getCategoryUid(),
                        upload.getKbUid(),
                        upload.getOrgUid())).sheet().doRead();
            } else if (upload.getType().equals(UploadTypeEnum.AUTOREPLY.name())) {
                // 导入自动回复
                // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
                // https://easyexcel.opensource.alibaba.com/docs/current/quickstart/read
                EasyExcel.read(filePath, AutoReplyFixedExcel.class, new AutoReplyFixedExcelListener(
                        AutoReplyFixedService,
                        // upload.getCategoryUid(),
                        upload.getKbUid(),
                        upload.getOrgUid())).sheet().doRead();
            } else if (upload.getType().equals(UploadTypeEnum.QUICKREPLY.name())) {
                // 导入快捷回复
                // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
                // https://easyexcel.opensource.alibaba.com/docs/current/quickstart/read
                EasyExcel.read(filePath, QuickReplyExcel.class, new QuickReplyExcelListener(quickReplyService,
                        // upload.getCategoryUid(),
                        upload.getKbUid(),
                        upload.getOrgUid())).sheet()
                        .doRead();
            } else if (upload.getType().equals(UploadTypeEnum.TABOO.name())) {
                // 导入敏感词
                // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
                // https://easyexcel.opensource.alibaba.com/docs/current/quickstart/read
                EasyExcel.read(filePath, TabooExcel.class, new TabooExcelListener(tabooService,
                        // upload.getCategoryUid(),
                        upload.getKbUid(),
                        upload.getOrgUid())).sheet().doRead();
            }
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
            // springAIVectorService.deleteDoc(upload.getDocIdList());
        }
    }

   

}
