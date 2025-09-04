/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-24 11:10:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-04 09:31:33
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.swagger;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger 分组和国际化支持配置
 * http://127.0.0.1:9003/swagger-ui/index.html
 */
@Configuration
public class SwaggerApiConfig {
    
    /**
     * Api分组接口 - 所有接口
     */
    @Bean
    public GroupedOpenApi allApis() {
        return GroupedOpenApi.builder()
                .group("all-apis")
                .displayName("All APIs")
                .pathsToMatch("/api/v1/**")
                .build();
    }
    
    /**
     * 群组管理接口
     */
    @Bean
    public GroupedOpenApi groupApis() {
        return GroupedOpenApi.builder()
                .group("group-apis")
                .displayName("Group Management APIs")
                .pathsToMatch( "/api/v1/group/**")
                .build();
    }

    /**
     * 部门管理接口
     */
    @Bean
    public GroupedOpenApi departmentApis() {
        return GroupedOpenApi.builder()
                .group("department-apis")
                .displayName("Department Management APIs")
                .pathsToMatch("/api/v1/department/**")
                .build();
    }

    /**
     * 成员管理接口
     */
    @Bean
    public GroupedOpenApi memberApis() {
        return GroupedOpenApi.builder()
                .group("member-apis")
                .displayName("Member Management APIs")
                .pathsToMatch("/api/v1/member/**")
                .build();
    }

    /**
     * 认证管理接口
     */
    @Bean
    public GroupedOpenApi authApis() {
        return GroupedOpenApi.builder()
                .group("auth-apis")
                .displayName("Authentication APIs")
                .pathsToMatch("/auth/v1/**")
                .build();
    }

    /**
     * 用户管理接口
     */
    @Bean
    public GroupedOpenApi userApis() {
        return GroupedOpenApi.builder()
                .group("user-apis")
                .displayName("User Management APIs")
                .pathsToMatch("/api/v1/user/**")
                .build();
    }

    /**
     * 组织管理接口
     */
    @Bean
    public GroupedOpenApi organizationApis() {
        return GroupedOpenApi.builder()
                .group("organization-apis")
                .displayName("Organization Management APIs")
                .pathsToMatch("/api/v1/org/**")
                .build();
    }

    /**
     * 角色管理接口
     */
    @Bean
    public GroupedOpenApi roleApis() {
        return GroupedOpenApi.builder()
                .group("role-apis")
                .displayName("Role Management APIs")
                .pathsToMatch("/api/v1/role/**")
                .build();
    }

    /**
     * 权限管理接口
     */
    @Bean
    public GroupedOpenApi authorityApis() {
        return GroupedOpenApi.builder()
                .group("authority-apis")
                .displayName("Authority Management APIs")
                .pathsToMatch("/api/v1/authority/**")
                .build();
    }

    /**
     * Token管理接口
     */
    @Bean
    public GroupedOpenApi tokenApis() {
        return GroupedOpenApi.builder()
                .group("token-apis")
                .displayName("Token Management APIs")
                .pathsToMatch("/api/v1/token/**")
                .build();
    }

    /**
     * 文章管理接口
     */
    @Bean
    public GroupedOpenApi articleApis() {
        return GroupedOpenApi.builder()
                .group("article-apis")
                .displayName("Article Management APIs")
                .pathsToMatch("/api/v1/article/**")
                .build();
    }

    /**
     * 自动回复固定回复接口
     */
    @Bean
    public GroupedOpenApi autoReplyFixedApis() {
        return GroupedOpenApi.builder()
                .group("auto-reply-fixed-apis")
                .displayName("Auto Reply Fixed APIs")
                .pathsToMatch("/api/v1/autoreply/fixed/**")
                .build();
    }

    /**
     * 自动回复关键词接口
     */
    @Bean
    public GroupedOpenApi autoReplyKeywordApis() {
        return GroupedOpenApi.builder()
                .group("auto-reply-keyword-apis")
                .displayName("Auto Reply Keyword APIs")
                .pathsToMatch("/api/v1/autoreply/keyword/**")
                .build();
    }

    /**
     * 常见问题接口
     */
    @Bean
    public GroupedOpenApi faqApis() {
        return GroupedOpenApi.builder()
                .group("faq-apis")
                .displayName("FAQ Management APIs")
                .pathsToMatch("/api/v1/faq/**")
                .build();
    }

    /**
     * 知识库接口
     */
    @Bean
    public GroupedOpenApi kbaseApis() {
        return GroupedOpenApi.builder()
                .group("kbase-apis")
                .displayName("Knowledge Base Management APIs")
                .pathsToMatch("/api/v1/kbase/**")
                .build();
    }

    /**
     * 文件分块接口
     */
    @Bean
    public GroupedOpenApi chunkApis() {
        return GroupedOpenApi.builder()
                .group("chunk-apis")
                .displayName("File Chunk Management APIs")
                .pathsToMatch("/api/v1/llm/chunk/**")
                .build();
    }

    /**
     * 文件管理接口
     */
    @Bean
    public GroupedOpenApi fileApis() {
        return GroupedOpenApi.builder()
                .group("file-apis")
                .displayName("File Management APIs")
                .pathsToMatch("/api/v1/llm/file/**")
                .build();
    }

    /**
     * 文本管理接口
     */
    @Bean
    public GroupedOpenApi textApis() {
        return GroupedOpenApi.builder()
                .group("text-apis")
                .displayName("Text Management APIs")
                .pathsToMatch("/api/v1/llm/text/**")
                .build();
    }

