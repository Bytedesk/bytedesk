package com.bytedesk.ai.robot.group;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.bytedesk.core.group.GroupEntity;
import com.bytedesk.core.group.GroupRestService;
import com.bytedesk.core.message.MessageExtra;
import com.bytedesk.core.message.MessageMention;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.enums.MessageTypeEnum;
import com.bytedesk.core.message.event.MessageJsonEvent;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadProtobuf;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.uid.UidUtils;

/**
 * 群机器人任务登记监听器单测：
 * 触发条件（GROUP+TEXT+人类+ROBOT mention+机器人在群内）、
 * 忽略条件（机器人消息/非群/无mention/伪造机器人uid）、幂等。
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GroupRobotMessageListenerTest {

    @Mock
    private GroupRobotExecutionRepository executionRepository;

    @Mock
    private GroupRestService groupRestService;

    @Mock
    private UidUtils uidUtils;

    private GroupRobotMessageListener listener;

    private GroupEntity group;

    @BeforeEach
    void setUp() {
        listener = new GroupRobotMessageListener(executionRepository, groupRestService, uidUtils);
        group = GroupEntity.builder().build();
        group.setUid("group-1");
        group.setOrgUid("org-1");
        group.addRobot("robot-1");
        lenient().when(groupRestService.findByUid("group-1")).thenReturn(Optional.of(group));
        lenient().when(uidUtils.getUid()).thenReturn("exec-1", "exec-2", "exec-3");
        lenient().when(executionRepository.findByOrgUidAndGroupUidAndSourceMessageUidAndRobotUid(
                any(), any(), any(), any())).thenReturn(Optional.empty());
    }

    private String buildMessageJson(String userType, String topic, String extraJson) {
        ThreadProtobuf thread = ThreadProtobuf.builder()
                .uid("thread-1")
                .type(ThreadTypeEnum.GROUP)
                .topic(topic)
                .build();
        UserProtobuf user = UserProtobuf.builder()
                .uid("user-1")
                .nickname("张三")
                .type(userType)
                .build();
        MessageProtobuf message = MessageProtobuf.builder()
                .uid("msg-1")
                .type(MessageTypeEnum.TEXT)
                .content("@客服机器人 你好")
                .thread(thread)
                .user(user)
                .extra(extraJson)
                .build();
        return message.toJson();
    }

    private String robotMentionExtra(String targetUid) {
        MessageExtra extra = MessageExtra.builder().build();
        extra.setMentions(List.of(MessageMention.builder()
                .targetType(MessageMention.TARGET_TYPE_ROBOT)
                .targetUid(targetUid)
                .label("客服机器人")
                .start(0)
                .end(6)
                .build()));
        return extra.toJson();
    }

    @Test
    void shouldRegisterExecutionForRobotMention() {
        String json = buildMessageJson("USER", "org/group/group-1", robotMentionExtra("robot-1"));
        listener.onMessageJsonEvent(new MessageJsonEvent(this, json));

        ArgumentCaptor<GroupRobotExecutionEntity> captor =
                ArgumentCaptor.forClass(GroupRobotExecutionEntity.class);
        verify(executionRepository).save(captor.capture());
        GroupRobotExecutionEntity saved = captor.getValue();
        assertThat(saved.getRobotUid()).isEqualTo("robot-1");
        assertThat(saved.getGroupUid()).isEqualTo("group-1");
        assertThat(saved.getOrgUid()).isEqualTo("org-1");
        assertThat(saved.getSourceMessageUid()).isEqualTo("msg-1");
        assertThat(saved.getStatus()).isEqualTo(GroupRobotExecutionEntity.STATUS_PENDING);
        assertThat(saved.getReplyMessageUid()).isNotBlank(); // 预分配回复 uid
    }

    @Test
    void shouldIgnoreRobotSenderToPreventLoop() {
        String json = buildMessageJson("ROBOT", "org/group/group-1", robotMentionExtra("robot-1"));
        listener.onMessageJsonEvent(new MessageJsonEvent(this, json));
        verify(executionRepository, never()).save(any());
    }

    @Test
    void shouldIgnoreSystemSender() {
        String json = buildMessageJson("SYSTEM", "org/group/group-1", robotMentionExtra("robot-1"));
        listener.onMessageJsonEvent(new MessageJsonEvent(this, json));
        verify(executionRepository, never()).save(any());
    }

    @Test
    void shouldIgnoreNonGroupThread() {
        String json = buildMessageJson("USER", "org/member/member-1", robotMentionExtra("robot-1"));
        listener.onMessageJsonEvent(new MessageJsonEvent(this, json));
        verify(executionRepository, never()).save(any());
    }

    @Test
    void shouldIgnoreMessageWithoutMentions() {
        MessageExtra extra = MessageExtra.builder().build();
        String json = buildMessageJson("USER", "org/group/group-1", extra.toJson());
        listener.onMessageJsonEvent(new MessageJsonEvent(this, json));
        verify(executionRepository, never()).save(any());
    }

    @Test
    void shouldIgnoreAllTypeMention() {
        MessageExtra extra = MessageExtra.builder().build();
        extra.setMentions(List.of(MessageMention.builder()
                .targetType(MessageMention.TARGET_TYPE_ALL)
                .targetUid("all")
                .label("所有人")
                .start(0)
                .end(4)
                .build()));
        String json = buildMessageJson("USER", "org/group/group-1", extra.toJson());
        listener.onMessageJsonEvent(new MessageJsonEvent(this, json));
        verify(executionRepository, never()).save(any());
    }

    @Test
    void shouldIgnoreRobotNotInGroup() {
        // 伪造 mention 一个不在群内的机器人 uid：防越权
        String json = buildMessageJson("USER", "org/group/group-1", robotMentionExtra("robot-fake"));
        listener.onMessageJsonEvent(new MessageJsonEvent(this, json));
        verify(executionRepository, never()).save(any());
    }

    @Test
    void shouldSkipWhenExecutionAlreadyExists() {
        when(executionRepository.findByOrgUidAndGroupUidAndSourceMessageUidAndRobotUid(
                "org-1", "group-1", "msg-1", "robot-1"))
                .thenReturn(Optional.of(GroupRobotExecutionEntity.builder().build()));
        String json = buildMessageJson("USER", "org/group/group-1", robotMentionExtra("robot-1"));
        listener.onMessageJsonEvent(new MessageJsonEvent(this, json));
        verify(executionRepository, never()).save(any());
    }

    @Test
    void shouldIgnoreNonTextMessage() {
        ThreadProtobuf thread = ThreadProtobuf.builder()
                .uid("thread-1").type(ThreadTypeEnum.GROUP).topic("org/group/group-1").build();
        UserProtobuf user = UserProtobuf.builder().uid("user-1").nickname("张三").type("USER").build();
        MessageProtobuf message = MessageProtobuf.builder()
                .uid("msg-1")
                .type(MessageTypeEnum.IMAGE)
                .content("[image]")
                .thread(thread)
                .user(user)
                .extra(robotMentionExtra("robot-1"))
                .build();
        listener.onMessageJsonEvent(new MessageJsonEvent(this, message.toJson()));
        verify(executionRepository, never()).save(any());
    }

    @Test
    void shouldNotThrowOnInvalidJson() {
        // 解析失败不能影响消息主链路
        listener.onMessageJsonEvent(new MessageJsonEvent(this, "not-json"));
        listener.onMessageJsonEvent(new MessageJsonEvent(this, ""));
        verify(executionRepository, never()).save(any());
    }
}
