package com.bytedesk.core.topic_subscription;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class TopicSubscriptionTools extends BaseTools<TopicSubscriptionRequest, TopicSubscriptionResponse> {

    public TopicSubscriptionTools(TopicSubscriptionRestService restService, ObjectMapper objectMapper) {
        super("topic_subscription", TopicSubscriptionRequest.class, restService, objectMapper);
    }

    @Tool(name = "topic_subscription_query_by_uid", description = "Query topic_subscription by uid. This tool returns structured data for AI tool invocation.")
    public Object topicSubscriptionQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(name = "topic_subscription_query_by_org", description = "Query topic_subscription by org with request json")
    public Object topicSubscriptionQueryByOrg(@ToolParam(description = "TopicSubscriptionRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(name = "topic_subscription_query_by_user", description = "Query topic_subscription by user with request json")
    public Object topicSubscriptionQueryByUser(@ToolParam(description = "TopicSubscriptionRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(name = "topic_subscription_create", description = "Create topic_subscription with request json. This tool returns structured data for AI tool invocation.")
    public Object topicSubscriptionCreate(@ToolParam(description = "TopicSubscriptionRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(name = "topic_subscription_update", description = "Update topic_subscription with request json. This tool returns structured data for AI tool invocation.")
    public Object topicSubscriptionUpdate(@ToolParam(description = "TopicSubscriptionRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(name = "topic_subscription_delete_by_uid", description = "Delete topic_subscription by uid. This tool returns structured data for AI tool invocation.")
    public Object topicSubscriptionDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
