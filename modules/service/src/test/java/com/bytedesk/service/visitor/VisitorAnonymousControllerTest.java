package com.bytedesk.service.visitor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.bytedesk.core.message.MessageProtobuf;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class VisitorAnonymousControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VisitorRestService visitorRestService;

    // orgUid=df_org_uid
    // type=0 or 1
    // sid=df_ag_uid or df_wg_uid
    private List<String> visitorUids;
    private static final int VISITOR_COUNT = 100;
    private static final int REQUEST_PER_VISITOR = 100;

    @BeforeEach
    void setUp() {
        // 初始化访客ID列表
        visitorUids = new ArrayList<>();
        for (int i = 0; i < VISITOR_COUNT; i++) {
            visitorUids.add("visitor_" + i);
        }

        // Mock visitorService的响应
        when(visitorRestService.create(any())).thenReturn(VisitorResponse.builder().build());
        when(visitorRestService.requestThread(any())).thenReturn(MessageProtobuf.builder().build());
    }

    @Test
    void testMassVisitorRequests() throws Exception {
        // 1. 首先创建100个访客
        log.info("开始创建{}个访客", VISITOR_COUNT);
        creates();

        // 2. 每个访客发起100次请求
        log.info("开始模拟每个访客发起{}次请求", REQUEST_PER_VISITOR);
        simulateVisitorRequests();

        // 3. 验证调用次数
        verify(visitorRestService, times(VISITOR_COUNT)).create(any());
        verify(visitorRestService, times(VISITOR_COUNT * REQUEST_PER_VISITOR)).requestThread(any());
    }

    private void creates() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (String visitorUid : visitorUids) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    VisitorRequest request = new VisitorRequest();
                    request.setUid(visitorUid);
                    request.setNickname("Visitor " + visitorUid);
                    request.setAvatar("https://example.com/avatar.png");

                    mockMvc.perform(post("/visitor/anonymous/init")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                            .andExpect(status().isOk());
                } catch (Exception e) {
                    log.error("创建访客失败: {}", visitorUid, e);
                }
            }, executor);
            futures.add(future);
        }

        // 等待所有访客创建完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();
    }

    private void simulateVisitorRequests() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(20);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (String visitorUid : visitorUids) {
            for (int i = 0; i < REQUEST_PER_VISITOR; i++) {
                final int requestNum = i;
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        VisitorRequest request = new VisitorRequest();
                        request.setUid(visitorUid);
                        // request.setWorkGroupWid("workgroup_1");
                        // request.setRequestNo("request_" + visitorUid + "_" + requestNum);

                        mockMvc.perform(post("/visitor/anonymous/requestThread")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk());
                    } catch (Exception e) {
                        log.error("访客请求失败: {} - 请求 {}", visitorUid, requestNum, e);
                    }
                }, executor);
                futures.add(future);
            }
        }

        // 等待所有请求完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();
    }

    @Test
    void testConcurrentVisitorRequests() throws Exception {
        // 创建10个访客同时发起10次请求的并发测试
        int concurrentVisitors = 10;
        int concurrentRequests = 10;
        
        ExecutorService executor = Executors.newFixedThreadPool(concurrentVisitors);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < concurrentVisitors; i++) {
            final String visitorUid = "concurrent_visitor_" + i;
            
            // 先创建访客
            VisitorRequest initRequest = new VisitorRequest();
            initRequest.setUid(visitorUid);
            initRequest.setNickname("Concurrent Visitor " + i);
            
            mockMvc.perform(post("/visitor/anonymous/init")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(initRequest)))
                    .andExpect(status().isOk());

            // 然后并发发起请求
            for (int j = 0; j < concurrentRequests; j++) {
                final int requestNum = j;
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        VisitorRequest request = new VisitorRequest();
                        request.setUid(visitorUid);
                        // request.setWorkGroupWid("workgroup_1");
                        // request.setRequestNo("concurrent_request_" + visitorUid + "_" + requestNum);

                        mockMvc.perform(post("/visitor/anonymous/requestThread")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk());
                    } catch (Exception e) {
                        log.error("并发请求失败: {} - 请求 {}", visitorUid, requestNum, e);
                    }
                }, executor);
                futures.add(future);
            }
        }

        // 等待所有并发请求完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();

        // 验证调用次数
        verify(visitorRestService, times(concurrentVisitors)).create(any());
        verify(visitorRestService, times(concurrentVisitors * concurrentRequests)).requestThread(any());
    }
} 