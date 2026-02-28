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
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.ConvertUtils;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/visitor/api/v1/message/reaction")
@Tag(name = "消息表情回复", description = "轻量级表情回复（访客端）")
@Description("Message Reaction Controller (Visitor) - public toggle")
public class MessageReactionRestControllerVisitor {

    private final MessageRepository messageRepository;
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
        if (!StringUtils.hasText(request.getUserUid())) {
            return ResponseEntity.badRequest().body(JsonResult.error("userUid required"));
        }

        Optional<MessageEntity> optional = messageRepository.findByUid(request.getMessageUid().trim());
        if (optional.isEmpty()) {
            return ResponseEntity.ok(JsonResult.error("message not found"));
        }

        MessageEntity message = optional.get();
    String next = MessageReactionJsonHelper.toggleReaction(
        message.getContent(),
        message.getType(),
        request.getEmoji(),
        request.getUserUid(),
        request.getUserNickname(),
        request.getUserAvatar());
        message.setContent(next);
        messageRepository.save(message);

        // 广播 reaction 更新消息，通知同 thread 的其他端及时刷新气泡内容
        if (message.getThread() != null) {
            MessageReactionUpdatePayload payload = MessageReactionUpdatePayload.builder()
                .messageUid(message.getUid())
                .content(next)
                .emoji(request.getEmoji())
                .userUid(request.getUserUid())
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
