/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-11 12:24:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-11 14:29:16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  技术/商务联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow.flow;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class FlowTemplateLoader {

  private final ResourceLoader resourceLoader;
  private final FlowRepository flowRepository;
  private final ObjectMapper objectMapper;

  @PostConstruct
  public void loadTemplates() throws IOException {
    // 加载所有模板文件
    String[] templates = {
        "workflows/audio-chat-gpt.json",
        "workflows/basic-chat-gpt.json",
        "workflows/chat-gpt-personas.json",
        "workflows/customer-support.json",
        "workflows/digital-product-payment.json",
        "workflows/dog-insurance-offer.json",
        "workflows/faq.json",
        "workflows/high-ticket-lead-follow-up.json",
        "workflows/lead-gen-ai.json",
        "workflows/lead-gen.json",
        "workflows/lead-magnet.json",
        "workflows/lead-scoring.json",
        "workflows/movie-recommendation.json",
        "workflows/nps.json",
        "workflows/onboarding.json",
        "workflows/openai-assistant-chat.json",
        "workflows/openai-conditions.json",
        "workflows/product-recommendation.json",
        "workflows/quick-carb-calculator.json",
        "workflows/quiz.json",
        "workflows/savings-estimator.json",
        "workflows/skin-typology.json"
    };

    for (String templatePath : templates) {
      Resource resource = resourceLoader.getResource("classpath:" + templatePath);
      try (InputStream is = resource.getInputStream()) {
        FlowTemplate template = objectMapper.readValue(is, FlowTemplate.class);
        saveTemplate(template);
      }
    }
  }

  private void saveTemplate(FlowTemplate template) {
    // 检查是否已存在
    if (flowRepository.findByUid(template.getId()).isPresent()) {
      return;
    }

    // 转换为 FlowEntity
    FlowEntity flow = FlowEntity.builder().build();
    flow.setUid(template.getId());
    flow.setName(template.getName());
    flow.setIcon(template.getIcon());
    flow.setGroups(JSON.toJSONString(template.getGroups()));
    flow.setEvents(JSON.toJSONString(template.getEvents()));
    flow.setVariables(JSON.toJSONString(template.getVariables()));
    flow.setEdges(JSON.toJSONString(template.getEdges()));
    flow.setTheme(JSON.toJSONString(template.getTheme()));
    flow.setSettings(JSON.toJSONString(template.getSettings()));
    flow.setSelectedThemeTemplateId(template.getSelectedThemeTemplateId());
    flow.setPublicId(template.getPublicId());
    flow.setCustomDomain(template.getCustomDomain());
    flow.setResultsTablePreferences(JSON.toJSONString(template.getResultsTablePreferences()));
    flow.setArchived(template.isArchived());
    flow.setClosed(template.isClosed());
    flow.setWhatsAppCredentialsId(template.getWhatsAppCredentialsId());
    flow.setCreatedAt(LocalDateTime.now());
    flow.setUpdatedAt(LocalDateTime.now());
    flowRepository.save(flow);
  }
}