    /**
     * 网站管理接口
     */
    @Bean
    public GroupedOpenApi websiteApis() {
        return GroupedOpenApi.builder()
                .group("website-apis")
                .displayName("Website Management APIs")
                .pathsToMatch("/api/v1/llm/website/**")
                .build();
    }

    /**
     * 快捷回复接口
     */
    @Bean
    public GroupedOpenApi quickReplyApis() {
        return GroupedOpenApi.builder()
                .group("quick-reply-apis")
                .displayName("Quick Reply Management APIs")
                .pathsToMatch("/api/v1/quickreply/**")
                .build();
    }

    /**
     * 敏感词管理接口
     */
    @Bean
    public GroupedOpenApi tabooApis() {
        return GroupedOpenApi.builder()
                .group("taboo-apis")
                .displayName("Taboo Word Management APIs")
                .pathsToMatch("/api/v1/taboo/**")
                .build();
    }

    /**
     * 客服管理接口
     */
    @Bean
    public GroupedOpenApi agentApis() {
        return GroupedOpenApi.builder()
                .group("agent-apis")
                .displayName("Agent Management APIs")
                .pathsToMatch("/api/v1/agent/**")
                .build();
    }

    /**
     * 客户管理接口
     */
    @Bean
    public GroupedOpenApi customerApis() {
        return GroupedOpenApi.builder()
                .group("customer-apis")
                .displayName("Customer Management APIs")
                .pathsToMatch("/api/v1/crm/**")
                .build();
    }

    /**
     * 消息反馈接口
     */
    @Bean
    public GroupedOpenApi messageFeedbackApis() {
        return GroupedOpenApi.builder()
                .group("message-feedback-apis")
                .displayName("Message Feedback APIs")
                .pathsToMatch("/api/v1/message/feedback/**")
                .build();
    }

    /**
     * 留言消息接口
     */
    @Bean
    public GroupedOpenApi messageLeaveApis() {
        return GroupedOpenApi.builder()
                .group("message-leave-apis")
                .displayName("Leave Message APIs")
                .pathsToMatch("/api/v1/message/leave/**")
                .build();
    }

    /**
     * 消息解析接口
     */
    @Bean
    public GroupedOpenApi messageParsedApis() {
        return GroupedOpenApi.builder()
                .group("message-parsed-apis")
                .displayName("Message Parsed APIs")
                .pathsToMatch("/api/v1/message/parsed/**")
                .build();
    }

    /**
     * 消息评价接口
     */
    @Bean
    public GroupedOpenApi messageRatingApis() {
        return GroupedOpenApi.builder()
                .group("message-rating-apis")
                .displayName("Message Rating APIs")
                .pathsToMatch("/api/v1/message/rating/**")
                .build();
    }

    /**
     * 未回复消息接口
     */
    @Bean
    public GroupedOpenApi messageUnansweredApis() {
        return GroupedOpenApi.builder()
                .group("message-unanswered-apis")
                .displayName("Unanswered Message APIs")
                .pathsToMatch("/api/v1/message/unanswered/**")
                .build();
    }

    /**
     * 未读消息接口
     */
    @Bean
    public GroupedOpenApi messageUnreadApis() {
        return GroupedOpenApi.builder()
                .group("message-unread-apis")
                .displayName("Unread Message APIs")
                .pathsToMatch("/api/v1/message/unread/**")
                .build();
    }

    /**
     * 队列管理接口
     */
    @Bean
    public GroupedOpenApi queueApis() {
        return GroupedOpenApi.builder()
                .group("queue-apis")
                .displayName("Queue Management APIs")
                .pathsToMatch("/api/v1/queue/**")
                .build();
    }

    /**
     * 队列成员接口
     */
    @Bean
    public GroupedOpenApi queueMemberApis() {
        return GroupedOpenApi.builder()
                .group("queue-member-apis")
                .displayName("Queue Member APIs")
                .pathsToMatch("/api/v1/queue/member/**")
                .build();
    }

    /**
     * 会话邀请接口
     */
    @Bean
    public GroupedOpenApi threadInviteApis() {
        return GroupedOpenApi.builder()
                .group("thread-invite-apis")
                .displayName("Thread Invitation APIs")
                .pathsToMatch("/api/v1/thread/invite/**")
                .build();
    }

    /**
     * 会话评价接口
     */
    @Bean
    public GroupedOpenApi threadRatingApis() {
        return GroupedOpenApi.builder()
                .group("thread-rating-apis")
                .displayName("Thread Rating APIs")
                .pathsToMatch("/api/v1/thread/rating/**")
                .build();
    }

    /**
     * 会话小结接口
     */
    @Bean
    public GroupedOpenApi threadSummaryApis() {
        return GroupedOpenApi.builder()
                .group("thread-summary-apis")
                .displayName("Thread Summary APIs")
                .pathsToMatch("/api/v1/thread/summary/**")
                .build();
    }

    /**
     * 会话转接接口
     */
    @Bean
    public GroupedOpenApi threadTransferApis() {
        return GroupedOpenApi.builder()
                .group("thread-transfer-apis")
                .displayName("Thread Transfer APIs")
                .pathsToMatch("/api/v1/thread/transfer/**")
                .build();
    }

    /**
     * 转接关键词接口
     */
    @Bean
    public GroupedOpenApi transferKeywordApis() {
        return GroupedOpenApi.builder()
                .group("transfer-keyword-apis")
                .displayName("Transfer Keyword APIs")
                .pathsToMatch("/api/v1/transfer/keyword/**")
                .build();
    }

