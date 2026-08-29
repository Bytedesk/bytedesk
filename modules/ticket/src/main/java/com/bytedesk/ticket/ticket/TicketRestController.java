/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 14:56:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-18 16:38:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.annotation.Idempotent;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.base.ExcelExportUtils;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.ticket.ticket.assignment.TicketAssignmentLogEntity;
import com.bytedesk.ticket.ticket.assignment.TicketAssignmentLogRepository;
import com.bytedesk.ticket.ticket.dto.TicketStatusCountResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;

@Slf4j
@RestController
@RequestMapping("/api/v1/ticket")
@AllArgsConstructor
public class TicketRestController extends BaseRestController<TicketRequest, TicketRestService> {
    
    private final TicketRestService ticketRestService;

    private final TicketAssignmentLogRepository assignmentLogRepository;

    private final MessageSource messageSource;

    @PreAuthorize(TicketPermissions.HAS_TICKET_READ)
    @ActionAnnotation(title = I18Consts.I18N_TICKET, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "query ticket by org")
    @GetMapping("/query/org")
    @Override
    public ResponseEntity<?> queryByOrg(TicketRequest request) {

        Page<TicketResponse> page = ticketRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @PreAuthorize(TicketPermissions.HAS_TICKET_READ)
    @ActionAnnotation(title = I18Consts.I18N_TICKET, action = I18Consts.I18N_ACTION_QUERY_USER, description = "query ticket by user")
    @GetMapping({ "/query", "/query/user" })
    @Override
    public ResponseEntity<?> queryByUser(TicketRequest request) {

        Page<TicketResponse> page = ticketRestService.queryByUser(request);
        
        return ResponseEntity.ok(JsonResult.success(page));
    }

    @PreAuthorize(TicketPermissions.HAS_TICKET_READ)
    @ActionAnnotation(title = I18Consts.I18N_TICKET, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query ticket by uid")
    @GetMapping("/query/uid")
    @Override
    public ResponseEntity<?> queryByUid(TicketRequest request) {
        
        TicketResponse response = ticketRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PreAuthorize(TicketPermissions.HAS_TICKET_READ)
    @ActionAnnotation(title = I18Consts.I18N_TICKET, action = I18Consts.I18N_ACTION_QUERY_BY_TOPIC, description = "query ticket by topic")
    @GetMapping("/query/thread/topic")
    public ResponseEntity<?> queryByThreadTopic(TicketRequest request) {

        Page<TicketResponse> page = ticketRestService.queryByThreadTopic(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @PreAuthorize(TicketPermissions.HAS_TICKET_READ)
    @ActionAnnotation(title = I18Consts.I18N_TICKET, action = I18Consts.I18N_ACTION_QUERY_BY_THREAD_UID, description = "query ticket by thread uid")
    @GetMapping("/query/thread/uid")
    public ResponseEntity<?> queryByThreadUid(TicketRequest request) {

        TicketResponse ticket = ticketRestService.queryByThreadUid(request);

        return ResponseEntity.ok(JsonResult.success(ticket));
    }

    @PreAuthorize(TicketPermissions.HAS_TICKET_READ)
    @ActionAnnotation(title = I18Consts.I18N_TICKET, action = I18Consts.I18N_ACTION_QUERY_BY_VISITOR_THREAD_UID, description = "query ticket by visitor thread uid")
    @GetMapping("/query/visitor/thread/uid")
    public ResponseEntity<?> queryByVisitorThreadUid(TicketRequest request) {

        Page<TicketResponse> page = ticketRestService.queryByVisitorThreadUid(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @PreAuthorize(TicketPermissions.HAS_TICKET_READ)
    @ActionAnnotation(title = I18Consts.I18N_TICKET, action = I18Consts.I18N_ACTION_QUERY_BY_VISITOR_THREAD_TOPIC, description = "query ticket by visitor thread topic")
    @GetMapping("/query/visitor/thread/topic")
    public ResponseEntity<?> queryByVisitorThreadTopic(TicketRequest request) {

        Page<TicketResponse> page = ticketRestService.queryByVisitorThreadTopic(request);

        return ResponseEntity.ok(JsonResult.success(page));
    }

    @PreAuthorize(TicketPermissions.HAS_TICKET_READ)
    @ActionAnnotation(title = I18Consts.I18N_TICKET, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query assignment log by ticket uid")
    @GetMapping("/query/assignment-log")
    public ResponseEntity<?> queryAssignmentLog(@RequestParam String ticketUid) {

        List<TicketAssignmentLogEntity> logs = assignmentLogRepository.findByTicketUidOrderByCreatedAtDesc(ticketUid);

        return ResponseEntity.ok(JsonResult.success(logs));
    }

    @PreAuthorize(TicketPermissions.HAS_TICKET_CREATE)
    @ActionAnnotation(title = I18Consts.I18N_TICKET, action = I18Consts.I18N_ACTION_CREATE, description = "create ticket")
    @PostMapping("/create")
    @Override
    @Idempotent(ttlSeconds = 120)
    public ResponseEntity<?> create(@RequestBody TicketRequest request) {

        TicketResponse response = ticketRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PreAuthorize(TicketPermissions.HAS_TICKET_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_TICKET, action = I18Consts.I18N_ACTION_UPDATE, description = "update ticket")
    @PostMapping("/update")
    @Override
    public ResponseEntity<?> update(@RequestBody TicketRequest request) {

        TicketResponse response = ticketRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(response));
    }

    @PreAuthorize(TicketPermissions.HAS_TICKET_DELETE)
    @ActionAnnotation(title = I18Consts.I18N_TICKET, action = I18Consts.I18N_ACTION_DELETE, description = "delete ticket")
    @PostMapping("/delete")
    @Override
    public ResponseEntity<?> delete(@RequestBody TicketRequest request) {

        ticketRestService.delete(request);
        
        return ResponseEntity.ok(JsonResult.success());
    }

    // https://github.com/alibaba/easyexcel
    // https://easyexcel.opensource.alibaba.com/docs/current/
    @PreAuthorize(TicketPermissions.HAS_TICKET_EXPORT)
    @ActionAnnotation(title = I18Consts.I18N_TICKET, action = I18Consts.I18N_ACTION_EXPORT, description = "export ticket")
    @GetMapping("/export")
    public Object export(TicketRequest request, HttpServletResponse response) {
        try {
            Locale locale = ExcelExportUtils.resolveLocale(request);
            Page<TicketEntity> ticketPage = ticketRestService.queryByOrgEntity(request);
            List<TicketExcel> excelList = ticketPage.getContent().stream()
                    .map(entity -> localizeExcel(ticketRestService.convertToExcel(entity), locale))
                    .collect(Collectors.toList());
            ExcelExportUtils.writeExcel(response, request, excelList, TicketExcel.class, "Ticket", "Ticket");
        } catch (Exception e) {
            log.error("export ticket failed: request={}", request, e);
            // 发生异常时重置响应
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            String message = e.getMessage() != null ? e.getMessage() : e.toString();
            return JsonResult.error(message);
        }
        return "";
    }

    /**
     * 将工单Excel中的状态/优先级/类型等枚举值转换为国际化文案
     * 与前端 TicketTable 列显示保持一致
     */
    private TicketExcel localizeExcel(TicketExcel excel, Locale locale) {
        if (excel == null) {
            return null;
        }
        excel.setStatus(localizeEnumText(excel.getStatus(), "ticket.status", locale));
        excel.setPriority(localizeEnumText(excel.getPriority(), "ticket.priority", locale));
        excel.setType(localizeEnumText(excel.getType(), "ticket.type", locale));
        return excel;
    }

    /**
     * 枚举值转国际化文案，未配置国际化key时回退为原始枚举值
     * 例如: PROCESSING -> i18n.ticket.status.processing -> 处理中
     */
    private String localizeEnumText(String enumValue, String keyPrefix, Locale locale) {
        if (enumValue == null || enumValue.isBlank()) {
            return enumValue;
        }
        String key = I18Consts.I18N_PREFIX + keyPrefix + "." + enumValue.toLowerCase();
        return messageSource.getMessage(key, null, enumValue, locale);
    }

    @PreAuthorize(TicketPermissions.HAS_TICKET_READ)
    @ActionAnnotation(title = I18Consts.I18N_TICKET, action = I18Consts.I18N_ACTION_COUNT_STATUS, description = "count ticket by status")
    @GetMapping("/count/status")
    public ResponseEntity<?> countStatus(TicketRequest request) {

        TicketStatusCountResponse counts = ticketRestService.countStatus(request);

        return ResponseEntity.ok(JsonResult.success(counts));
    }


}