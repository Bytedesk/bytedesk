package com.bytedesk.core.message.playback;

import java.util.Optional;

import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageRepository;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.utils.MessageUtils;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.ConvertUtils;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/message/playback")
@Tag(name = "消息播放状态", description = "音频/语音消息播放状态更新（客服端）")
@Description("Message Playback Controller (Agent) - mark media message as played")
public class MessagePlaybackRestControllerAgent {

    private final MessageRepository messageRepository;
    private final AuthService authService;
    private final IMessageSendService messageSendService;

    @Operation(summary = "标记媒体消息已播放", description = "将 AUDIO/VOICE 消息 content 中的 played 字段标记为 true/false")
    @PostMapping("/played")
    public ResponseEntity<?> markPlayed(@RequestBody MessagePlaybackMarkPlayedRequest request) {
        if (request == null || !StringUtils.hasText(request.getMessageUid())) {
            return ResponseEntity.badRequest().body(JsonResult.error("messageUid required"));
        }

        UserEntity user = authService.getUser();
        if (user == null || !StringUtils.hasText(user.getUid())) {
            return ResponseEntity.status(401).body(JsonResult.error("login required"));
        }
        String userUid = user.getUid();

        Optional<MessageEntity> optional = messageRepository.findByUid(request.getMessageUid().trim());
        if (optional.isEmpty()) {
            return ResponseEntity.ok(JsonResult.error("message not found"));
        }

        MessageEntity message = optional.get();
        MessageTypeEnum messageType = MessageTypeEnum.fromValue(message.getType());
        if (messageType != MessageTypeEnum.AUDIO && messageType != MessageTypeEnum.VOICE) {
            return ResponseEntity.badRequest().body(JsonResult.error("only AUDIO/VOICE message supported"));
        }

        boolean played = request.getPlayed() == null || request.getPlayed();
        String next = MessagePlaybackJsonHelper.markPlayed(message.getContent(), message.getType(), played);
        message.setContent(next);
        messageRepository.save(message);

        // 广播播放状态更新消息，通知同 thread 的其他端即时刷新对应消息内容
        if (message.getThread() != null) {
            MessagePlaybackUpdatePayload payload = MessagePlaybackUpdatePayload.builder()
                .messageUid(message.getUid())
                .content(next)
                .played(played)
                .userUid(userUid)
                .serverTimestamp(System.currentTimeMillis())
                .build();

            messageSendService.sendProtobufMessage(
                MessageUtils.createThreadMessage(
                    UidUtils.getInstance().getUid(),
                    message.getThread(),
                    MessageTypeEnum.PLAYBACK,
                    payload.toJson()));
        }

        return ResponseEntity.ok(JsonResult.success(ConvertUtils.convertToMessageResponse(message)));
    }
}
