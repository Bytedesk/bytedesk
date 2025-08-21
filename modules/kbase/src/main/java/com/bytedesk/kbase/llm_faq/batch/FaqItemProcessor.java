/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-17 10:42:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-17 10:42:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_faq.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bytedesk.kbase.llm_faq.FaqEntity;
import com.bytedesk.kbase.llm_faq.FaqExcel;
import com.bytedesk.kbase.llm_faq.FaqRestService;

import lombok.extern.slf4j.Slf4j;

/**
 * FAQ数据处理器
 * 将Excel中的FAQ数据转换为FaqEntity对象
 */
@Slf4j
@Component
public class FaqItemProcessor implements ItemProcessor<FaqExcel, FaqEntity> {

    @Autowired
    private FaqRestService faqRestService;

    private String kbType;
    private String fileUid;
    private String kbUid;
    private String orgUid;
    
    /**
     * 设置批处理上下文参数
     * 
     * @param kbType 知识库类型
     * @param fileUid 文件UID
     * @param kbUid 知识库UID
     * @param orgUid 组织UID
     */
    public void setParameters(String kbType, String fileUid, String kbUid, String orgUid) {
        this.kbType = kbType;
        this.fileUid = fileUid;
        this.kbUid = kbUid;
        this.orgUid = orgUid;
    }

    /**
     * 处理每一条Excel中的FAQ数据
     * 
     * @param item Excel中的FAQ数据
     * @return 转换后的FaqEntity对象，如果已存在则返回null
     * @throws Exception 处理过程中的异常
     */
    @Override
    public FaqEntity process(FaqExcel item) throws Exception {
        log.debug("Processing FAQ: {}", item.getQuestion());
        
        // 使用已有的转换逻辑
        return faqRestService.convertExcelToFaq(item, kbType, fileUid, kbUid, orgUid);
    }
}
