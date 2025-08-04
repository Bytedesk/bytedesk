<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-04 14:54:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-04 15:05:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
-->
# k8s

```bash
# mac 安装
brew install kompose
# 转换文件
kompose convert -f docker-compose.yaml
```

# Kompose 转换说明

## 转换命令

```bash
kompose convert -f docker-compose.yaml
```

## 关于警告信息的说明

在运行 `kompose convert` 命令时，可能会看到以下警告信息：

```
WARN File don't exist or failed to check if the directory is empty: stat :/app/uploads: no such file or directory
WARN File don't exist or failed to check if the directory is empty: stat :/var/lib/artemis/data: no such file or directory
WARN File don't exist or failed to check if the directory is empty: stat :/usr/share/elasticsearch/data: no such file or directory
WARN File don't exist or failed to check if the directory is empty: stat :/data: no such file or directory
WARN File don't exist or failed to check if the directory is empty: stat :/var/lib/mysql: no such file or directory
```

### 这些警告的原因

这些警告是 kompose 工具在转换过程中尝试检查容器内目录是否存在时产生的。由于转换过程是在本地环境中进行的，这些容器内的目录路径在本地文件系统中并不存在，因此产生了警告。

### 警告的影响

**这些警告不会影响最终的 Kubernetes 配置文件生成**，所有必要的配置文件都会正确创建：

- Deployment 文件
- Service 文件  
- PersistentVolumeClaim 文件

### 解决方案

1. **忽略警告**：这些警告是正常的，可以安全忽略
2. **使用 .komposeignore 文件**：已创建 `.komposeignore` 文件来忽略这些目录检查
3. **添加 volume driver**：已在 `docker-compose.yaml` 中为所有 volumes 添加了 `driver: local` 配置

### 验证转换结果

转换完成后，会生成以下文件：

- `bytedesk-deployment.yaml` - 主应用部署配置
- `bytedesk-service.yaml` - 主应用服务配置
- `bytedesk-*-deployment.yaml` - 各个依赖服务的部署配置
- `bytedesk-*-service.yaml` - 各个依赖服务的服务配置
- `*-data-persistentvolumeclaim.yaml` - 数据持久化配置

所有配置文件都是有效的 Kubernetes 资源定义，可以直接部署到 Kubernetes 集群中。
