/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-10 11:45:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-11 09:58:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  技术/商务联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow.result;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResultService {
    private final ResultRepository resultRepository;

    public Result createResult(Result result) {
        result.setCreatedAt(LocalDateTime.now());
        result.setUpdatedAt(LocalDateTime.now());
        return resultRepository.save(result);
    }

    public Result updateResult(String id, Result result) {
        Result existingResult = resultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Result not found"));

        existingResult.setAnswers(result.getAnswers());
        existingResult.setVariables(result.getVariables());
        existingResult.setStatus(result.getStatus());
        existingResult.setUpdatedAt(LocalDateTime.now());

        return resultRepository.save(existingResult);
    }

    public List<Result> getBotResults(String botId) {
        return resultRepository.findByBotId(botId);
    }

    public void deleteResults(String botId) {
        List<Result> results = resultRepository.findByBotId(botId);
        resultRepository.deleteAll(results);
    }
}
