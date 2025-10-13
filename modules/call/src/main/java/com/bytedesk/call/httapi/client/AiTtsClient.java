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
public class AiTtsClient {

    private final RestTemplate restTemplate;

    @Value("${bytedesk.ai.service.baseUrl:http://127.0.0.1:9003}")
    private String aiBaseUrl;

    /**
     * 非实时 TTS：返回可供 FreeSWITCH playback 的 URL 或本地文件路径
     */
    public String synthesize(String text, String uuid, int turn) {
        String url = aiBaseUrl + "/api/v1/tts/synthesize";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("text", text);
        form.add("uuid", uuid);
        form.add("turn", Integer.toString(turn));
        form.add("format", "wav");

        HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(form, headers);
        String audioUrlOrPath = restTemplate.postForObject(url, req, String.class);
        return audioUrlOrPath;
    }
}
