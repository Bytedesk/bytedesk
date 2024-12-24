package com.bytedesk.starter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.visitor.VisitorResponse;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
properties = {
    "bytedesk.socket.enabled=false",  // 禁用MQTT服务器
    "server.port=0"  // 使用随机端口
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
            .build();
        initRequest.setOrgUid(ORG_UID);
        initRequest.setType(TYPE);

        ResponseEntity<VisitorResponse> visitorResponse = restTemplate.postForEntity(
            "/visitor/api/v1/init",
            initRequest,
            VisitorResponse.class
        );

        // Verify visitor creation response
        assertThat(visitorResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(visitorResponse.getBody()).isNotNull();
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
        ResponseEntity<String> threadResponse = restTemplate.postForEntity(
            "/visitor/api/v1/request",
            threadRequest,
            String.class
        );

        // Verify thread request response
        assertThat(threadResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(threadResponse.getBody()).isNotNull();
    }

    @Test
    public void testConcurrentVisitorRequests() throws InterruptedException {
        int concurrentRequests = 10;
        Thread[] threads = new Thread[concurrentRequests];
        
        for (int i = 0; i < concurrentRequests; i++) {
            threads[i] = new Thread(() -> {
                VisitorRequest initRequest = VisitorRequest.builder()
                    .sid(AGENT_UID)
                    .build();
                initRequest.setOrgUid(ORG_UID);
                initRequest.setType(TYPE);

                ResponseEntity<VisitorResponse> response = restTemplate.postForEntity(
                    "/visitor/api/v1/init",
                    initRequest,
                    VisitorResponse.class
                );

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(response.getBody()).isNotNull();
                assertThat(response.getBody().getUid()).isNotNull();
            });
            threads[i].start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }
    }

    @Test
    public void testVisitorInitWithInvalidParams() {
        // Test with missing required parameters
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("type", TYPE); // Missing orgUid and sid

        ResponseEntity<VisitorResponse> response = restTemplate.postForEntity(
            "/visible/api/v1/init",
            params,
            VisitorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
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
            executorService.submit(() -> {
                try {
                    // Step 1: Create visitor
                    VisitorRequest initRequest = VisitorRequest.builder()
                        .sid(AGENT_UID)
                        .build();
                    initRequest.setOrgUid(ORG_UID);
                    initRequest.setType(TYPE);

                    ResponseEntity<VisitorResponse> visitorResponse = restTemplate.postForEntity(
                        "/visitor/api/v1/init",
                        initRequest,
                        VisitorResponse.class
                    );

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

                        ResponseEntity<String> threadResponse = restTemplate.postForEntity(
                            "/visitor/api/v1/request",
                            threadRequest,
                            String.class
                        );

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