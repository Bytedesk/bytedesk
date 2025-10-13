package com.bytedesk.call.httapi.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AiAsrClient {

    private final RestTemplate restTemplate;

    @Value("${bytedesk.ai.service.baseUrl:http://127.0.0.1:9003}")
    private String aiBaseUrl;

    /**
     * 非实时 ASR：将 FreeSWITCH 本地录音文件路径传给 AI 服务（由 AI 服务自行拉取/挂载共享目录）
     * 返回识别文本
     */
    public String transcribe(String recordPath, String uuid, int turn) {
        String url = aiBaseUrl + "/api/v1/asr/transcribe";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("filePath", recordPath);
        form.add("uuid", uuid);
        form.add("turn", Integer.toString(turn));

        HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(form, headers);
        String text = restTemplate.postForObject(url, req, String.class);
        return text != null ? text : "";
    }
}
