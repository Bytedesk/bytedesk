package com.bytedesk.ai.voice;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/tts")
public class TtsRestController {

    // 合成输出目录（建议与 FreeSWITCH 可访问的目录一致）
    @Value("${bytedesk.ai.tts.outputDir:/usr/local/freeswitch/recordings}")
    private String outputDir;

    /**
     * 非实时 TTS：输入文本，返回可 playback 的本地文件路径（当前为 mock：只生成空 wav 占位）
     */
    @PostMapping(value = "/synthesize", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> synthesize(
            @RequestParam("text") String text,
            @RequestParam(value = "uuid", required = false) String uuid,
            @RequestParam(value = "turn", required = false, defaultValue = "1") int turn,
            @RequestParam(value = "format", required = false, defaultValue = "wav") String format
    ) throws Exception {
        log.info("[TTS] synthesize uuid={}, turn={}, len={}", uuid, turn, text != null ? text.length() : 0);

        // TODO: 对接真实 TTS，返回 HTTP URL 或本地文件绝对路径
        // 这里生成一个 0 字节的 wav 占位文件，确保 FS 能尝试 playback；上线前请替换为真实音频
        String filename = "ai-bot-tts-" + (uuid != null ? uuid : "nouuid") + "-" + turn + "-" + Instant.now().toEpochMilli() + ".wav";
        Path dir = Paths.get(outputDir);
        Files.createDirectories(dir);
        Path file = dir.resolve(filename);
        if (!Files.exists(file)) {
            Files.createFile(file);
        }
        String localPath = file.toString();
        return ResponseEntity.ok(localPath);
    }
}
