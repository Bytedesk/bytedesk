/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-21 14:23:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-23 12:19:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot.agent;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;

import com.bytedesk.ai.robot.RobotConsts;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotLlm;
import com.bytedesk.ai.robot.RobotProtobuf;
import com.bytedesk.ai.robot.RobotRestService;
import com.bytedesk.ai.springai.service.SpringAIService;
import com.bytedesk.ai.springai.service.SpringAIServiceRegistry;
import com.bytedesk.kbase.faq.FaqRequest;

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
    private final SpringAIServiceRegistry springAIServiceRegistry;

    /**
     * Base function to handle LLM interactions
     */
    private String processLlmRequest(String name, String orgUid, String query) {
        Optional<RobotEntity> robotOptional = robotRestService.findByNameAndOrgUidAndDeletedFalse(name, orgUid);
        if (robotOptional.isPresent()) {
            RobotLlm llm = robotOptional.get().getLlm();
            String provider = llm.getProvider();
            
            try {
                // Get the appropriate service from registry
                SpringAIService service = springAIServiceRegistry.getServiceByProviderName(provider);
                
                // 使用新添加的接口方法直接调用大模型并获取结果
                RobotProtobuf robot = RobotProtobuf.convertFromRobotEntity(robotOptional.get());
                return service.processDirectLlmRequest(query, robot);
               
            } catch (IllegalArgumentException e) {
                log.warn("Provider {} not found, falling back to OpenAI", provider);
            }
        }
        return "Robot not found";
    }

    // ==================== 工单相关 ====================

    /**
     * 工单生成
     */
    public String autoFillTicket(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_TICKET_GENERATE, orgUid, content);
    }

    /**
     * 工单助手
     */
    public String ticketAssistant(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_TICKET_ASSISTANT, orgUid, content);
    }

    /**
     * 工单解决方案推荐
     */
    public String ticketSolutionRecommendation(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_TICKET_SOLUTION_RECOMMENDATION, orgUid, content);
    }

    /**
     * 工单小结
     */
    public String ticketSummary(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_TICKET_SUMMARY, orgUid, content);
    }

    // ==================== 客服相关 ====================

    /**
     * 客服问答
     */
    public String customerService(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_CUSTOMER_SERVICE, orgUid, content);
    }

    /**
     * 客服助手
     */
    public String customerAssistant(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_CUSTOMER_ASSISTANT, orgUid, content);
    }

    /**
     * 客服专家
     */
    public String customerServiceExpert(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_CUSTOMER_SERVICE_EXPERT, orgUid, content);
    }

    /**
     * 售前客服
     */
    public String preSaleCustomerAssistant(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_PRE_SALE_CUSTOMER_ASSISTANT, orgUid, content);
    }

    /**
     * 售后客服
     */
    public String afterSaleCustomerAssistant(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_AFTER_SALE_CUSTOMER_ASSISTANT, orgUid, content);
    }

    /**
     * 物流客服
     */
    public String logisticsCustomerAssistant(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_LOGISTICS_CUSTOMER_ASSISTANT, orgUid, content);
    }

    // ==================== 访客相关 ====================

    /**
     * 访客画像
     */
    public String visitorPortrait(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_VISITOR_PORTRAIT, orgUid, content);
    }

    /**
     * 接客助手
     */
    public String visitorInvitation(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_VISITOR_INVITATION, orgUid, content);
    }

    /**
     * 导购助手
     */
    public String visitorRecommendation(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_VISITOR_RECOMMENDATION, orgUid, content);
    }

    // ==================== 质检相关 ====================

    /**
     * 机器人质检
     */
    public String robotInspection(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_ROBOT_INSPECTION, orgUid, content);
    }

    /**
     * 客服质检
     */
    public String agentInspection(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_AGENT_INSPECTION, orgUid, content);
    }

    // ==================== 语言处理相关 ====================

    /**
     * 语言翻译
     */
    public String languageTranslation(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_LANGUAGE_TRANSLATION, orgUid, content);
    }

    /**
     * 语言识别
     */
    public String languageRecognition(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_LANGUAGE_RECOGNITION, orgUid, content);
    }

    /**
     * 语义分析
     */
    public String semanticAnalysis(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_SEMANTIC_ANALYSIS, orgUid, content);
    }

    /**
     * 实体识别
     */
    public String entityRecognition(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_ENTITY_RECOGNITION, orgUid, content);
    }

    /**
     * 情感分析
     */
    public String sentimentAnalysis(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_SENTIMENT_ANALYSIS, orgUid, content);
    }

    /**
     * 情绪分析
     */
    public String emotionAnalysis(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_EMOTION_ANALYSIS, orgUid, content);
    }

    // ==================== 会话相关 ====================

    /**
     * 会话分类
     */
    public String threadClassification(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_THREAD_CLASSIFICATION, orgUid, content);
    }

    /**
     * 会话摘要
     */
    public String threadSummary(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_THREAD_SUMMARY, orgUid, content);
    }

    /**
     * 输入补全
     */
    public String threadCompletion(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_THREAD_COMPLETION, orgUid, content);
    }

    // ==================== 内容生成相关 ====================

    /**
     * 生成FAQ
     * 
     * @param content 用于生成FAQ的内容
     * @param orgUid 组织ID
     * @return FaqRequest对象列表
     */
    public List<FaqRequest> generateFaq(String content, String orgUid) {
        String response = processLlmRequest(RobotConsts.ROBOT_NAME_FAQ_GENERATE, orgUid, content);
        log.debug("生成FAQ原始返回: {}", response);
        
        // 调用新的转换工具类将JSON字符串转换为FaqRequest列表
        return RobotFaqConverter.convertToFaqRequests(response);
    }

    /**
     * 生成公众号文章
     */
    public String generateWechatArticle(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_GENERATE_WECHAT_ARTICLE, orgUid, content);
    }

    /**
     * 生成小红书文章
     */
    public String generateXiaohongshuArticle(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_GENERATE_XIAOHONGSHU_ARTICLE, orgUid, content);
    }

    // ==================== 文本处理相关 ====================

    /**
     * 中文分词
     */
    public String chineseWordSegmentation(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_CHINESE_WORD_SEGMENTATION, orgUid, content);
    }

    /**
     * 词性标注
     */
    public String partOfSpeechTagging(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_PART_OF_SPEECH_TAGGING, orgUid, content);
    }

    /**
     * 依存句法分析
     */
    public String dependencyParsing(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_DEPENDENCY_PARSING, orgUid, content);
    }

    /**
     * 成分句法分析
     */
    public String constituencyParsing(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_CONSTITUENCY_PARSING, orgUid, content);
    }

    /**
     * 语义依存分析
     */
    public String semanticDependencyAnalysis(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_SEMANTIC_DEPENDENCY_ANALYSIS, orgUid, content);
    }

    /**
     * 语义角色标注
     */
    public String semanticRoleLabeling(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_SEMANTIC_ROLE_LABELING, orgUid, content);
    }

    /**
     * 抽象意义表示
     */
    public String abstractMeaningRepresentation(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_ABSTRACT_MEANING_REPRESENTATION, orgUid, content);
    }

    /**
     * 指代消解
     */
    public String coreferenceResolution(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_COREFERENCE_RESOLUTION, orgUid, content);
    }

    /**
     * 语义文本相似度
     */
    public String semanticTextSimilarity(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_SEMANTIC_TEXT_SIMILARITY, orgUid, content);
    }

    /**
     * 文本风格转换
     */
    public String textStyleTransfer(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_TEXT_STYLE_TRANSFER, orgUid, content);
    }

    /**
     * 关键词短语提取
     */
    public String keywordExtraction(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_KEYWORD_EXTRACTION, orgUid, content);
    }

    /**
     * 文本纠错
     */
    public String textCorrection(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_TEXT_CORRECTION, orgUid, content);
    }

    /**
     * 文本分类
     */
    public String textClassification(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_TEXT_CLASSIFICATION, orgUid, content);
    }

    /**
     * FAQ相似问题生成
     * 将大模型返回的相似问题数组添加到FaqRequest对象的similarQuestions字段中
     */
    public FaqRequest faqSimilarQuestions(String content, String orgUid) {
        String response = processLlmRequest(RobotConsts.ROBOT_NAME_FAQ_SIMILAR_QUESTIONS, orgUid, content);
        log.debug("FAQ相似问题生成原始返回: {}", response);
        
        // 创建FaqRequest对象
        FaqRequest faqRequest = new FaqRequest();
        faqRequest.setQuestion(content);
        
        // 解析JSON数组
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<String> similarQuestions = objectMapper.readValue(response, new TypeReference<List<String>>() {});
            faqRequest.setSimilarQuestions(similarQuestions);
        } catch (JsonProcessingException e) {
            log.error("解析相似问题JSON失败: {}", e.getMessage());
            // 创建一个空列表作为默认值
            faqRequest.setSimilarQuestions(new ArrayList<>());
        }
        
        return faqRequest;
    }

    // ==================== 其他功能 ====================

    /**
     * 空白智能体
     */
    public String voidAgent(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_VOID_AGENT, orgUid, content);
    }

    /**
     * 问题扩写
     */
    public String queryExpansion(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_QUERY_EXPANSION, orgUid, content);
    }

    /**
     * 意图改写
     */
    public String intentRewrite(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_INTENT_REWRITE, orgUid, content);
    }

    /**
     * 意图识别
     */
    public String intentClassification(String content, String orgUid) {
        return processLlmRequest(RobotConsts.ROBOT_NAME_INTENT_CLASSIFICATION, orgUid, content);
    }
}
