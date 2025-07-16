/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-23 12:16:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-23 16:55:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot.agent;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.kbase.faq.FaqRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/robot/agent")
@RequiredArgsConstructor
public class RobotAgentController {
    
    private final RobotAgentService robotAgentService;

    // ==================== 工单相关 ====================
    
    @PostMapping("/ticket/auto-fill")
    public ResponseEntity<?> autoFillTicket(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.autoFillTicket(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/ticket/assistant")
    public ResponseEntity<?> ticketAssistant(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.ticketAssistant(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/ticket/solution-recommendation")
    public ResponseEntity<?> ticketSolutionRecommendation(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.ticketSolutionRecommendation(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/ticket/summary")
    public ResponseEntity<?> ticketSummary(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.ticketSummary(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    // ==================== 客服相关 ====================

    @PostMapping("/customer/service")
    public ResponseEntity<?> customerService(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.customerService(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/customer/assistant")
    public ResponseEntity<?> customerAssistant(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.customerAssistant(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/customer/expert")
    public ResponseEntity<?> customerServiceExpert(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.customerServiceExpert(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/customer/pre-sale")
    public ResponseEntity<?> preSaleCustomerAssistant(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.preSaleCustomerAssistant(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/customer/after-sale")
    public ResponseEntity<?> afterSaleCustomerAssistant(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.afterSaleCustomerAssistant(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/customer/logistics")
    public ResponseEntity<?> logisticsCustomerAssistant(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.logisticsCustomerAssistant(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    // ==================== 访客相关 ====================

    @PostMapping("/visitor/portrait")
    public ResponseEntity<?> visitorPortrait(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.visitorPortrait(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/visitor/invitation")
    public ResponseEntity<?> visitorInvitation(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.visitorInvitation(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/visitor/recommendation")
    public ResponseEntity<?> visitorRecommendation(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.visitorRecommendation(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    // ==================== 质检相关 ====================

    @PostMapping("/inspection/robot")
    public ResponseEntity<?> robotInspection(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.robotInspection(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/inspection/agent")
    public ResponseEntity<?> agentInspection(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.agentInspection(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    // ==================== 语言处理相关 ====================

    @PostMapping("/language/translation")
    public ResponseEntity<?> languageTranslation(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.languageTranslation(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/language/recognition")
    public ResponseEntity<?> languageRecognition(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.languageRecognition(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/language/semantic-analysis")
    public ResponseEntity<?> semanticAnalysis(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.semanticAnalysis(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/language/entity-recognition")
    public ResponseEntity<?> entityRecognition(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.entityRecognition(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/language/sentiment-analysis")
    public ResponseEntity<?> sentimentAnalysis(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.sentimentAnalysis(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/language/emotion-analysis")
    public ResponseEntity<?> emotionAnalysis(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.emotionAnalysis(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    // ==================== 会话相关 ====================

    @PostMapping("/thread/classification")
    public ResponseEntity<?> threadClassification(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.threadClassification(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/thread/summary")
    public ResponseEntity<?> threadSummary(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.threadSummary(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/thread/completion")
    public ResponseEntity<?> threadCompletion(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.threadCompletion(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    // ==================== 内容生成相关 ====================

    /**
     * 生成FAQ
     * 
     * @param request 生成请求体
     * @return FaqRequest对象列表
     */
    @PostMapping("/content/generate-faq")
    public ResponseEntity<?> generateFaq(@RequestBody RobotAgentRequest request) {
        List<FaqRequest> faqRequests = robotAgentService.generateFaq(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", faqRequests));
    }

    @PostMapping("/content/generate-wechat-article")
    public ResponseEntity<?> generateWechatArticle(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.generateWechatArticle(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/content/generate-xiaohongshu-article")
    public ResponseEntity<?> generateXiaohongshuArticle(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.generateXiaohongshuArticle(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    // ==================== 文本处理相关 ====================

    @PostMapping("/text/chinese-word-segmentation")
    public ResponseEntity<?> chineseWordSegmentation(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.chineseWordSegmentation(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/text/part-of-voice-tagging")
    public ResponseEntity<?> partOfSpeechTagging(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.partOfSpeechTagging(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/text/dependency-parsing")
    public ResponseEntity<?> dependencyParsing(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.dependencyParsing(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/text/constituency-parsing")
    public ResponseEntity<?> constituencyParsing(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.constituencyParsing(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/text/semantic-dependency-analysis")
    public ResponseEntity<?> semanticDependencyAnalysis(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.semanticDependencyAnalysis(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/text/semantic-role-labeling")
    public ResponseEntity<?> semanticRoleLabeling(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.semanticRoleLabeling(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/text/abstract-meaning-representation")
    public ResponseEntity<?> abstractMeaningRepresentation(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.abstractMeaningRepresentation(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/text/coreference-resolution")
    public ResponseEntity<?> coreferenceResolution(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.coreferenceResolution(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/text/semantic-text-similarity")
    public ResponseEntity<?> semanticTextSimilarity(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.semanticTextSimilarity(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/text/style-transfer")
    public ResponseEntity<?> textStyleTransfer(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.textStyleTransfer(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/text/keyword-extraction")
    public ResponseEntity<?> keywordExtraction(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.keywordExtraction(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/text/correction")
    public ResponseEntity<?> textCorrection(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.textCorrection(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/text/classification")
    public ResponseEntity<?> textClassification(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.textClassification(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/text/faq-similar-questions")
    public ResponseEntity<?> faqSimilarQuestions(@RequestBody RobotAgentRequest request) {
        FaqRequest faqRequest = robotAgentService.faqSimilarQuestions(request.getQuestion(), request.getOrgUid());
        log.info("faqSimilarQuestions: {}", faqRequest.getSimilarQuestions());
        return ResponseEntity.ok(JsonResult.success("success", faqRequest));
    }

    // ==================== 其他功能 ====================

    @PostMapping("/void-agent")
    public ResponseEntity<?> voidAgent(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.voidAgent(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/query-expansion")
    public ResponseEntity<?> queryExpansion(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.queryExpansion(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/intent/rewrite")
    public ResponseEntity<?> intentRewrite(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.intentRewrite(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }

    @PostMapping("/intent/classification")
    public ResponseEntity<?> intentClassification(@RequestBody RobotAgentRequest request) {
        String response = robotAgentService.intentClassification(request.getContent(), request.getOrgUid());
        return ResponseEntity.ok(JsonResult.success("success", response));
    }
}
