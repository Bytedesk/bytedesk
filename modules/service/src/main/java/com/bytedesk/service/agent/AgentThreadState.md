<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-18 16:17:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-18 16:50:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# 客服状态变化流程

省掉了判断是否引进存在进行中会话和默认机器人流程

```mermaid
---
title: 一对一会话流程
---
flowchart
A[A-访客] --> |请求一对一人工| B{B-是否在线&接待-INITIAL}
B --> |否|C(C-留言-OFFLINE)
B --> |是|D{D-是否接待满员-INITIAL}
D --> |否|E(E-接待-STARTED)
D --> |是|F(F-排队-QUEUING)
F --> |接入|E(E-接待-STARTED)
E --> |结束|G(G-结束-CLOSED)
C --> |超时自动结束|G(G-结束-CLOSED)
```

## 其他

vscode中使用mermaid画/显示流程图，需要安装插件`Markdown Preview Mermaid Support`

- [mermaid docs](https://mermaid.js.org/syntax/flowchart.html#circle-edge-example)
- [mermaid markdown](https://marketplace.visualstudio.com/items?itemName=bierner.markdown-mermaid)
- [mermaid](https://github.com/mermaid-js/mermaid)
- [mermaid online editor](https://mermaid.live/edit)
