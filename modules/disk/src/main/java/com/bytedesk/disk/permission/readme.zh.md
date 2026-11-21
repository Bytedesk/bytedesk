# permission 网盘权限包

本包包含两层权限机制：**模块级 RBAC 功能权限**（入口门禁）+ **目录级 ACL 资源权限**（逐资源校验）。

## 1. 模块级 RBAC（功能权限）

- `DiskPermissions`：定义 `DISK_READ / DISK_CREATE / DISK_UPDATE / DISK_DELETE / DISK_EXPORT` 权限常量，供 Controller 上 `@PreAuthorize(HAS_DISK_READ)` 做粗粒度入口检查（「你能不能用网盘这个功能」）。
- `DiskInitializer`：启动时通过 `AuthorityRestService.createForPlatform` 将 `DISK_*` 权限注册进平台权限表，由角色体系（`RoleAuthorityRules` 打包进各默认角色）赋予成员。
- 默认角色：ROLE_USER 默认集含 `DISK_READ`（入口可见，即前端 `canDisk()` 菜单显隐依据）；SUPER/ADMIN 由事件绑定与全量初始化获得全部 `DISK_*`。目录内实际能做什么由下方 ACL 决定。

## 2. 目录级 ACL（资源权限）

`DiskPermissionEntity`（表 `bytedesk_disk_permission`）回答「你对**这一个目录**能做什么」：

- 一条授权 = `folderUid × targetType(MEMBER/DEPT) × targetUid × perms 集合`；
- `DiskPermEnum` 为 5 级权限集合（集合语义，非等级语义）：`VIEW / DOWNLOAD / UPLOAD / EDIT / MANAGE`；
- 有效权限计算（`DiskPermissionRestService.effectivePerms`）：**成员直授 ∪ 部门授权 ∪ 沿途父目录继承**；个人空间仅所有者拥有全权限；
- 授权不暗示其他权限，各操作所需权限组合见模块计划文档 §8 的操作矩阵。

### 为什么不用角色（RBAC）来做目录授权

1. **授权项是运行时数据，不是代码常量**：目录 × 成员/部门的组合运行时无限增长，无法枚举成 `hasAuthority('...')` 字符串；
2. **授权目标不只有用户**：`targetType` 支持 DEPT，给部门授权后成员增减自动按最新名单生效；若用角色模拟需为每个目录动态建角色，角色数量爆炸；
3. **权限语义是「集合并集 + 继承」而非等级**：同一人可在不同目录持有不同权限子集，且支持父目录继承与显式断开继承，Spring Security 的扁平 authority 集合没有「资源树」概念；
4. **需要授权委托与即时回收**：持有 `MANAGE` 的目录管理员可授权他人（不得超出自身范围），部门调整、成员退出后新请求立刻按最新结果判断。

## 3. 类清单

| 类 | 职责 |
| --- | --- |
| `DiskPermissions` | 模块级 RBAC 权限常量（`DISK_*` + `@PreAuthorize` 表达式） |
| `DiskInitializer` | 启动初始化，注册模块权限 |
| `DiskPermEnum` | 目录权限枚举（VIEW/DOWNLOAD/UPLOAD/EDIT/MANAGE） |
| `DiskPermissionEntity` | 目录授权实体（folderUid + targetType/targetUid + perms） |
| `DiskPermissionRepository` | 授权数据访问 |
| `DiskPermissionRequest/Response` | 授权 CRUD 请求/响应对象 |
| `DiskPermissionRestController` | 授权接口（query/create/update/delete） |
| `DiskPermissionRestService` | 授权 CRUD + 有效权限计算 |
