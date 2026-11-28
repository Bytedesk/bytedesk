# 阿里云百炼决策模型对接（Spring AI `DecisionModel` + 百炼 `decision-model`）

对接阿里云百炼决策模型接口：**TypeSafe System One 协议**，`POST /compatible-mode/v1/systemone`。
一次请求携带业务 `state` 与若干 typed 问题（`choice` / `noul` / `score`），模型**一次前向**返回每个问题的判定结果、概率分布与置信度，**不生成文本**，因此延迟与成本与输出长度无关。适合工单分流、内容审核、智能体路由与结果校验等高频结构化决策场景。

- 官方文档：<https://docs.bailian.console.aliyun.com/zh/model-studio/decision-model-api>
- Spring AI Decision Models：<https://docs.spring.io/spring-ai/reference/api/decision.html>
- 同类演示（Ollama 本地决策模型）：[ollama/readme.decision.zh.md](../../../ollama/readme.decision.zh.md)

## 设计要点

| 决策 | 说明 |
| --- | --- |
| 复用 Spring AI `SystemOneClient` / `SystemOneDecisionModel` | 百炼决策模型即 System One 协议的实现，请求/返回结构与 Spring AI 完全一致（`model` / `state` / `questions` / `answers` / `probabilities` / `confidence` / `legend`）。无需引入新依赖：`spring-ai-systemone` 已随 `spring-ai-starter-model-ollama` 传递进入 `modules/ai`。 |
| 与 Ollama 决策演示共用 `DecisionModel` 抽象 | `DashScopeDecisionService.call(DecisionPrompt)` 返回 Spring AI `DecisionResponse`，后续客服路由 / 工单分流可以 provider 无关的方式接入，切换 Ollama / 百炼只换实现。 |
| 不使用 Spring AI 自动配置 | 各 profile 均显式 `spring.ai.model.decision=none`（自动配置会与项目自定义 `OllamaApi` 冲突）。模型由 Service 手动构建，`baseUrl` 指向百炼业务空间网关。 |
| `systemOnePath` 显式设为 `/systemone` | Spring AI 默认 `systemOnePath=/v1/systemone`，与百炼 `.../compatible-mode/v1` 拼接会变成 `/compatible-mode/v1/v1/systemone`（404）。 |
| 回填 `request_id` | Spring AI 只从 `x-typesafe-request-id` 响应头读取 request id；百炼在响应体返回 `request_id`。Service 用拦截器把响应体中的 `request_id` 回填到该头，`DecisionResponseMetadata.getId()` 才有值，便于排障。 |

## 代码结构

| 类 | 职责 |
| --- | --- |
| [DashScopeDecisionController](DashScopeDecisionController.java) | `/dashscope/decision/**` HTTP 接口；debug 开关、参数校验、HTTP 状态码映射 |
| [DashScopeDecisionService](DashScopeDecisionService.java) | 解析 provider / 全局凭据与端点，构建 `SystemOneDecisionModel`，DTO ↔ `DecisionPrompt` / `DecisionResponse` 映射 |
| [DashScopeDecisionProperties](DashScopeDecisionProperties.java) | `spring.ai.dashscope.decision.*` 配置；由 [DashScopeDecisionConfig](DashScopeDecisionConfig.java) 注册绑定 |
| [DashScopeDecisionEndpoint](DashScopeDecisionEndpoint.java) | 端点解析：`decision.base-url` > `decision.workspace-id(+region)` > `spring.ai.dashscope.base-url` 回退 |
| [DashScopeDecisionRequest](DashScopeDecisionRequest.java) | 请求 DTO：`model` / `providerUid` / `state` / `questions`（`type` / `instructions` / `criteria`），字段与官方一致 |
| [DashScopeDecisionResponse](DashScopeDecisionResponse.java) | 响应 DTO：`model` / `requestId` / `answers`（`choice` / `noul` / `score` / `probabilities` / `confidence` / `legend`）/ `usage` / `durationMs` |

## 配置

配置位于 `starter/src/main/resources/properties/<profile>/ai-dashscope.properties`（kingbase 在 `application-kingbase.properties`）：

```properties
spring.ai.dashscope.decision.enabled=true
# decision-model（正式版）/ decision-model-preview（预览版）
spring.ai.dashscope.decision.model=decision-model
# 端点解析优先级：decision.base-url > decision.workspace-id(+region) > spring.ai.dashscope.base-url 回退
# 本地 base-url 为业务空间网关 .../api/v1，回退时自动改写为 .../compatible-mode/v1，无需额外配置。
# spring.ai.dashscope.decision.workspace-id=llm-xxxxxxxxxxxx
# spring.ai.dashscope.decision.region=cn-beijing
# spring.ai.dashscope.decision.base-url=
# spring.ai.dashscope.decision.api-key=
spring.ai.dashscope.decision.timeout=60s
```

端点解析示例（`spring.ai.dashscope.base-url=https://llm-l33c9v25gxaavg8k.cn-beijing.maas.aliyuncs.com/api/v1`）：

