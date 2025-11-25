// package com.bytedesk.service.queue;

// import static org.junit.jupiter.api.Assertions.assertTrue;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;

// import java.util.Optional;
// import java.util.concurrent.CountDownLatch;
// import java.util.concurrent.CyclicBarrier;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;
// import java.util.concurrent.TimeUnit;

// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.orm.ObjectOptimisticLockingFailureException;

// import com.bytedesk.service.queue_member.QueueMemberEntity;

// @ExtendWith(MockitoExtension.class)
// class QueueAutoAssignTest {

//     @Mock
//     private QueueService queueService;

//     private QueueAutoAssignService queueAutoAssignService;

//     private ExecutorService executor;

//     @BeforeEach
//     void setUp() {
//         queueAutoAssignService = new QueueAutoAssignService(queueService);
//         executor = Executors.newFixedThreadPool(2);
//     }

//     @AfterEach
//     void tearDown() {
//         executor.shutdownNow();
//     }

//     @Test
//     void retriesAfterOptimisticLockAcrossConcurrentTriggers() throws InterruptedException {
//         when(queueService.assignNextAgentQueueMember("agent-1"))
//                 .thenThrow(new ObjectOptimisticLockingFailureException(QueueMemberEntity.class, "member-1"))
//                 .thenReturn(Optional.of(new QueueService.QueueAssignmentResult("agent-1", "thread-1", "member-1")))
//                 .thenReturn(Optional.empty());

//         CyclicBarrier startBarrier = new CyclicBarrier(2);
//         CountDownLatch finished = new CountDownLatch(2);

//         Runnable trigger = () -> {
//             try {
//                 startBarrier.await(1, TimeUnit.SECONDS);
//                 queueAutoAssignService.triggerAgentAutoAssign(
//                         "agent-1",
//                         QueueAutoAssignTriggerSource.AGENT_STATUS_EVENT,
//                         1);
//             } catch (Exception e) {
//                 throw new RuntimeException(e);
//             } finally {
//                 finished.countDown();
//             }
//         };

//         executor.submit(trigger);
//         executor.submit(trigger);

//         assertTrue(finished.await(2, TimeUnit.SECONDS), "Auto-assign triggers did not finish in time");
//         verify(queueService, times(3)).assignNextAgentQueueMember("agent-1");
//     }
// }
