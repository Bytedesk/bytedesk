package com.bytedesk.ticket.process;

import java.util.Map;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("llmTask")
public class LlmTask implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        // 占位：读取输入变量，调用 AI 服务（后续对接 ai 模块的 service），写回输出
        // 示例：String prompt = (String) execution.getVariable("prompt");
        // String answer = llmService.chat(prompt);
        // execution.setVariable("answer", answer);

        Map<String, Object> vars = execution.getVariables();
        execution.setVariable("llmExecuted", true);
        execution.setVariable("varsSnapshot", vars);
    }
}
