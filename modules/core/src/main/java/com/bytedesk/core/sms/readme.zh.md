# sms

该包负责短信记录管理、发送服务协同与短信生命周期接口。

## 实现要点

- 核心模型包括 SmsEntity、SmsRequest、SmsResponse、SmsExcel、SmsSendResult、SmsTypeEnum。
- SmsRepository、SmsSpecification、SmsRestController、SmsRestService 提供持久化、条件过滤和短信记录管理接口。
- SmsSendService 与 SmsExternalSender 负责短信投递编排和外部发送器集成。
- SmsInitializer、SmsPermissions、SmsTools、SmsEntityListener、SmsEventListener 与 event 子包提供初始化数据、权限元数据、辅助逻辑以及创建、更新、删除生命周期事件。
