package com.bytedesk.service.robot_to_agent_settings;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageResponse;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.message.enums.MessageTypeEnum;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.enums.ThreadProcessStatusEnum;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.thread.enums.ThreadTransferStatusEnum;
import com.bytedesk.service.robot_to_agent_settings.event.VisitorRobotMessageEvent;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.visitor.VisitorRestService;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupRestService;
import com.bytedesk.service.workgroup_settings.WorkgroupSettingsEntity;

/**
 * 关键词转人工监听器修复（2026-09-14 规划 P1-2/P1-3）的防回归测试：
 * - Web 渠道转人工不再透传源会话 extra（thread_user 超长根因）
 * - visitorUid 口径与首次建会话同源（thread.user 中 VisitorProtobuf.visitorUid）
 * - 社交渠道保留 extra 透传
 * - 转人工失败后回滚：QUEUING→ROBOTING、agentReplied 标记清理、挂起标记清理
 */
@ExtendWith(MockitoExtension.class)
class RobotToAgentKeywordListenerTest {

    private static final String THREAD_UID = "thread-1";
    private static final String WORKGROUP_UID = "wg-1";
    private static final String ORG_UID = "org-1";
    private static final String INTERNAL_VISITOR_UID = "1001";
    private static final String EXTERNAL_VISITOR_UID = "ext-visitor-9";
    private static final String TOPIC = "org/workgroup/" + WORKGROUP_UID + "/" + EXTERNAL_VISITOR_UID;
    private static final String HUGE_EXTRA = "{\"preForm\":\"" + "x".repeat(40 * 1024) + "\"}";

    @Mock
    private ThreadRestService threadRestService;

    @Mock
    private MessageRestService messageRestService;

    @Mock
    private WorkgroupRestService workgroupRestService;

    @Mock
    private VisitorRestService visitorRestService;

    @Test
    void webChannelTransferShouldNotCarrySourceExtraAndUseExternalVisitorUid() {
        RobotToAgentKeywordListener listener = buildListener();
        ThreadEntity thread = buildWorkgroupThread("WEB");
        when(threadRestService.findByUid(THREAD_UID)).thenReturn(Optional.of(thread));
        when(workgroupRestService.findByUid(WORKGROUP_UID)).thenReturn(Optional.of(buildWorkgroup()));
        // 消息实体尚未落库，走挂起标记路径
        when(messageRestService.findByUid("msg-1")).thenReturn(Optional.empty());

        listener.onVisitorRobotMessageEvent(new VisitorRobotMessageEvent(this,
                buildMessageJson("msg-1", "转人工")));

        ArgumentCaptor<VisitorRequest> captor = ArgumentCaptor.forClass(VisitorRequest.class);
        verify(visitorRestService).requestThread(captor.capture());
        VisitorRequest request = captor.getValue();

        // Web 渠道：不透传源会话 extra
        org.junit.jupiter.api.Assertions.assertTrue(request.getExtra() == null
                || request.getExtra().isEmpty()
                || "{}".equals(request.getExtra()));
        // visitorUid 与首次建会话口径同源（外部稳定 ID），而非内部 uid
        org.junit.jupiter.api.Assertions.assertEquals(EXTERNAL_VISITOR_UID, request.getVisitorUid());
        // 身份字段仍为内部 uid
        org.junit.jupiter.api.Assertions.assertEquals(INTERNAL_VISITOR_UID, request.getUid());
    }

    @Test
    void socialChannelTransferShouldKeepExtraPassthrough() {
        RobotToAgentKeywordListener listener = buildListener();
        ThreadEntity thread = buildWorkgroupThread("WECHAT");
        when(threadRestService.findByUid(THREAD_UID)).thenReturn(Optional.of(thread));
        when(workgroupRestService.findByUid(WORKGROUP_UID)).thenReturn(Optional.of(buildWorkgroup()));
        when(messageRestService.findByUid("msg-1")).thenReturn(Optional.empty());

        listener.onVisitorRobotMessageEvent(new VisitorRobotMessageEvent(this,
                buildMessageJson("msg-1", "转人工")));

        ArgumentCaptor<VisitorRequest> captor = ArgumentCaptor.forClass(VisitorRequest.class);
        verify(visitorRestService).requestThread(captor.capture());
        // 社交渠道：保留 extra 透传（下游 buildWorkgroupExtra 直接使用请求 extra）
        org.junit.jupiter.api.Assertions.assertEquals(HUGE_EXTRA, captor.getValue().getExtra());
    }

