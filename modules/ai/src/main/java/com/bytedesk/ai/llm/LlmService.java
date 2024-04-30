/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-25 12:08:16
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-11 12:09:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.llm;
import org.springframework.stereotype.Service;

import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LlmService {

    private LlmRepository llmRepository;

    private final UidUtils uidUtils;

    public JsonResult<?> create(LlmRequest llmRequest) {


        return JsonResult.success();
    }

    public Llm getLlm(String type) {
        Llm llm = new Llm();
        llm.setLid(uidUtils.getCacheSerialUid());
        llm.setName("智谱AI");
        llm.setDescription("对接智谱API");
        llm.setType(type);
        llm.setEmbeddings("m3e-base");
        llm.setTopK(3);
        llm.setTemperature(0.9);
        llm.setScoreThreshold(0.5);
        llm.setPrompt("你是一个聪明、对人类有帮助的人工智能，你可以对人类提出的问题给出有用、详细、礼貌的回答");
        return llmRepository.save(llm);
    }

    public void initData() {

        if (llmRepository.count() > 0) {
            return;
        }
        // 
        getLlm("system");
    }


}
