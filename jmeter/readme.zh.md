<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-24 14:05:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-31 18:29:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# 压力测试

- [下载 jmeter](https://jmeter.apache.org/download_jmeter.cgi)

## 测试步骤

这两个JMeter测试文件分别对应：

1. agent_single_visitor_test.jmx/workgroup_single_visitor_test.jmx:
    - agent: 单客服、workgroup: 客服组
    - 模拟单个访客流程
    - 包含init和request两个请求
    - 设置了必要的请求头和参数
    - 使用JSON提取器获取响应数据
2. agent_multiple_visitors_test.jmx/workgroup_multiple_visitors_test.jmx:
    - agent: 单客服、workgroup: 客服组
    - 模拟100个并发访客
    - 每个访客发送100个请求
    - 使用计数器生成唯一访客ID
    - 包含结果查看器和汇总报告

### 使用方法

1. 在JMeter中导入这些.jmx文件
2. 根据需要调整HOST和PORT变量
3. 运行测试并查看结果

这些测试文件完全模拟了Java测试代码中的行为，并添加了性能测试相关的配置。

注意：mac上双击 bin/ApacheJMeter.jar 启动 jmeter 会报错，需要使用终端运行。

```bash
# cd /Users/ningjinpeng/Desktop/apache-jmeter-5.6.3
cd bin/ && ./jmeter.sh
```
