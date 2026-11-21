/*
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StringListConverterTest {

    private final StringListConverter converter = new StringListConverter();

    @Test
    public void testEmptyCsvReadsAsEmptyList() {
        // 空列表持久化为 ""，读回必须是空列表而不是 [""]，
        // 否则 group.robotUids 之类的字段会带着空白元素触发下游接口报错
        assertTrue(converter.convertToEntityAttribute("").isEmpty());
        assertTrue(converter.convertToEntityAttribute(null).isEmpty());
    }

    @Test
    public void testBlankEntriesFilteredOnRead() {
        // 兼容历史脏数据："a,,b" / " a , b " 读取时过滤空白并 trim
        List<String> result = converter.convertToEntityAttribute("a,,b");
        assertEquals(Arrays.asList("a", "b"), result);
        assertEquals(Arrays.asList("a", "b"), converter.convertToEntityAttribute(" a , b "));
    }

    @Test
    public void testRoundTrip() {
        List<String> input = new ArrayList<>(Arrays.asList("uid1", "uid2"));
        String csv = converter.convertToDatabaseColumn(input);
        assertEquals("uid1,uid2", csv);
        assertEquals(input, converter.convertToEntityAttribute(csv));
    }

    @Test
    public void testWriteBlankEntriesAndNulls() {
        // 写入时空白元素被剔除
        assertEquals("a", converter.convertToDatabaseColumn(new ArrayList<>(Arrays.asList("a", "", "  "))));
        assertEquals("", converter.convertToDatabaseColumn(null));
        assertEquals("", converter.convertToDatabaseColumn(new ArrayList<>()));
    }
}
