<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-28 11:11:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-28 11:13:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# token

## 第三方平台访问令牌

- 用于第三方平台调用本系统 API 接口时的身份认证
- 可以设置不同的权限级别和访问范围
- 支持多种权限控制

## 用户登录令牌

- 记录用户登录会话的token
- 支持token过期机制
- 当token过期后,客户端需要重新登录获取新token

## 这样的设计可以

- 支持灵活的认证授权机制
- 实现细粒度的权限控制
- 管理token生命周期
- 支持多客户端接入
- 保证系统安全性

## 建议添加的优化

- 添加token使用记录
- 支持token自动续期
- 实现token黑名单机制
- 增加token吊销原因记录
- 支持多种token格式(如JWT)
