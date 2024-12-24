<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-24 17:49:47
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-24 17:50:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# ip block

## 主要功能

1. 记录每个IP对特定接口的访问次数和时间
2. 如果1分钟内访问次数超过60次，自动将IP加入黑名单
3. 黑名单IP将被禁止访问24小时
4. 支持白名单IP配置，白名单IP不受限制
5. 使用拦截器在请求处理前进行检查

## 使用方式

1. 添加白名单IP：通过管理接口或直接操作数据库
2. 系统会自动检测并处理恶意IP
3. 黑名单到期后自动解除限制