    /**
     * 访客管理接口
     */
    @Bean
    public GroupedOpenApi visitorApis() {
        return GroupedOpenApi.builder()
                .group("visitor-apis")
                .displayName("Visitor Management APIs")
                .pathsToMatch("/api/v1/visitor/**")
                .build();
    }

    /**
     * 工作组管理接口
     */
    @Bean
    public GroupedOpenApi workgroupApis() {
        return GroupedOpenApi.builder()
                .group("workgroup-apis")
                .displayName("Workgroup Management APIs")
                .pathsToMatch("/api/v1/workgroup/**")
                .build();
    }

    /**
     * 工单管理接口
     */
    @Bean
    public GroupedOpenApi ticketApis() {
        return GroupedOpenApi.builder()
                .group("ticket-apis")
                .displayName("Ticket Management APIs")
                .pathsToMatch("/api/v1/ticket/**")
                .build();
    }

    /**
     * 浏览记录接口
     */
    @Bean
    public GroupedOpenApi browseApis() {
        return GroupedOpenApi.builder()
                .group("browse-apis")
                .displayName("Browse Record APIs")
                .pathsToMatch("/api/v1/browse/**")
                .build();
    }

    /**
     * 黑名单管理接口
     */
    @Bean
    public GroupedOpenApi blackApis() {
        return GroupedOpenApi.builder()
                .group("black-apis")
                .displayName("Blacklist Management APIs")
                .pathsToMatch("/api/v1/black/**")
                .build();
    }

    /**
     * 会话管理接口
     */
    @Bean
    public GroupedOpenApi threadApis() {
        return GroupedOpenApi.builder()
                .group("thread-apis")
                .displayName("Thread Management APIs")
                .pathsToMatch("/api/v1/thread/**")
                .build();
    }

    /**
     * 消息管理接口
     */
    @Bean
    public GroupedOpenApi messageApis() {
        return GroupedOpenApi.builder()
                .group("message-apis")
                .displayName("Message Management APIs")
                .pathsToMatch("/api/v1/message/**")
                .build();
    }

    /**
     * 机器人管理接口
     */
    @Bean
    public GroupedOpenApi robotApis() {
        return GroupedOpenApi.builder()
                .group("robot-apis")
                .displayName("Robot Management APIs")
                .pathsToMatch("/api/v1/robot/**")
                .build();
    }

    /**
     * LLM提供商管理接口
     */
    @Bean
    public GroupedOpenApi llmProviderApis() {
        return GroupedOpenApi.builder()
                .group("llm-provider-apis")
                .displayName("LLM Provider Management APIs")
                .pathsToMatch("/api/v1/provider/**")
                .build();
    }

    /**
     * LLM模型管理接口
     */
    @Bean
    public GroupedOpenApi llmModelApis() {
        return GroupedOpenApi.builder()
                .group("llm-model-apis")
                .displayName("LLM Model Management APIs")
                .pathsToMatch("/api/v1/model/**")
                .build();
    }

    /**
     * 工作流管理接口
     */
    @Bean
    public GroupedOpenApi workflowApis() {
        return GroupedOpenApi.builder()
                .group("workflow-apis")
                .displayName("Workflow Management APIs")
                .pathsToMatch("/api/v1/workflow/**")
                .build();
    }

    /**
     * 评论管理接口
     */
    @Bean
    public GroupedOpenApi commentApis() {
        return GroupedOpenApi.builder()
                .group("comment-apis")
                .displayName("Comment Management APIs")
                .pathsToMatch("/api/v1/comment/**")
                .build();
    }

    /**
     * 文件上传管理接口
     */
    @Bean
    public GroupedOpenApi uploadApis() {
        return GroupedOpenApi.builder()
                .group("upload-apis")
                .displayName("File Upload APIs")
                .pathsToMatch("/api/v1/upload/**")
                .build();
    }

    /**
     * 会话管理接口
     */
    @Bean
    public GroupedOpenApi sessionApis() {
        return GroupedOpenApi.builder()
                .group("session-apis")
                .displayName("Session Management APIs")
                .pathsToMatch("/session/**", "/cookie/**")
                .build();
    }

    /**
     * 分类管理接口
     */
    @Bean
    public GroupedOpenApi categoryApis() {
        return GroupedOpenApi.builder()
                .group("category-apis")
                .displayName("Category Management APIs")
                .pathsToMatch("/api/v1/category/**")
                .build();
    }

    /**
     * 剪贴板管理接口
     */
    @Bean
    public GroupedOpenApi clipboardApis() {
        return GroupedOpenApi.builder()
                .group("clipboard-apis")
                .displayName("Clipboard Management APIs")
                .pathsToMatch("/api/v1/clipboard/**")
                .build();
    }

    /**
     * 标签管理接口
     */
    @Bean
    public GroupedOpenApi tagApis() {
        return GroupedOpenApi.builder()
                .group("tag-apis")
                .displayName("Tag Management APIs")
                .pathsToMatch("/api/v1/tag/**")
                .build();
    }

    /**
     * 群组邀请管理接口
     */
    @Bean
    public GroupedOpenApi groupInviteApis() {
        return GroupedOpenApi.builder()
                .group("group-invite-apis")
                .displayName("Group Invitation Management APIs")
                .pathsToMatch("/api/v1/group/invite/**")
                .build();
    }

    /**
     * 群组通知管理接口
     */
    @Bean
    public GroupedOpenApi groupNoticeApis() {
        return GroupedOpenApi.builder()
                .group("group-notice-apis")
                .displayName("Group Notice Management APIs")
                .pathsToMatch("/api/v1/group/notice/**")
                .build();
    }

