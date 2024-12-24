package com.bytedesk.starter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.visitor.VisitorResponse;
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
public class VisitorAnonymousControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String ORG_UID = "df_org_uid";
    private static final String AGENT_UID = "df_ag_uid";
    private static final String TYPE = "0";

    @Test
    public void testVisitorFlowWithRealServer() {
        // Step 1: Create visitor
        VisitorRequest initRequest = VisitorRequest.builder()
                .sid(AGENT_UID)
                .nickname("Test Visitor")
                .build();
        initRequest.setUid("visitor_test_uid");
        initRequest.setOrgUid(ORG_UID);
        initRequest.setType(TYPE);
        initRequest.setClient(ClientEnum.TEST.name());

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
        ResponseEntity<VisitorResponse> visitorResponse = restTemplate.exchange(
                "/visitor/api/v1/init",
                HttpMethod.GET,
                requestEntity,
                VisitorResponse.class);

        // 打印原始响应内容
        System.out.println("Response Status: " + visitorResponse.getStatusCode());
        System.out.println("Response Headers: " + visitorResponse.getHeaders());
        System.out.println("Response Body: " + visitorResponse.getBody());

        // Verify visitor creation response
        assertThat(visitorResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(visitorResponse.getBody()).as("Response body should not be null").isNotNull();
        assertThat(visitorResponse.getBody().getUid()).isNotNull();
        assertThat(visitorResponse.getBody().getNickname()).isNotNull();
        assertThat(visitorResponse.getBody().getAvatar()).isNotNull();

        // Step 2: Request thread with visitor info
        VisitorRequest threadRequest = VisitorRequest.builder()
                .sid(AGENT_UID)
                .nickname(visitorResponse.getBody().getNickname())
                .avatar(visitorResponse.getBody().getAvatar())
                .build();
        threadRequest.setUid(visitorResponse.getBody().getUid());
        threadRequest.setOrgUid(ORG_UID);
        threadRequest.setType(TYPE);
        threadRequest.setClient(ClientEnum.TEST.name());

        ResponseEntity<String> threadResponse = restTemplate.postForEntity(
                "/visitor/api/v1/request",
                threadRequest,
                String.class);

        // Verify thread request response
        assertThat(threadResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(threadResponse.getBody()).isNotNull();
    }

    @Test
    public void testConcurrentVisitorRequests() throws InterruptedException {
        int concurrentRequests = 10;
        Thread[] threads = new Thread[concurrentRequests];

        for (int i = 0; i < concurrentRequests; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                VisitorRequest initRequest = VisitorRequest.builder()
                        .sid(AGENT_UID)
                        .build();
                initRequest.setUid("visitor_test_uid_" + index);
                initRequest.setOrgUid(ORG_UID);
                initRequest.setType(TYPE);
                initRequest.setClient(ClientEnum.TEST.name());

                // 创建HTTP Headers并添加必要的信息
                HttpHeaders headers = new HttpHeaders();
                headers.set("User-Agent", "Mozilla/5.0 (Test Browser)");
                headers.set("X-Real-IP", "127.0.0.1");
                headers.set("X-Forwarded-For", "127.0.0.1");
                headers.set("Referer", "https://test.bytedesk.com");

                // 使用HttpEntity包装请求
                HttpEntity<VisitorRequest> requestEntity = new HttpEntity<>(initRequest, headers);

                ResponseEntity<VisitorResponse> visitorResponse = restTemplate.exchange(
                        "/visitor/api/v1/init",
                        HttpMethod.GET,
                        requestEntity,
                        VisitorResponse.class);

                assertThat(visitorResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(visitorResponse.getBody()).isNotNull();
                assertThat(visitorResponse.getBody().getUid()).isNotNull();
            });
            threads[i].start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }
    }

    @Test
    public void testMultipleVisitorsWithMultipleRequests() throws InterruptedException {
        int visitorCount = 100;
        int requestsPerVisitor = 100;

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
                    initRequest.setOrgUid(ORG_UID);
                    initRequest.setType(TYPE);
                    initRequest.setClient(ClientEnum.TEST.name());

                    // 创建HTTP Headers并添加必要的信息
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("User-Agent", "Mozilla/5.0 (Test Browser)");
                    headers.set("X-Real-IP", "127.0.0.1");
                    headers.set("X-Forwarded-For", "127.0.0.1");
                    headers.set("Referer", "https://test.bytedesk.com");

                    // 使用HttpEntity包装请求
                    HttpEntity<VisitorRequest> requestEntity = new HttpEntity<>(initRequest, headers);

                    ResponseEntity<VisitorResponse> visitorResponse = restTemplate.exchange(
                            "/visitor/api/v1/init",
                            HttpMethod.GET,
                            requestEntity,
                            VisitorResponse.class);

                    assertThat(visitorResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(visitorResponse.getBody()).isNotNull();
                    VisitorResponse visitor = visitorResponse.getBody();

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
                        threadRequest.setClient(ClientEnum.TEST.name());

                        ResponseEntity<String> threadResponse = restTemplate.postForEntity(
                                "/visitor/api/v1/request",
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