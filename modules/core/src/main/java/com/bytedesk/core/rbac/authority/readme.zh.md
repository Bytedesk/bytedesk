# authority

该包负责权限项管理、细粒度权限元数据与权限生命周期接口。

## 实现要点

- 核心文件包括 AuthorityEntity、AuthorityRequest、AuthorityResponse，用于权限记录建模。
- AuthorityRepository、AuthoritySpecification、AuthorityRestController、AuthorityRestService 提供持久化、条件过滤和权限项管理接口。
- AuthorityEntityListener、AuthorityEventListener 与 event 子包负责权限生命周期回调以及创建、更新、删除事件。
- AuthorityInitializer 与 AuthorityPermissions 提供内置权限初始化数据和权限元数据。
