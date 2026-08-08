package com.bytedesk.ai.zhipuai;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.bytedesk.ai.providers.zhipuai.ZhipuaiConfig;

class ZhipuaiSdkMigrationTest {

    private static final Path MAIN_SOURCE_DIR = Path.of("src/main/java/com/bytedesk/ai/zhipuai");

    @Test
    void zhipuaiMainSourcesShouldNotReferenceLegacyOapiSdk() throws IOException {
        try (Stream<Path> paths = Files.walk(MAIN_SOURCE_DIR)) {
            List<String> legacyReferences = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .filter(path -> !path.getFileName().toString().equals("ZhipuaiService.java"))
                    .flatMap(this::readLinesSafely)
                    .filter(line -> line.contains("com.zhipu.oapi") || line.contains("zhipu.oapi"))
                    .collect(Collectors.toList());

            assertTrue(legacyReferences.isEmpty(), () -> "Legacy oapi references still exist: " + legacyReferences);
        }
    }

    @Test
    void zhipuaiClientBeanShouldGracefullyHandleSdkRuntimeIncompatibility() {
        ZhipuaiConfig config = new ZhipuaiConfig();
        ReflectionTestUtils.setField(config, "apiKey", "sk-test-compatibility-check");
        ReflectionTestUtils.setField(config, "connectionTimeout", 30);
        ReflectionTestUtils.setField(config, "readTimeout", 10);
        ReflectionTestUtils.setField(config, "writeTimeout", 10);
        ReflectionTestUtils.setField(config, "pingInterval", 10);
        ReflectionTestUtils.setField(config, "maxIdleConnections", 8);
        ReflectionTestUtils.setField(config, "keepAliveDuration", 1);

        Object client = assertDoesNotThrow(config::zhipuAiClient);
        assertNull(client);
    }

    private Stream<String> readLinesSafely(Path path) {
        try {
            return Files.readAllLines(path).stream().map(line -> path.getFileName() + ": " + line.trim());
        } catch (IOException exception) {
            throw new RuntimeException("Failed to read " + path, exception);
        }
    }
}