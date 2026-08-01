package com.bytedesk.ticket.ticket;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bytedesk.ai.mcp.dto.McpTicketCreateRequest;
import com.bytedesk.ai.mcp.dto.McpTicketCreateResponse;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.ticket.ticket.enums.TicketTypeEnum;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BytedeskTicketMcpTools {

    private final ObjectMapper objectMapper;
    private final TicketRestService ticketRestService;

    @Tool(description = "Create a Bytedesk ticket. Input is McpTicketCreateRequest json; reporterUid, orgUid, title and description are required.")
    public Object bytedeskTicketCreate(@ToolParam(description = "McpTicketCreateRequest json") String requestJson) {
        long startedAt = System.currentTimeMillis();
        McpTicketCreateRequest request = parse(requestJson);
        validate(request);

        TicketRequest ticketRequest = TicketRequest.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .workgroupUid(request.getWorkgroupUid())
                .categoryUid(request.getCategoryUid())
                .contactName(request.getContactName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .reporter(UserProtobuf.builder()
                        .uid(request.getReporterUid())
                        .nickname(resolveReporterNickname(request))
                        .type(UserTypeEnum.USER.name())
                        .build())
                .build();
                ticketRequest.setOrgUid(request.getOrgUid());
                ticketRequest.setType(TicketTypeEnum.getNameFromValue(request.getType()));

        TicketResponse response = ticketRestService.create(ticketRequest);
        McpTicketCreateResponse result = McpTicketCreateResponse.builder()
                .uid(response.getUid())
                .ticketNumber(response.getTicketNumber())
                .title(response.getTitle())
                .status(response.getStatus())
                .priority(response.getPriority())
                .type(response.getType())
                .orgUid(response.getOrgUid())
                .createdAt(response.getCreatedAt() != null ? response.getCreatedAt().toString() : null)
                .reporterUid(response.getReporter() != null ? response.getReporter().getUid() : null)
                .reporterNickname(response.getReporter() != null ? response.getReporter().getNickname() : null)
                .workgroupUid(response.getWorkgroupUid())
                .build();
        log.info("[MCP-AUDIT] tool={} orgUid={} reporterUid={} ticketUid={} durationMs={}",
                "bytedeskTicketCreate", request.getOrgUid(), request.getReporterUid(), result.getUid(),
                System.currentTimeMillis() - startedAt);
        return result;
    }

    private McpTicketCreateRequest parse(String requestJson) {
        try {
            return objectMapper.readValue(requestJson, McpTicketCreateRequest.class);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Invalid ticket create request json: " + exception.getMessage(), exception);
        }
    }

    private void validate(McpTicketCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("ticket create request is required");
        }
        if (!StringUtils.hasText(request.getOrgUid())) {
            throw new IllegalArgumentException("orgUid is required");
        }
        if (!StringUtils.hasText(request.getReporterUid())) {
            throw new IllegalArgumentException("reporterUid is required");
        }
        if (!StringUtils.hasText(request.getTitle())) {
            throw new IllegalArgumentException("title is required");
        }
        if (!StringUtils.hasText(request.getDescription())) {
            throw new IllegalArgumentException("description is required");
        }
        if (StringUtils.hasText(request.getEmail()) && !request.getEmail().contains("@")) {
            throw new IllegalArgumentException("invalid email");
        }
    }

    private String resolveReporterNickname(McpTicketCreateRequest request) {
        if (StringUtils.hasText(request.getReporterNickname())) {
            return request.getReporterNickname();
        }
        if (StringUtils.hasText(request.getContactName())) {
            return request.getContactName();
        }
        return request.getReporterUid();
    }
}