package com.bytedesk.core.topic_subscription;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class TopicSubscriptionTools extends BaseTools<TopicSubscriptionRequest, TopicSubscriptionResponse> {

    public TopicSubscriptionTools(TopicSubscriptionRestService restService, ObjectMapper objectMapper) {
        super("topicSubscription", TopicSubscriptionRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query topicSubscription by uid")
    public Object topicSubscriptionQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query topicSubscription by org with request json")
    public Object topicSubscriptionQueryByOrg(@ToolParam(description = "TopicSubscriptionRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query topicSubscription by user with request json")
    public Object topicSubscriptionQueryByUser(@ToolParam(description = "TopicSubscriptionRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create topicSubscription with request json")
    public Object topicSubscriptionCreate(@ToolParam(description = "TopicSubscriptionRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update topicSubscription with request json")
    public Object topicSubscriptionUpdate(@ToolParam(description = "TopicSubscriptionRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete topicSubscription by uid")
    public Object topicSubscriptionDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
