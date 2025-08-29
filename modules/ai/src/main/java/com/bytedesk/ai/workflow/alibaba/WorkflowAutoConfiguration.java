/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-14 11:17:19
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-29 16:20:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.workflow.alibaba;

// import com.alibaba.cloud.ai.graph.GraphRepresentation;
// import com.alibaba.cloud.ai.graph.KeyStrategy;
// import com.alibaba.cloud.ai.graph.StateGraph;
// import com.alibaba.cloud.ai.graph.exception.GraphStateException;
// import com.alibaba.cloud.ai.graph.node.QuestionClassifierNode;
// import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;

// import lombok.extern.slf4j.Slf4j;

// import org.springframework.ai.chat.client.ChatClient;
// import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
// import org.springframework.ai.chat.model.ChatModel;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// import static com.alibaba.cloud.ai.graph.StateGraph.END;
// import static com.alibaba.cloud.ai.graph.StateGraph.START;
// import static com.alibaba.cloud.ai.graph.action.AsyncEdgeAction.edge_async;
// import static com.alibaba.cloud.ai.graph.action.AsyncNodeAction.node_async;

/**
 * 
 */
// @Slf4j
// @Configuration
// public class WorkflowAutoConfiguration {

// 	@Bean
// 	public StateGraph workflowGraph(ChatModel chatModel) throws GraphStateException {

// 		ChatClient chatClient = ChatClient.builder(chatModel).defaultAdvisors(new SimpleLoggerAdvisor()).build();

// 		QuestionClassifierNode feedbackClassifier = QuestionClassifierNode.builder()
// 			.chatClient(chatClient)
// 			.inputTextKey("input")
// 			.outputKey("classifier_output")
// 			.categories(List.of("positive feedback", "negative feedback"))
// 			.classificationInstructions(
// 					List.of("Try to understand the user's feeling when he/she is giving the feedback."))
// 			.build();

// 		QuestionClassifierNode specificQuestionClassifier = QuestionClassifierNode.builder()
// 			.chatClient(chatClient)
// 			.inputTextKey("input")
// 			.outputKey("classifier_output")
// 			.categories(List.of("after-sale service", "transportation", "product quality", "others"))
// 			.classificationInstructions(List
// 				.of("What kind of service or help the customer is trying to get from us? Classify the question based on your understanding."))
// 			.build();

// 		StateGraph stateGraph = new StateGraph("Consumer Service Workflow Demo", () -> {
// 			Map<String, KeyStrategy> strategies = new HashMap<>();
// 			strategies.put("input", new ReplaceStrategy());
// 			strategies.put("classifier_output", new ReplaceStrategy());
// 			strategies.put("solution", new ReplaceStrategy());
// 			return strategies;
// 		}).addNode("feedback_classifier", node_async(feedbackClassifier))
// 			.addNode("specific_question_classifier", node_async(specificQuestionClassifier))
// 			.addNode("recorder", node_async(new RecordingNode()))

// 			.addEdge(START, "feedback_classifier")
// 			.addConditionalEdges("feedback_classifier",
// 					edge_async(new CustomerServiceController.FeedbackQuestionDispatcher()),
// 					Map.of("positive", "recorder", "negative", "specific_question_classifier"))
// 			.addConditionalEdges("specific_question_classifier",
// 					edge_async(new CustomerServiceController.SpecificQuestionDispatcher()),
// 					Map.of("after-sale", "recorder", "transportation", "recorder", "quality", "recorder", "others",
// 							"recorder"))
// 			.addEdge("recorder", END);

// 		GraphRepresentation graphRepresentation = stateGraph.getGraph(GraphRepresentation.Type.PLANTUML,
// 				"workflow graph");

// 		log.info("workflow graph: {}", graphRepresentation.content());

// 		return stateGraph;
// 	}

// }
