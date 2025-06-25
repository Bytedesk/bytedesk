/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-24 11:10:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-25 22:18:28
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
                .displayName("所有接口")
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
                .displayName("群组接口")
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
                .displayName("部门接口")
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
                .displayName("成员接口")
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
                .displayName("认证接口")
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
                .displayName("用户接口")
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
                .displayName("组织接口")
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
                .displayName("角色接口")
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
                .displayName("权限接口")
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
                .displayName("Token接口")
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
                .displayName("文章接口")
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
                .displayName("固定回复接口")
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
                .displayName("关键词回复接口")
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
                .displayName("常见问题接口")
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
                .displayName("知识库接口")
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
                .displayName("文件分块接口")
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
                .displayName("文件管理接口")
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
                .displayName("文本管理接口")
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
                .displayName("网站管理接口")
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
                .displayName("快捷回复接口")
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
                .displayName("敏感词管理接口")
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
                .displayName("客服接口")
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
                .displayName("客户接口")
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
                .displayName("消息反馈接口")
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
                .displayName("留言消息接口")
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
                .displayName("消息解析接口")
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
                .displayName("消息评价接口")
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
                .displayName("未回复消息接口")
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
                .displayName("未读消息接口")
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
                .displayName("队列接口")
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
                .displayName("队列成员接口")
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
                .displayName("会话邀请接口")
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
                .displayName("会话评价接口")
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
                .displayName("会话小结接口")
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
                .displayName("会话转接接口")
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
                .displayName("转接关键词接口")
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
                .displayName("访客接口")
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
                .displayName("工作组接口")
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
                .displayName("工单接口")
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
                .displayName("浏览记录接口")
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
                .displayName("黑名单接口")
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
                .displayName("会话管理接口")
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
                .displayName("消息管理接口")
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
                .displayName("机器人接口")
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
                .displayName("LLM提供商接口")
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
                .displayName("LLM模型接口")
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
                .displayName("工作流接口")
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
                .displayName("评论接口")
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
                .displayName("文件上传接口")
                .pathsToMatch("/api/v1/upload/**")
                .build();
    }
}
