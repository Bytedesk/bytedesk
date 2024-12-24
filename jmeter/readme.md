<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-09 16:47:04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-24 17:10:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# JMeter Performance Tests

This folder contains JMeter test plans for testing the visitor flow functionality.

## Test Files

- `visitor_flow_test.jmx`: Tests single visitor flow
- `multiple_visitors_test.jmx`: Tests multiple concurrent visitors

## Running Tests

1. Create a `report` folder in the same directory as the .jmx files:

```bash
mkdir report
```

2. Run the tests using JMeter GUI or command line:

```bash
jmeter -n -t visitor_flow_test.jmx -l report/visitor_flow.log -e -o report/visitor_flow_dashboard
jmeter -n -t multiple_visitors_test.jmx -l report/multiple_visitors.log -e -o report/multiple_visitors_dashboard
```

## Test Results

Results are stored in the `report` folder:

- `*_results.jtl`: Detailed test results
- `*_aggregate.jtl`: Aggregate statistics
- `*_graph.jtl`: Performance graphs
- `*_dashboard`: HTML dashboard reports

## Report Structure

- Response times
- Throughput
- Error rates
- Detailed request/response data
- Performance graphs
