/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-28 13:05:59
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-10 17:03:44
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.test.prefix;

import org.springframework.data.annotation.Id;

import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.AutoComplete;
import com.redis.om.spring.annotations.AutoCompletePayload;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

// https://github.com/redis/redis-om-spring
// https://github.com/redis-developer/redis-om-autocomplete-demo
@Data
@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Document
public class Airport {
  @Id
  private String id;
  @AutoComplete @NonNull
  private String name;
  @AutoCompletePayload("name") @NonNull
  private String code;
  @AutoCompletePayload("name") @NonNull
  private String state;
}
