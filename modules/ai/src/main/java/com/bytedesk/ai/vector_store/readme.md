<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-03-11 21:00:25
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-11 21:00:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
-->
# 向量数据库（Vector Database）

ByteDesk支持多种向量数据库，用于构建知识库、RAG检索系统等。

## 支持的向量数据库

### 1. Redis Vector 

Redis作为向量数据库（使用RediSearch模块），是ByteDesk默认启用的向量存储方案，无需额外配置即可使用。

- **优点**：设置简单，与ByteDesk配合使用的Redis实例共享，无需额外部署
- **适用场景**：中小规模知识库、开发测试环境

### 2. Chroma