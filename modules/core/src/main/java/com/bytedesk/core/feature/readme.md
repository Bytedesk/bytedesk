<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-19 10:20:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-19 10:21:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# Features

## 功能注册

```java
@PostConstruct
public void init() {
    // 注册IM模块基础功能
    featureService.registerFeature(
        "im.chat", 
        "即时通讯", 
        "im"
    );
    
    // 注册工单模块功能
    featureService.registerFeature(
        "ticket.basic",
        "工单管理",
        "ticket"
    );
    
    // 注册论坛模块功能
    featureService.registerFeature(
        "forum.post",
        "帖子管理", 
        "forum"
    );
}
```

## 功能状态更新

## 获取所有已启用的功能

```java
List<FeatureEntity> enabledFeatures = featureService.getEnabledFeatures();
```

## 按模块获取功能

## 检查功能是否启用

```java
if (featureService.isFeatureEnabled("ticket.basic")) {
    // 执行工单相关功能
} else {
    throw new FeatureNotEnabledException("工单功能未启用");
}
```

## 获取功能配置

## 更新功能配置

## 获取模块功能统计
