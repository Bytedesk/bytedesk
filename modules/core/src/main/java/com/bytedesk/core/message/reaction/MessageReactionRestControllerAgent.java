package com.bytedesk.core.message.reaction;

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
@RequestMapping("/api/v1/message/reaction")
@Tag(name = "消息表情回复", description = "轻量级表情回复（客服端）")
@Description("Message Reaction Controller (Agent) - authenticated toggle")
public class MessageReactionRestControllerAgent {

    private final MessageRepository messageRepository;
    private final AuthService authService;
    private final IMessageSendService messageSendService;

    @Operation(summary = "切换消息表情回复", description = "同一用户对同一 emoji：已点则取消，未点则添加")
    @PostMapping("/toggle")
    public ResponseEntity<?> toggle(@RequestBody MessageReactionToggleRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(JsonResult.error("request required"));
        }
        if (!StringUtils.hasText(request.getMessageUid())) {
            return ResponseEntity.badRequest().body(JsonResult.error("messageUid required"));
        }
        if (!StringUtils.hasText(request.getEmoji())) {
            return ResponseEntity.badRequest().body(JsonResult.error("emoji required"));
        }

        UserEntity user = authService.getUser();
        if (user == null || !StringUtils.hasText(user.getUid())) {
            return ResponseEntity.status(401).body(JsonResult.error("login required"));
        }
        String userUid = user.getUid();
        String nickname = user.getNickname();
        String avatar = user.getAvatar();

        Optional<MessageEntity> optional = messageRepository.findByUid(request.getMessageUid().trim());
        if (optional.isEmpty()) {
            return ResponseEntity.ok(JsonResult.error("message not found"));
        }

        MessageEntity message = optional.get();
        String next = MessageReactionJsonHelper.toggleReaction(
                message.getContent(),
                message.getType(),
                request.getEmoji(),
                userUid,
                nickname,
                avatar);
        message.setContent(next);
        messageRepository.save(message);

        // 广播 reaction 更新消息，通知同 thread 的其他端及时刷新气泡内容
        if (message.getThread() != null) {
            MessageReactionUpdatePayload payload = MessageReactionUpdatePayload.builder()
                    .messageUid(message.getUid())
                    .content(next)
                    .emoji(request.getEmoji())
                    .userUid(userUid)
                    .serverTimestamp(System.currentTimeMillis())
                    .build();

            messageSendService.sendProtobufMessage(
                    MessageUtils.createThreadMessage(
                            UidUtils.getInstance().getUid(),
                            message.getThread(),
                            MessageTypeEnum.REACTION,
                            payload.toJson()));
        }

        return ResponseEntity.ok(JsonResult.success(ConvertUtils.convertToMessageResponse(message)));
    }
}
