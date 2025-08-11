# CallNumberEntity 完善更新说明

## 更新概述

根据 `deploy/freeswitch/freeswitch_mysql_schema.sql` 中的 `freeswitch_users` 表结构，对 `CallNumberEntity.java` 进行了全面完善，使其与数据库表结构完全匹配。

## 主要更新内容

### 1. 表名更新
- 将 `@Table(name = "bytedesk_call_user")` 改为 `@Table(name = "freeswitch_users")`
- 确保实体类与实际的数据库表名一致

### 2. 字段映射完善

#### 核心字段（与数据库完全对应）
- `userId` ← `user_id` (SIP用户名，主键)
- `domain` ← `domain` (SIP域名，默认值: 14.103.165.199)
- `password` ← `password` (密码)
- `vmPassword` ← `vm_password` (语音邮件密码)
- `enabled` ← `enabled` (是否启用)
- `callerIdName` ← `caller_id_name` (呼叫显示名称)
- `callerIdNumber` ← `caller_id_number` (呼叫显示号码)
- `outboundCallerIdName` ← `outbound_caller_id_name` (外呼显示名称)
- `outboundCallerIdNumber` ← `outbound_caller_id_number` (外呼显示号码)
- `tollAllow` ← `toll_allow` (通话权限，默认值: domestic,international,local)
- `accountcode` ← `accountcode` (账户代码)
- `userContext` ← `user_context` (用户上下文，默认值: default)
- `callgroup` ← `callgroup` (呼叫组，默认值: bytedesk)

#### 兼容字段（保留原有功能）
- `displayName` ← `display_name` (显示名称，兼容字段)
- `email` (邮箱)
- `lastRegister` ← `last_register` (最后注册时间)
- `registerIp` ← `register_ip` (注册IP地址)
- `userAgent` ← `user_agent` (用户代理)
- `remarks` (备注)

### 3. 字段约束完善
- 添加了 `@Column` 注解的 `nullable`、`length` 等约束
- 设置了合适的默认值
- 确保必填字段的 `nullable = false`

### 4. 方法增强
- `getSipAddress()`: 返回完整的SIP地址 (userId@domain)
- `isOnline()`: 检查用户是否在线
- `getEffectiveCallerIdName()`: 获取有效的呼叫显示名称
- `getEffectiveCallerIdNumber()`: 获取有效的呼叫显示号码
- `hasOutboundPermission()`: 检查外呼权限

### 5. 相关类更新
- 更新了 `CallNumberEntityListener.java` 中的字段引用
- 将 `getUsername()` 改为 `getUserId()`

## 数据库兼容性

### 字段长度限制
- `user_id`: 100字符
- `domain`: 100字符
- `password`: 100字符
- `vm_password`: 50字符
- `caller_id_name`: 100字符
- `caller_id_number`: 50字符
- `outbound_caller_id_name`: 100字符
- `outbound_caller_id_number`: 50字符
- `toll_allow`: 200字符
- `accountcode`: 50字符
- `user_context`: 50字符
- `callgroup`: 50字符

### 默认值设置
- `domain`: "14.103.165.199"
- `enabled`: true
- `toll_allow`: "domestic,international,local"
- `user_context`: "default"
- `callgroup`: "bytedesk"

## 使用建议

1. **SIP地址获取**: 使用 `getSipAddress()` 方法获取完整的SIP地址
2. **呼叫显示**: 使用 `getEffectiveCallerIdName()` 和 `getEffectiveCallerIdNumber()` 获取有效的显示信息
3. **权限检查**: 使用 `hasOutboundPermission()` 检查用户的外呼权限
4. **在线状态**: 使用 `isOnline()` 检查用户是否在线

## 注意事项

1. 实体类现在完全对应 `freeswitch_users` 表结构
2. 保留了原有的兼容字段，确保向后兼容
3. 所有字段都添加了适当的数据库约束
4. 更新了相关的监听器类以匹配新的字段名

## 测试建议

1. 验证实体类与数据库表的映射关系
2. 测试所有新增的业务方法
3. 验证事件发布和监听功能
4. 检查数据库约束是否生效
