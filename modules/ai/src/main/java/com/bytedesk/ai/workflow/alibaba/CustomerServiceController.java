/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-14 11:17:19
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-29 17:16:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.workflow.alibaba;

// import java.util.HashMap;
// import java.util.Map;

// import com.alibaba.cloud.ai.graph.CompileConfig;
// import com.alibaba.cloud.ai.graph.CompiledGraph;
// import com.alibaba.cloud.ai.graph.exception.GraphStateException;
// import com.alibaba.cloud.ai.graph.OverAllState;
// import com.alibaba.cloud.ai.graph.StateGraph;
// import com.alibaba.cloud.ai.graph.action.EdgeAction;
// import com.alibaba.cloud.ai.graph.observation.GraphObservationLifecycleListener;
// import io.micrometer.observation.ObservationRegistry;
// import lombok.extern.slf4j.Slf4j;

// import org.springframework.beans.factory.ObjectProvider;
// import org.springframework.beans.factory.annotation.Qualifier;
// import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// @Slf4j
// @RestController
// @RequestMapping("/customer")
// @ConditionalOnProperty(prefix = "spring.ai.workflow.graph", name = "enabled", havingValue = "true", matchIfMissing = false)
// public class CustomerServiceController {

// 	private CompiledGraph compiledGraph;

// 	public CustomerServiceController(@Qualifier("workflowGraph") StateGraph stateGraph,
// 			ObjectProvider<ObservationRegistry> observationRegistry) throws GraphStateException {
// 		this.compiledGraph = stateGraph.compile(CompileConfig.builder()
// 			.withLifecycleListener(new GraphObservationLifecycleListener(
// 					observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP)))
// 			.build());
// 	}

// 	// http://localhost:9003/customer/chat?query=我收到的产品有快递破损，需要退换货？
// 	// http://localhost:9003/customer/chat?query=我的产品不能正常工作了，要怎么去做维修？
// 	// http://localhost:9003/customer/chat?query=商品收到了，非常好，下次还会买。
// 	@GetMapping("/chat")
// 	public String simpleChat(String query) throws Exception {
// 		return compiledGraph.invoke(Map.of("input", query)).get().value("solution").get().toString();
// 	}

// 	public static class FeedbackQuestionDispatcher implements EdgeAction {
// 		@Override
// 		public String apply(OverAllState state) throws Exception {
// 			String classifierOutput = (String) state.value("classifier_output").orElse("");
// 			log.info("classifierOutput: {}", classifierOutput);
// 			if (classifierOutput.contains("positive")) {
// 				return "positive";
// 			}
// 			return "negative";
// 		}
// 	}

// 	public static class SpecificQuestionDispatcher implements EdgeAction {
// 		@Override
// 		public String apply(OverAllState state) throws Exception {
// 			String classifierOutput = (String) state.value("classifier_output").orElse("");
// 			log.info("classifierOutput: {}", classifierOutput);
// 			Map<String, String> classifierMap = new HashMap<>();
// 			classifierMap.put("after-sale", "after-sale");
// 			classifierMap.put("quality", "quality");
// 			classifierMap.put("transportation", "transportation");
// 			for (Map.Entry<String, String> entry : classifierMap.entrySet()) {
// 				if (classifierOutput.contains(entry.getKey())) {
// 					return entry.getValue();
// 				}
// 			}
// 			return "others";
// 		}
// 	}
// }
