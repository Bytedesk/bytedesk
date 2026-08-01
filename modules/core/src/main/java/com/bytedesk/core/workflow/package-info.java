/**
 * workflow package for workflow definition persistence, execution context management, schema compilation, and orchestration services.
 * workflow 包，负责工作流定义持久化、执行上下文管理、Schema 编译与流程编排服务。
 *
 * <p>The package combines workflow entities, request and response models, repository and specification queries,
 * execution context objects, service orchestration, listeners, initializer hooks, and event-driven extensions,
 * while delegating node, edge, and compiler details to dedicated subpackages.
 * 该包组合了工作流实体、请求响应模型、仓库与 Specification 查询、执行上下文对象、服务编排、监听器、初始化钩子和事件扩展，
 * 并将节点、连线与编译器细节拆分到专门子包中。
 *
 * @author bytedesk.com
 */
@NullMarked
package com.bytedesk.core.workflow;

import org.jspecify.annotations.NullMarked;