    /**
     * 通知管理接口
     */
    @Bean
    public GroupedOpenApi noticeApis() {
        return GroupedOpenApi.builder()
                .group("notice-apis")
                .displayName("Notice Management APIs")
                .pathsToMatch("/api/v1/notice/**")
                .build();
    }

    /**
     * 推送管理接口
     */
    @Bean
    public GroupedOpenApi pushApis() {
        return GroupedOpenApi.builder()
                .group("push-apis")
                .displayName("Push Notification APIs")
                .pathsToMatch("/api/v1/push/**")
                .build();
    }

    /**
     * 操作日志管理接口
     */
    @Bean
    public GroupedOpenApi actionApis() {
        return GroupedOpenApi.builder()
                .group("action-apis")
                .displayName("Action Log Management APIs")
                .pathsToMatch("/api/v1/action/**")
                .build();
    }

    /**
     * 菜单管理接口
     */
    @Bean
    public GroupedOpenApi menuApis() {
        return GroupedOpenApi.builder()
                .group("menu-apis")
                .displayName("Menu Management APIs")
                .pathsToMatch("/api/v1/menu/**")
                .build();
    }

    /**
     * 功能特性管理接口
     */
    @Bean
    public GroupedOpenApi featureApis() {
        return GroupedOpenApi.builder()
                .group("feature-apis")
                .displayName("Feature Management APIs")
                .pathsToMatch("/api/v1/features/**", "/features/**")
                .build();
    }

    /**
     * 验证码管理接口
     */
    @Bean
    public GroupedOpenApi kaptchaApis() {
        return GroupedOpenApi.builder()
                .group("kaptcha-apis")
                .displayName("Captcha Management APIs")
                .pathsToMatch("/kaptcha/api/v1/**")
                .build();
    }

    /**
     * 工作流变量管理接口
     */
    @Bean
    public GroupedOpenApi workflowVariableApis() {
        return GroupedOpenApi.builder()
                .group("workflow-variable-apis")
                .displayName("Workflow Variable Management APIs")
                .pathsToMatch("/api/v1/workflow/variable/**")
                .build();
    }

    /**
     * 工作流结果管理接口
     */
    @Bean
    public GroupedOpenApi workflowResultApis() {
        return GroupedOpenApi.builder()
                .group("workflow-result-apis")
                .displayName("Workflow Result Management APIs")
                .pathsToMatch("/api/v1/workflow/result/**")
                .build();
    }

    /**
     * 收藏管理接口
     */
    @Bean
    public GroupedOpenApi favoriteApis() {
        return GroupedOpenApi.builder()
                .group("favorite-apis")
                .displayName("Favorite Management APIs")
                .pathsToMatch("/api/v1/favorite/**")
                .build();
    }

    /**
     * 灰度发布管理接口
     */
    @Bean
    public GroupedOpenApi grayReleaseApis() {
        return GroupedOpenApi.builder()
                .group("gray-release-apis")
                .displayName("Gray Release Management APIs")
                .pathsToMatch("/api/v1/gray-release/**")
                .build();
    }

    /**
     * 定时任务管理接口
     */
    @Bean
    public GroupedOpenApi quartzApis() {
        return GroupedOpenApi.builder()
                .group("quartz-apis")
                .displayName("Quartz Job Management APIs")
                .pathsToMatch("/api/v1/quartz/**")
                .build();
    }

    /**
     * IP管理接口
     */
    @Bean
    public GroupedOpenApi ipApis() {
        return GroupedOpenApi.builder()
                .group("ip-apis")
                .displayName("IP Management APIs")
                .pathsToMatch("/ip/api/v1/**", "/api/v1/ip/**")
                .build();
    }

    /**
     * IP黑名单管理接口
     */
    @Bean
    public GroupedOpenApi ipBlacklistApis() {
        return GroupedOpenApi.builder()
                .group("ip-blacklist-apis")
                .displayName("IP Blacklist Management APIs")
                .pathsToMatch("/api/v1/ip/black/**")
                .build();
    }

    /**
     * IP白名单管理接口
     */
    @Bean
    public GroupedOpenApi ipWhitelistApis() {
        return GroupedOpenApi.builder()
                .group("ip-whitelist-apis")
                .displayName("IP Whitelist Management APIs")
                .pathsToMatch("/api/v1/ip/white/**")
                .build();
    }

    /**
     * IP访问管理接口
     */
    @Bean
    public GroupedOpenApi ipAccessApis() {
        return GroupedOpenApi.builder()
                .group("ip-access-apis")
                .displayName("IP Access Management APIs")
                .pathsToMatch("/api/v1/ip/access/**")
                .build();
    }

    /**
     * 配置管理接口（补充bytedesk路径）
     */
    @Bean
    public GroupedOpenApi configBytedeskApis() {
        return GroupedOpenApi.builder()
                .group("config-bytedesk-apis")
                .displayName("Bytedesk Configuration APIs")
                .pathsToMatch("/config/bytedesk/**")
                .build();
    }

    /**
     * 频道管理接口
     */
    @Bean
    public GroupedOpenApi channelApis() {
        return GroupedOpenApi.builder()
                .group("channel-apis")
                .displayName("Channel Management APIs")
                .pathsToMatch("/api/v1/channel/**")
                .build();
    }

    /**
     * MQTT管理接口
     */
    @Bean
    public GroupedOpenApi mqttApis() {
        return GroupedOpenApi.builder()
                .group("mqtt-apis")
                .displayName("MQTT Management APIs")
                .pathsToMatch("/mqtt/api/v1/**")
                .build();
    }

