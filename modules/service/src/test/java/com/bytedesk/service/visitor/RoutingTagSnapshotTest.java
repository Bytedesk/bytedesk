package com.bytedesk.service.visitor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.thread.ThreadEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

class RoutingTagSnapshotTest {

    @Test
    void snapshotAndEvaluationDoNotLeakThroughJsonOrToString() throws Exception {
        ThreadEntity thread = ThreadEntity.builder().uid("thread-1")
                .routingTagSnapshot(List.of("private-route-tag"))
                .tagRoutingEvaluated(true).build();
        assertFalse(JSON.toJSONString(thread).contains("routingTag"));
        assertFalse(JSON.toJSONString(thread).contains("tagRoutingEvaluated"));
        assertFalse(new ObjectMapper().writeValueAsString(thread).contains("routingTag"));
        assertFalse(new ObjectMapper().writeValueAsString(thread).contains("tagRoutingEvaluated"));
        assertFalse(thread.toString().contains("private-route-tag"));
    }

    @Test
    void rejectsJsonNumbersInsteadOfCoercingToUidStrings() {
        assertThrows(Exception.class, () -> new ObjectMapper().readValue(
                "{\"routingTags\":[123]}", VisitorRequest.class));
    }
}
