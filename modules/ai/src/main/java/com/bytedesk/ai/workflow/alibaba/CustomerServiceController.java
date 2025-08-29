/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bytedesk.ai.workflow.alibaba;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.cloud.ai.graph.CompileConfig;
import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.EdgeAction;
import com.alibaba.cloud.ai.graph.observation.GraphObservationLifecycleListener;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/customer")
public class CustomerServiceController {

	private CompiledGraph compiledGraph;

	public CustomerServiceController(@Qualifier("workflowGraph") StateGraph stateGraph,
			ObjectProvider<ObservationRegistry> observationRegistry) throws GraphStateException {
		this.compiledGraph = stateGraph.compile(CompileConfig.builder()
			.withLifecycleListener(new GraphObservationLifecycleListener(
					observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP)))
			.build());
	}

	// http://localhost:9003/customer/chat?query=我收到的产品有快递破损，需要退换货？
	// http://localhost:9003/customer/chat?query=我的产品不能正常工作了，要怎么去做维修？
	// http://localhost:9003/customer/chat?query=商品收到了，非常好，下次还会买。
	@GetMapping("/chat")
	public String simpleChat(String query) throws Exception {

		return compiledGraph.invoke(Map.of("input", query)).get().value("solution").get().toString();
	}

	public static class FeedbackQuestionDispatcher implements EdgeAction {

		@Override
		public String apply(OverAllState state) throws Exception {

			String classifierOutput = (String) state.value("classifier_output").orElse("");
			log.info("classifierOutput: {}", classifierOutput);

			if (classifierOutput.contains("positive")) {
				return "positive";
			}
			return "negative";
		}

	}

	public static class SpecificQuestionDispatcher implements EdgeAction {

		@Override
		public String apply(OverAllState state) throws Exception {

			String classifierOutput = (String) state.value("classifier_output").orElse("");
			log.info("classifierOutput: {}", classifierOutput);

			Map<String, String> classifierMap = new HashMap<>();
			classifierMap.put("after-sale", "after-sale");
			classifierMap.put("quality", "quality");
			classifierMap.put("transportation", "transportation");

			for (Map.Entry<String, String> entry : classifierMap.entrySet()) {
				if (classifierOutput.contains(entry.getKey())) {
					return entry.getValue();
				}
			}

			return "others";
		}

	}

}