    /**
     * JMS管理接口
     */
    @Bean
    public GroupedOpenApi jmsApis() {
        return GroupedOpenApi.builder()
                .group("jms-apis")
                .displayName("JMS Management APIs")
                .pathsToMatch("/jms/**")
                .build();
    }

    /**
     * Redis流管理接口
     */
    @Bean
    public GroupedOpenApi redisStreamApis() {
        return GroupedOpenApi.builder()
                .group("redis-stream-apis")
                .displayName("Redis Stream Management APIs")
                .pathsToMatch("/redis/stream/**")
                .build();
    }

    /**
     * Redis发布订阅管理接口
     */
    @Bean
    public GroupedOpenApi redisPubsubApis() {
        return GroupedOpenApi.builder()
                .group("redis-pubsub-apis")
                .displayName("Redis PubSub Management APIs")
                .pathsToMatch("/redis/pubsub/**")
                .build();
    }

    /**
     * 配置管理接口
     */
    @Bean
    public GroupedOpenApi configApis() {
        return GroupedOpenApi.builder()
                .group("config-apis")
                .displayName("Configuration Management APIs")
                .pathsToMatch("/config/**")
                .build();
    }

    /**
     * OAuth2管理接口
     */
    @Bean
    public GroupedOpenApi oauth2Apis() {
        return GroupedOpenApi.builder()
                .group("oauth2-apis")
                .displayName("OAuth2 Management APIs")
                .pathsToMatch("/api/v1/oauth2/**", "/sso/oauth2/**")
                .build();
    }

    /**
     * 组织申请管理接口
     */
    @Bean
    public GroupedOpenApi organizationApplyApis() {
        return GroupedOpenApi.builder()
                .group("organization-apply-apis")
                .displayName("Organization Application Management APIs")
                .pathsToMatch("/api/v1/org/apply/**")
                .build();
    }

    /**
     * 助手管理接口
     */
    @Bean
    public GroupedOpenApi assistantApis() {
        return GroupedOpenApi.builder()
                .group("assistant-apis")
                .displayName("Assistant Management APIs")
                .pathsToMatch("/api/v1/assistant/**")
                .build();
    }

    /**
     * URL管理接口
     */
    @Bean
    public GroupedOpenApi urlApis() {
        return GroupedOpenApi.builder()
                .group("url-apis")
                .displayName("URL Management APIs")
                .pathsToMatch("/api/v1/url/**", "/url/**")
                .build();
    }

    /**
     * 文件预览接口
     */
    @Bean
    public GroupedOpenApi filePreviewApis() {
        return GroupedOpenApi.builder()
                .group("file-preview-apis")
                .displayName("File Preview APIs")
                .pathsToMatch("/file/**")
                .build();
    }

    /**
     * 访客上传接口
     */
    @Bean
    public GroupedOpenApi visitorUploadApis() {
        return GroupedOpenApi.builder()
                .group("visitor-upload-apis")
                .displayName("Visitor Upload APIs")
                .pathsToMatch("/visitor/api/v1/upload/**")
                .build();
    }

    /**
     * 工作时间管理接口
     */
    @Bean
    public GroupedOpenApi worktimeApis() {
        return GroupedOpenApi.builder()
                .group("worktime-apis")
                .displayName("Worktime Management APIs")
                .pathsToMatch("/api/v1/worktime/**")
                .build();
    }

    /**
     * 工作时间设置管理接口
     */
    @Bean
    public GroupedOpenApi worktimeSettingApis() {
        return GroupedOpenApi.builder()
                .group("worktime-setting-apis")
                .displayName("Worktime Setting Management APIs")
                .pathsToMatch("/api/v1/worktime/setting/**")
                .build();
    }

    /**
     * 模板管理接口
     */
    @Bean
    public GroupedOpenApi templateApis() {
        return GroupedOpenApi.builder()
                .group("template-apis")
                .displayName("Template Management APIs")
                .pathsToMatch("/api/v1/template/**")
                .build();
    }

    /**
     * 访客会话管理接口
     */
    @Bean
    public GroupedOpenApi visitorThreadApis() {
        return GroupedOpenApi.builder()
                .group("visitor-thread-apis")
                .displayName("Visitor Thread Management APIs")
                .pathsToMatch("/visitor/api/v1/visitor_thread/**")
                .build();
    }

    /**
     * 访客评价匿名接口
     */
    @Bean
    public GroupedOpenApi visitorRatingAnonymousApis() {
        return GroupedOpenApi.builder()
                .group("visitor-rating-anonymous-apis")
                .displayName("Visitor Rating Anonymous APIs")
                .pathsToMatch("/visitor/api/v1/visitor/rating/**")
                .build();
    }

    /**
     * 素材管理接口
     */
    @Bean
    public GroupedOpenApi materialApis() {
        return GroupedOpenApi.builder()
                .group("material-apis")
                .displayName("Material Management APIs")
                .pathsToMatch("/api/v1/material/**")
                .build();
    }

    /**
     * 文章归档管理接口
     */
    @Bean
    public GroupedOpenApi articleArchiveApis() {
        return GroupedOpenApi.builder()
                .group("article-archive-apis")
                .displayName("Article Archive Management APIs")
                .pathsToMatch("/api/v1/article_archive/**")
                .build();
    }

    /**
     * 文章匿名接口
     */
    @Bean
    public GroupedOpenApi articleAnonymousApis() {
        return GroupedOpenApi.builder()
                .group("article-anonymous-apis")
                .displayName("Article Anonymous APIs")
                .pathsToMatch("/visitor/api/v1/article/**")
                .build();
    }

