/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 17:06:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.base.ExcelExportUtils;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.enums.ThreadCloseTypeEnum;
import com.bytedesk.core.thread.enums.ThreadProcessStatusEnum;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 会话管理接口
 * 
 * @author Jackning
 * @since 2024-01-29
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/thread")
@Tag(name = "Thread Management", description = "Thread management APIs, including query, create, update, delete, pin, and star operations")
public class ThreadRestController extends BaseRestController<ThreadRequest, ThreadRestService> {

    private static final DateTimeFormatter EXPORT_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int[] EXPORT_COLUMN_WIDTHS = {25, 20, 20, 20, 20, 20, 28};

    private final ThreadRestService threadRestService;

    private final MessageSource messageSource;

    /**
     * 根据组织查询会话
     * 
     * @param request 查询请求
     * @return 分页会话列表
     */
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_READ)
    @ActionAnnotation(title = I18Consts.I18N_THREAD, action = I18Consts.I18N_ACTION_QUERY_ORG, description = "queryByOrg thread")
    @Operation(summary = "Query Threads by Organization", description = "Return the thread list for the current organization")
    @Override
    @GetMapping("/query/org")
    public ResponseEntity<?> queryByOrg(ThreadRequest request) {

        Page<ThreadResponse> threadPage = threadRestService.queryByOrg(request);
        //
        return ResponseEntity.ok(JsonResult.success(threadPage));
    }

    /**
     * 根据用户查询会话
     * 
     * @param request 查询请求
     * @return 分页会话列表
     */
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_READ)
    @ActionAnnotation(title = I18Consts.I18N_THREAD, action = I18Consts.I18N_ACTION_QUERY_USER, description = "queryByUser thread")
    @Operation(summary = "Query Threads by User", description = "Return the thread list for the current user") 
    @Override
    @GetMapping({"/query", "/query/user"})
    public ResponseEntity<?> queryByUser(ThreadRequest request) {

        Page<ThreadResponse> threadPage = threadRestService.queryByUser(request);
        //
        return ResponseEntity.ok(JsonResult.success(threadPage));
    }

    /**
     * 根据UID查询会话
     * 
     * @param request 查询请求
     * @return 会话信息
     */
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_READ)
    @ActionAnnotation(title = I18Consts.I18N_THREAD, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "queryByUid thread")
    @Operation(summary = "Query Thread by UID", description = "Query the thread by unique identifier")
    @Override
    @GetMapping("/query/uid")
    public ResponseEntity<?> queryByUid(ThreadRequest request) {

        ThreadResponse threadResponse = threadRestService.queryByUid(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    /**
     * 查询邀请会话
     * 
     * @param request 查询请求
     * @return 分页邀请会话列表
     */
    @GetMapping("/query/invite")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_READ)
    @ActionAnnotation(title = I18Consts.I18N_THREAD, action = I18Consts.I18N_ACTION_QUERY_INVITE_THREAD, description = "query invite threads")
    @Operation(summary = "Query Invite Threads", description = "Query threads related to invitations")
    public ResponseEntity<?> queryByThreadInvite(ThreadRequest request) {

        Page<ThreadResponse> threadPage = threadRestService.queryByOrg(request);
        //
        return ResponseEntity.ok(JsonResult.success(threadPage));
    }

    /**
     * 根据主题查询会话
     * 
     * @param request 查询请求
     * @return 会话信息
     */
    @GetMapping("/query/topic")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_READ)
    @ActionAnnotation(title = I18Consts.I18N_THREAD, action = I18Consts.I18N_ACTION_QUERY_BY_TOPIC, description = "query thread by topic")
    @Operation(summary = "Query Threads by Topic", description = "Find related threads by topic")
    public ResponseEntity<?> queryByThreadTopic(ThreadRequest request) {
  
        Page<ThreadResponse> threadResponse = threadRestService.queryByTopic(request);
  
        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    /**
     * 主要用于查询 某个成员 的 某个群组会话
     * @param request
     * @return
     */
    @GetMapping("/query/topic/owner")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_READ)
    @ActionAnnotation(title = I18Consts.I18N_THREAD, action = I18Consts.I18N_ACTION_QUERY_BY_TOPIC_USER, description = "query thread by topic and owner")
    @Operation(summary = "Query Threads by Topic and User", description = "Find related threads by topic and user")
    public ResponseEntity<?> queryByTopicAndOwner(ThreadRequest request) {
        
        ThreadResponse threadResponse = threadRestService.queryByTopicAndOwner(request);
 
        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    
    /**
     * 创建会话
     * 
     * @param request 创建会话请求
     * @return 创建的会话
     */
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_CREATE)
    @ActionAnnotation(title = I18Consts.I18N_THREAD, action = I18Consts.I18N_ACTION_CREATE, description = "create thread")
    @Operation(summary = "Create Thread", description = "Create a new thread")
    @Override
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ThreadRequest request) {
        //
        ThreadResponse threadResponse = threadRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    /**
     * 更新会话
     * 
     * @param request 更新会话请求
     * @return 更新后的会话
     */
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_THREAD, action = I18Consts.I18N_ACTION_UPDATE, description = "update thread")
    @Operation(summary = "Update Thread", description = "Update existing thread information")
    @Override
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody ThreadRequest request) {

        ThreadResponse threadResponse = threadRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    /**
     * 更新会话置顶状态
     * 
     * @param request 更新请求
     * @return 更新后的会话
     */
    @PostMapping("/update/top")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_THREAD, action = I18Consts.I18N_ACTION_UPDATE_TOP, description = "update thread top")
    @Operation(summary = "Update Thread Pin Status", description = "Set or cancel thread pinning")
    public ResponseEntity<?> updateTop(@RequestBody ThreadRequest request) {

        ThreadResponse thread = threadRestService.updateTop(request);

        return ResponseEntity.ok(JsonResult.success(thread));
    }

    /**
     * 更新会话标星状态
     * 
     * @param request 更新请求
     * @return 更新后的会话
     */
    @PostMapping("/update/star")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_THREAD, action = I18Consts.I18N_ACTION_UPDATE_STAR, description = "update thread star")
    @Operation(summary = "Update Thread Star Status", description = "Set or cancel thread starring")
    public ResponseEntity<?> updateStar(@RequestBody ThreadRequest request) {

        ThreadResponse threadResponse = threadRestService.updateStar(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    /**
     * 更新会话静音状态
     * 
     * @param request 更新请求
     * @return 更新后的会话
     */
    @PostMapping("/update/mute")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_THREAD, action = I18Consts.I18N_ACTION_UPDATE_MUTE, description = "update thread mute")
    @Operation(summary = "Update Thread Mute Status", description = "Set or cancel thread muting")
    public ResponseEntity<?> updateMute(@RequestBody ThreadRequest request) {

        ThreadResponse threadResponse = threadRestService.updateMute(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    @PostMapping("/update/hide")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_THREAD, action = I18Consts.I18N_ACTION_UPDATE_HIDE, description = "update thread hide")
    @Operation(summary = "Update Thread Hidden Status", description = "Set or cancel thread hiding")
    public ResponseEntity<?> updateHide(@RequestBody ThreadRequest request) {
        
        ThreadResponse threadResponse = threadRestService.updateHide(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    @PostMapping("/update/fold")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_THREAD, action = I18Consts.I18N_ACTION_UPDATE_FOLD, description = "update thread fold")
    @Operation(summary = "Update Thread Fold Status", description = "Set or cancel thread folding")
    public ResponseEntity<?> updateFold(@RequestBody ThreadRequest request) {
        
        ThreadResponse threadResponse = threadRestService.updateFold(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    /**
     * 更新会话用户信息
     * 
     * @param request 更新请求
     * @return 更新后的会话
     */
    @PostMapping("/update/user")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_THREAD, action = I18Consts.I18N_ACTION_UPDATE_USER, description = "update thread user")
    @Operation(summary = "Update Thread User Information", description = "Update the user information associated with the thread")
    public ResponseEntity<?> updateUser(@RequestBody ThreadRequest request) {

        ThreadResponse threadResponse = threadRestService.updateUser(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    /**
     * 更新会话标签列表
     * 
     * @param request 更新请求
     * @return 更新后的会话
     */
    @PostMapping("/update/tagList")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_THREAD, action = I18Consts.I18N_ACTION_UPDATE_TAG_LIST, description = "update thread tagList")
    @Operation(summary = "Update Thread Tag List", description = "Update the thread tag information")
    public ResponseEntity<?> updateTagList(@RequestBody ThreadRequest request) {
        
        ThreadResponse threadResponse = threadRestService.updateTagList(request);
        
        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    /**
     * 更新会话未读状态
     * 
     * @param request 更新请求
     * @return 更新后的会话
     */
    @PostMapping("/update/unread")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_THREAD, action = I18Consts.I18N_ACTION_UPDATE_UNREAD, description = "update thread unread")
    @Operation(summary = "Update Thread Unread Status", description = "Mark the thread as read or unread")
    public ResponseEntity<?> updateUnread(@RequestBody ThreadRequest request) {

        ThreadResponse threadResponse = threadRestService.updateUnread(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    /**
     * 更新会话状态
     *
     * @param request 更新请求
     * @return 更新后的会话
     */
    @PostMapping("/update/status")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_THREAD, action = I18Consts.I18N_ACTION_UPDATE_STATUS, description = "update thread status")
    @Operation(summary = "Update Thread Status", description = "Update the processing status of the thread")
    public ResponseEntity<?> updateStatus(@RequestBody ThreadRequest request) {
        
        ThreadResponse threadResponse = threadRestService.updateStatus(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    /**
     * 查询用户所有客服会话
     * 
     * @return 用户所有会话列表
     */
    @GetMapping("/query/by/user/topics")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_READ)
    @ActionAnnotation(title = I18Consts.I18N_THREAD, action = I18Consts.I18N_ACTION_QUERY_USER_SERVICE_THREAD, description = "query threads by user topics")
    @Operation(summary = "Query All Service Threads for User", description = "Query all customer service threads for the user")
    public ResponseEntity<?> queryByUserTopics(ThreadRequest request) {
        
        Page<ThreadResponse> responses = threadRestService.queryThreadsByUserTopics(request);

        return ResponseEntity.ok(JsonResult.success(responses));
    }

    @PostMapping("/update/note")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_THREAD, action = I18Consts.I18N_ACTION_UPDATE_NOTE, description = "update thread note")
    @Operation(summary = "Update Thread Note", description = "Update the note information of the thread")
    public ResponseEntity<?> updateNote(@RequestBody ThreadRequest request) {
        
        ThreadResponse threadResponse = threadRestService.updateNote(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    /**
     * 管理后台更新会话（聚合更新）
     * - 主要给管理后台编辑抽屉使用，避免多次接口调用
     * - 当前支持：status、tagList（后续可按需扩展）
     */
    @PostMapping("/update/admin")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_THREAD, action = I18Consts.I18N_ACTION_ADMIN_UPDATE, description = "admin update thread")
    @Operation(summary = "Admin Update Thread", description = "Aggregate thread field updates from the admin console")
    public ResponseEntity<?> adminUpdate(@RequestBody ThreadRequest request) {

        ThreadResponse threadResponse = threadRestService.adminUpdate(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    /**
     * 关闭会话
     * 
     * @param request 关闭请求
     * @return 关闭后的会话
     */
    @PostMapping("/close")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_THREAD, action = I18Consts.I18N_ACTION_CLOSE, description = "close thread")
    @Operation(summary = "Close Thread", description = "Close the specified thread")
    public ResponseEntity<?> close(@RequestBody ThreadRequest request) {

        request.setCloseType(ThreadCloseTypeEnum.AGENT.name());
        ThreadResponse threadResponse = threadRestService.closeByUid(request);
        // 
        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }

    @PostMapping("/close/topic")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_UPDATE)
    @ActionAnnotation(title = I18Consts.I18N_THREAD, action = I18Consts.I18N_ACTION_CLOSE_BY_TOPIC, description = "close thread by topic")
    @Operation(summary = "Close Thread by Topic", description = "Close the thread for the specified topic")
    public ResponseEntity<?> closeByTopic(@RequestBody ThreadRequest request) {
        
        request.setCloseType(ThreadCloseTypeEnum.AGENT.name());
        ThreadResponse threadResponse = threadRestService.closeByTopic(request);

        return ResponseEntity.ok(JsonResult.success(threadResponse));
    }
    
    /**
     * 删除会话
     * 
     * @param request 删除请求
     * @return 删除结果
     */
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_DELETE)
    @ActionAnnotation(title = I18Consts.I18N_THREAD, action = I18Consts.I18N_ACTION_DELETE, description = "delete thread")
    @Operation(summary = "Delete Thread", description = "Delete the specified thread")
    @Override
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody ThreadRequest request) {
        
        threadRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success("delete thread success"));
    }

    /**
     * 导出会话列表
     * 
     * @param request 导出请求
     * @param response HTTP响应
     * @return 导出结果
     */
    @GetMapping("/export")
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_EXPORT)
    @ActionAnnotation(title = I18Consts.I18N_THREAD, action = I18Consts.I18N_ACTION_EXPORT, description = "export thread")
    @Operation(summary = "Export Thread List", description = "Export thread data to Excel format")
    public Object export(ThreadRequest request, HttpServletResponse response) {
        try {
            Locale locale = ExcelExportUtils.resolveLocale(request);
            String sheetName = localize("export.thread.sheet", "Thread", locale);
            String filePrefix = localize("export.thread.file.prefix", "Thread", locale);

            Page<ThreadEntity> threadPage = threadRestService.queryByOrgEntity(request);
            List<List<Object>> rows = threadPage.getContent().stream()
                    .map(entity -> buildExportRow(entity, locale))
                    .collect(Collectors.toList());

            ExcelExportUtils.writeCustomExcel(
                response,
                sheetName,
                filePrefix,
                buildExportHead(locale),
                rows,
                EXPORT_COLUMN_WIDTHS);
        } catch (Exception e) {
            log.error("export thread failed: request={}", request, e);
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            String message = e.getMessage() != null ? e.getMessage() : e.toString();
            return JsonResult.error(message);
        }
        return "";
    }

    private List<List<String>> buildExportHead(Locale locale) {
        return List.of(
                List.of(localize("export.thread.column.visitorNickname", "Visitor", locale)),
                List.of(localize("export.thread.column.agentNickname", "Agent", locale)),
                List.of(localize("export.thread.column.robotNickname", "Robot", locale)),
                List.of(localize("export.thread.column.workgroupNickname", "Workgroup", locale)),
                List.of(localize("export.thread.column.status", "Status", locale)),
                List.of(localize("export.thread.column.channel", "Channel", locale)),
                List.of(localize("export.thread.column.createdAt", "Created At", locale)));
    }

    private List<Object> buildExportRow(ThreadEntity entity, Locale locale) {
        String visitorNickname = extractNickname(entity.getUser());
        String agentNickname = extractNickname(entity.getAgent());
        String robotNickname = extractNickname(entity.getRobot());
        String workgroupNickname = extractNickname(entity.getWorkgroup());
        String status = nullableToEmpty(localizeThreadStatus(entity.getStatus(), locale));
        String channel = nullableToEmpty(localizeChannel(entity.getChannel(), locale));
        String createdAt = entity.getCreatedAt() != null ? entity.getCreatedAt().format(EXPORT_DATETIME_FORMATTER) : "";
        return List.of(visitorNickname, agentNickname, robotNickname, workgroupNickname, status, channel, createdAt);
    }

    private String extractNickname(String userJson) {
        if (!StringUtils.hasText(userJson)) {
            return "";
        }
        try {
            UserProtobuf user = UserProtobuf.fromJson(userJson);
            return nullableToEmpty(user.getNickname());
        } catch (Exception e) {
            return "";
        }
    }

    private String localizeThreadStatus(String status, Locale locale) {
        if (!StringUtils.hasText(status)) {
            return "";
        }
        try {
            ThreadProcessStatusEnum statusEnum = ThreadProcessStatusEnum.fromValue(status);
            return switch (statusEnum) {
                case NEW -> localize("thread.process.status.new", "New", locale);
                case ROBOTING -> localize("thread.process.status.roboting", "Robot Handling", locale);
                case OFFLINE -> localize("thread.process.status.offline", "Agent Offline", locale);
                case QUEUING -> localize("thread.process.status.queuing", "Queuing", locale);
                case CHATTING -> localize("thread.process.status.chatting", "Chatting", locale);
                case TIMEOUT -> localize("thread.process.status.timeout", "Timeout", locale);
                case CLOSED -> localize("thread.process.status.closed", "Closed", locale);
            };
        } catch (Exception e) {
            return status;
        }
    }

    private String localizeChannel(String channel, Locale locale) {
        if (!StringUtils.hasText(channel)) {
            return "";
        }
        if (locale != null && locale.getLanguage() != null && locale.getLanguage().startsWith("zh")) {
            return ChannelEnum.toChineseDisplay(channel);
        }
        return humanizeEnumValue(channel);
    }

    private String humanizeEnumValue(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        String[] parts = value.toLowerCase(Locale.ROOT).split("_");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return builder.length() > 0 ? builder.toString() : value;
    }

    private String localize(String key, String defaultMessage, Locale locale) {
        return messageSource.getMessage(key, null, defaultMessage, locale);
    }

    private String nullableToEmpty(String value) {
        return value == null ? "" : value;
    }

    /**
     * 发送消息前申请服务端分配的消息元信息
     *
     * @param request 包含 threadUid 的请求
        * @return 包含 messageUid、timestamp 的元信息
     */
    // @PreAuthorize(ThreadPermissions.HAS_THREAD_READ)
    @ActionAnnotation(title = I18Consts.I18N_THREAD, action = I18Consts.I18N_ACTION_REQUEST_MESSAGE_METADATA, description = "request message metadata")
    @Operation(summary = "Request Message Metadata", description = "Get the server-assigned message UID and timestamp before sending a message")
    @PostMapping("/message/meta")
    public ResponseEntity<?> requestMessageMetadata(@RequestBody ThreadRequest request) {
        if (request == null || !StringUtils.hasText(request.getUid())) {
            return ResponseEntity.ok(JsonResult.error("thread uid required"));
        }
        ThreadSequenceResponse response = threadRestService.allocateMessageMetadata(request.getUid());
        return ResponseEntity.ok(JsonResult.success("获取消息元信息成功", response));
    }

    
}
