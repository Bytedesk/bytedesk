package com.bytedesk.core.message;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.bytedesk.core.constant.TypeConsts;

class MessageSpecificationTest {

    @Test
    void serviceComponentShouldIncludeRobotTopics() {
        List<String> keywords = MessageSpecification.resolveTopicKeywordsForComponentType(
                TypeConsts.COMPONENT_TYPE_SERVICE);

        assertThat(keywords).containsExactly("agent", "workgroup", "robot");
    }

    @Test
    void robotComponentShouldOnlyIncludeRobotTopics() {
        List<String> keywords = MessageSpecification.resolveTopicKeywordsForComponentType(
                TypeConsts.COMPONENT_TYPE_ROBOT);

        assertThat(keywords).containsExactly("robot");
    }
}