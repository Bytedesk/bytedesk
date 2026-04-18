package com.bytedesk.service.visitor_thread;

import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bytedesk.ai.robot.RobotRestService;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.quartz.event.QuartzOneMinEvent;
import com.bytedesk.core.thread.ActiveThreadCache;
import com.bytedesk.core.thread.ActiveThreadCacheService;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.service.workgroup.WorkgroupRestService;

@ExtendWith(MockitoExtension.class)
class VisitorThreadEventListenerTest {

    @Mock
    private VisitorThreadTimeoutService visitorThreadTimeoutService;

    @Mock
    private WorkgroupRestService workgroupRestService;

    @Mock
    private AgentRestService agentRestService;

    @Mock
    private RobotRestService robotRestService;

    @Mock
    private IMessageSendService messageSendService;

    @Mock
    private ThreadRestService threadRestService;

    @Mock
    private QueueMemberRestService queueMemberRestService;

    @Mock
    private ActiveThreadCacheService activeThreadCacheService;

    @Mock
    private VisitorThreadTriggerService visitorThreadTriggerService;

    private VisitorThreadEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new VisitorThreadEventListener(
                visitorThreadTimeoutService,
                workgroupRestService,
                agentRestService,
                robotRestService,
                messageSendService,
                threadRestService,
                queueMemberRestService,
                activeThreadCacheService,
                visitorThreadTriggerService);
    }

    @Test
    void onQuartzOneMinEventSkipsCacheEntriesWithoutUid() {
        ActiveThreadCache invalidCache = ActiveThreadCache.builder()
                .uid(null)
                .status("CHATTING")
                .type("AGENT")
                .build();

        ActiveThreadCache validCache = ActiveThreadCache.builder()
                .uid("thread-1")
                .status("CHATTING")
                .type("AGENT")
                .build();

        ThreadEntity thread = new ThreadEntity();
        thread.setUid("thread-1");
        thread.setStatus("CHATTING");

        when(activeThreadCacheService.getAllActiveServiceThreads()).thenReturn(List.of(invalidCache, validCache));
        when(threadRestService.findByUid("thread-1")).thenReturn(Optional.of(thread));

        listener.onQuartzOneMinEvent(new QuartzOneMinEvent(this));

        verify(threadRestService, never()).findByUid((String) null);
        verify(threadRestService).findByUid("thread-1");
        verify(visitorThreadTimeoutService).autoRemindAgentOrCloseThread(anyList());
        verify(visitorThreadTriggerService).processProactiveTriggerFromCache(validCache);
        verify(visitorThreadTriggerService, times(1)).processProactiveTriggerFromCache(validCache);
        verify(visitorThreadTriggerService, never()).processProactiveTriggerFromCache(invalidCache);
    }
}