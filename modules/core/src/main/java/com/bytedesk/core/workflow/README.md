# 工作流类结构说明

## 概述

这个工作流类结构是基于提供的JSON数据设计的，支持复杂的工作流定义，包括多种节点类型和边的关系。

## 核心类结构

### 基础类

#### BaseNode

- 所有节点的基类
- 包含通用字段：uid、name、type、description、status、meta等
- 包含NodeData内部类，用于存储节点的具体数据
- 支持blocks和edges字段，用于group和loop节点

#### BaseEdge

- 所有边的基类
- 包含通用字段：uid、name、type、description等
- 支持sourceNodeID、targetNodeID、sourcePortID等字段

### 节点类型

#### WorkflowStartNode

- 开始节点
- 继承自BaseNode

#### WorkflowEndNode

- 结束节点
- 继承自BaseNode

#### WorkflowConditionNode

- 条件节点
- 包含条件相关的特殊字段
- 支持ConditionItem和ConditionValue内部类

#### WorkflowLoopNode

- 循环节点
- 包含循环次数等特殊字段

#### WorkflowLLMNode

- LLM节点
- 包含模型类型、温度、系统提示等特殊字段

#### WorkflowTextNode

- 文本节点
- 包含文本内容字段

#### WorkflowCommentNode

- 注释节点
- 包含大小和注释内容字段

#### WorkflowGroupNode

- 分组节点
- 包含颜色等特殊字段

### 边类型

#### WorkflowEdge

- 工作流边
- 继承自BaseEdge

### 枚举类型

#### WorkflowNodeTypeEnum

- 定义所有支持的节点类型
- 提供从字符串值获取枚举的方法

### 工具类

#### WorkflowNodeFactory

- 节点工厂类
- 根据节点类型创建相应的节点实例

#### WorkflowUtils

- 工作流工具类
- 提供JSON序列化/反序列化方法
- 提供工作流验证方法

#### WorkflowDocument

- 工作流文档类
- 包含节点列表和边列表
- 支持完整的JSON序列化/反序列化

## 使用示例

### 创建简单工作流

```java
// 创建开始节点
WorkflowStartNode startNode = WorkflowStartNode.builder()
    .uid("start_0")
    .type("start")
    .name("Start")
    .build();

// 创建结束节点
WorkflowEndNode endNode = WorkflowEndNode.builder()
    .uid("end_0")
    .type("end")
    .name("End")
    .build();

// 创建边
WorkflowEdge edge = WorkflowEdge.builder()
    .sourceNodeID("start_0")
    .targetNodeID("end_0")
    .build();

// 创建工作流文档
WorkflowDocument document = WorkflowDocument.builder()
    .nodes(Arrays.asList(startNode, endNode))
    .edges(Arrays.asList(edge))
    .build();
```

### JSON序列化/反序列化

```java
// 转换为JSON
String json = WorkflowUtils.toJson(document);

// 从JSON解析
WorkflowDocument parsedDocument = WorkflowUtils.parseWorkflowDocument(json);
```

### 使用节点工厂

```java
// 根据类型创建节点
BaseNode node = WorkflowNodeFactory.createNode("start", "start_0", "Start");
```

### 验证工作流

```java
// 验证工作流完整性
boolean isValid = WorkflowUtils.validateWorkflowDocument(document);
```

## 支持的节点类型

1. **start** - 开始节点
2. **end** - 结束节点
3. **condition** - 条件节点
4. **loop** - 循环节点
5. **llm** - LLM节点
6. **text** - 文本节点
7. **comment** - 注释节点
8. **group** - 分组节点

## 特性

- 完整的JSON序列化/反序列化支持
- 类型安全的节点创建
- 工作流验证功能
- 灵活的扩展性
- 支持复杂的嵌套结构（group、loop节点）
- 支持条件分支和端口

## 注意事项

1. 所有节点类都使用了Lombok注解，确保编译时生成必要的代码
2. 使用FastJSON2进行JSON处理，确保高性能
3. 所有类都实现了Serializable接口，支持序列化
4. 使用Builder模式，便于对象创建
5. 支持继承和多态，便于扩展新的节点类型
