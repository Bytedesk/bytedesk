# message

该包负责消息记录管理、投递编排、实时发送与消息生命周期接口。

## 实现要点

- 核心模型包括 MessageEntity、AbstractMessageEntity、MessageRequest、MessageResponse、MessageExtra、MessageStatusEnum、MessageTypeEnum、MessageVisibilityEnum、MessageNoticeTypeEnum。
- MessageRepository、MessageSpecification、MessageRestController、MessageRestService、MessageService 提供持久化、条件过滤、管理接口和消息领域编排能力。
- IMessageSendService、MessageSendServiceImpl、MessagePersistService、MessagePersistCache、MessageQueueService、MessageSocketService 负责发送链路编排、持久化缓冲、队列处理与实时 Socket 投递。
- MessageProtobuf、MessageTypeConverter、MessageTools 提供 protobuf 映射、类型转换与共享辅助逻辑。
- MessageEntityListener、MessageEventListener 与 event 子包负责创建、更新、JSON 消息事件；content、preview、playback、reaction、extra、utils 子包拆分了承载的专门消息能力。
