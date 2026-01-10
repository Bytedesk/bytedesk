# 演示流程模板说明

本目录包含用于展示 微语 工单系统工作流能力的演示流程模板。

## 流程列表

### 1. 请假审批流程 (leave-approval-process.bpmn20.xml)

**用途**: 演示企业内部员工请假审批流程

**流程特点**:
- 多级审批（主管/经理/HR）
- 根据请假天数自动路由（≤3天主管审批，>3天经理审批）
- 审批超时定时器提醒（经理审批2天超时）
- 条件分支（批准/拒绝）
- HR备案节点

**主要节点**:
- 提交请假申请
- 主管审批（短期请假）
- 部门经理审批（长期请假，带超时提醒）
- HR备案
- 批准/拒绝结束

**流程变量**:
- `leaveDays`: 请假天数
- `applicantUid`: 申请人UID
- `supervisorGroupId`: 主管组ID
- `managerGroupId`: 经理组ID
- `hrGroupId`: HR组ID
- `supervisorApproved`: 主管审批结果
- `managerApproved`: 经理审批结果

---

### 2. 报销审批流程 (reimbursement-approval-process.bpmn20.xml)

**用途**: 演示费用报销审批流程

**流程特点**:
- 金额分级审批（≤5000元经理审批，>5000元并行审批）
- 并行网关（财务总监+总经理同时审批）
- 财务初审节点
- 自动打款服务任务
- 通知服务

**主要节点**:
- 提交报销申请
- 财务初审
- 部门经理审批（小额）
- 财务总监+总经理并行审批（大额）
- 打款处理（服务任务）
- 发送打款通知

**流程变量**:
- `amount`: 报销金额
- `applicantUid`: 申请人UID
- `financeGroupId`: 财务组ID
- `managerGroupId`: 经理组ID
- `cfoGroupId`: 财务总监组ID
- `ceoGroupId`: 总经理组ID
- `financeApproved`: 财务初审结果
- `managerApproved`: 经理审批结果
- `cfoApproved`: 财务总监审批结果
- `ceoApproved`: 总经理审批结果

---

### 3. IT支持流程 (it-support-process.bpmn20.xml)

**用途**: 演示IT工单处理流程

**流程特点**:
- 工单分类路由（硬件/软件/网络/账号）
- 优先级判断（高优先级升级处理）
- SLA超时定时器
- 用户验证反馈
- 自动化处理（账号问题）
- 满意度调查

**主要节点**:
- 提交IT工单
- 分类路由（硬件/软件/网络/自动账号处理）
- 优先级判断
- 高优先级升级处理（带超时提醒）
- 用户验证
- 关闭工单+满意度调查

**流程变量**:
- `category`: 工单分类（HARDWARE/SOFTWARE/NETWORK/ACCOUNT）
- `priority`: 优先级（LOW/NORMAL/HIGH/URGENT）
- `requesterUid`: 请求人UID
- `hardwareGroupId`: 硬件支持组ID
- `softwareGroupId`: 软件支持组ID
- `networkGroupId`: 网络支持组ID
- `itManagerGroupId`: IT经理组ID
- `solved`: 用户验证结果
- `slaTimeISO`: SLA时间（ISO格式）

---

## 工作流能力展示

这些演示流程展示了以下BPMN 2.0工作流能力：

### 基础元素
- ✅ 开始事件 (Start Event)
- ✅ 结束事件 (End Event)
- ✅ 用户任务 (User Task)
- ✅ 服务任务 (Service Task)

### 网关
- ✅ 排他网关 (Exclusive Gateway) - 条件分支
- ✅ 并行网关 (Parallel Gateway) - 并行审批
- ✅ 事件网关 (Event Gateway)

### 高级特性
- ✅ 定时器事件 (Timer Event) - 超时处理
- ✅ 边界事件 (Boundary Event) - 任务超时
- ✅ 条件表达式 (Condition Expression)
- ✅ 流程变量 (Process Variables)
- ✅ 任务监听器 (Task Listener)
- ✅ 执行监听器 (Execution Listener)

### 实际应用场景
- ✅ 多级审批
- ✅ 条件路由
- ✅ 并行处理
- ✅ 超时提醒
- ✅ 自动化任务
- ✅ 用户反馈循环

---

## 初始化

演示流程会在系统启动时自动初始化，通过 `ProcessInitializer.initProcessDemos()` 方法加载。

初始化逻辑位于：
- `ProcessRestService.initProcessDemos()`
- `ProcessInitializer.afterSingletonsInstantiated()`

## 自定义开发

参考这些流程模板，可以快速创建您自己的业务流程：

1. 复制相应的 BPMN 文件作为模板
2. 修改流程定义和业务规则
3. 在 `ProcessDemoConsts` 中添加常量定义
4. 在 `ProcessRestService` 中添加初始化方法
5. 实现必要的 Delegate 类处理服务任务和监听器

## 技术支持

- Flowable BPMN 2.0 引擎
- Spring Boot 集成
- 支持可视化流程设计器（Flowable Modeler）
