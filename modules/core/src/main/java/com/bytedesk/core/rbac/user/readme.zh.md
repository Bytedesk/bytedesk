# user

该包负责用户档案管理、安全身份适配、组织角色映射与用户生命周期接口。

## 实现要点

- 核心模型包括 UserEntity、UserRequest、UserResponse、UserResponseSimple、UserResponseContact、UserExtra、UserTypeEnum、UserOrganizationRoleEntity，用于用户档案与成员关系建模。
- UserRepository、UserSpecification、UserRestController、UserRestService、UserService 提供持久化、条件过滤、REST 接口与用户领域编排能力。
- UserDetailsImpl 与 UserDetailsServiceImpl 将用户数据接入 Spring Security 的认证授权流程。
- UserProtobuf、UserConvertUtils、UserTools、UserUtils 提供 protobuf 映射、对象转换辅助与共享工具逻辑。
- UserEntityListener、UserEventListener 与 event 子包负责创建、更新、登录、登出事件；UserInitializer 与 UserPermissions 提供初始化数据和权限元数据。
