/**
 * 阿里云百炼决策模型对接（TypeSafe System One 协议，{@code POST /compatible-mode/v1/systemone}）。
 *
 * <p>一次请求携带业务 {@code state} 与若干 typed 问题（{@code choice} / {@code noul} / {@code score}），
 * 模型一次前向返回判定结果、概率分布与置信度，不生成文本。适合工单分流、内容审核、智能体路由与结果校验。</p>
 *
 * <p>复用 Spring AI 2.1.0 的 {@code SystemOneClient} / {@code SystemOneDecisionModel}，
 * 与 Ollama 决策演示共用 {@code DecisionModel} 抽象；模型手动构建，不使用 Spring AI 自动配置。</p>
 *
 * <p>主要组件：</p>
 * <ul>
 *   <li>{@link com.bytedesk.ai.provider.aliyun.dashscope.decision.DashScopeDecisionService} - 构建决策模型并执行，DTO 与 Spring AI DecisionPrompt / DecisionResponse 互转</li>
 *   <li>{@link com.bytedesk.ai.provider.aliyun.dashscope.decision.DashScopeDecisionController} - {@code /dashscope/decision/**} 调试接口（受 bytedesk.debug 保护）</li>
 *   <li>{@link com.bytedesk.ai.provider.aliyun.dashscope.decision.DashScopeDecisionProperties} - {@code spring.ai.dashscope.decision.*} 配置</li>
 *   <li>{@link com.bytedesk.ai.provider.aliyun.dashscope.decision.DashScopeDecisionEndpoint} - 端点解析（base-url / workspace-id / base-url 回退）</li>
 * </ul>
 *
 * https://docs.bailian.console.aliyun.com/zh/model-studio/decision-model-api
 */
package com.bytedesk.ai.provider.aliyun.dashscope.decision;
