/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-13 17:11:14
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-26 17:42:40
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
    
}
