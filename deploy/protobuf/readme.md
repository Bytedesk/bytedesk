<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-23 11:30:16
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-19 11:05:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 * 仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售
 * Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 * contact: 270580156@qq.com
 * 联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved.
-->

# Proto 说明

## 兼容es6步骤

- npm install -g protoc-gen-js
- protoc --js_out=import_style=es6,binary:. message.proto thread.proto user.proto

- 修改message_pb.js

顶部添加下面代码

```js
import * as jspb from "google-protobuf";
var goog = jspb;
var global = function () {
  if (this) {
    return this;
  }
  if (typeof window !== "undefined") {
    return window;
  }
  if (typeof global !== "undefined") {
    return global;
  }
  if (typeof self !== "undefined") {
    return self;
  }
  return Function("return this")();
}.call(null);

import { default as User } from "./user_pb.js";
import { default as Thread } from "./thread_pb.js";
```

最底部添加如下代码

```js
export default proto;
```

- 修改thread_pb.js和user_pb.js, 顶部添加如下代码

```js
import * as jspb from "google-protobuf";
var goog = jspb;
var global = function () {
  if (this) {
    return this;
  }
  if (typeof window !== "undefined") {
    return window;
  }
  if (typeof global !== "undefined") {
    return global;
  }
  if (typeof self !== "undefined") {
    return self;
  }
  return Function("return this")();
}.call(null);
```

最底部添加如下代码

```js
export default proto;
```

- 导入到mqtt.js

```js
import { default as messageProto } from "./protobuf/message_pb.js";
```
