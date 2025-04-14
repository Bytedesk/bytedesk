/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-28 11:44:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-14 09:39:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ai.springai.siliconflow;

import com.alibaba.fastjson2.JSON;
import com.aliyun.oss.common.utils.StringUtils;
import com.bytedesk.ai.springai.base.BaseSpringAIService;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeEnum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Optional;

/**
 * @author: https://github.com/fzj111
 * date: 2025-03-19
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "spring.ai.siliconflow.chat.enabled", havingValue = "true", matchIfMissing = false)
public class SpringAISiliconFlowService extends BaseSpringAIService {

    @Autowired(required = false)
    private Optional<OpenAiChatModel> siliconFlowChatModel;

    public SpringAISiliconFlowService() {
        super(); // 调用基类的无参构造函数
    }
}
