/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-01 06:18:42
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-24 09:55:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.member;

import java.util.List;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.member.mq.MemberBatchMessageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// https://easyexcel.opensource.alibaba.com/docs/current/quickstart/read
@Slf4j
@RequiredArgsConstructor
public class MemberExcelListener implements ReadListener<MemberExcelImport> {

    private final MemberBatchMessageService memberBatchMessageService;

    private final String orgUid;
    
    /**
     * 每隔100条发送到消息队列，避免内存积累过多
     */
    private static final int BATCH_COUNT = 100;

    /**
     * 缓存的数据
     */
    private List<MemberExcelImport> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    
    /**
     * 这个每一条数据解析都会来调用
     */
    @Override
    public void invoke(MemberExcelImport data, AnalysisContext context) {
        log.info("MemberExcelListener invoke: {}", JSON.toJSONString(data));
        // 直接缓存Excel数据，不再进行转换
        cachedDataList.add(data);
        // 达到BATCH_COUNT了，需要发送到消息队列，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            sendToQueue();
            // 发送完成清理 list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("MemberExcelListener doAfterAllAnalysed");
        // 这里也要发送数据到队列，确保最后遗留的数据也处理
        sendToQueue();
        log.info("所有数据解析完成，已发送到消息队列进行异步处理！");
    }
    
    /**
     * 发送数据到消息队列进行异步处理
     * 这样可以避免批量保存时的OptimisticLockException问题
     */
    private void sendToQueue() {
        if (cachedDataList.size() > 0) {
            // 删除第一行标头（如果存在）
            if (cachedDataList.size() > 0 && isHeaderRow(cachedDataList.get(0))) {
                cachedDataList.remove(0);
            }
        }
        
        if (cachedDataList.size() > 0) {
            log.info("{}条数据，开始发送到消息队列进行异步处理！", cachedDataList.size());
            // 发送到消息队列进行异步批量导入
            memberBatchMessageService.sendBatchImportMessages(cachedDataList, orgUid);
            log.info("发送到消息队列成功！");
        }
    }
    
    /**
     * 判断是否为表头行
     * 简单的判断逻辑，可以根据实际情况调整
     */
    private boolean isHeaderRow(MemberExcelImport memberExcel) {
        return memberExcel != null && 
               ("姓名".equals(memberExcel.getNickname()) || 
                "昵称".equals(memberExcel.getNickname()) ||
                "工号".equals(memberExcel.getJobNo()) ||
                "职位".equals(memberExcel.getJobTitle()));
    }
    
}
