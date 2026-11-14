package com.bytedesk.service.visitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

class RoutingTagsTest {

    @Test
    void preservesUidCaseAndOrderWithoutProfileTags() {
        assertEquals(List.of("VIP_1", "tag-2"), VisitorRoutingTags.normalize(
                List.of(" VIP_1 ", "", "tag-2", "VIP_1")));
        assertTrue(VisitorRoutingTags.normalize(null).isEmpty());
    }

    @Test
    void rejectsInvalidJsonItemsAndUnboundedInputs() {
        assertThrows(IllegalArgumentException.class, () -> VisitorRoutingTags.normalize(Arrays.asList("tag-1", null)));
        assertThrows(IllegalArgumentException.class, () -> VisitorRoutingTags.normalize(List.of(1)));
        assertThrows(IllegalArgumentException.class, () -> VisitorRoutingTags.normalize(List.of("a,b")));
        assertThrows(IllegalArgumentException.class, () -> VisitorRoutingTags.normalize(List.of("a".repeat(65))));
        assertThrows(IllegalArgumentException.class, () -> VisitorRoutingTags.normalize(
                IntStream.range(0, 11).mapToObj(i -> "tag-" + i).toList()));
        assertThrows(IllegalArgumentException.class, () -> VisitorRoutingTags.normalize(
                IntStream.range(0, 1025).mapToObj(i -> "").toList()));
    }
}
