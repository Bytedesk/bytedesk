# CIDR IP段匹配功能说明

## 概述

本功能为 Bytedesk 系统添加了对 CIDR 格式 IP 段的支持，允许在白名单和黑名单中使用 IP 段（如 `192.168.1.0/24`）而不是单个 IP 地址。

## 功能特性

### 1. 支持的 CIDR 格式

- **IPv4 CIDR**: `192.168.1.0/24`, `10.0.0.0/8`, `172.16.0.0/12` 等
- **精确匹配**: `192.168.1.1/32` (等同于单个IP)
- **全匹配**: `0.0.0.0/0` (匹配所有IPv4地址)

### 2. 验证功能

- `IpUtils.isValidCidr(String cidr)`: 验证 CIDR 格式是否正确
- `IpUtils.isIpInCidrRange(String ip, String cidr)`: 检查 IP 是否在 CIDR 范围内

## 使用方法

### 在 IP 白名单中使用

1. **添加 CIDR 格式的白名单条目**:
   ```java
   // 在数据库中存储 CIDR 格式的 IP
   IpWhiteEntity whiteEntry = new IpWhiteEntity();
   whiteEntry.setIp("192.168.1.0/24");  // 允许整个 192.168.1.x 网段
   whiteEntry.setDescription("内网访问");
   ```

2. **检查 IP 是否在白名单中**:
   ```java
   String clientIp = "192.168.1.100";
   boolean isWhitelisted = ipWhiteRestService.isIpInWhitelist(clientIp);
   // 如果 clientIp 在 192.168.1.0/24 范围内，返回 true
   ```

### 在 IP 黑名单中使用

1. **添加 CIDR 格式的黑名单条目**:
   ```java
   // 在数据库中存储 CIDR 格式的 IP
   IpBlackEntity blackEntry = new IpBlackEntity();
   blackEntry.setIp("10.0.0.0/8");  // 封禁整个 10.x.x.x 网段
   blackEntry.setReason("恶意访问");
   ```

2. **检查 IP 是否在黑名单中**:
   ```java
   String clientIp = "10.1.2.3";
   boolean isBlocked = ipBlackRestService.isIpBlocked(clientIp);
   // 如果 clientIp 在 10.0.0.0/8 范围内，返回 true
   ```

## 在拦截器中的使用

`IpAccessInterceptor` 已经更新为支持 CIDR 格式：

```java
@Override
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    String ip = IpUtils.getClientIp(request);
    
    // 检查白名单（支持 CIDR）
    if (ipWhiteRestService.isIpInWhitelist(ip)) {
        return true;  // 允许访问
    }
    
    // 检查黑名单（支持 CIDR）
    if (ipBlackRestService.isIpBlocked(ip)) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return false;  // 拒绝访问
    }
    
    return true;
}
```

## 常见 CIDR 示例

| CIDR 格式 | 描述 | 包含的 IP 范围 |
|-----------|------|----------------|
| `192.168.1.0/24` | 局域网 C 类网段 | 192.168.1.0 - 192.168.1.255 |
| `10.0.0.0/8` | 私有网络 A 类 | 10.0.0.0 - 10.255.255.255 |
| `172.16.0.0/12` | 私有网络 B 类 | 172.16.0.0 - 172.31.255.255 |
| `0.0.0.0/0` | 所有 IPv4 地址 | 0.0.0.0 - 255.255.255.255 |
| `192.168.1.1/32` | 单个 IP 地址 | 仅 192.168.1.1 |

## 性能考虑

- CIDR 匹配会遍历所有白名单/黑名单条目
- 建议在生产环境中使用缓存来优化性能
- 对于大量 IP 段的情况，可以考虑使用更高效的数据结构

## 测试

运行测试来验证功能：

```bash
# 运行 CIDR 功能测试
mvn test -Dtest=IpUtilsCidrTest -pl modules/core

# 或者运行演示程序
java -cp target/classes com.bytedesk.core.utils.CidrDemo
```

## 注意事项

1. **优先级**: 精确匹配优先于 CIDR 匹配
2. **白名单优先**: 如果 IP 在白名单中，即使也在黑名单中也会被允许
3. **格式验证**: 系统会自动验证 CIDR 格式的正确性
4. **向后兼容**: 现有的单个 IP 地址功能保持不变

## 更新日志

- **2025-01-20**: 添加 CIDR 支持功能
- 扩展 `IpUtils` 类支持 CIDR 格式验证和匹配
- 更新 `IpWhiteRestService` 和 `IpBlackRestService` 支持 CIDR 匹配
- 修改 `IpAccessInterceptor` 使用新的 CIDR 匹配方法 