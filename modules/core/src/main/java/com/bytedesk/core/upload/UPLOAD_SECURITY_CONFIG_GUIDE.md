# 文件上传安全配置使用指南

## 概述

文件上传安全配置已经在代码中提供了合理的默认值，无需额外配置即可使用。如果需要自定义配置，可以在 `application.properties` 或 `application.yml` 中添加相应配置。

## 默认配置

系统提供了以下默认安全配置：

### 文件大小限制

- 默认最大文件大小：10MB (10485760 bytes)

### 允许的文件类型

- **图片文件**：jpg, jpeg, png, gif, bmp, webp, svg
- **文档文件**：pdf, doc, docx, xls, xlsx, ppt, pptx, txt
- **压缩文件**：zip, rar, 7z, tar, gz
- **音频文件**：mp3, wav, aac, ogg, flac, m4a
- **视频文件**：mp4, avi, mov, wmv, flv, mkv, webm

### 危险文件类型（被禁止）

- **可执行文件**：exe, msi, dmg, app, deb, rpm
- **脚本文件**：jsp, php, asp, aspx, sh, bat, cmd, vbs, ps1
- **网页文件**：js, html, htm, xml, xhtml
- **Java相关**：jar, war, class, java
- **其他危险文件**：dll, com, cgi, scr, pif, lnk, reg, hta

### 安全特性

- 图片内容验证：启用
- 文件名过滤：启用
- 强制重命名：启用
- 上传日志记录：启用
- 病毒扫描：禁用（预留功能）

## 配置方式

### 1. 使用默认配置（推荐）

无需任何配置，系统会自动使用内置的安全默认值。

### 2. 基本自定义配置

在 `application.properties` 中添加：

```properties
# 文件大小限制（5MB）
bytedesk.upload.security.max-file-size=5242880

# 禁用强制重命名
bytedesk.upload.security.force-rename=false

# 禁用上传日志
bytedesk.upload.security.enable-upload-log=false
```

### 3. 高级自定义配置

完全自定义允许的文件类型：

```properties
# 只允许图片和PDF
bytedesk.upload.security.allowed-extensions=jpg,jpeg,png,gif,pdf
bytedesk.upload.security.allowed-mime-types=image/jpeg,image/png,image/gif,application/pdf

# 自定义危险文件类型
bytedesk.upload.security.dangerous-extensions=exe,bat,sh,php,jsp
```

## 常见使用场景

### 场景1：头像上传（严格模式）

```properties
bytedesk.upload.security.max-file-size=2097152
bytedesk.upload.security.allowed-extensions=jpg,jpeg,png
bytedesk.upload.security.allowed-mime-types=image/jpeg,image/png
bytedesk.upload.security.force-rename=true
```

### 场景2：文档管理系统（宽松模式）

```properties
bytedesk.upload.security.max-file-size=52428800
bytedesk.upload.security.allowed-extensions=pdf,doc,docx,xls,xlsx,ppt,pptx,txt,zip
bytedesk.upload.security.force-rename=false
bytedesk.upload.security.enable-file-name-filter=false
```

### 场景3：多媒体平台

```properties
bytedesk.upload.security.max-file-size=104857600
bytedesk.upload.security.allowed-extensions=jpg,jpeg,png,gif,mp4,avi,mov,mp3,wav
bytedesk.upload.security.enable-image-validation=true
```

## 配置参数说明

| 参数名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `max-file-size` | long | 10485760 | 最大文件大小（字节） |
| `enable-image-validation` | boolean | true | 是否验证图片内容 |
| `enable-file-name-filter` | boolean | true | 是否过滤文件名 |
| `force-rename` | boolean | true | 是否强制重命名 |
| `max-file-name-length` | int | 255 | 文件名最大长度 |
| `enable-upload-log` | boolean | true | 是否记录上传日志 |
| `enable-virus-scan` | boolean | false | 是否启用病毒扫描 |
| `allowed-extensions` | List String | 见默认列表 | 允许的文件扩展名 |
| `dangerous-extensions` | List String | 见默认列表 | 危险文件扩展名 |
| `allowed-mime-types` | List String | 见默认列表 | 允许的MIME类型 |

## 安全建议

1. **生产环境建议**：
   - 保持 `force-rename=true`
   - 保持 `enable-file-name-filter=true`
   - 保持 `enable-image-validation=true`
   - 启用 `enable-upload-log=true`

2. **文件大小限制**：
   - 根据实际需求设置合理的文件大小限制
   - 考虑服务器存储空间和网络带宽

3. **文件类型控制**：
   - 仅允许业务必需的文件类型
   - 定期审查和更新允许的文件类型列表

4. **监控和审计**：
   - 定期检查上传日志
   - 监控异常上传行为
   - 设置告警机制

## 注意事项

1. 配置修改后需要重启应用才能生效
2. 列表类型的配置使用逗号分隔
3. 文件大小单位为字节
4. MIME类型配置要与文件扩展名配置保持一致
5. 空的列表配置会使用默认值

## 故障排除

### 问题1：文件上传被拒绝

- 检查文件扩展名是否在允许列表中
- 检查MIME类型是否匹配
- 检查文件大小是否超过限制

### 问题2：配置不生效

- 确认配置语法正确
- 确认应用已重启
- 检查日志中的配置加载信息

### 问题3：上传日志过多

- 将 `enable-upload-log` 设置为 `false`
- 或者调整日志级别配置
