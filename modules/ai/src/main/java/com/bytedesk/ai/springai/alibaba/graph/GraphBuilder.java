package com.bytedesk.ai.springai.alibaba.graph;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.alibaba.cloud.ai.graph.StateGraph.END;
import static com.alibaba.cloud.ai.graph.StateGraph.START;
import static com.alibaba.cloud.ai.graph.action.AsyncEdgeAction.edge_async;

import com.alibaba.cloud.ai.graph.node.HttpNode;
import com.alibaba.cloud.ai.graph.node.HttpNode.RetryConfig;
import com.alibaba.cloud.ai.graph.node.QuestionClassifierNode;
import com.alibaba.cloud.ai.graph.node.VariableAggregatorNode;

@Component
public class GraphBuilder {

    @Bean
    public CompiledGraph buildGraph(ChatModel chatModel

    ) throws GraphStateException {
        ChatClient chatClient = ChatClient.builder(chatModel).defaultAdvisors(new SimpleLoggerAdvisor()).build();

        // new stateGraph
        StateGraph stateGraph = new StateGraph(() -> {
            Map<String, KeyStrategy> strategies = new HashMap<>();
            strategies.put("output", (o1, o2) -> o2);
            strategies.put("httpNode3_output", (o1, o2) -> o2);
            strategies.put("questionClassifyNode1_output", (o1, o2) -> o2);
            strategies.put("end1_output", (o1, o2) -> o2);
            strategies.put("questionClassifyNode2_output", (o1, o2) -> o2);
            strategies.put("review", (o1, o2) -> o2);
            strategies.put("httpNode1_output", (o1, o2) -> o2);
            strategies.put("httpNode2_output", (o1, o2) -> o2);
            return strategies;
        });
        // add nodes
        // —— QuestionClassifierNode [1711529036587] ——
        QuestionClassifierNode questionClassifyNode1 = QuestionClassifierNode.builder().chatClient(chatClient)
                .inputTextKey("review").categories(List.of("正面评价", "负面评价")).outputKey("questionClassifyNode1_output")
                .classificationInstructions(List.of("请根据输入内容选择对应分类")).build();
        stateGraph.addNode("questionClassifyNode1", AsyncNodeAction.node_async(questionClassifyNode1));

        // —— HttpNode [1711529059204] ——
        HttpNode httpNode1 = HttpNode.builder().url("https://www.example.com")
                .retryConfig(new RetryConfig(3, 100, true)).outputKey("httpNode1_output").build();
        stateGraph.addNode("httpNode1", AsyncNodeAction.node_async(httpNode1));

        // —— QuestionClassifierNode [1711529066687] ——
        QuestionClassifierNode questionClassifyNode2 = QuestionClassifierNode.builder().chatClient(chatClient)
                .inputTextKey("review").categories(List.of("售后问题", "运输问题")).outputKey("questionClassifyNode2_output")
                .classificationInstructions(List.of("请根据输入内容选择对应分类")).build();
        stateGraph.addNode("questionClassifyNode2", AsyncNodeAction.node_async(questionClassifyNode2));

        // —— HttpNode [1711529077513] ——
        HttpNode httpNode2 = HttpNode.builder().url("https://www.example.com")
                .retryConfig(new RetryConfig(3, 100, true)).outputKey("httpNode2_output").build();
        stateGraph.addNode("httpNode2", AsyncNodeAction.node_async(httpNode2));

        // —— HttpNode [1711529078719] ——
        HttpNode httpNode3 = HttpNode.builder().url("https://www.example.com")
                .retryConfig(new RetryConfig(3, 100, true)).outputKey("httpNode3_output").build();
        stateGraph.addNode("httpNode3", AsyncNodeAction.node_async(httpNode3));

        // —— VariableAggregatorNode [1718995432944] ——
        VariableAggregatorNode variableAggregatorNode1 = VariableAggregatorNode.builder()
                .variables(List.of(List.of("httpNode1_output", "body"), List.of("httpNode2_output", "body"),
                        List.of("httpNode3_output", "body")))
                .outputKey("output").outputType("string").build();
        stateGraph.addNode("variableAggregatorNode1", AsyncNodeAction.node_async(variableAggregatorNode1));

        // add edges
        stateGraph.addEdge(START, "questionClassifyNode1");
        stateGraph.addEdge("httpNode1", "variableAggregatorNode1");
        stateGraph.addEdge("httpNode2", "variableAggregatorNode1");
        stateGraph.addEdge("httpNode3", "variableAggregatorNode1");
        stateGraph.addEdge("variableAggregatorNode1", END);
        stateGraph.addConditionalEdges("questionClassifyNode1", edge_async(state -> {
            String value = state.value("questionClassifyNode1_output", String.class).orElse("");
            if (value.contains("正面评价"))
                return "正面评价";
            if (value.contains("负面评价"))
                return "负面评价";
            return null;
        }), Map.of("正面评价", "httpNode1", "负面评价", "questionClassifyNode2"));
        stateGraph.addConditionalEdges("questionClassifyNode2", edge_async(state -> {
            String value = state.value("questionClassifyNode2_output", String.class).orElse("");
            if (value.contains("售后问题"))
                return "售后问题";
            if (value.contains("运输问题"))
                return "运输问题";
            return null;
        }), Map.of("售后问题", "httpNode2", "运输问题", "httpNode3"));

        return stateGraph.compile();
    }

}
