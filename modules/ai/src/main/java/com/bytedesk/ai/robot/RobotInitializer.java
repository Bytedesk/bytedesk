/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-26 15:01:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.bytedesk.ai.provider.vendors.ollama.OllamaChatService;
import com.bytedesk.ai.springai.SpringAIVectorService;
import com.bytedesk.ai.springai.demo.bytedesk.SpringAIBytedeskService;
import com.bytedesk.ai.springai.demo.utils.FileContent;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.utils.Utils;
import com.bytedesk.kbase.faq.FaqRestService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
@AllArgsConstructor
public class RobotInitializer implements SmartInitializingSingleton {

    private final RobotRestService robotService;

    private final SpringAIBytedeskService springAIBytedeskService;

    private final StringRedisTemplate stringRedisTemplate;

    private final SpringAIVectorService springAIVectorService;

    private final Optional<OllamaChatService> ollamaChatService;

    private final FaqRestService faqRestService;
    
    @Override
    public void afterSingletonsInstantiated() {
        initRobot();
    }

    private void initRobot() {
        //
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        // 为初始组织创建一个机器人
        robotService.initDefaultRobot(orgUid);
        
        // 首先redis中是否已经初始化此数据，如果没有，继续执行演示数据初始化
        String isInit = stringRedisTemplate.opsForValue().get(RobotConsts.ROBOT_INIT_DEMO_BYTEDESK_KEY);
        if (isInit == null) {
            String kbUid = Utils.formatUid(orgUid, BytedeskConsts.DEFAULT_KB_LLM_UID);
            // 默认使用演示文档内容，填充且只填充超级管理员演示机器人
            List<FileContent> files = springAIBytedeskService.getAllFiles();
            // 写入到redis vector 中
            for (FileContent file : files) {
                springAIVectorService.readText(file.getFilename(), file.getContent(), kbUid, orgUid);
                // 
                ollamaChatService.ifPresent(service -> {
                    String qaPairs = service.generateFaqPairsAsync(file.getContent());
                    log.info("generateFaqPairsAsync qaPairs {}", qaPairs);
                    // faqRestService.saveFaqPairs(qaPairs, kbUid, orgUid, "");
                });
            }
            // 设置redis key 为已初始化
            stringRedisTemplate.opsForValue().set(RobotConsts.ROBOT_INIT_DEMO_BYTEDESK_KEY, "true");
            // 删除 redis key
            // redisTemplate.delete(RobotConsts.ROBOT_INIT_DEMO_KEY);
            // 根据文档内容，生成问答对
            
        }
        
    }   
}