    @Test
    void failedTransferShouldRollbackThreadStateAndReplyMark() {
        RobotToAgentKeywordListener listener = buildListener();
        ThreadEntity thread = buildWorkgroupThread("WEB");
        when(threadRestService.findByUid(THREAD_UID))
                .thenReturn(Optional.of(thread))
                .thenReturn(Optional.of(thread)); // 第二次：回滚时重新加载
        when(workgroupRestService.findByUid(WORKGROUP_UID)).thenReturn(Optional.of(buildWorkgroup()));
        when(messageRestService.findByUid("msg-1"))
                .thenReturn(Optional.empty())     // 第一次：挂起标记注册
                .thenReturn(Optional.of(buildRepliedMessage("msg-1"))); // 第二次：回滚清理
        // requestThread 抛异常触发回滚
        doThrow(new RuntimeException("Data truncation"))
                .when(visitorRestService).requestThread(any(VisitorRequest.class));

        listener.onVisitorRobotMessageEvent(new VisitorRobotMessageEvent(this,
                buildMessageJson("msg-1", "转人工")));

        // 回滚：QUEUING → ROBOTING，transferStatus → NONE（第一次 save 为 markThreadTransferPending，第二次为回滚）
        ArgumentCaptor<ThreadEntity> threadCaptor = ArgumentCaptor.forClass(ThreadEntity.class);
        verify(threadRestService, org.mockito.Mockito.times(2)).save(threadCaptor.capture());
        ThreadEntity rolledBack = threadCaptor.getValue();
        org.junit.jupiter.api.Assertions.assertEquals(
                ThreadProcessStatusEnum.ROBOTING.name(), rolledBack.getStatus());
        org.junit.jupiter.api.Assertions.assertEquals(
                ThreadTransferStatusEnum.NONE.name(), rolledBack.getTransferStatus());
        // 回滚：agentReplied 标记清空
        ArgumentCaptor<MessageEntity> messageCaptor = ArgumentCaptor.forClass(MessageEntity.class);
        verify(messageRestService).save(messageCaptor.capture());
        MessageEntity rolledBackMessage = messageCaptor.getValue();
        org.junit.jupiter.api.Assertions.assertEquals(Boolean.FALSE, rolledBackMessage.getAgentReplied());
        org.junit.jupiter.api.Assertions.assertNull(rolledBackMessage.getAgentRepliedAt());
        org.junit.jupiter.api.Assertions.assertNull(rolledBackMessage.getAgentRepliedByUid());
    }

    @Test
    void failedTransferShouldNotRollbackWhenAgentAlreadyAssigned() {
        RobotToAgentKeywordListener listener = buildListener();
        // 转人工前置检查用的会话：无客服分配；
        ThreadEntity thread = buildWorkgroupThread("WEB");
        // 回滚时重新加载到的会话：已被其他流程推进（分配了客服），不应被回滚覆盖
        ThreadEntity advancedThread = buildWorkgroupThread("WEB");
        advancedThread.setQueuing();
        advancedThread.setAgent(UserProtobuf.builder().uid("agent-9").build().toJson());
        when(threadRestService.findByUid(THREAD_UID))
                .thenReturn(Optional.of(thread))
                .thenReturn(Optional.of(advancedThread));
        when(workgroupRestService.findByUid(WORKGROUP_UID)).thenReturn(Optional.of(buildWorkgroup()));
        when(messageRestService.findByUid("msg-1"))
                .thenReturn(Optional.empty())                            // 第一次：挂起标记注册
                .thenReturn(Optional.of(buildRepliedMessage("msg-1"))); // 第二次：回滚清理
        doThrow(new RuntimeException("Data truncation"))
                .when(visitorRestService).requestThread(any(VisitorRequest.class));

        listener.onVisitorRobotMessageEvent(new VisitorRobotMessageEvent(this,
                buildMessageJson("msg-1", "转人工")));

        // 仅 markThreadTransferPending 的一次 save（作用于前置检查返回的会话对象），回滚不再 save
        verify(threadRestService, org.mockito.Mockito.times(1)).save(any(ThreadEntity.class));
        // 但消息 agentReplied 标记仍应回滚
        verify(messageRestService).save(any(MessageEntity.class));
    }