    /**
     * 意图设置管理接口
     */
    @Bean
    public GroupedOpenApi intentionSettingsApis() {
        return GroupedOpenApi.builder()
                .group("intention-settings-apis")
                .displayName("Intention Settings Management APIs")
                .pathsToMatch("/api/v1/intention/settings/**")
                .build();
    }

    /**
     * 服务设置管理接口
     */
    @Bean
    public GroupedOpenApi serviceSettingsApis() {
        return GroupedOpenApi.builder()
                .group("service-settings-apis")
                .displayName("Service Settings Management APIs")
                .pathsToMatch("/api/v1/service/setting/**")
                .build();
    }

    /**
     * 降级设置管理接口
     */
    @Bean
    public GroupedOpenApi ratedownSettingsApis() {
        return GroupedOpenApi.builder()
                .group("ratedown-settings-apis")
                .displayName("Ratedown Settings Management APIs")
                .pathsToMatch("/api/v1/ratedown/setting/**")
                .build();
    }

    /**
     * 敏感词消息管理接口
     */
    @Bean
    public GroupedOpenApi tabooMessageApis() {
        return GroupedOpenApi.builder()
                .group("taboo-message-apis")
                .displayName("Taboo Message Management APIs")
                .pathsToMatch("/api/v1/taboo/message/**")
                .build();
    }

    /**
     * 知识库统计管理接口
     */
    @Bean
    public GroupedOpenApi kbaseStatisticApis() {
        return GroupedOpenApi.builder()
                .group("kbase-statistic-apis")
                .displayName("KbaseStatistic Management APIs")
                .pathsToMatch("/api/v1/kbase/statistic/**")
                .build();
    }

    /**
     * Janus音频管理接口
     */
    @Bean
    public GroupedOpenApi janusAudioApis() {
        return GroupedOpenApi.builder()
                .group("janus-audio-apis")
                .displayName("JanusAudio Management APIs")
                .pathsToMatch("/api/v1/janus/audio/**")
                .build();
    }

    /**
     * Janus视频管理接口
     */
    @Bean
    public GroupedOpenApi janusVideoApis() {
        return GroupedOpenApi.builder()
                .group("janus-video-apis")
                .displayName("JanusVideo Management APIs")
                .pathsToMatch("/api/v1/janus/video/**")
                .build();
    }

    /**
     * 统一消息管理接口（企业版）
     */
    @Bean
    public GroupedOpenApi unifiedMessageApis() {
        return GroupedOpenApi.builder()
                .group("unified-message-apis")
                .displayName("Unified Message Management APIs")
                .pathsToMatch("/api/v1/message/unified/**")
                .build();
    }

    /**
     * 质量申诉管理接口
     */
    @Bean
    public GroupedOpenApi qualityAppealApis() {
        return GroupedOpenApi.builder()
                .group("quality-appeal-apis")
                .displayName("QualityAppeal Management APIs")
                .pathsToMatch("/api/v1/quality/appeal/**")
                .build();
    }

    /**
     * 屏幕管理接口
     */
    @Bean
    public GroupedOpenApi screenApis() {
        return GroupedOpenApi.builder()
                .group("screen-apis")
                .displayName("Screen Management APIs")
                .pathsToMatch("/api/v1/screen/**")
                .build();
    }

    /**
     * 设置模板管理接口
     */
    @Bean
    public GroupedOpenApi settingsTemplateApis() {
        return GroupedOpenApi.builder()
                .group("settings-template-apis")
                .displayName("SettingsTemplate Management APIs")
                .pathsToMatch("/api/v1/settings/template/**")
                .build();
    }

    /**
     * 表单结果管理接口
     */
    @Bean
    public GroupedOpenApi formResultApis() {
        return GroupedOpenApi.builder()
                .group("form-result-apis")
                .displayName("Form Result Management APIs")
                .pathsToMatch("/api/v1/form/result/**")
                .build();
    }

    /**
     * 表单管理接口
     */
    @Bean
    public GroupedOpenApi formApis() {
        return GroupedOpenApi.builder()
                .group("form-apis")
                .displayName("Form Management APIs")
                .pathsToMatch("/api/v1/form/**")
                .build();
    }

    /**
     * 客服状态设置管理接口
     */
    @Bean
    public GroupedOpenApi agentStatusSettingApis() {
        return GroupedOpenApi.builder()
                .group("agent-status-setting-apis")
                .displayName("Agent Status Setting Management APIs")
                .pathsToMatch("/api/v1/agent/status/setting/**")
                .build();
    }

    /**
     * 客服状态管理接口
     */
    @Bean
    public GroupedOpenApi agentStatusApis() {
        return GroupedOpenApi.builder()
                .group("agent-status-apis")
                .displayName("Agent Status Management APIs")
                .pathsToMatch("/api/v1/agent/status/**")
                .build();
    }

    /**
     * 主题管理接口
     */
    @Bean
    public GroupedOpenApi topicApis() {
        return GroupedOpenApi.builder()
                .group("topic-apis")
                .displayName("Topic Management APIs")
                .pathsToMatch("/api/v1/topic/**")
                .build();
    }

    /**
     * 访客消息管理接口
     */
    @Bean
    public GroupedOpenApi visitorMessageApis() {
        return GroupedOpenApi.builder()
                .group("visitor-message-apis")
                .displayName("Visitor Message Management APIs")
                .pathsToMatch("/api/v1/visitor/message/**")
                .build();
    }

