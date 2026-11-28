/*
 * @Author: bytedesk.com
 * @Description: FreeSWITCH ESL REST 控制器
 */
package com.bytedesk.call.esl;

import com.bytedesk.call.esl.client.internal.IModEslApi;
import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.call.xml_curl_trace.XmlCurlTraceService;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.core.rbac.role.RolePermissions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;

@Slf4j
@RestController
@RequestMapping("/api/v1/freeswitch/esl")
@ConditionalOnProperty(prefix = "bytedesk.call.freeswitch", name = "enabled", havingValue = "true", matchIfMissing = false)
public class EslController {

    private final EslService eslService;
    private final EslEventStreamService eslEventStreamService;
    private final ObjectProvider<XmlCurlTraceService> xmlCurlTraceServiceProvider;

    public EslController(EslService eslService,
                         EslEventStreamService eslEventStreamService,
                         ObjectProvider<XmlCurlTraceService> xmlCurlTraceServiceProvider) {
        this.eslService = eslService;
        this.eslEventStreamService = eslEventStreamService;
        this.xmlCurlTraceServiceProvider = xmlCurlTraceServiceProvider;
    }

    // 健康与通用
    /** FreeSWITCH 运行状态 */
    // http://127.0.0.1:9003/api/v1/freeswitch/esl/status
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Get ESL Status", description = "Retrieve the FreeSWITCH ESL connection status")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query esl")
    @GetMapping("/status")
    public ResponseEntity<JsonResult<?>> status() {
        Map<String, Object> res = eslService.status();
        return ResponseEntity.ok(JsonResult.success("ok", res));
    }

    /** ESL连接与订阅状态（可观测性） */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Get ESL State", description = "Retrieve the FreeSWITCH ESL state")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_QUERY_STATE, description = "query state esl")
    @GetMapping("/state")
    public ResponseEntity<JsonResult<?>> state() {
        Map<String, Object> state = eslService.connectionState();
        return ResponseEntity.ok(JsonResult.success("ok", state));
    }

    /** 通用同步 API（等同 fs_cli 中的 api 命令） */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Execute ESL API", description = "Execute a FreeSWITCH ESL api command")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query esl")
    @PostMapping("/api")
    public ResponseEntity<JsonResult<?>> api(@RequestBody @Valid ApiRequest req) {
        Map<String, Object> res = eslService.api(req.getCommand(), req.getArgs());
        return ResponseEntity.ok(JsonResult.success("ok", res));
    }

    /** 原生命令入口（不自动追加 api/bgapi 前缀） */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Execute ESL Command", description = "Execute a FreeSWITCH ESL command")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_EXECUTE, description = "execute esl")
    @PostMapping("/command")
    public ResponseEntity<JsonResult<?>> command(@RequestBody @Valid CommandRequest req) {
        Map<String, Object> res = eslService.command(req.getCommand());
        return ResponseEntity.ok(JsonResult.success("ok", res));
    }

    /** 通用异步 BGAPI（返回 BACKGROUND_JOB 事件数据） */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Execute ESL Background API", description = "Execute a FreeSWITCH ESL background api command")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query esl")
    @PostMapping("/bgapi")
    public CompletableFuture<ResponseEntity<JsonResult<?>>> bgapi(@RequestBody @Valid ApiRequest req) {
        return eslService.bgapi(req.getCommand(), req.getArgs())
                .thenApply(data -> ResponseEntity.ok(JsonResult.success("accepted", data)));
    }

    /** 设置事件订阅（event plain ...） */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Get Event Subscriptions", description = "Retrieve current ESL event subscriptions")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_CREATE, description = "create esl")
    @PostMapping("/events/subscriptions")
    public ResponseEntity<JsonResult<?>> eventSubscriptions(@RequestBody(required = false) EventSubscriptionRequest req) {
        String events = req == null ? null : req.getEvents();
        return ResponseEntity.ok(JsonResult.success("event subscriptions", eslService.eventSubscriptions(events)));
    }

    /** 取消事件订阅（noevents） */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Get No Events", description = "Retrieve ESL no-events state")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query esl")
    @DeleteMapping("/events/subscriptions")
    public ResponseEntity<JsonResult<?>> noEvents() {
        return ResponseEntity.ok(JsonResult.success("noevents", eslService.noEvents()));
    }

    /** 增加事件过滤器（filter Event-Name CHANNEL_CREATE） */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Add Event Filter", description = "Add an ESL event filter")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_CREATE, description = "create esl")
    @PostMapping("/events/filters")
    public ResponseEntity<JsonResult<?>> addEventFilter(@RequestBody @Valid EventFilterRequest req) {
        return ResponseEntity.ok(JsonResult.success("event filter add",
                eslService.addEventFilter(req.getEventHeader(), req.getValueToFilter())));
    }

    /** 删除事件过滤器（filter delete Event-Name CHANNEL_CREATE） */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Delete Event Filter", description = "Delete an ESL event filter")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_DELETE, description = "delete esl")
    @DeleteMapping("/events/filters")
    public ResponseEntity<JsonResult<?>> deleteEventFilter(@RequestBody @Valid EventFilterRequest req) {
        return ResponseEntity.ok(JsonResult.success("event filter delete",
                eslService.deleteEventFilter(req.getEventHeader(), req.getValueToFilter())));
    }

    /** 获取最近ESL事件（用于排查事件链路） */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Get Recent Events", description = "Retrieve recent ESL events")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query esl")
    @GetMapping("/events/recent")
    public ResponseEntity<JsonResult<?>> recentEvents(
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(required = false) String eventName,
            @RequestParam(required = false) String eventSubclass) {
        return ResponseEntity.ok(JsonResult.success("recent events",
                eslEventStreamService.recentEvents(limit, eventName, eventSubclass)));
    }

    /** 订阅ESL事件流（SSE） */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Stream Events", description = "Stream ESL events via SSE")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_SEND, description = "send esl")
    @GetMapping(value = "/events/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamEvents(
            @RequestParam(required = false) String eventName,
            @RequestParam(required = false) String eventSubclass) {
        return eslEventStreamService.subscribe(eventName, eventSubclass);
    }

    /** 事件流运行状态 */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Get Stream State", description = "Retrieve the ESL event stream state")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query esl")
    @GetMapping("/events/stream/state")
    public ResponseEntity<JsonResult<?>> streamState() {
        return ResponseEntity.ok(JsonResult.success("stream state", eslEventStreamService.streamState()));
    }

    /** 获取最近 xml_curl 请求，用于排查分机加载/注册加载/CDR配置加载 */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Get Recent XML CURL Requests", description = "Retrieve recent XML CURL requests")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query esl")
    @GetMapping("/xmlcurl/recent")
    public ResponseEntity<JsonResult<?>> recentXmlCurlRequests(
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(required = false) String section,
            @RequestParam(required = false) String category) {
        XmlCurlTraceService traceService = xmlCurlTraceServiceProvider.getIfAvailable();
        if (traceService == null) {
            return ResponseEntity.ok(JsonResult.success("xmlcurl recent", java.util.Collections.emptyList()));
        }
        return ResponseEntity.ok(JsonResult.success("xmlcurl recent",
            traceService.recent(limit, section, category)));
    }

    // 配置相关
    /** 触发 reloadxml（重载 XML 配置与拨号计划） */
    // http://127.0.0.1:9003/api/v1/freeswitch/esl/reloadxml
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Reload XML", description = "Reload FreeSWITCH XML configuration")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_EXECUTE, description = "execute esl")
    @PostMapping("/reloadxml")
    public ResponseEntity<JsonResult<?>> reloadxml() {
        return ResponseEntity.ok(JsonResult.success("reloadxml", eslService.reloadXml()));
    }

    /** 触发 reloadacl（重载 ACL） */
    // http://127.0.0.1:9003/api/v1/freeswitch/esl/reloadacl
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Reload ACL", description = "Reload FreeSWITCH ACL configuration")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_EXECUTE, description = "execute esl")
    @PostMapping("/reloadacl")
    public ResponseEntity<JsonResult<?>> reloadacl() {
        return ResponseEntity.ok(JsonResult.success("reloadacl", eslService.reloadAcl()));
    }

    /** 刷新 XML 缓存（配合 mod_xml_curl） */
    // http://127.0.0.1:9003/api/v1/freeswitch/esl/xml_flush_cache
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Flush XML Cache", description = "Flush the FreeSWITCH XML cache")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_EXECUTE, description = "execute esl")
    @PostMapping("/xml_flush_cache")
    public ResponseEntity<JsonResult<?>> xmlFlushCache() {
        return ResponseEntity.ok(JsonResult.success("xml_flush_cache", eslService.xmlFlushCache()));
    }

    /** show 系列查询（channels/calls/registrations 等） */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Show ESL Info", description = "Execute the FreeSWITCH show command")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query esl")
    @GetMapping("/show/{what}")
    public ResponseEntity<JsonResult<?>> show(@PathVariable String what) {
        return ResponseEntity.ok(JsonResult.success("show", eslService.show(what)));
    }

    // sofia
    /** 获取 sofia 总览状态 */
    // http://127.0.0.1:9003/api/v1/freeswitch/esl/sofia/status
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Get Sofia Status", description = "Retrieve FreeSWITCH sofia status")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query esl")
    @GetMapping("/sofia/status")
    public ResponseEntity<JsonResult<?>> sofiaStatus() {
        return ResponseEntity.ok(JsonResult.success("sofia status", eslService.sofiaStatus()));
    }

    /** 对指定 profile 执行动作（rescan/restart/start/stop） */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Get Sofia Profile", description = "Retrieve FreeSWITCH sofia profile details")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_QUERY_DETAIL, description = "query esl")
    @PostMapping("/sofia/profile/{profile}/{action}")
    public ResponseEntity<JsonResult<?>> sofiaProfile(@PathVariable String profile, @PathVariable String action) {
        return ResponseEntity.ok(JsonResult.success("sofia profile", eslService.sofiaProfileAction(profile, action)));
    }

    // 呼叫控制
    /** 直接透传 originate 参数串 */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Originate Call", description = "Originate a call via FreeSWITCH")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_EXECUTE, description = "execute esl")
    @PostMapping("/originate")
    public ResponseEntity<JsonResult<?>> originate(@RequestBody @Valid OriginateRequest req) {
        return ResponseEntity.ok(JsonResult.success("originate", eslService.originateRaw(req.getArgs())));
    }

    /** 应答通道 */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Answer Call", description = "Answer a call by UUID")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_EXECUTE, description = "execute esl")
    @PostMapping("/uuid/answer/{uuid}")
    public ResponseEntity<JsonResult<?>> uuidAnswer(@PathVariable String uuid) {
        return ResponseEntity.ok(JsonResult.success("uuid_answer", eslService.uuidAnswer(uuid)));
    }

    /** 挂断通道（可带 hangup cause） */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Kill Call Channel", description = "Kill a call channel by UUID")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_EXECUTE, description = "execute esl")
    @PostMapping("/uuid/kill/{uuid}")
    public ResponseEntity<JsonResult<?>> uuidKill(@PathVariable String uuid, @RequestParam(required = false) String cause) {
        return ResponseEntity.ok(JsonResult.success("uuid_kill", eslService.uuidKill(uuid, cause)));
    }

    /** 停止通道当前播放的媒体应用 */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Break Call Media", description = "Break call media playback by UUID")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_EXECUTE, description = "execute esl")
    @PostMapping("/uuid/break/{uuid}")
    public ResponseEntity<JsonResult<?>> uuidBreak(@PathVariable String uuid, @RequestParam(defaultValue = "true") boolean stopAll) {
        return ResponseEntity.ok(JsonResult.success("uuid_break", eslService.uuidBreak(uuid, stopAll)));
    }

    /** 转接通道 */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Transfer Call", description = "Transfer a call by UUID")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_EXECUTE, description = "execute esl")
    @PostMapping("/uuid/transfer")
    public ResponseEntity<JsonResult<?>> uuidTransfer(@RequestBody @Valid UuidTransferRequest req) {
        return ResponseEntity.ok(JsonResult.success("uuid_transfer",
                eslService.uuidTransfer(req.getUuid(), req.getDest(), req.getDialplan(), req.getContext(), req.getLeg())));
    }

    /** 桥接两个通道 */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Bridge Calls", description = "Bridge two call channels by UUID")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_EXECUTE, description = "execute esl")
    @PostMapping("/uuid/bridge")
    public ResponseEntity<JsonResult<?>> uuidBridge(@RequestBody @Valid UuidBridgeRequest req) {
        return ResponseEntity.ok(JsonResult.success("uuid_bridge", eslService.uuidBridge(req.getUuidA(), req.getUuidB())));
    }

    /** 向通道注入播放（广播） */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Broadcast Call Media", description = "Broadcast media to a call by UUID")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_EXECUTE, description = "execute esl")
    @PostMapping("/uuid/broadcast")
    public ResponseEntity<JsonResult<?>> uuidBroadcast(@RequestBody @Valid UuidBroadcastRequest req) {
        return ResponseEntity.ok(JsonResult.success("uuid_broadcast", eslService.uuidBroadcast(req.getUuid(), req.getFile(), req.getLegs())));
    }

    /** 控制通道录音 */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Record Call", description = "Start or stop call recording by UUID")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_EXECUTE, description = "execute esl")
    @PostMapping("/uuid/record")
    public ResponseEntity<JsonResult<?>> uuidRecord(@RequestBody @Valid UuidRecordRequest req) {
        return ResponseEntity.ok(JsonResult.success("uuid_record", eslService.uuidRecord(req.getUuid(), req.getAction(), req.getPath())));
    }

    /** 设置通道变量 */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Set Channel Variable", description = "Set a channel variable on a call by UUID")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_EXECUTE, description = "execute esl")
    @PostMapping("/uuid/setvar")
    public ResponseEntity<JsonResult<?>> uuidSetVar(@RequestBody @Valid UuidVarRequest req) {
        return ResponseEntity.ok(JsonResult.success("uuid_setvar", eslService.uuidSetVar(req.getUuid(), req.getVar(), req.getValue())));
    }

    /** 获取通道变量 */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Get Channel Variable", description = "Get a channel variable of a call by UUID")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_EXECUTE, description = "execute esl")
    @GetMapping("/uuid/getvar")
    public ResponseEntity<JsonResult<?>> uuidGetVar(@RequestParam String uuid, @RequestParam String var) {
        return ResponseEntity.ok(JsonResult.success("uuid_getvar", eslService.uuidGetVar(uuid, var)));
    }

    /** 发送 DTMF */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Send DTMF", description = "Send DTMF digits to a call by UUID")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_EXECUTE, description = "execute esl")
    @PostMapping("/uuid/dtmf")
    public ResponseEntity<JsonResult<?>> uuidSendDtmf(@RequestBody @Valid UuidDtmfRequest req) {
        return ResponseEntity.ok(JsonResult.success("uuid_send_dtmf", eslService.uuidSendDtmf(req.getUuid(), req.getDtmf())));
    }

    // 会议
    /** conference 子命令统一入口 */
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Conference Command", description = "Execute a FreeSWITCH conference command")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_EXECUTE, description = "execute esl")
    @PostMapping("/conference")
    public ResponseEntity<JsonResult<?>> conference(@RequestBody @Valid ConferenceRequest req) {
        return ResponseEntity.ok(JsonResult.success("conference", eslService.conference(req.getRoom(), req.getSubCommand(), req.getArgs())));
    }

    // 日志级别便捷接口（可选）
    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Set Log Level", description = "Set the ESL log level")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_EXECUTE, description = "execute esl")
    @PostMapping("/log/level/{level}")
    public ResponseEntity<JsonResult<?>> setLogLevel(@PathVariable IModEslApi.LoggingLevel level) {
        return ResponseEntity.ok(JsonResult.success("set log level", eslService.setLogLevel(level)));
    }

    @PreAuthorize(RolePermissions.ROLE_SUPER)
    @Operation(summary = "Cancel Log Level", description = "Cancel the ESL log level override")
    @ActionAnnotation(title = I18Consts.I18N_ESL, action = I18Consts.I18N_ACTION_EXECUTE, description = "execute esl")
    @PostMapping("/log/cancel")
    public ResponseEntity<JsonResult<?>> cancelLogLevel() {
        return ResponseEntity.ok(JsonResult.success("cancel log", eslService.cancelLogging()));
    }

    // ===================== DTOs =====================
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiRequest {
        @NotBlank
        private String command;
        private String args;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommandRequest {
        @NotBlank
        private String command;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventSubscriptionRequest {
        private String events;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventFilterRequest {
        @NotBlank
        private String eventHeader;
        @NotBlank
        private String valueToFilter;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Validated
    public static class OriginateRequest {
        @NotBlank
        private String args; // 完整 originate 参数串
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UuidTransferRequest {
        @NotBlank
        private String uuid;
        @NotBlank
        private String dest;
        private String dialplan; // e.g. XML
        private String context;  // e.g. default
        private String leg;      // -bleg | -both
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UuidBridgeRequest {
        @NotBlank
        private String uuidA;
        @NotBlank
        private String uuidB;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UuidBroadcastRequest {
        @NotBlank
        private String uuid;
        @NotBlank
        private String file;
        private String legs; // aleg|bleg|both
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UuidRecordRequest {
        @NotBlank
        private String uuid;
        @NotBlank
        private String action; // start|stop
        private String path;   // start 时可选
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UuidVarRequest {
        @NotBlank
        private String uuid;
        @NotBlank
        private String var;
        private String value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UuidDtmfRequest {
        @NotBlank
        private String uuid;
        @NotBlank
        private String dtmf;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConferenceRequest {
        @NotBlank
        private String room;
        @NotBlank
        private String subCommand; // list|kick|mute|unmute|record start|stop|bgdial 等
        private String args;       // 可选参数
    }
}
