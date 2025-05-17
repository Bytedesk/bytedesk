/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-17 10:46:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-17 10:46:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.faq.batch;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.bytedesk.kbase.faq.FaqExcel;

import lombok.extern.slf4j.Slf4j;

/**
 * 实现Spring Batch的ItemReader接口，用于读取Excel文件中的FAQ数据
 */
@Slf4j
@Component
public class FaqExcelReader implements ItemReader<FaqExcel> {

    private Resource resource;
    private Queue<FaqExcel> items;
    private boolean initialized = false;

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    /**
     * 读取下一个FAQ记录
     */
    @Override
    public FaqExcel read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (!initialized) {
            initialize();
        }
        
        return items.poll();
    }
    
    /**
     * 初始化并预加载所有Excel数据
     */
    private void initialize() throws IOException {
        log.info("初始化Excel读取器: {}", resource.getFilename());
        items = new LinkedList<>();
        
        // 使用自定义监听器加载所有数据到队列
        EasyExcel.read(resource.getInputStream(), FaqExcel.class, new FaqExcelBatchReadListener(items)).sheet().doRead();
        
        initialized = true;
        log.info("Excel加载完成，共读取{}条记录", items.size());
    }
    
    /**
     * 自定义监听器，将读取到的数据添加到队列中
     */
    private static class FaqExcelBatchReadListener implements ReadListener<FaqExcel> {
        
        private final Queue<FaqExcel> itemsQueue;
        
        public FaqExcelBatchReadListener(Queue<FaqExcel> itemsQueue) {
            this.itemsQueue = itemsQueue;
        }

        @Override
        public void invoke(FaqExcel data, AnalysisContext context) {
            itemsQueue.offer(data);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // 读取完成
        }
    }
}
