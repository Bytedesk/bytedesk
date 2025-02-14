<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 19:09:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-14 12:47:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
-->
# ai module

## 机器人

### 机器人分类

- 客服问答：根据提供的文档信息回答问题，文档信息如下: {context} 问题: {query} 当用户提出的问题无法根据文档内容进行回复或者你也不清楚时，回复:未查找到相关问题答案. 另外，请提供更多相关的问答对。
- 问题扩写：根据用户的问题，生成多个不同表述的问题，确保问题的多样性。
- 意图改写：根据用户的问题，生成多个不同表述的问题，确保问题的多样性。
- 意图识别：根据用户的问题，识别用户的意图，并返回意图的类型。
- 情绪分析：根据用户的问题，识别用户的情绪，并返回情绪的类型。
- 机器人质检：对机器人回答的问题进行评测，并给出评测结果。
- 客服质检：对客服的回答进行质检，并给出质检结果。
- 会话小结：对会话进行总结，并给出总结结果。
- 工单助手：从对话记录中提取工单信息，自动填写工单表单。可用于访客端和客服端。
- 工单解决方案推荐：访客填写完工单标题和描述后，自动搜索解决方案和FAQ，通过大模型给出解决方案，同时适用于访客端和客服端。
- 工单小结：对工单进行总结，并给出总结结果。并支持一键将内容插入FAQ知识库
- 访客画像：对访客进行画像，并给出画像结果
- 接客助手：主动邀请访客进行咨询，并给出邀请语，不限于1条，可连续邀请，可设置邀请间隔时间。
- 导购助手：针对电商网站，根据访客的浏览行为，给出推荐商品，并给出推荐理由。
- 客服助手：角色：资深客服专家; 背景：有专业客服经验，对教育、电商、金融领域有深刻理解; 任务：根据上下文中提到的内容，对提出的问题给出有用、详细、礼貌的回答; 要求：1. 解决客户提出的问题，2. 安抚客户情绪，3. 提升客户满意度"
- 售前客服：针对售前咨询的客户，给出专业的解答，并给出推荐商品，并给出推荐理由。
- 售后客服：针对售后咨询的客户，给出专业的解答，并给出解决方案，并给出解决方案的理由。
- 物流客服：针对物流咨询的客户，给出专业的解答，并给出解决方案，并给出解决方案的理由。

## 参考

- [ticket](https://mp.weixin.qq.com/s/MNZR2tkVANfQKWqyAKlSPQ)
