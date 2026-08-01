# message

This package manages message records, delivery orchestration, realtime sending, and message lifecycle APIs.

## Implementation Notes

- Core models include MessageEntity, AbstractMessageEntity, MessageRequest, MessageResponse, MessageExtra, MessageStatusEnum, MessageTypeEnum, MessageVisibilityEnum, and MessageNoticeTypeEnum.
- MessageRepository, MessageSpecification, MessageRestController, MessageRestService, and MessageService provide persistence, filtering, management endpoints, and message-domain orchestration.
- IMessageSendService, MessageSendServiceImpl, MessagePersistService, MessagePersistCache, MessageQueueService, and MessageSocketService cover send flow orchestration, persistence buffering, queueing, and realtime socket delivery.
- MessageProtobuf, MessageTypeConverter, and MessageTools provide protobuf mapping, type conversion, and shared helper logic.
- MessageEntityListener, MessageEventListener, and the event subpackage handle create, update, and JSON message events, while content, preview, playback, reaction, extra, and utils split specialized message capabilities.
