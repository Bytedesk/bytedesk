<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-09 10:08:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-09 12:32:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# Jar 部署

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
./start.sh
# 停止
./stop.sh
```
