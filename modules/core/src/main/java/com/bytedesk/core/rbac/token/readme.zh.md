# token

该包负责令牌记录管理、令牌作用域与类型建模，以及令牌生命周期接口。

## 实现要点

- 核心文件包括 TokenEntity、TokenRequest、TokenResponse、TokenScopeEnum、TokenTypeEnum，用于令牌记录及元数据建模。
- TokenRepository、TokenSpecification、TokenRestController、TokenRestService 提供持久化、条件过滤与令牌管理接口。
- TokenEntityListener、TokenEventListener 与 event 子包负责令牌生命周期回调以及创建、更新事件。
- TokenInitializer 与 TokenPermissions 提供令牌管理所需的初始化数据和权限元数据。
