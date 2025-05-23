/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-21 14:23:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-23 08:46:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot;

import java.util.Optional;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 智能工单填写、智能小结、智能质检
 * @Author: jackning
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RobotAgentService {

    private final RobotRestService robotRestService;
    
    @Autowired
    private ChatClient chatClient;

    /**
     * Base function to handle LLM interactions
     */
    private String processLlmRequest(String name, String orgUid, String query) {
        Optional<RobotEntity> robotOptional = robotRestService.findByNameAndOrgUidAndDeletedFalse(name, orgUid);
        if (robotOptional.isPresent()) {
            RobotLlm llm = robotOptional.get().getLlm();
            String prompt = llm.getPrompt();
            String model = llm.getModel();
            Double temperature = llm.getTemperature();
            Double topP = llm.getTopP();

            // Create chat options
            OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model(model)
                .temperature(temperature)
                .topP(topP)
                .build();

            // Get response using prompt() method with options
            return chatClient.prompt()
                .system(prompt)
                .user(query)
                .options(options)
                .call()
                .content();
        }
        return "Robot not found";
    }

    // ==================== 工单相关 ====================

    /**
     * 工单生成
     */
    public String autoFillTicket(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_TICKET_GENERATE, request.getOrgUid(), request.getContent());
    }

    /**
     * 工单助手
     */
    public String ticketAssistant(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_TICKET_ASSISTANT, request.getOrgUid(), request.getContent());
    }

    /**
     * 工单解决方案推荐
     */
    public String ticketSolutionRecommendation(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_TICKET_SOLUTION_RECOMMENDATION, request.getOrgUid(), request.getContent());
    }

    /**
     * 工单小结
     */
    public String ticketSummary(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_TICKET_SUMMARY, request.getOrgUid(), request.getContent());
    }

    // ==================== 客服相关 ====================

    /**
     * 客服问答
     */
    public String customerService(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_CUSTOMER_SERVICE, request.getOrgUid(), request.getContent());
    }

    /**
     * 客服助手
     */
    public String customerAssistant(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_CUSTOMER_ASSISTANT, request.getOrgUid(), request.getContent());
    }

    /**
     * 客服专家
     */
    public String customerServiceExpert(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_CUSTOMER_SERVICE_EXPERT, request.getOrgUid(), request.getContent());
    }

    /**
     * 售前客服
     */
    public String preSaleCustomerAssistant(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_PRE_SALE_CUSTOMER_ASSISTANT, request.getOrgUid(), request.getContent());
    }

    /**
     * 售后客服
     */
    public String afterSaleCustomerAssistant(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_AFTER_SALE_CUSTOMER_ASSISTANT, request.getOrgUid(), request.getContent());
    }

    /**
     * 物流客服
     */
    public String logisticsCustomerAssistant(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_LOGISTICS_CUSTOMER_ASSISTANT, request.getOrgUid(), request.getContent());
    }

    // ==================== 访客相关 ====================

    /**
     * 访客画像
     */
    public String visitorPortrait(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_VISITOR_PORTRAIT, request.getOrgUid(), request.getContent());
    }

    /**
     * 接客助手
     */
    public String visitorInvitation(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_VISITOR_INVITATION, request.getOrgUid(), request.getContent());
    }

    /**
     * 导购助手
     */
    public String visitorRecommendation(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_VISITOR_RECOMMENDATION, request.getOrgUid(), request.getContent());
    }

    // ==================== 质检相关 ====================

    /**
     * 机器人质检
     */
    public String robotInspection(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_ROBOT_INSPECTION, request.getOrgUid(), request.getContent());
    }

    /**
     * 客服质检
     */
    public String agentInspection(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_AGENT_INSPECTION, request.getOrgUid(), request.getContent());
    }

    // ==================== 语言处理相关 ====================

    /**
     * 语言翻译
     */
    public String languageTranslation(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_LANGUAGE_TRANSLATION, request.getOrgUid(), request.getContent());
    }

    /**
     * 语言识别
     */
    public String languageRecognition(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_LANGUAGE_RECOGNITION, request.getOrgUid(), request.getContent());
    }

    /**
     * 语义分析
     */
    public String semanticAnalysis(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_SEMANTIC_ANALYSIS, request.getOrgUid(), request.getContent());
    }

    /**
     * 实体识别
     */
    public String entityRecognition(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_ENTITY_RECOGNITION, request.getOrgUid(), request.getContent());
    }

    /**
     * 情感分析
     */
    public String sentimentAnalysis(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_SENTIMENT_ANALYSIS, request.getOrgUid(), request.getContent());
    }

    /**
     * 情绪分析
     */
    public String emotionAnalysis(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_EMOTION_ANALYSIS, request.getOrgUid(), request.getContent());
    }

    // ==================== 会话相关 ====================

    /**
     * 会话分类
     */
    public String threadClassification(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_THREAD_CLASSIFICATION, request.getOrgUid(), request.getContent());
    }

    /**
     * 会话摘要
     */
    public String threadSummary(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_THREAD_SUMMARY, request.getOrgUid(), request.getContent());
    }

    /**
     * 输入补全
     */
    public String threadCompletion(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_THREAD_COMPLETION, request.getOrgUid(), request.getContent());
    }

    // ==================== 内容生成相关 ====================

    /**
     * 生成FAQ
     */
    public String generateFaq(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_GENERATE_FAQ, request.getOrgUid(), request.getContent());
    }

    /**
     * 生成公众号文章
     */
    public String generateWechatArticle(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_GENERATE_WECHAT_ARTICLE, request.getOrgUid(), request.getContent());
    }

    /**
     * 生成小红书文章
     */
    public String generateXiaohongshuArticle(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_GENERATE_XIAOHONGSHU_ARTICLE, request.getOrgUid(), request.getContent());
    }

    // ==================== 文本处理相关 ====================

    /**
     * 中文分词
     */
    public String chineseWordSegmentation(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_CHINESE_WORD_SEGMENTATION, request.getOrgUid(), request.getContent());
    }

    /**
     * 词性标注
     */
    public String partOfSpeechTagging(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_PART_OF_SPEECH_TAGGING, request.getOrgUid(), request.getContent());
    }

    /**
     * 依存句法分析
     */
    public String dependencyParsing(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_DEPENDENCY_PARSING, request.getOrgUid(), request.getContent());
    }

    /**
     * 成分句法分析
     */
    public String constituencyParsing(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_CONSTITUENCY_PARSING, request.getOrgUid(), request.getContent());
    }

    /**
     * 语义依存分析
     */
    public String semanticDependencyAnalysis(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_SEMANTIC_DEPENDENCY_ANALYSIS, request.getOrgUid(), request.getContent());
    }

    /**
     * 语义角色标注
     */
    public String semanticRoleLabeling(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_SEMANTIC_ROLE_LABELING, request.getOrgUid(), request.getContent());
    }

    /**
     * 抽象意义表示
     */
    public String abstractMeaningRepresentation(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_ABSTRACT_MEANING_REPRESENTATION, request.getOrgUid(), request.getContent());
    }

    /**
     * 指代消解
     */
    public String coreferenceResolution(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_COREFERENCE_RESOLUTION, request.getOrgUid(), request.getContent());
    }

    /**
     * 语义文本相似度
     */
    public String semanticTextSimilarity(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_SEMANTIC_TEXT_SIMILARITY, request.getOrgUid(), request.getContent());
    }

    /**
     * 文本风格转换
     */
    public String textStyleTransfer(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_TEXT_STYLE_TRANSFER, request.getOrgUid(), request.getContent());
    }

    /**
     * 关键词短语提取
     */
    public String keywordExtraction(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_KEYWORD_EXTRACTION, request.getOrgUid(), request.getContent());
    }

    /**
     * 文本纠错
     */
    public String textCorrection(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_TEXT_CORRECTION, request.getOrgUid(), request.getContent());
    }

    /**
     * 文本分类
     */
    public String textClassification(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_TEXT_CLASSIFICATION, request.getOrgUid(), request.getContent());
    }

    /**
     * FAQ相似问题生成
     */
    public String faqSimilarQuestions(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_FAQ_SIMILAR_QUESTIONS, request.getOrgUid(), request.getContent());
    }

    // ==================== 其他功能 ====================

    /**
     * 空白智能体
     */
    public String voidAgent(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_VOID_AGENT, request.getOrgUid(), request.getContent());
    }

    /**
     * 问题扩写
     */
    public String queryExpansion(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_QUERY_EXPANSION, request.getOrgUid(), request.getContent());
    }

    /**
     * 意图改写
     */
    public String intentRewrite(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_INTENT_REWRITE, request.getOrgUid(), request.getContent());
    }

    /**
     * 意图识别
     */
    public String intentClassification(RobotRequest request) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_INTENT_CLASSIFICATION, request.getOrgUid(), request.getContent());
    }
}
