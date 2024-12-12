<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-09 10:49:09
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-09 11:33:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# ByteDesk 质检模块

## 功能说明

质检模块用于对客服会话进行质量检查和评分,主要包含以下功能:

### 1. 人工质检

- 质检员可以对会话进行评分和评价
- 支持多维度评分(响应速度、服务态度、解决方案、服务规范)
- 可添加质检评语和建议

### 2. 自动质检

- 根据预设规则自动评分
- 支持响应时间检查
- 支持关键词检查(必用词/禁用词)
- 定时批量执行自动质检

### 3. 质检规则

- 可配置的质检规则
- 支持分类规则
- 支持评分规则
- 支持启用/禁用规则

### 4. 质检统计

- 客服质检得分统计
- 多维度评分分布
- 低分会话统计
- 质检趋势分析

## 配置说明

在application.yml中配置:

```yaml
bytedesk:
  quality:
    # 自动质检配置
    auto-inspection-enabled: true
    auto-inspection-delay: 24
    auto-inspection-batch-size: 100
    
    # 评分配置  
    min-score: 0
    max-score: 100
    pass-score: 60
    
    # 响应时间配置(秒)
    fast-response-time: 30
    normal-response-time: 120
    slow-response-time: 300
    
    # 解决时间配置(分钟)
    fast-solution-time: 5
    normal-solution-time: 30
    slow-solution-time: 120
```

## API说明

### 质检管理

- POST /api/v1/quality/inspections - 创建质检
- PUT /api/v1/quality/inspections/{id} - 更新质检
- DELETE /api/v1/quality/inspections/{id} - 删除质检
- GET /api/v1/quality/inspections - 获取质检列表

### 规则管理

- POST /api/v1/quality/rules - 创建规则
- PUT /api/v1/quality/rules/{id} - 更新规则
- DELETE /api/v1/quality/rules/{id} - 删除规则
- GET /api/v1/quality/rules - 获取规则列表

### 统计分析

- GET /api/v1/quality/stats/agent/{id}/score - 获取客服评分
- GET /api/v1/quality/stats/agent/{id}/distribution - 获取评分分布

```