    /**
     * 追踪管理接口
     */
    @Bean
    public GroupedOpenApi traceApis() {
        return GroupedOpenApi.builder()
                .group("trace-apis")
                .displayName("Trace Management APIs")
                .pathsToMatch("/api/v1/trace/**")
                .build();
    }

    /**
     * MinIO存储管理接口
     */
    @Bean
    public GroupedOpenApi minioStorageApis() {
        return GroupedOpenApi.builder()
                .group("minio-storage-apis")
                .displayName("MinIO Storage APIs")
                .pathsToMatch("/api/v1/minio/**")
                .build();
    }

    /**
     * MaxKB聊天接口
     */
    @Bean
    public GroupedOpenApi maxkbChatApis() {
        return GroupedOpenApi.builder()
                .group("maxkb-chat-apis")
                .displayName("MaxKB Chat API")
                .pathsToMatch("/api/v1/maxkb/chat/**")
                .build();
    }

    /**
     * 工作流节点管理接口
     */
    @Bean
    public GroupedOpenApi workflowNodeApis() {
        return GroupedOpenApi.builder()
                .group("workflow-node-apis")
                .displayName("WorkflowNode Management APIs")
                .pathsToMatch("/api/v1/workflow/node/**")
                .build();
    }

    /**
     * 消息纠错管理接口
     */
    @Bean
    public GroupedOpenApi messageCorrectionApis() {
        return GroupedOpenApi.builder()
                .group("message-correction-apis")
                .displayName("Message Correction Management APIs")
                .pathsToMatch("/api/v1/message/correction/**")
                .build();
    }

    /**
     * 质量计划管理接口
     */
    @Bean
    public GroupedOpenApi qualityPlanApis() {
        return GroupedOpenApi.builder()
                .group("quality-plan-apis")
                .displayName("QualityPlan Management APIs")
                .pathsToMatch("/api/v1/quality/plan/**")
                .build();
    }

    /**
     * Token统计管理接口
     */
    @Bean
    public GroupedOpenApi statisticTokenApis() {
        return GroupedOpenApi.builder()
                .group("statistic-token-apis")
                .displayName("StatisticToken Management APIs")
                .pathsToMatch("/api/v1/statistic/token/**")
                .build();
    }

    /**
     * 路由规则管理接口
     */
    @Bean
    public GroupedOpenApi routingRuleApis() {
        return GroupedOpenApi.builder()
                .group("routing-rule-apis")
                .displayName("Routing Rule Management APIs")
                .pathsToMatch("/api/v1/routing/rule/**")
                .build();
    }

    /**
     * AI统计管理接口
     */
    @Bean
    public GroupedOpenApi aiStatisticApis() {
        return GroupedOpenApi.builder()
                .group("ai-statistic-apis")
                .displayName("AiStatistic Management APIs")
                .pathsToMatch("/api/v1/ai/statistic/**")
                .build();
    }

    /**
     * 呼叫MRCP管理接口
     */
    @Bean
    public GroupedOpenApi callMrcpApis() {
        return GroupedOpenApi.builder()
                .group("call-mrcp-apis")
                .displayName("CallMrcp Management APIs")
                .pathsToMatch("/api/v1/call/mrcp/**")
                .build();
    }

    /**
     * 呼叫IVR管理接口
     */
    @Bean
    public GroupedOpenApi callIvrApis() {
        return GroupedOpenApi.builder()
                .group("call-ivr-apis")
                .displayName("CallIvr Management APIs")
                .pathsToMatch("/api/v1/call/ivr/**")
                .build();
    }

    /**
     * 质量检查管理接口
     */
    @Bean
    public GroupedOpenApi qualityCheckApis() {
        return GroupedOpenApi.builder()
                .group("quality-check-apis")
                .displayName("QualityCheck Management APIs")
                .pathsToMatch("/api/v1/quality/check/**")
                .build();
    }

    /**
     * 质量流程管理接口
     */
    @Bean
    public GroupedOpenApi qualityFlowApis() {
        return GroupedOpenApi.builder()
                .group("quality-flow-apis")
                .displayName("QualityFlow Management APIs")
                .pathsToMatch("/api/v1/quality/flow/**")
                .build();
    }

    /**
     * 购物管理接口
     */
    @Bean
    public GroupedOpenApi shoppingApis() {
        return GroupedOpenApi.builder()
                .group("shopping-apis")
                .displayName("Shopping Management APIs")
                .pathsToMatch("/api/v1/shopping/**")
                .build();
    }

    /**
     * 工单模板管理接口
     */
    @Bean
    public GroupedOpenApi ticketTemplateApis() {
        return GroupedOpenApi.builder()
                .group("ticket-template-apis")
                .displayName("TicketTemplate Management APIs")
                .pathsToMatch("/api/v1/ticket/template/**")
                .build();
    }

    /**
     * 服务器指标管理接口
     */
    @Bean
    public GroupedOpenApi serverMetricsApis() {
        return GroupedOpenApi.builder()
                .group("server-metrics-apis")
                .displayName("Server Metrics Management APIs")
                .pathsToMatch("/api/v1/server/metrics/**")
                .build();
    }

    /**
     * 时刻管理接口
     */
    @Bean
    public GroupedOpenApi momentApis() {
        return GroupedOpenApi.builder()
                .group("moment-apis")
                .displayName("Moment Management APIs")
                .pathsToMatch("/api/v1/moment/**")
                .build();
    }

    /**
     * 质量统计管理接口
     */
    @Bean
    public GroupedOpenApi qualityStatisticApis() {
        return GroupedOpenApi.builder()
                .group("quality-statistic-apis")
                .displayName("QualityStatistic Management APIs")
                .pathsToMatch("/api/v1/quality/statistic/**")
                .build();
    }

