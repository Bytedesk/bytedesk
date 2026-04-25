# email

该包负责邮件记录管理、服务商与协议元数据，以及邮件投递生命周期接口。

## 实现要点

- 核心模型包括 EmailEntity、EmailRequest、EmailResponse、EmailExcel、EmailExtra、EmailSendResult、EmailConnectionStatusEnum、EmailProtocolEnum、EmailProviderEnum、EmailTypeEnum。
- EmailRepository、EmailSpecification、EmailRestController、EmailRestService 提供持久化、条件过滤以及邮件记录和账户设置管理接口。
- EmailSendService 与 EmailListenerConfig 负责邮件发送编排和监听/运行时配置。
- EmailTools 与 util 子包提供邮件处理所需的共享辅助逻辑。
- EmailEntityListener、event 子包与 EmailPermissions 负责生命周期集成、创建更新删除事件以及权限元数据。
