/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-19 12:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-19 12:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bytedesk.team.member.MemberEntity;
import com.bytedesk.team.member.MemberResponse;

@Configuration
public class ModelMapperConfig {
    
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        
        // 设置更严格的匹配策略
        modelMapper.getConfiguration()
            .setMatchingStrategy(MatchingStrategies.STRICT)
            .setSkipNullEnabled(true)
            .setFieldMatchingEnabled(true)
            .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
        
        // 解决 MemberEntity 到 MemberResponse 的映射问题
        modelMapper.createTypeMap(MemberEntity.class, MemberResponse.class)
            .addMappings(mapper -> {
                // 明确指定 user 映射
                mapper.map(src -> src.getUser(), MemberResponse::setUser);
                // 确保继承的 uid 正确映射
                mapper.map(src -> src.getUid(), MemberResponse::setUid);
            });
        
        return modelMapper;
    }
}