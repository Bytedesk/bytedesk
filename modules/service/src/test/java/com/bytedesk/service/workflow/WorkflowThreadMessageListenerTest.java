package com.bytedesk.service.workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageSocketService;
import com.bytedesk.core.message.content.ChoiceContent;
import com.bytedesk.core.message.enums.MessageStatusEnum;
import com.bytedesk.core.message.enums.MessageTypeEnum;
import com.bytedesk.core.message.event.MessageJsonEvent;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadExtra;
import com.bytedesk.core.thread.ThreadProtobuf;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.enums.ThreadProcessStatusEnum;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.workflow.WorkflowEntity;
import com.bytedesk.core.workflow.WorkflowRestService;

@ExtendWith(MockitoExtension.class)
class WorkflowThreadMessageListenerTest {

    @Mock
    private ThreadRestService threadRestService;

    @Mock
    private WorkflowRestService workflowRestService;

    @Mock
    private WorkflowChatService workflowChatService;

    @Mock
    private MessageSocketService messageSocketService;

    private WorkflowThreadMessageListener listener;

    private ExecutorService executorService;

    @BeforeEach
    void setUp() {
        listener = new WorkflowThreadMessageListener(
                threadRestService,
                workflowRestService,
                workflowChatService,
                messageSocketService);
        executorService = Executors.newFixedThreadPool(2);
    }

    @AfterEach
    void tearDown() {
        executorService.shutdownNow();
    }

    @Test
    void concurrentChoiceSubmitShouldOnlySendFollowupOnce() throws Exception {
        String threadUid = "thread-1";
        String workflowUid = "workflow-1";
        String choiceNodeId = "lead-choice-education";
        String selectedOptionKey = "lead-education-college";

        ThreadEntity staleThreadBeforeFirstLock = buildWorkflowThread(threadUid, workflowUid, choiceNodeId);
        ThreadEntity staleThreadBeforeSecondLock = buildWorkflowThread(threadUid, workflowUid, choiceNodeId);
        ThreadEntity liveThread = buildWorkflowThread(threadUid, workflowUid, choiceNodeId);
        WorkflowEntity workflow = WorkflowEntity.builder().uid(workflowUid).schema("{}").build();

        CountDownLatch firstEventReloadedThreadInsideLock = new CountDownLatch(1);
        CountDownLatch secondEventCheckedPreLockState = new CountDownLatch(1);
        AtomicInteger lookupCount = new AtomicInteger();
        when(threadRestService.findByUid(threadUid)).thenAnswer(invocation -> {
            int currentLookup = lookupCount.incrementAndGet();
            if (currentLookup == 1) {
                return Optional.of(staleThreadBeforeFirstLock);
            }
            if (currentLookup == 2) {
                firstEventReloadedThreadInsideLock.countDown();
                return Optional.of(liveThread);
            }
            if (currentLookup == 3) {
                secondEventCheckedPreLockState.countDown();
                return Optional.of(staleThreadBeforeSecondLock);
            }
            return Optional.of(liveThread);
        });
        when(workflowRestService.findByUid(workflowUid)).thenReturn(Optional.of(workflow));

        AtomicInteger continueCount = new AtomicInteger();
        AtomicBoolean secondProcessingSawClearedState = new AtomicBoolean(false);
        when(workflowChatService.continueAfterChoiceMessages(eq(workflow), any(ThreadEntity.class), eq(selectedOptionKey)))
                .thenAnswer(invocation -> {
                    ThreadEntity thread = invocation.getArgument(1);
                    int currentContinue = continueCount.incrementAndGet();
                    if (currentContinue == 1) {
                        assertThat(secondEventCheckedPreLockState.await(5, TimeUnit.SECONDS)).isTrue();
                        thread.setExtra(ThreadExtra.builder().workflowWaitingChoiceNodeId(null).build().toJson());
                        return List.of(buildWorkflowResponse(threadUid));
                    }

                    ThreadExtra latestExtra = ThreadExtra.fromJson(thread.getExtra());
                    secondProcessingSawClearedState.set(latestExtra != null
                            && latestExtra.getWorkflowWaitingChoiceNodeId() == null);
                    return List.of();
                });

        MessageJsonEvent event = new MessageJsonEvent(this, buildChoiceSubmitMessage(threadUid, choiceNodeId, selectedOptionKey).toJson());

        Future<?> first = executorService.submit(() -> listener.onMessageJsonEvent(event));
        Future<?> second = executorService.submit(() -> {
            try {
                assertThat(firstEventReloadedThreadInsideLock.await(5, TimeUnit.SECONDS)).isTrue();
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new AssertionError(exception);
            }
            listener.onMessageJsonEvent(event);
        });

        first.get(5, TimeUnit.SECONDS);
        second.get(5, TimeUnit.SECONDS);

        assertThat(secondProcessingSawClearedState).isTrue();
        verify(workflowChatService, times(2)).continueAfterChoiceMessages(eq(workflow), any(ThreadEntity.class), eq(selectedOptionKey));
        verify(messageSocketService, times(1)).sendStompMessage(anyString());
    }

    private ThreadEntity buildWorkflowThread(String threadUid, String workflowUid, String waitingChoiceNodeId) {
        return ThreadEntity.builder()
                .uid(threadUid)
                .topic("topic-1")
                .type(ThreadTypeEnum.WORKFLOW.name())
                .status(ThreadProcessStatusEnum.CHATTING.name())
                .workflow(UserProtobuf.builder().uid(workflowUid).build().toJson())
                .extra(ThreadExtra.builder().workflowWaitingChoiceNodeId(waitingChoiceNodeId).build().toJson())
                .build();
    }

    private MessageProtobuf buildChoiceSubmitMessage(String threadUid, String choiceUid, String selectedOptionKey) {
        ChoiceContent content = ChoiceContent.builder()
                .choiceUid(choiceUid)
                .content("1.大专")
                .selectedValues(List.of(selectedOptionKey))
                .options(List.of(ChoiceContent.ChoiceOption.builder()
                        .optionUid(selectedOptionKey)
                        .title("1.大专")
                        .value("大专")
                        .build()))
                .build();
        return MessageProtobuf.builder()
                .uid("msg-choice-submit-1")
                .type(MessageTypeEnum.CHOICE_SUBMIT)
                .content(content.toJson())
                .status(MessageStatusEnum.READ)
                .createdAt(ZonedDateTime.now())
                .channel(ChannelEnum.WEB)
                .thread(ThreadProtobuf.builder()
                        .uid(threadUid)
                        .topic("topic-1")
                        .type(ThreadTypeEnum.WORKFLOW)
                        .status(ThreadProcessStatusEnum.CHATTING)
                        .build())
                .user(UserProtobuf.builder()
                        .uid("visitor-1")
                        .type(UserTypeEnum.VISITOR.name())
                        .build())
                .build();
    }

    private MessageProtobuf buildWorkflowResponse(String threadUid) {
        return MessageProtobuf.builder()
                .uid("msg-followup-1")
                .type(MessageTypeEnum.CHOICE)
                .content("{\"content\":\"嗯嗯，您主要是想简单拿证还是想系统学习知识呢？\"}")
                .status(MessageStatusEnum.READ)
                .createdAt(ZonedDateTime.now())
                .channel(ChannelEnum.WEB)
                .thread(ThreadProtobuf.builder()
                        .uid(threadUid)
                        .topic("topic-1")
                        .type(ThreadTypeEnum.WORKFLOW)
                        .status(ThreadProcessStatusEnum.CHATTING)
                        .build())
                .build();
    }
}