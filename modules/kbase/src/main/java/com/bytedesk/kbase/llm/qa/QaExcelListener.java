/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-30 21:02:37
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-22 14:37:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm.qa;

import java.util.List;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson2.JSON;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// https://easyexcel.opensource.alibaba.com/docs/current/quickstart/read
@Slf4j
@RequiredArgsConstructor
public class QaExcelListener implements ReadListener<QaExcel> {

    private final QaRestService qaService;

    // private final String categoryUid;
    private final String uploadType;

    private final String fileUid;

    private final String kbUid;

    private final String orgUid;
    
    /**
     * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 100;

    /**
     * 缓存的数据
     */
    private List<QaEntity> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    
    /**
     * 这个每一条数据解析都会来调用
     */
    @Override
    public void invoke(QaExcel data, AnalysisContext context) {
        log.info("QaExcelListener invoke: {}", JSON.toJSONString(data));
        QaEntity qa = qaService.convertExcelToQa(data, uploadType, fileUid, kbUid, orgUid);
        cachedDataList.add(qa);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("QaExcelListener doAfterAllAnalysed");
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        log.info("所有数据解析完成！");
    }
    
    /**
     * 加上存储数据库
     */
    private void saveData() {
        // if (cachedDataList.size() > 0) {
        //     // 删除第一行标头
        //     cachedDataList.remove(0);
        // }
        log.info("{}条数据，开始存储数据库！", cachedDataList.size());
        qaService.saveAll(cachedDataList);
        log.info("存储数据库成功！");
    }

}
