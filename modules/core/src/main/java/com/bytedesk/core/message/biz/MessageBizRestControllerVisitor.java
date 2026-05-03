package com.bytedesk.core.message.biz;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Locale;
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
import com.bytedesk.core.message.enums.MessageTypeEnum;
import com.bytedesk.core.message.utils.MessageUtils;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.ConvertUtils;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/visitor/api/v1/message/biz")
@Tag(name = "商品/订单消息更新", description = "更新原商品或订单消息内容（访客端）")
@Description("Message Biz Controller (Visitor) - update goods/order content")
public class MessageBizRestControllerVisitor {

    private static final Duration BIZ_UPDATE_WINDOW = Duration.ofMinutes(5);

    private final MessageRepository messageRepository;
    private final IMessageSendService messageSendService;

    @Operation(summary = "更新商品/订单消息", description = "更新原商品或订单消息 content，并广播补丁消息通知其他端刷新")
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody MessageBizUpdateRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(JsonResult.error("request required"));
        }
        if (!StringUtils.hasText(request.getMessageUid())) {
            return ResponseEntity.badRequest().body(JsonResult.error("messageUid required"));
        }
        if (!StringUtils.hasText(request.getBizType())) {
            return ResponseEntity.badRequest().body(JsonResult.error("bizType required"));
        }
        if (!StringUtils.hasText(request.getContent())) {
            return ResponseEntity.badRequest().body(JsonResult.error("content required"));
        }

        MessageTypeEnum bizType;
        try {
            bizType = MessageTypeEnum.valueOf(request.getBizType().trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(JsonResult.error("unsupported bizType"));
        }

        if (!MessageTypeEnum.GOODS.equals(bizType) && !MessageTypeEnum.ORDER.equals(bizType)) {
            return ResponseEntity.badRequest().body(JsonResult.error("unsupported bizType"));
        }

        Optional<MessageEntity> optional = messageRepository.findByUid(request.getMessageUid().trim());
        if (optional.isEmpty()) {
            return ResponseEntity.ok(JsonResult.error("message not found"));
        }

        MessageEntity message = optional.get();
        String currentType = message.getType();
        if (!bizType.name().equalsIgnoreCase(currentType)) {
            return ResponseEntity.badRequest().body(JsonResult.error("message type mismatch"));
        }
        if (!Boolean.TRUE.equals(message.isFromVisitor())) {
            return ResponseEntity.badRequest().body(JsonResult.error("only visitor goods/order messages can be updated"));
        }

        ZonedDateTime createdAt = message.getCreatedAt();
        if (createdAt == null || Duration.between(createdAt, ZonedDateTime.now()).compareTo(BIZ_UPDATE_WINDOW) > 0) {
            return ResponseEntity.badRequest().body(JsonResult.error("message update window expired"));
        }

        message.setContent(request.getContent());
        messageRepository.save(message);

        if (message.getThread() != null) {
            MessageTypeEnum patchType = MessageTypeEnum.GOODS.equals(bizType)
                ? MessageTypeEnum.GOODS_UPDATE
                : MessageTypeEnum.ORDER_UPDATE;
            MessageBizUpdatePayload payload = MessageBizUpdatePayload.builder()
                .messageUid(message.getUid())
                .bizType(bizType.name())
                .content(request.getContent())
                .serverTimestamp(System.currentTimeMillis())
                .build();

            messageSendService.sendProtobufMessage(
                MessageUtils.createThreadMessage(
                    UidUtils.getInstance().getUid(),
                    message.getThread(),
                    patchType,
                    payload.toJson()));
        }

        return ResponseEntity.ok(JsonResult.success(ConvertUtils.convertToMessageResponse(message)));
    }
}