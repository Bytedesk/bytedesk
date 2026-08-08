package com.bytedesk.ai.kbase;

// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.bytedesk.ai.mcp.BytedeskExternalMcpTools;
// import com.bytedesk.ai.mcp.dto.McpKnowledgeSearchRequest;
// import com.bytedesk.core.utils.JsonResult;

// import lombok.RequiredArgsConstructor;

// @RestController
// @RequestMapping("/api/v1/ai/kbase")
// @RequiredArgsConstructor
// public class KnowledgeSearchRestController {

//     private final BytedeskExternalMcpTools bytedeskExternalMcpTools;

//     @PostMapping("/search")
//     @PreAuthorize("hasAuthority('FAQ_READ')")
//     public ResponseEntity<?> search(@RequestBody McpKnowledgeSearchRequest request) {
//         return ResponseEntity.ok(JsonResult.success(bytedeskExternalMcpTools.searchKnowledge(request)));
//     }
// }