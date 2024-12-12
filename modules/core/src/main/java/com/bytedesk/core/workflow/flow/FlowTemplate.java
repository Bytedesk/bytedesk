/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-11 12:24:30
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-11 12:31:16
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

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

import com.bytedesk.core.workflow.flow.model.Event;
import com.bytedesk.core.workflow.flow.model.Group;
import com.bytedesk.core.workflow.flow.model.Edge;
import com.bytedesk.core.workflow.flow.model.Variable;

@Data
public class FlowTemplate {
  private String version;
  private String id;
  private String name;
  private List<Event> events;
  private List<Group> groups;
  private List<Edge> edges;
  private List<Variable> variables;
  private Object theme;
  private String selectedThemeTemplateId;
  private Object settings;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String icon;
  private String folderId;
  private String publicId;
  private String customDomain;
  private String workspaceId;
  private Object resultsTablePreferences;
  private boolean isArchived;
  private boolean isClosed;
  private String whatsAppCredentialsId;
}