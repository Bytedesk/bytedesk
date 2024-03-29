<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:43:44
 * @LastEditors: jack ning github@bytedesk.com
 * @LastEditTime: 2024-03-29 16:21:21
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved.
-->

# bytedesk

- 启动：mvn spring-boot:run
- 清理：mvn clean
- 查看依赖：mvn dependency:tree
- 依赖分析：mvn dependency:analyze
- 手动拉取依赖：mvn dependency:resolve
- mvn compile -Dmaven.test.skip=true
- 本地安装：mvn install -Dmaven.test.skip=true
- 本地打包：mvn package -Dmaven.test.skip=true

- 查看端口占用：lsof -i:port，其中：port 为端口号, lsof -i:9003
- 杀掉进程：kill -9 pid，其中：pid 为进程 pid
<!-- 13408419243 -->

```bash
# 点击左侧的debug，运行和调试，会自动热加载
```
