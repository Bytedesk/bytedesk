/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-28 06:48:10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-24 08:00:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
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
import com.bytedesk.core.event.GenericApplicationEvent;
import com.bytedesk.kbase.auto_reply.AutoReplyExcel;
import com.bytedesk.kbase.auto_reply.AutoReplyExcelListener;
import com.bytedesk.kbase.auto_reply.AutoReplyService;
import com.bytedesk.kbase.faq.FaqExcel;
import com.bytedesk.kbase.faq.FaqExcelListener;
import com.bytedesk.kbase.faq.FaqService;
import com.bytedesk.kbase.keyword.KeywordExcel;
import com.bytedesk.kbase.keyword.KeywordExcelListener;
import com.bytedesk.kbase.keyword.KeywordService;
import com.bytedesk.kbase.quick_reply.QuickReplyExcel;
import com.bytedesk.kbase.quick_reply.QuickReplyExcelListener;
import com.bytedesk.kbase.quick_reply.QuickReplyService;
import com.bytedesk.kbase.taboo.TabooExcel;
import com.bytedesk.kbase.taboo.TabooExcelListener;
import com.bytedesk.kbase.taboo.TabooService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class UploadEventListener {

    private final UploadService uploadService;

    private final KeywordService keywordService;

    private final FaqService faqService;

    private final AutoReplyService autoReplyService;

    private final QuickReplyService quickReplyService;

    private final TabooService tabooService;

    private final UploadVectorStore uploadVectorStore;

    @EventListener
    public void onUploadCreateEvent(GenericApplicationEvent<UploadCreateEvent> event) throws IOException {
        Upload upload = event.getObject().getUpload();
        log.info("UploadEventListener create: {}", upload.toString());
        // etl分块处理
        if (upload.getType().equals(UploadTypeEnum.LLM.name())) {
            uploadVectorStore.readSplitWriteToVectorStore(upload);
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
                EasyExcel.read(filePath, KeywordExcel.class,
                        new KeywordExcelListener(keywordService,
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
                EasyExcel.read(filePath, AutoReplyExcel.class, new AutoReplyExcelListener(
                        autoReplyService,
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
            } else if (upload.getType().equals(UploadTypeEnum.MEMBER.name())) {
                // TODO: 导入成员
                // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
                // https://easyexcel.opensource.alibaba.com/docs/current/quickstart/read
                // EasyExcel.read(filePath, MemberExcel.class, new
                // MemberExcelListener(memberService)).sheet().doRead();
            }
        }

    }

    @EventListener
    public void onUploadUpdateEvent(GenericApplicationEvent<UploadUpdateEvent> event) {
        Upload upload = event.getObject().getUpload();
        log.info("UploadEventListener update: {}", upload.toString());
        // 后台删除文件记录
        if (upload.isDeleted()) {
            // 删除redis中缓存的document
            uploadVectorStore.deleteDoc(upload.getDocIdList());
        }
    }

}
