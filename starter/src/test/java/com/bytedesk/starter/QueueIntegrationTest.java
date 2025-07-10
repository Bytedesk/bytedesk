package com.bytedesk.starter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.service.visitor.VisitorRequest;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Collections;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "bytedesk.socket.enabled=false", // 禁用MQTT服务器
        "server.port=0" // 使用随机端口
})
public class QueueIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String ORG_UID = "df_org_uid";
    private static final String AGENT_UID = "df_ag_uid"; // df_ag_uid or df_wg_uid
    private static final String TYPE = "0";// 0: agent, 1: workgroup

    @Test
    public void testVisitorFlowWithRealServer() {
        // Step 1: Create visitor
        VisitorRequest initRequest = VisitorRequest.builder()
                .sid(AGENT_UID)
                .nickname("Test Visitor")
                .build();
        initRequest.setUid("visitor_test_uid");
        initRequest.setNickname("Visitor Test");
        initRequest.setOrgUid(ORG_UID);
        initRequest.setType(TYPE);
        initRequest.setChannel(ChannelEnum.TEST.name());
        initRequest.setBrowser("{\"name\":\"Chrome\",\"version\":\"131.0.0.0\",\"major\":\"131\"}");
        initRequest.setDevice("{\"name\":\"iPhone\",\"version\":\"13.5\"}");
        initRequest.setOs("{\"name\":\"Mac OS\",\"version\":\"10.15.7\"}");
        initRequest.setReferrer("http://127.0.0.1:9003/dev");

        // 创建HTTP Headers并添加必要的信息
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0 (Test Browser)");
        headers.set("X-Real-IP", "127.0.0.1");
        headers.set("X-Forwarded-For", "127.0.0.1");
        headers.set("Referer", "https://test.bytedesk.com");
        headers.setContentType(MediaType.APPLICATION_JSON); // 添加Content-Type
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON)); // 添加Accept

        // 使用HttpEntity包装请求
        HttpEntity<VisitorRequest> requestEntity = new HttpEntity<>(initRequest, headers);

        // 然后再进行实际的测试请求
        ResponseEntity<JsonResult<UserProtobuf>> visitorResponse = restTemplate.exchange(
                "/visitor/api/v1/init",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<JsonResult<UserProtobuf>>() {});

        // 打印原始响应内容
        System.out.println("Response Status: " + visitorResponse.getStatusCode());
        System.out.println("Response Headers: " + visitorResponse.getHeaders());
        UserProtobuf visitor = visitorResponse.getBody().getData();
        System.out.println("Response Body: " + visitor);

        // Verify visitor creation response
        assertThat(visitorResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(visitorResponse.getBody()).as("Response body should not be null").isNotNull();
        assertThat(visitor.getUid()).isNotNull();
        assertThat(visitor.getNickname()).isNotNull();
        assertThat(visitor.getAvatar()).isNotNull();

        // Step 2: Request thread with visitor info
        VisitorRequest threadRequest = VisitorRequest.builder()
                .sid(AGENT_UID)
                .nickname(visitor.getNickname())
                .avatar(visitor.getAvatar())
                .build();
        threadRequest.setUid(visitor.getUid());
        threadRequest.setOrgUid(ORG_UID);
        threadRequest.setType(TYPE);
        threadRequest.setChannel(ChannelEnum.TEST.name());
        threadRequest.setBrowser("{\"name\":\"Chrome\",\"version\":\"131.0.0.0\",\"major\":\"131\"}");
        threadRequest.setDevice("{\"name\":\"iPhone\",\"version\":\"13.5\"}");
        threadRequest.setOs("{\"name\":\"Mac OS\",\"version\":\"10.15.7\"}");
        threadRequest.setReferrer("http://127.0.0.1:9003/dev");

        ResponseEntity<String> threadResponse = restTemplate.postForEntity(
                "/visitor/api/v1/thread",
                threadRequest,
                String.class);

        // Verify thread request response
        assertThat(threadResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(threadResponse.getBody()).isNotNull();
    }

    @Test
    public void testMultipleVisitorsWithMultipleRequests() throws InterruptedException {
        int visitorCount = 10;
        int requestsPerVisitor = 1;

        // Create a thread pool for managing concurrent visitors
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        CountDownLatch completionLatch = new CountDownLatch(visitorCount);

        // Create visitors and their requests
        for (int i = 0; i < visitorCount; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    // Step 1: Create visitor
                    VisitorRequest initRequest = VisitorRequest.builder()
                            .sid(AGENT_UID)
                            .build();
                    initRequest.setUid("visitor_test_uid_" + index);
                    initRequest.setNickname("Visitor Test " + index);
                    initRequest.setOrgUid(ORG_UID);
                    initRequest.setType(TYPE);
                    initRequest.setChannel(ChannelEnum.TEST.name());
                    initRequest.setBrowser("{\"name\":\"Chrome\",\"version\":\"131.0.0.0\",\"major\":\"131\"}");
                    initRequest.setDevice("{\"name\":\"iPhone\",\"version\":\"13.5\"}");
                    initRequest.setOs("{\"name\":\"Mac OS\",\"version\":\"10.15.7\"}");
                    initRequest.setReferrer("http://127.0.0.1:9003/dev");

                    // 创建HTTP Headers并添加必要的信息
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("User-Agent", "Mozilla/5.0 (Test Browser)");
                    headers.set("X-Real-IP", "127.0.0.1");
                    headers.set("X-Forwarded-For", "127.0.0.1");
                    headers.set("Referer", "https://test.bytedesk.com");

                    // 使用HttpEntity包装请求
                    HttpEntity<VisitorRequest> requestEntity = new HttpEntity<>(initRequest, headers);

                    ResponseEntity<JsonResult<UserProtobuf>> visitorResponse = restTemplate.exchange(
                            "/visitor/api/v1/init",
                            HttpMethod.POST,
                            requestEntity,
                            new ParameterizedTypeReference<JsonResult<UserProtobuf>>() {});

                    assertThat(visitorResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(visitorResponse.getBody()).isNotNull();
                    UserProtobuf visitor = visitorResponse.getBody().getData();

                    // Step 2: Make multiple thread requests for this visitor
                    for (int j = 0; j < requestsPerVisitor; j++) {
                        VisitorRequest threadRequest = VisitorRequest.builder()
                                .sid(AGENT_UID)
                                .nickname(visitor.getNickname())
                                .avatar(visitor.getAvatar())
                                .build();
                        threadRequest.setUid(visitor.getUid());
                        threadRequest.setOrgUid(ORG_UID);
                        threadRequest.setType(TYPE);
                        threadRequest.setChannel(ChannelEnum.TEST.name());
                        threadRequest.setBrowser("{\"name\":\"Chrome\",\"version\":\"131.0.0.0\",\"major\":\"131\"}");
                        threadRequest.setDevice("{\"name\":\"iPhone\",\"version\":\"13.5\"}");
                        threadRequest.setOs("{\"name\":\"Mac OS\",\"version\":\"10.15.7\"}");
                        threadRequest.setReferrer("http://127.0.0.1:9003/dev");

                        ResponseEntity<String> threadResponse = restTemplate.postForEntity(
                                "/visitor/api/v1/thread",
                                threadRequest,
                                String.class);

                        assertThat(threadResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                        assertThat(threadResponse.getBody()).isNotNull();
                    }
                } finally {
                    completionLatch.countDown();
                }
            });
        }

        // Wait for all visitors to complete their requests
        boolean completed = completionLatch.await(5, TimeUnit.MINUTES);
        executorService.shutdown();

        assertThat(completed).isTrue()
                .withFailMessage("Not all visitors completed their requests within the timeout period");
    }

}