| 场景 | 解析结果 |
| --- | --- |
| 仅配置全局 base-url | `https://llm-l33c9v25gxaavg8k.cn-beijing.maas.aliyuncs.com/compatible-mode/v1` |
| 配置 `workspace-id=llm-abc` | `https://llm-abc.cn-beijing.maas.aliyuncs.com/compatible-mode/v1` |
| 配置 `decision.base-url` | 原样使用（优先级最高） |

apiKey 优先级：`decision.api-key` > provider（`providerUid` 对应 `LlmProviderEntity.apiKey`）> `spring.ai.dashscope.api-key`。

## 前置条件

| 要求 | 如何检查 |
| --- | --- |
| 已开通百炼决策模型并有 API Key | <https://bailian.console.aliyun.com/?apiKey=1#/api-key> |
| `bytedesk.debug=true` | 接口默认受 debug 开关保护，仅用于联调验证 |
| 业务空间 ID 或可访问的 base-url | <https://docs.bailian.console.aliyun.com/zh/model-studio/regions#h2_migrate_domain> |

## 测试步骤

启动后端后（默认 `http://127.0.0.1:9003`），按顺序执行：

1. **确认端点与模型**（不调用模型）：

   ```bash
   curl "http://127.0.0.1:9003/dashscope/decision/config"
   ```

   预期 `endpoint` 为 `.../compatible-mode/v1`，且**不包含** `/api/v1` 或重复的 `/v1/v1`。

2. **是非判断（noul）**：

   ```bash
   curl "http://127.0.0.1:9003/dashscope/decision/noul?text=客户明确表示要投诉到监管部门。"
   ```

   预期 `answers.noul.noul` 为 0~1 之间的 P(yes)，且 `answers.noul` 不含 `confidence`。

3. **工单分流示例（choice + noul + score 三问合一）**：

   ```bash
   curl "http://127.0.0.1:9003/dashscope/decision/ticket?content=订单支付后超过24小时仍未到账，用户无法继续使用核心服务。"
   ```

   预期：`department.choice` ∈ {billing, technical, other} 且带 `confidence`；`escalate.noul` 为 P(yes)；`severity.score` 为 0~3 之间的加权期望（如 `2.25`）并带 `legend`。

4. **通用 POST（自定义 state / questions）**：

   ```bash
   curl -X POST "http://127.0.0.1:9003/dashscope/decision" \
     -H "Content-Type: application/json" \
     -d '{
       "state": {"ticket_id": "T-1001", "content": "订单支付后超过 24 小时仍未到账。"},
       "questions": {
         "department": {"type": "choice", "instructions": "应该由哪个团队处理？",
                        "criteria": {"billing": "支付、退款和账单问题", "technical": "产品故障和集成问题", "other": "其他问题"}},
         "escalate":   {"type": "noul",   "instructions": "是否需要立即通知值班人员？"},
         "severity":   {"type": "score",  "instructions": "这个问题有多严重？",
                        "criteria": ["轻微问题，不影响功能", "部分功能受影响，但存在替代方案", "核心功能不可用，没有替代方案", "造成严重业务或安全影响"]}
       }
     }'
   ```

   可选字段：`model`（覆盖默认模型）、`providerUid`（按 provider 取 apiKey/baseUrl）。

5. **错误场景**：

   | 场景 | 预期 |
   | --- | --- |
   | `bytedesk.debug=false` | HTTP 403 `Service is not available` |
   | apiKey 错误 | HTTP 502 `DashScope decision authentication failed; check the api key` |
   | model 不存在 | HTTP 502 `DashScope decision model not found; check the model name` |
   | 端点不可达 / 超时 | HTTP 503 `DashScope decision is unavailable or timed out` |
   | `questions.bad.type=unknown` | HTTP 400，提示 `unsupported type 'unknown'` |
   | `questions` 为空或 > 16 个 | HTTP 400（Bean Validation） |

## 单元测试

```bash
./starter/mvnw -f pom.xml -pl modules/ai test -Dtest='DashScopeDecision*Test'
```

- `DashScopeDecisionEndpointTest`：端点解析优先级与改写规则、DTO 字段布局。
- `DashScopeDecisionServiceTest`：用本地 HTTP 服务模拟 `/compatible-mode/v1/systemone`，验证 URL 拼接、Bearer 鉴权、请求体结构、官方返回示例的答案映射、provider 凭据解析、401 映射。
- `DashScopeDecisionConfigurationTest`：`spring.ai.dashscope.decision.*` 属性绑定与 Controller 的 `enabled` 条件装配（组件扫描方式，与生产一致）。

## 后续

- 将决策模型应用到客服系统（意图识别 / 工单分流 / 智能体路由）见规划文档：
  [docs/plans/2026-11-26-decision-model-customer-service-application-plan.md](../../../../../../../../docs/plans/2026-11-26-decision-model-customer-service-application-plan.md)。
