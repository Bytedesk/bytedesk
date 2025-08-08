/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-14 17:38:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-08 17:50:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.alibaba.graph;

// import com.alibaba.cloud.ai.graph.CompiledGraph;
// import com.alibaba.cloud.ai.graph.NodeOutput;
// import com.alibaba.cloud.ai.graph.OverAllState;
// import com.alibaba.cloud.ai.graph.async.AsyncGenerator;
// import org.springframework.beans.factory.annotation.Qualifier;
// import org.springframework.http.MediaType;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
// import reactor.core.publisher.Flux;

// import java.util.Map;
// import java.util.HashMap;

// @RestController
// @RequestMapping("/springai/alibaba/graph/run")
// public class GraphRunController {

//     private CompiledGraph graph;

//     public GraphRunController(@Qualifier("buildGraph") CompiledGraph graph) {
//         this.graph = graph;
//     }

//     // http://127.0.0.1:9003/springai/alibaba/graph/run/stream
//     @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//     public Flux<NodeOutput> stream(@RequestBody Map<String, Object> inputs) throws Exception {
//         AsyncGenerator<NodeOutput> nodeOutputs = graph.stream(inputs);
//         return Flux.fromStream(nodeOutputs.stream());
//     }

//     // http://127.0.0.1:9003/springai/alibaba/graph/run/invoke
//     @PostMapping(value = "/invoke")
//     public OverAllState invoke(@RequestBody Map<String, Object> inputs) throws Exception {
//         return graph.invoke(inputs).orElse(null);
//     }

//     // http://127.0.0.1:9003/springai/alibaba/graph/run/start
//     @PostMapping(value = "/start")
//     public Map<String, Object> startInvoke(@RequestBody Map<String, Object> inputs) throws Exception {
//         Map<String, Object> startInputs = new HashMap<>();
//         startInputs.put("review", inputs.get("review")); // null
//         return graph.invoke(startInputs).get().data();
//     }

// }
