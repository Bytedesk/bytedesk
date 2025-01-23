<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-01 13:20:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-23 13:54:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# ticket

- [flowable-tutorial](https://www.baeldung.com/flowable)
- [flowable-starters](https://www.flowable.com/open-source/docs/bpmn/ch05a-Spring-Boot#flowable-starters)

## 1. 启动项目

```bash
# https://hub.docker.com/r/flowable/flowable-ui
# 启动flowable-ui
docker run -d -p 8080:8080 flowable/flowable-ui
# 访问flowable-ui
# http://localhost:8080/flowable-ui/
# http://14.103.165.199:8080/flowable-ui/
# 用户名: admin
# 密码: test
```

## TODO: 功能列表

1. 工单基础CRUD功能
2. 工单流转流程
3. 工单评论功能
4. 工单附件功能 (附件上传、下载)
5. 通知提醒功能
6. 报表统计功能
7. 权限控制系统

## 3. 参考资料

- [flowable-tutorial](https://www.baeldung.com/flowable)
- [flowable-starters](https://www.flowable.com/open-source/docs/bpmn/ch05a-Spring-Boot#flowable-starters)
