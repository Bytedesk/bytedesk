package com.bytedesk.core.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class DeviceNameUtilsTest {

    @Test
    void nullAndBlankFallBackToUnknownDevice() {
        assertEquals(DeviceNameUtils.UNKNOWN_DEVICE, DeviceNameUtils.sanitize(null));
        assertEquals(DeviceNameUtils.UNKNOWN_DEVICE, DeviceNameUtils.sanitize(""));
        assertEquals(DeviceNameUtils.UNKNOWN_DEVICE, DeviceNameUtils.sanitize("   "));
    }

    @Test
    void keepsReadableNamesIncludingChinese() {
        assertEquals("iPhone 15 Pro · iOS 18", DeviceNameUtils.sanitize("iPhone 15 Pro · iOS 18"));
        assertEquals("Pixel 8 · Android 15", DeviceNameUtils.sanitize("  Pixel 8 · Android 15  "));
        assertEquals("MacBook-Pro.local", DeviceNameUtils.sanitize("MacBook-Pro.local"));
    }

    @Test
    void stripsControlAndInvisibleCharacters() {
        assertEquals("Chrome on Windows 11",
                DeviceNameUtils.sanitize("Chrome\r\n\t on Windows 11"));
        assertEquals("abc", DeviceNameUtils.sanitize("a\u0000b\u001Fc"));
        // 零宽空格等 FORMAT 字符被移除
        assertEquals("iPhone", DeviceNameUtils.sanitize("i\u200bPhone"));
    }

    @Test
    void truncatesOverlongNames() {
        String longName = "D".repeat(DeviceNameUtils.MAX_LENGTH + 50);
        String sanitized = DeviceNameUtils.sanitize(longName);
        assertEquals(DeviceNameUtils.MAX_LENGTH, sanitized.length());
    }

    @Test
    void allControlCharactersFallBackToUnknownDevice() {
        assertEquals(DeviceNameUtils.UNKNOWN_DEVICE, DeviceNameUtils.sanitize("\u0000\u0001\u0002"));
    }
}
