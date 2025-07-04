package com.bytedesk.starter;

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
// import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.visitor.VisitorResponse;
import com.bytedesk.service.visitor.VisitorRestService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class QueueTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // @MockBean
    private VisitorRestService visitorRestService;

    private static final String ORG_UID = "df_org_uid";
    private static final String AGENT_UID = "df_ag_uid"; // df_ag_uid or df_wg_uid
    private static final String TYPE = "0";// 0: agent, 1: workgroup
    private static final int VISITOR_COUNT = 100;
    private static final int REQUEST_PER_VISITOR = 100;

    private List<VisitorResponse> visitors;

    @BeforeEach
    void setUp() {
        // 初始化访客列表
        visitors = new ArrayList<>();
        for (int i = 0; i < VISITOR_COUNT; i++) {
            VisitorResponse visitor = VisitorResponse.builder()
                .nickname("Visitor " + i)
                .avatar(AvatarConsts.getDefaultVisitorAvatarUrl())
                .build();
            visitor.setUid("visitor_" + i);
            visitors.add(visitor);
        }

        // Mock visitorService的响应
        when(visitorRestService.initVisitor(any())).thenAnswer(invocation -> {
            int index = (int)(Math.random() * VISITOR_COUNT);
            return visitors.get(index);
        });
    }

    @Test
    void testMassVisitorRequests() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // 创建访客
        for (int i = 0; i < VISITOR_COUNT; i++) {
            final int visitorIndex = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    // 1. 初始化访客
                    VisitorRequest initRequest = VisitorRequest.builder()
                        .sid(AGENT_UID)
                        .build();
                    initRequest.setType(TYPE);
                    initRequest.setOrgUid(ORG_UID);

                    String initResult = mockMvc.perform(post("/visitor/api/v1/init")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(initRequest)))
                            .andExpect(status().isOk())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();

                    // 解析返回的访客信息
                    JsonResult<?> initResponse = objectMapper.readValue(initResult, JsonResult.class);
                    VisitorResponse visitor = objectMapper.convertValue(initResponse.getData(), VisitorResponse.class);

                    // 2. 请求会话
                    for (int j = 0; j < REQUEST_PER_VISITOR; j++) {
                        VisitorRequest threadRequest = VisitorRequest.builder()
                            .sid(AGENT_UID)
                            .nickname(visitor.getNickname())
                            .avatar(visitor.getAvatar())
                            .build();
                        threadRequest.setOrgUid(ORG_UID);
                        threadRequest.setType(TYPE);
                        threadRequest.setUid(visitor.getUid());

                        mockMvc.perform(post("/visitor/api/v1/request")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(threadRequest)))
                                .andExpect(status().isOk());
                    }
                } catch (Exception e) {
                    log.error("访客请求失败: {}", visitorIndex, e);
                }
            }, executor);
            futures.add(future);
        }

        // 等待所有请求完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();

        // 验证调用次数
        verify(visitorRestService, times(VISITOR_COUNT)).initVisitor(any());
        verify(visitorRestService, times(VISITOR_COUNT * REQUEST_PER_VISITOR)).requestThread(any());
    }

    @Test
    void testConcurrentVisitorRequests() throws Exception {
        int concurrentVisitors = 10;
        int concurrentRequests = 10;
        
        ExecutorService executor = Executors.newFixedThreadPool(concurrentVisitors);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < concurrentVisitors; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    // 1. 初始化访客
                    VisitorRequest initRequest = VisitorRequest.builder()
                        .sid(AGENT_UID)
                        .build();
                    initRequest.setType(TYPE);
                    initRequest.setOrgUid(ORG_UID);

                    String initResult = mockMvc.perform(post("/visitor/api/v1/init")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(initRequest)))
                            .andExpect(status().isOk())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();

                    // 解析返回的访客信息
                    JsonResult<?> initResponse = objectMapper.readValue(initResult, JsonResult.class);
                    VisitorResponse visitor = objectMapper.convertValue(initResponse.getData(), VisitorResponse.class);

                    // 2. 并发请求会话
                    for (int j = 0; j < concurrentRequests; j++) {
                        VisitorRequest threadRequest = VisitorRequest.builder()
                            .sid(AGENT_UID)
                            .nickname(visitor.getNickname())
                            .avatar(visitor.getAvatar())
                            .build();
                        threadRequest.setType(TYPE);
                        threadRequest.setOrgUid(ORG_UID);
                        threadRequest.setUid(visitor.getUid());

                        mockMvc.perform(post("/visitor/api/v1/request")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(threadRequest)))
                                .andExpect(status().isOk());
                    }
                } catch (Exception e) {
                    log.error("并发请求失败", e);
                }
            }, executor);
            futures.add(future);
        }

        // 等待所有并发请求完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();

        // 验证调用次数
        verify(visitorRestService, times(concurrentVisitors)).initVisitor(any());
        verify(visitorRestService, times(concurrentVisitors * concurrentRequests)).requestThread(any());
    }
} 