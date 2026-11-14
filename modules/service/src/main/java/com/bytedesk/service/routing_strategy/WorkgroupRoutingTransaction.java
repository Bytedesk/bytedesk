package com.bytedesk.service.routing_strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRepository;
import com.bytedesk.core.message.MessageProtobuf;

@Service
public class WorkgroupRoutingTransaction {

    private final ThreadRepository threadRepository;
    private final TransactionTemplate transaction;
    private final ThreadLocal<List<Runnable>> commitActions = new ThreadLocal<>();
    private final ThreadLocal<Supplier<MessageProtobuf>> continuation = new ThreadLocal<>();

    public WorkgroupRoutingTransaction(ThreadRepository threadRepository,
            PlatformTransactionManager transactionManager) {
        this.threadRepository = threadRepository;
        this.transaction = new TransactionTemplate(transactionManager);
        this.transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.transaction.setTimeout(15);
    }

    public MessageProtobuf execute(String threadUid, Function<ThreadEntity, MessageProtobuf> action) {
        List<Runnable> actions = new ArrayList<>();
        List<Runnable> previousActions = commitActions.get();
        Supplier<MessageProtobuf> previousContinuation = continuation.get();
        continuation.remove();
        commitActions.set(actions);
        MessageProtobuf result;
        Supplier<MessageProtobuf> deferred;
        try {
            result = transaction.execute(status -> {
                ThreadEntity thread = threadRepository.findByUidForRouting(threadUid)
                        .orElseThrow(() -> new IllegalArgumentException("Workgroup thread not found"));
                MessageProtobuf outcome = action.apply(thread);
                // Commit the decision with the actual human outcome, never before selecting/enqueuing.
                if (continuation.get() == null && thread.getTagRoutingEvaluated() != null
                        && (thread.isChatting() || thread.isQueuing() || thread.isOffline())) {
                    thread.setTagRoutingEvaluated(true);
                    threadRepository.saveAndFlush(thread);
                }
                return outcome;
            });
            deferred = continuation.get();
        } finally {
            if (previousActions == null) {
                commitActions.remove();
            } else {
                commitActions.set(previousActions);
            }
            if (previousContinuation == null) {
                continuation.remove();
            } else {
                continuation.set(previousContinuation);
            }
        }
        // External notifications run only once the database transaction and row lock have ended.
        for (Runnable committedAction : actions) {
            committedAction.run();
        }
        return deferred != null ? deferred.get() : result;
    }

    public void afterCommit(Runnable action) {
        List<Runnable> actions = commitActions.get();
        if (actions == null) {
            action.run();
        } else {
            actions.add(action);
        }
    }

    public boolean isActive() {
        return commitActions.get() != null;
    }

    public MessageProtobuf deferResult(Supplier<MessageProtobuf> action) {
        if (!isActive() || continuation.get() != null) {
            throw new IllegalStateException("Deferred routing requires one active allocation transaction");
        }
        continuation.set(action);
        return null;
    }
}
