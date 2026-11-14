package com.bytedesk.remote.session;

import java.util.UUID;
import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.message.*;
import com.bytedesk.core.message.enums.*;
import com.bytedesk.core.rbac.user.UserUtils;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.webrtc.room.*;
import com.bytedesk.webrtc.participant.ParticipantRestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class DesktopSessionEvents {
    private final RoomRestService rooms;
    private final ParticipantRestService participants;
    private final ThreadRestService threads;
    private final IMessageSendService messages;

    public String open(DesktopSessionEntity s) {
        return rooms.createServiceRoom(s.getUid(), RoomTypeEnum.REMOTE_ASSIST, "Remote assistance", 2,
                s.getOrgUid(), s.getViewerUid()).getUid();
    }

    public void join(DesktopSessionEntity s) {
        participants.recordCallJoin(s.getUid(), "REMOTE_ASSIST", s.getRoomUid(),
                s.getViewerUid(), s.getViewerName(), false, s.getOrgUid());
        participants.recordCallJoin(s.getUid(), "REMOTE_ASSIST", s.getRoomUid(),
                s.getHostUid(), "Visitor", true, s.getOrgUid());
    }

    public void close(DesktopSessionEntity s) {
        participants.closeCallRecords(s.getUid());
    }

    public void notifyThread(DesktopSessionEntity s, String event) {
        var thread = threads.findByUid(s.getThreadUid()).orElse(null);
        if (thread == null) {
            log.warn("Remote assistance notification unavailable: session={}, thread={}, event={}",
                    s.getUid(), s.getThreadUid(), event);
            return;
        }
        // TEXT keeps existing visitor clients compatible; never persist invitation or host tokens.
        var message = MessageProtobuf.builder().uid(UUID.randomUUID().toString())
                .type(MessageTypeEnum.TEXT).content("[Remote assistance] " + event + " (" + s.getUid() + ")")
                .status(MessageStatusEnum.SENDING).createdAt(BdDateUtils.now()).channel(ChannelEnum.SYSTEM)
                .thread(thread.toProtobuf()).user(UserUtils.getSystemUser())
                .extra(MessageExtra.fromOrgUid(s.getOrgUid()).toJson()).build();
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() { messages.sendJsonMessage(message.toJson()); }
            });
        } else {
            messages.sendJsonMessage(message.toJson());
        }
    }
}