    /**
     * 关系管理接口
     */
    @Bean
    public GroupedOpenApi relationApis() {
        return GroupedOpenApi.builder()
                .group("relation-apis")
                .displayName("Relation Management APIs")
                .pathsToMatch("/api/v1/relation/**")
                .build();
    }

    /**
     * 服务器管理接口
     */
    @Bean
    public GroupedOpenApi serverApis() {
        return GroupedOpenApi.builder()
                .group("server-apis")
                .displayName("Server Management APIs")
                .pathsToMatch("/api/v1/server/**")
                .build();
    }

    /**
     * MCP服务器管理接口
     */
    @Bean
    public GroupedOpenApi mcpServerApis() {
        return GroupedOpenApi.builder()
                .group("mcp-server-apis")
                .displayName("McpServer Management APIs")
                .pathsToMatch("/api/v1/mcp/server/**")
                .build();
    }

    /**
     * 微信公众号用户管理接口
     */
    @Bean
    public GroupedOpenApi wechatMpUserApis() {
        return GroupedOpenApi.builder()
                .group("wechat-mp-user-apis")
                .displayName("WeChat MP User Management APIs")
                .pathsToMatch("/api/v1/wechat/mp/user/**")
                .build();
    }

    /**
     * 微信公众号标签管理接口
     */
    @Bean
    public GroupedOpenApi wechatMpTagApis() {
        return GroupedOpenApi.builder()
                .group("wechat-mp-tag-apis")
                .displayName("WeChat MP Tag Management APIs")
                .pathsToMatch("/api/v1/wechat/mp/tag/**")
                .build();
    }

    /**
     * 微信公众号菜单管理接口
     */
    @Bean
    public GroupedOpenApi wechatMpMenuApis() {
        return GroupedOpenApi.builder()
                .group("wechat-mp-menu-apis")
                .displayName("WeChat MP Menu Management APIs")
                .pathsToMatch("/api/v1/wechat/mp/menu/**")
                .build();
    }

    /**
     * 企业微信管理接口
     */
    @Bean
    public GroupedOpenApi wechatWorkApis() {
        return GroupedOpenApi.builder()
                .group("wechat-work-apis")
                .displayName("WeChat Work Management APIs")
                .pathsToMatch("/api/v1/wechat/work/**")
                .build();
    }

    /**
     * Webhook管理接口
     */
    @Bean
    public GroupedOpenApi webhookApis() {
        return GroupedOpenApi.builder()
                .group("webhook-apis")
                .displayName("Webhook Management APIs")
                .pathsToMatch("/api/v1/webhook/**")
                .build();
    }

    /**
     * 邮件管理接口
     */
    @Bean
    public GroupedOpenApi emailApis() {
        return GroupedOpenApi.builder()
                .group("email-apis")
                .displayName("Email Management APIs")
                .pathsToMatch("/api/v1/email/**")
                .build();
    }

    /**
     * 邮件模板管理接口
     */
    @Bean
    public GroupedOpenApi emailTemplateApis() {
        return GroupedOpenApi.builder()
                .group("email-template-apis")
                .displayName("Email Template Management APIs")
                .pathsToMatch("/api/v1/email/template/**")
                .build();
    }

    /**
     * 抖音管理接口
     */
    @Bean
    public GroupedOpenApi douyinApis() {
        return GroupedOpenApi.builder()
                .group("douyin-apis")
                .displayName("Douyin Management APIs")
                .pathsToMatch("/api/v1/douyin/**")
                .build();
    }

    /**
     * Telegram管理接口
     */
    @Bean
    public GroupedOpenApi telegramApis() {
        return GroupedOpenApi.builder()
                .group("telegram-apis")
                .displayName("Telegram Management APIs")
                .pathsToMatch("/api/v1/telegram/**")
                .build();
    }

    /**
     * 任务管理接口
     */
    @Bean
    public GroupedOpenApi taskApis() {
        return GroupedOpenApi.builder()
                .group("task-apis")
                .displayName("Task Management APIs")
                .pathsToMatch("/api/v1/task/**")
                .build();
    }

    /**
     * 项目管理接口
     */
    @Bean
    public GroupedOpenApi projectApis() {
        return GroupedOpenApi.builder()
                .group("project-apis")
                .displayName("Project Management APIs")
                .pathsToMatch("/api/v1/project/**")
                .build();
    }

    /**
     * 产品管理接口
     */
    @Bean
    public GroupedOpenApi productApis() {
        return GroupedOpenApi.builder()
                .group("product-apis")
                .displayName("Product Management APIs")
                .pathsToMatch("/api/v1/product/**")
                .build();
    }

    /**
     * 商店应用管理接口
     */
    @Bean
    public GroupedOpenApi shopAppApis() {
        return GroupedOpenApi.builder()
                .group("shop-app-apis")
                .displayName("Shop App Management APIs")
                .pathsToMatch("/api/v1/shop/app/**")
                .build();
    }

    /**
     * 服务统计管理接口
     */
    @Bean
    public GroupedOpenApi serviceStatisticApis() {
        return GroupedOpenApi.builder()
                .group("service-statistic-apis")
                .displayName("Service Statistic Management APIs")
                .pathsToMatch("/api/v1/service/statistic/**")
                .build();
    }

    /**
     * 许可证管理接口
     */
    @Bean
    public GroupedOpenApi licenseApis() {
        return GroupedOpenApi.builder()
                .group("license-apis")
                .displayName("License Management APIs")
                .pathsToMatch("/api/v1/license/**")
                .build();
    }
}