    private RobotToAgentKeywordListener buildListener() {
        return new RobotToAgentKeywordListener(
                threadRestService,
                messageRestService,
                workgroupRestService,
                visitorRestService);
    }

    /**
     * 构造工作组机器人会话：
     * - thread.user 中 VisitorProtobuf 的 visitorUid 为外部稳定 ID（EXTRA 模拟历史污染：内含超大设置快照）
     * - thread.extra 为超大设置快照（历史 bug 场景）
     * - topic 后缀为外部 visitorUid（与 resolveVisitorUidForThreadTopic 首次建会话口径一致）
     */
    private ThreadEntity buildWorkgroupThread(String channel) {
        return ThreadEntity.builder()
                .uid(THREAD_UID)
                .topic(TOPIC)
                .type(ThreadTypeEnum.WORKGROUP.name())
                .status(ThreadProcessStatusEnum.ROBOTING.name())
                .orgUid(ORG_UID)
                .channel(channel)
                .user("{\"uid\":\"" + INTERNAL_VISITOR_UID + "\",\"visitorUid\":\"" + EXTERNAL_VISITOR_UID
                        + "\",\"nickname\":\"访客\",\"extra\":\"\"}")
                // resolveWorkgroup 从 thread.workgroup JSON 解析工作组 uid
                .workgroup("{\"uid\":\"" + WORKGROUP_UID + "\"}")
                .extra(HUGE_EXTRA)
                .build();
    }

    private WorkgroupEntity buildWorkgroup() {
        RobotToAgentSettingsEntity settings = RobotToAgentSettingsEntity.builder()
                .enabled(Boolean.TRUE)
                .keywordTriggerEnabled(Boolean.TRUE)
                .triggerKeywords(List.of("转人工"))
                .build();
        WorkgroupSettingsEntity workgroupSettings = WorkgroupSettingsEntity.builder()
                .robotToAgentSettings(settings)
                .build();
        return WorkgroupEntity.builder()
                .uid(WORKGROUP_UID)
                .orgUid(ORG_UID)
                .nickname("WG")
                .settings(workgroupSettings)
                .build();
    }

    private MessageEntity buildRepliedMessage(String uid) {
        MessageEntity message = MessageEntity.builder()
                .uid(uid)
                .build();
        message.setAgentReplied(Boolean.TRUE);
        message.setAgentRepliedAt(java.time.ZonedDateTime.now());
        message.setAgentRepliedByUid("robot-1");
        return message;
    }

    /** 访客 SSE 上行消息 JSON（MessageResponse），thread.user.uid 为内部 uid */
    private String buildMessageJson(String messageUid, String content) {
        MessageResponse response = MessageResponse.builder()
                .uid(messageUid)
                .type(MessageTypeEnum.TEXT.name())
                .content(content)
                .thread(com.bytedesk.core.thread.ThreadResponse.builder()
                        .uid(THREAD_UID)
                        .build())
                .user(UserProtobuf.builder()
                        .uid(INTERNAL_VISITOR_UID)
                        .nickname("访客")
                        .type(UserTypeEnum.VISITOR.name())
                        .build())
                .build();
        return JSON.toJSONString(response);
    }
}
