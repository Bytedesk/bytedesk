package com.bytedesk.starter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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
        MultiValueMap<String, String> visitorParams = new LinkedMultiValueMap<>();
        visitorParams.add("orgUid", ORG_UID);
        visitorParams.add("type", TYPE);
        visitorParams.add("sid", AGENT_UID);

        ResponseEntity<VisitorResponse> visitorResponse = restTemplate.postForEntity(
            "/visitor/api/v1/init",
            visitorParams,
            VisitorResponse.class
        );

        // Verify visitor creation response
        assertThat(visitorResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(visitorResponse.getBody()).isNotNull();
        assertThat(visitorResponse.getBody().getUid()).isNotNull();
        assertThat(visitorResponse.getBody().getNickname()).isNotNull();
        assertThat(visitorResponse.getBody().getAvatar()).isNotNull();

        // Step 2: Request thread with visitor info
        MultiValueMap<String, String> threadParams = new LinkedMultiValueMap<>();
        threadParams.add("orgUid", ORG_UID);
        threadParams.add("type", TYPE);
        threadParams.add("sid", AGENT_UID);
        threadParams.add("uid", visitorResponse.getBody().getUid());
        threadParams.add("nickname", visitorResponse.getBody().getNickname());
        threadParams.add("avatar", visitorResponse.getBody().getAvatar());

        ResponseEntity<String> threadResponse = restTemplate.postForEntity(
            "/visitor/api/v1/request",
            threadParams,
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
                MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
                params.add("orgUid", ORG_UID);
                params.add("type", TYPE);
                params.add("sid", AGENT_UID);

                ResponseEntity<VisitorResponse> response = restTemplate.postForEntity(
                    "/visitor/api/v1/init",
                    params,
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
                    MultiValueMap<String, String> visitorParams = new LinkedMultiValueMap<>();
                    visitorParams.add("orgUid", ORG_UID);
                    visitorParams.add("type", TYPE);
                    visitorParams.add("sid", AGENT_UID);

                    ResponseEntity<VisitorResponse> visitorResponse = restTemplate.postForEntity(
                        "/visitor/api/v1/init",
                        visitorParams,
                        VisitorResponse.class
                    );

                    assertThat(visitorResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(visitorResponse.getBody()).isNotNull();
                    VisitorResponse visitor = visitorResponse.getBody();

                    // Step 2: Make multiple thread requests for this visitor
                    for (int j = 0; j < requestsPerVisitor; j++) {
                        MultiValueMap<String, String> threadParams = new LinkedMultiValueMap<>();
                        threadParams.add("orgUid", ORG_UID);
                        threadParams.add("type", TYPE);
                        threadParams.add("sid", AGENT_UID);
                        threadParams.add("uid", visitor.getUid());
                        threadParams.add("nickname", visitor.getNickname());
                        threadParams.add("avatar", visitor.getAvatar());

                        ResponseEntity<String> threadResponse = restTemplate.postForEntity(
                            "/visitor/api/v1/request",
                            threadParams,
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