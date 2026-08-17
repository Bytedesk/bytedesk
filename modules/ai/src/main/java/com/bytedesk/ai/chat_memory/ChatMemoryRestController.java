/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-08-11
 * @Description: Spring AI ChatMemory 查询控制器（只读 + 按会话删除）。
 *   响应结构对齐前端 HttpPageResult / HttpResult 约定。
 */
package com.bytedesk.ai.chat_memory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/chat/memory")
@AllArgsConstructor
@Tag(name = "ChatMemory Management", description = "Spring AI ChatMemory read-only query APIs")
public class ChatMemoryRestController {

    private final ChatMemoryRestService chatMemoryRestService;

    @Operation(summary = "Query ChatMemory Records (paginated)")
    @PreAuthorize(ChatMemoryPermissions.HAS_CHAT_MEMORY_READ)
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(ChatMemoryRequest request) {
        List<ChatMemoryRecord> records = chatMemoryRestService.queryByOrg(request);
        long total = chatMemoryRestService.count(request);
        // 构建对齐前端 resolvePageContent/resolvePageTotal 的结构
        List<Map<String, Object>> content = records.stream().map(r -> {
            Map<String, Object> m = new HashMap<>();
            m.put("conversationId", r.getConversationId());
            m.put("content", r.getContent());
            m.put("type", r.getType());
            // 格式化为 yyyy-MM-dd HH:mm:ss（RowMapper 已转为北京时间，此处仅格式化，避免 LocalDateTime.toString() 输出 ISO 带 T 格式）
            m.put("timestamp", r.getTimestamp() != null ? r.getTimestamp().format(BdDateUtils.getDateTimeFormatter()) : null);
            m.put("sequenceId", r.getSequenceId());
            return m;
        }).toList();
        int pageNumber = request.getPageNumber() != null ? request.getPageNumber() : 0;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 10;
        Map<String, Object> data = new HashMap<>();
        data.put("content", content);
        data.put("totalElements", total);
        data.put("totalPages", (int) Math.ceil((double) total / pageSize));
        data.put("number", pageNumber);
        data.put("numberOfElements", content.size());
        data.put("first", pageNumber == 0);
        data.put("last", (pageNumber + 1) * pageSize >= total);
        data.put("empty", content.isEmpty());
        return ResponseEntity.ok(JsonResult.success(data));
    }

    @Operation(summary = "Delete ChatMemory by conversationId")
    @PreAuthorize(ChatMemoryPermissions.HAS_CHAT_MEMORY_DELETE)
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody Map<String, String> body) {
        String conversationId = body.get("conversationId");
        if (conversationId == null || conversationId.isBlank()) {
            return ResponseEntity.ok(JsonResult.error("conversationId is required"));
        }
        chatMemoryRestService.deleteByConversationId(conversationId);
        return ResponseEntity.ok(JsonResult.success());
    }
}
