/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-23 12:16:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-23 12:17:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot.agent;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/robot/agent")
@RequiredArgsConstructor
public class RobotAgentController {
    
    private final RobotAgentService robotAgentService;

    // ==================== 工单相关 ====================
    
    @PostMapping("/ticket/auto-fill")
    public String autoFillTicket(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.autoFillTicket(content, orgUid);
    }

    @PostMapping("/ticket/assistant")
    public String ticketAssistant(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.ticketAssistant(content, orgUid);
    }

    @PostMapping("/ticket/solution-recommendation")
    public String ticketSolutionRecommendation(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.ticketSolutionRecommendation(content, orgUid);
    }

    @PostMapping("/ticket/summary")
    public String ticketSummary(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.ticketSummary(content, orgUid);
    }

    // ==================== 客服相关 ====================

    @PostMapping("/customer/service")
    public String customerService(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.customerService(content, orgUid);
    }

    @PostMapping("/customer/assistant")
    public String customerAssistant(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.customerAssistant(content, orgUid);
    }

    @PostMapping("/customer/expert")
    public String customerServiceExpert(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.customerServiceExpert(content, orgUid);
    }

    @PostMapping("/customer/pre-sale")
    public String preSaleCustomerAssistant(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.preSaleCustomerAssistant(content, orgUid);
    }

    @PostMapping("/customer/after-sale")
    public String afterSaleCustomerAssistant(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.afterSaleCustomerAssistant(content, orgUid);
    }

    @PostMapping("/customer/logistics")
    public String logisticsCustomerAssistant(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.logisticsCustomerAssistant(content, orgUid);
    }

    // ==================== 访客相关 ====================

    @PostMapping("/visitor/portrait")
    public String visitorPortrait(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.visitorPortrait(content, orgUid);
    }

    @PostMapping("/visitor/invitation")
    public String visitorInvitation(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.visitorInvitation(content, orgUid);
    }

    @PostMapping("/visitor/recommendation")
    public String visitorRecommendation(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.visitorRecommendation(content, orgUid);
    }

    // ==================== 质检相关 ====================

    @PostMapping("/inspection/robot")
    public String robotInspection(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.robotInspection(content, orgUid);
    }

    @PostMapping("/inspection/agent")
    public String agentInspection(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.agentInspection(content, orgUid);
    }

    // ==================== 语言处理相关 ====================

    @PostMapping("/language/translation")
    public String languageTranslation(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.languageTranslation(content, orgUid);
    }

    @PostMapping("/language/recognition")
    public String languageRecognition(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.languageRecognition(content, orgUid);
    }

    @PostMapping("/language/semantic-analysis")
    public String semanticAnalysis(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.semanticAnalysis(content, orgUid);
    }

    @PostMapping("/language/entity-recognition")
    public String entityRecognition(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.entityRecognition(content, orgUid);
    }

    @PostMapping("/language/sentiment-analysis")
    public String sentimentAnalysis(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.sentimentAnalysis(content, orgUid);
    }

    @PostMapping("/language/emotion-analysis")
    public String emotionAnalysis(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.emotionAnalysis(content, orgUid);
    }

    // ==================== 会话相关 ====================

    @PostMapping("/thread/classification")
    public String threadClassification(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.threadClassification(content, orgUid);
    }

    @PostMapping("/thread/summary")
    public String threadSummary(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.threadSummary(content, orgUid);
    }

    @PostMapping("/thread/completion")
    public String threadCompletion(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.threadCompletion(content, orgUid);
    }

    // ==================== 内容生成相关 ====================

    @PostMapping("/content/generate-faq")
    public String generateFaq(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.generateFaq(content, orgUid);
    }

    @PostMapping("/content/generate-wechat-article")
    public String generateWechatArticle(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.generateWechatArticle(content, orgUid);
    }

    @PostMapping("/content/generate-xiaohongshu-article")
    public String generateXiaohongshuArticle(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.generateXiaohongshuArticle(content, orgUid);
    }

    // ==================== 文本处理相关 ====================

    @PostMapping("/text/chinese-word-segmentation")
    public String chineseWordSegmentation(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.chineseWordSegmentation(content, orgUid);
    }

    @PostMapping("/text/part-of-speech-tagging")
    public String partOfSpeechTagging(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.partOfSpeechTagging(content, orgUid);
    }

    @PostMapping("/text/dependency-parsing")
    public String dependencyParsing(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.dependencyParsing(content, orgUid);
    }

    @PostMapping("/text/constituency-parsing")
    public String constituencyParsing(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.constituencyParsing(content, orgUid);
    }

    @PostMapping("/text/semantic-dependency-analysis")
    public String semanticDependencyAnalysis(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.semanticDependencyAnalysis(content, orgUid);
    }

    @PostMapping("/text/semantic-role-labeling")
    public String semanticRoleLabeling(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.semanticRoleLabeling(content, orgUid);
    }

    @PostMapping("/text/abstract-meaning-representation")
    public String abstractMeaningRepresentation(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.abstractMeaningRepresentation(content, orgUid);
    }

    @PostMapping("/text/coreference-resolution")
    public String coreferenceResolution(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.coreferenceResolution(content, orgUid);
    }

    @PostMapping("/text/semantic-text-similarity")
    public String semanticTextSimilarity(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.semanticTextSimilarity(content, orgUid);
    }

    @PostMapping("/text/style-transfer")
    public String textStyleTransfer(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.textStyleTransfer(content, orgUid);
    }

    @PostMapping("/text/keyword-extraction")
    public String keywordExtraction(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.keywordExtraction(content, orgUid);
    }

    @PostMapping("/text/correction")
    public String textCorrection(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.textCorrection(content, orgUid);
    }

    @PostMapping("/text/classification")
    public String textClassification(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.textClassification(content, orgUid);
    }

    @PostMapping("/text/faq-similar-questions")
    public String faqSimilarQuestions(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.faqSimilarQuestions(content, orgUid);
    }

    // ==================== 其他功能 ====================

    @PostMapping("/void-agent")
    public String voidAgent(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.voidAgent(content, orgUid);
    }

    @PostMapping("/query-expansion")
    public String queryExpansion(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.queryExpansion(content, orgUid);
    }

    @PostMapping("/intent/rewrite")
    public String intentRewrite(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.intentRewrite(content, orgUid);
    }

    @PostMapping("/intent/classification")
    public String intentClassification(@RequestParam String content, @RequestParam String orgUid) {
        return robotAgentService.intentClassification(content, orgUid);
    }
}
