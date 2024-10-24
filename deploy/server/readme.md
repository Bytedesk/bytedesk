<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-09 10:08:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-23 15:07:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# bytedesk jar deploy

## download [server](https://cdn.weiyuai.cn/server.zip)

```bash
# if jar not found, download from: https://cdn.weiyuai.cn/server.zip
# edit config/application.properties
# change mysql credentials
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/bytedesk_im
spring.datasource.username=root
spring.datasource.password=password
# change redis credentials
spring.data.redis.database=0
spring.data.redis.host=127.0.0.1
spring.data.redis.port=6379
spring.data.redis.password=password

# change permission
chmod +x start.sh
chmod +x stop.sh
# start
# mac or linux
./start.sh
# windows
start.bat
# stop
# mac or linux
./stop.sh
# windows
stop.bat
```
