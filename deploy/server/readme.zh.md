<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-09 10:08:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-10 05:46:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# 微语Jar包部署

[微语](https://www.weiyuai.cn)是开源免费的企业协作系统，支持企业IM、在线客服、AI助手、知识库、帮助中心、工单系统等。

## 下载 [server](https://cdn.weiyuai.cn/server.zip)

```bash
# 因jar包太大, 从这里下载: https://cdn.weiyuai.cn/server.zip
# 编辑配置文件：config/application.properties
# 修改数据库连接信息
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/bytedesk_im
spring.datasource.username=root
spring.datasource.password=password
# 修改redis连接信息
spring.data.redis.database=0
spring.data.redis.host=127.0.0.1
spring.data.redis.port=6379
spring.data.redis.password=password
# 赋予权限
chmod +x start.sh
chmod +x stop.sh
# 启动
# 在Mac或Linux上运行
./start.sh
# 在Windows上运行
start.bat
# 停止
# 在Mac或Linux上运行
./stop.sh
# 在Windows上运行
stop.bat
```
