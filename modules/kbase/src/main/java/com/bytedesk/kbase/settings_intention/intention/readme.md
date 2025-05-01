<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-29 16:03:25
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-29 16:03:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
-->
# 意图识别

## 完善的意图分类系统功能

扩展了 IntentionSettingsEntity 类：

添加了意图信心度阈值设置
支持多级子意图分类结构
增加针对不同意图的预设回复功能
添加未匹配意图的处理方式选项
增加意图标签属性和优先级配置
添加意图跟踪和意图转换阈值设置
创建了完整的意图检测服务体系：

定义了 IntentionDetectionService 接口，提供全面的意图识别能力
实现了 IntentionDetectionServiceImpl 类，包含意图检测、历史记录和推荐回复等功能
添加了关键数据模型：

IntentionResult：存储意图检测结果、置信度和推荐回复
IntentionCategory：定义意图分类层次结构和相关属性
IntentionTrainingData：支持意图模型训练和优化
IntentionTransition：记录会话中意图变化的历史

## 基于业内最佳实践的核心改进

上下文感知意图识别：系统会考虑历史消息作为上下文，不仅仅基于单条消息判断意图

多级意图分类：支持主意图和子意图的层次结构，实现更精细的意图分类

意图置信度阈值：引入置信度机制，当意图识别不确定时主动澄清

意图实体提取：除了识别意图外，还可提取相关实体（如订单号、产品名称）

意图转换跟踪：完整记录会话中意图的变化过程，支持分析用户需求演变

预设回复模板：根据识别的意图提供针对性的回复建议

意图未匹配处理：当无法确定意图时，提供多种处理策略（转人工、默认回复、请求澄清）

意图优先级：不同意图可设置不同优先级，处理意图冲突情况

意图标签系统：通过标签对意图进行分组和分类，便于管理

可训练的意图模型：支持通过真实对话数据不断优化意图识别能力

## 实现亮点

可扩展性强：系统设计支持未来接入不同的NLU模型或大语言模型
完整的历史记录：记录意图变化的完整历史，便于分析优化
面向业务设计：系统考虑了客服场景的实际需求，包括转人工、澄清问题等机制
灵活的配置：通过配置可调整各种阈值和行为，无需修改代码
