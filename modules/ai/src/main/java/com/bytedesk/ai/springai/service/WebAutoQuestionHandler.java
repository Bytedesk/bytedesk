package com.bytedesk.ai.springai.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springaicommunity.agent.tools.AskUserQuestionTool.Question;
import org.springaicommunity.agent.tools.AskUserQuestionTool.QuestionHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * 适用于 HTTP/REST 场景的 {@link QuestionHandler} 实现。
 * <p>
 * 命令行版本的 {@code CommandLineQuestionHandler} 依赖 {@code System.in} 交互输入，
 * 在 Web 请求线程中会永久阻塞，因此 Skills 测试接口需要一个非阻塞的处理器。
 * <p>
 * 本处理器为<strong>无状态、线程安全</strong>实现：
 * <ul>
 *   <li>将 AI 提出的每个问题记录到日志，便于演示时观察 Agent 的澄清行为；</li>
 *   <li>自动为每个问题选择<strong>第一个</strong>选项作为答案——
 *       按 AskUserQuestionTool 约定，推荐选项应放在选项列表首位，
 *       因此「选第一个」等价于「采纳推荐」，使演示在无人工干预下也能端到端跑通。</li>
 * </ul>
 */
@Slf4j
public class WebAutoQuestionHandler implements QuestionHandler {

    @Override
    public Map<String, String> handle(List<Question> questions) {
        Map<String, String> answers = new LinkedHashMap<>();

        for (Question question : questions) {
            String questionText = question.question();
            List<Question.Option> options = question.options();

            String answer;
            if (options != null && !options.isEmpty()) {
                // 采纳推荐项（约定为列表第一个，标签常带 "(Recommended)"）
                answer = options.get(0).label();
            } else {
                answer = "(no options, auto-accepted)";
            }

            log.info("[AskUserQuestion] {} -> 选中: {}", questionText, answer);
            if (log.isDebugEnabled() && options != null) {
                options.forEach(o -> log.debug("  候选项: {} - {}", o.label(), o.description()));
            }

            answers.put(questionText, answer);
        }

        return answers;
    }
}
