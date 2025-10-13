package com.bytedesk.ai.voice;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/asr")
public class AsrRestController {

    /**
     * 非实时 ASR：输入为 FreeSWITCH 本地录音文件路径（挂载共享目录或通过 NFS/S3 亦可换成 URL）
     * 返回识别文本（当前返回 mock，后续对接真实 ASR）
     */
    @PostMapping(value = "/transcribe", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> transcribe(
            @RequestParam("filePath") String filePath,
            @RequestParam(value = "uuid", required = false) String uuid,
            @RequestParam(value = "turn", required = false, defaultValue = "1") int turn
    ) {
        log.info("[ASR] transcribe file={}, uuid={}, turn={}", filePath, uuid, turn);
        // TODO: 调用真实 ASR 服务（Whisper/Vosk/Cloud ASR），读取 filePath 内容
        String text = ""; // mock 空，方便 LLM 做兜底
        return ResponseEntity.ok(text);
    }
}
