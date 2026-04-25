# relation

该包负责用户与业务关系建模、状态流转、互动统计与关系接口服务。

## 实现要点

- 核心领域文件包括 RelationEntity、RelationRequest、RelationResponse、RelationExcel、RelationTypeEnum 与 RelationStatusEnum。
- RelationRepository、RelationSpecification、RelationRestController、RelationRestService 提供持久化、条件过滤和接口编排能力。
- RelationEntityListener、RelationEventListener 以及 event 子包负责生命周期回调和领域事件处理。
- RelationUtils 与 RelationTools 集中封装关系命名、校验、评分和通用辅助逻辑。
- RelationInitializer 与 RelationPermissions 负责默认数据和权限元数据初始化。
