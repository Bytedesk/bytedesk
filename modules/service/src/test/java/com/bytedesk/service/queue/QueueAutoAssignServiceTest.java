// package com.bytedesk.service.queue;

// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.verifyNoInteractions;
// import static org.mockito.Mockito.when;

// import java.util.Optional;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// @ExtendWith(MockitoExtension.class)
// class QueueAutoAssignServiceTest {

//     @Mock
//     private QueueService queueService;

//     private QueueAutoAssignService queueAutoAssignService;

//     @BeforeEach
//     void setUp() {
//         queueAutoAssignService = new QueueAutoAssignService(queueService);
//     }

//     @Test
//     void triggerAgentAutoAssignNormalizesSlotHint() {
//         when(queueService.assignNextAgentQueueMember("agent-1"))
//                 .thenReturn(Optional.empty());

//         queueAutoAssignService.triggerAgentAutoAssign(
//                 "agent-1",
//                 QueueAutoAssignTriggerSource.MANUAL_ENDPOINT,
//                 0);

//         verify(queueService, times(1)).assignNextAgentQueueMember("agent-1");
//     }

//     @Test
//     void triggerAgentAutoAssignStopsWhenQueueEmpty() {
//         when(queueService.assignNextAgentQueueMember("agent-1"))
//             .thenReturn(Optional.of(new QueueService.QueueAssignmentResult("agent-1", "thread-1", "member-1")))
//             .thenReturn(Optional.empty());

//         queueAutoAssignService.triggerAgentAutoAssign(
//                 "agent-1",
//                 QueueAutoAssignTriggerSource.AGENT_STATUS_EVENT,
//                 3);

//         verify(queueService, times(2)).assignNextAgentQueueMember("agent-1");
//     }

//     @Test
//     void triggerAgentAutoAssignSkipsBlankAgentUid() {
//         queueAutoAssignService.triggerAgentAutoAssign(
//                 " ",
//                 QueueAutoAssignTriggerSource.MANUAL_ENDPOINT,
//                 2);

//         verifyNoInteractions(queueService);
//     }
// }
