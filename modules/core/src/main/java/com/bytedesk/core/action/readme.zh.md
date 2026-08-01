# action

该包负责动作实体管理、执行事件处理、权限初始化与异步分发支撑。

## 实现要点

- 核心模型包括 ActionEntity、ActionRequest、ActionResponse、ActionExcel 与 ActionTypeEnum。
- ActionRepository、ActionSpecification、ActionRestController 与 ActionRestService 提供持久化、条件查询和接口入口。
- ActionEntityListener、ActionEventListener 以及 event 子包处理实体生命周期和领域事件。
- ActionInitializer 与 ActionPermissions 负责初始化数据和权限元数据注册。
- aop、disruptor 子包分别扩展横切拦截与异步事件分发能力。
