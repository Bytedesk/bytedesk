/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-13 17:11:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-06 16:12:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.redis.RedisConsts;

public class RobotConsts {
    private RobotConsts() {}

    // 定义 Redis 队列的 key
    public static final String ROBOT_FAQ_QUEUE_KEY = RedisConsts.BYTEDESK_REDIS_PREFIX+ "robot:faq:queue";
    // airline key
    public static final String ROBOT_INIT_DEMO_AIRLINE_KEY = RedisConsts.BYTEDESK_REDIS_PREFIX + "robot:init:demo:airline";
    // bytedesk key
    public static final String ROBOT_INIT_DEMO_BYTEDESK_KEY = RedisConsts.BYTEDESK_REDIS_PREFIX + "robot:init:demo:bytedesk";
    // shopping key
    public static final String ROBOT_INIT_DEMO_SHOPPING_KEY = RedisConsts.BYTEDESK_REDIS_PREFIX + "robot:init:demo:shopping";
    // 
    public static final String CATEGORY_JOB = I18Consts.I18N_PREFIX + "JOB";
    public static final String CATEGORY_LANGUAGE = I18Consts.I18N_PREFIX + "LANGUAGE";
    public static final String CATEGORY_TOOL = I18Consts.I18N_PREFIX + "TOOL";
    public static final String CATEGORY_WRITING = I18Consts.I18N_PREFIX + "WRITING";

    // robot name
    public static final String ROBOT_NAME_VOID_AGENT = "void_agent";
    public static final String ROBOT_NAME_CUSTOMER_SERVICE = "customer_service";
    public static final String ROBOT_NAME_QUERY_EXPANSION = "query_expansion";
    public static final String ROBOT_NAME_INTENT_REWRITE = "intent_rewrite";
    public static final String ROBOT_NAME_INTENT_CLASSIFICATION = "intent_classification";
    public static final String ROBOT_NAME_EMOTION_ANALYSIS = "emotion_analysis";
    public static final String ROBOT_NAME_ROBOT_INSPECTION = "robot_inspection";
    public static final String ROBOT_NAME_AGENT_INSPECTION = "agent_inspection";
    public static final String ROBOT_NAME_SESSION_SUMMARY = "session_summary";
    public static final String ROBOT_NAME_TICKET_ASSISTANT = "ticket_assistant";
    public static final String ROBOT_NAME_TICKET_SOLUTION_RECOMMENDATION = "ticket_solution_recommendation";
    public static final String ROBOT_NAME_TICKET_SUMMARY = "ticket_summary";
    public static final String ROBOT_NAME_VISITOR_PORTRAIT = "visitor_portrait";
    public static final String ROBOT_NAME_VISITOR_INVITATION = "visitor_invitation";
    public static final String ROBOT_NAME_VISITOR_RECOMMENDATION = "visitor_recommendation";
    public static final String ROBOT_NAME_CUSTOMER_ASSISTANT = "customer_assistant";
    public static final String ROBOT_NAME_PRE_SALE_CUSTOMER_ASSISTANT = "pre_sale_customer_assistant";
    public static final String ROBOT_NAME_AFTER_SALE_CUSTOMER_ASSISTANT = "after_sale_customer_assistant";
    public static final String ROBOT_NAME_LOGISTICS_CUSTOMER_ASSISTANT = "logistics_customer_assistant";
    public static final String ROBOT_NAME_LANGUAGE_TRANSLATION = "language_translation";
    public static final String ROBOT_NAME_LANGUAGE_RECOGNITION = "language_recognition";
    public static final String ROBOT_NAME_SEMANTIC_ANALYSIS = "semantic_analysis";
    public static final String ROBOT_NAME_ENTITY_RECOGNITION = "entity_recognition";
    public static final String ROBOT_NAME_SENTIMENT_ANALYSIS = "sentiment_analysis";
    public static final String ROBOT_NAME_SESSION_SUMMARY_ROBOT = "session_summary";
    public static final String ROBOT_NAME_SESSION_CLASSIFICATION = "session_classification";
    public static final String ROBOT_NAME_GENERATE_TICKET = "generate_ticket";
    public static final String ROBOT_NAME_CUSTOMER_SERVICE_EXPERT = "customer_service_expert";
    public static final String ROBOT_NAME_GENERATE_FAQ = "generate_faq";

    // 
    public static final String ROBOT_LLM_DEFAULT_PROMPT = "角色：资深客服专家; 背景：有专业客服经验，对教育、电商、金融领域有深刻理解; 任务：根据上下文中提到的内容，对提出的问题给出有用、详细、礼貌的回答; 要求：1. 解决客户提出的问题，2. 安抚客户情绪，3. 提升客户满意度";

    public static final String PROMPT_LLM_GENERATE_FAQ_TEMPLATE = """
            基于以下给定的文本，生成一组高质量的问答对。请遵循以下指南:

            1. 问题部分：
            - 为同一个主题创建多个不同表述的问题，确保问题的多样性
            - 每个问题应考虑用户可能的多种问法，例如：
              - 直接询问（如"什么是...？"）
              - 请求确认（如"是否可以说...？"）
              - 如何做（如"如何实现...？"）
              - 为什么（如"为什么需要...？"）
              - 比较类（如"...和...有什么区别？"）

            2. 答案部分：
            - 答案应该准确、完整且易于理解
            - 使用简洁清晰的语言
            - 适当添加示例说明
            - 可以包含关键步骤或要点
            - 必要时提供相关概念解释

            3. 格式要求：
            - 以JSON数组形式输出
            - 每个问答对包含question和answer字段
            - 可选添加type字段标识问题类型
            - 可选添加tags字段标识相关标签

            4. 质量控制：
            - 确保问答对之间不重复
            - 问题应该有实际意义和价值
            - 答案需要准确且有帮助
            - 覆盖文本中的重要信息点

            给定文本：
            {chunk}

            请基于这个文本生成问答对
            """;

    public static final String PROMPT_BLUEPRINT = """
                根据提供的文档信息回答问题，文档信息如下:
                {context}
                问题:
                {query}
                当用户提出的问题无法根据文档内容进行回复或者你也不清楚时，回复:未查找到相关问题答案.
                """;

    public static final String PROMPT_TEMPLATE = """
              任务描述：根据用户的查询和文档信息回答问题，并结合历史聊天记录生成简要的回答。

              用户查询: {query}

              历史聊天记录: {history}

              搜索结果: {context}

              请根据以上信息生成一个简单明了的回答，确保信息准确且易于理解。
              当用户提出的问题无法根据文档内容进行回复或者你也不清楚时，回复:未查找到相关问题答案.
              另外，请提供更多相关的问答对。
              回答内容请以JSON格式输出，格式如下：
              {
                "answer": "回答内容",
                "additional_qa_pairs": [
                    {"question": "相关问题1", "answer": "相关答案1"},
                    {"question": "相关问题2", "answer": "相关答案2"}
                ]
              }
            """;
    
}
