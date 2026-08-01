# role

该包负责角色定义管理、角色规则元数据与角色生命周期接口。

## 实现要点

- 核心文件包括 RoleEntity、RoleRequest、RoleResponse、RoleResponseSimple、RoleExcel，用于角色数据与导入导出建模。
- RoleRepository、RoleSpecification、RoleRestController、RoleRestService 提供持久化、条件过滤和角色管理接口。
- RoleAuthorityRules 与 RoleConsts 集中定义内置角色规则、常量和授权相关辅助元数据。
- RoleEntityListener、RoleEventListener 与 event 子包负责角色生命周期回调以及创建、更新事件。
- RoleInitializer 与 RolePermissions 提供角色初始化数据和权限元数据。
