/*
 * @Author: bytedesk.com
 * @Description: FreeSWITCH ESL REST 控制器
 */
package com.bytedesk.call.config.esl;

import com.bytedesk.call.config.esl.client.internal.IModEslApi;
import com.bytedesk.call.xml_curl.XmlCurlTraceService;
import com.bytedesk.core.utils.JsonResult;

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
    @GetMapping("/status")
    public ResponseEntity<JsonResult<?>> status() {
        Map<String, Object> res = eslService.status();
        return ResponseEntity.ok(JsonResult.success("ok", res));
    }

    /** ESL连接与订阅状态（可观测性） */
    @GetMapping("/state")
    public ResponseEntity<JsonResult<?>> state() {
        Map<String, Object> state = eslService.connectionState();
        return ResponseEntity.ok(JsonResult.success("ok", state));
    }

    /** 通用同步 API（等同 fs_cli 中的 api 命令） */
    @PostMapping("/api")
    public ResponseEntity<JsonResult<?>> api(@RequestBody @Valid ApiRequest req) {
        Map<String, Object> res = eslService.api(req.getCommand(), req.getArgs());
        return ResponseEntity.ok(JsonResult.success("ok", res));
    }

    /** 原生命令入口（不自动追加 api/bgapi 前缀） */
    @PostMapping("/command")
    public ResponseEntity<JsonResult<?>> command(@RequestBody @Valid CommandRequest req) {
        Map<String, Object> res = eslService.command(req.getCommand());
        return ResponseEntity.ok(JsonResult.success("ok", res));
    }

    /** 通用异步 BGAPI（返回 BACKGROUND_JOB 事件数据） */
    @PostMapping("/bgapi")
    public CompletableFuture<ResponseEntity<JsonResult<?>>> bgapi(@RequestBody @Valid ApiRequest req) {
        return eslService.bgapi(req.getCommand(), req.getArgs())
                .thenApply(data -> ResponseEntity.ok(JsonResult.success("accepted", data)));
    }

    /** 设置事件订阅（event plain ...） */
    @PostMapping("/events/subscriptions")
    public ResponseEntity<JsonResult<?>> eventSubscriptions(@RequestBody(required = false) EventSubscriptionRequest req) {
        String events = req == null ? null : req.getEvents();
        return ResponseEntity.ok(JsonResult.success("event subscriptions", eslService.eventSubscriptions(events)));
    }

    /** 取消事件订阅（noevents） */
    @DeleteMapping("/events/subscriptions")
    public ResponseEntity<JsonResult<?>> noEvents() {
        return ResponseEntity.ok(JsonResult.success("noevents", eslService.noEvents()));
    }

    /** 增加事件过滤器（filter Event-Name CHANNEL_CREATE） */
    @PostMapping("/events/filters")
    public ResponseEntity<JsonResult<?>> addEventFilter(@RequestBody @Valid EventFilterRequest req) {
        return ResponseEntity.ok(JsonResult.success("event filter add",
                eslService.addEventFilter(req.getEventHeader(), req.getValueToFilter())));
    }

    /** 删除事件过滤器（filter delete Event-Name CHANNEL_CREATE） */
    @DeleteMapping("/events/filters")
    public ResponseEntity<JsonResult<?>> deleteEventFilter(@RequestBody @Valid EventFilterRequest req) {
        return ResponseEntity.ok(JsonResult.success("event filter delete",
                eslService.deleteEventFilter(req.getEventHeader(), req.getValueToFilter())));
    }

    /** 获取最近ESL事件（用于排查事件链路） */
    @GetMapping("/events/recent")
    public ResponseEntity<JsonResult<?>> recentEvents(
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(required = false) String eventName,
            @RequestParam(required = false) String eventSubclass) {
        return ResponseEntity.ok(JsonResult.success("recent events",
                eslEventStreamService.recentEvents(limit, eventName, eventSubclass)));
    }

    /** 订阅ESL事件流（SSE） */
    @GetMapping(value = "/events/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamEvents(
            @RequestParam(required = false) String eventName,
            @RequestParam(required = false) String eventSubclass) {
        return eslEventStreamService.subscribe(eventName, eventSubclass);
    }

    /** 事件流运行状态 */
    @GetMapping("/events/stream/state")
    public ResponseEntity<JsonResult<?>> streamState() {
        return ResponseEntity.ok(JsonResult.success("stream state", eslEventStreamService.streamState()));
    }

    /** 获取最近 xml_curl 请求，用于排查分机加载/注册加载/CDR配置加载 */
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
    @PostMapping("/reloadxml")
    public ResponseEntity<JsonResult<?>> reloadxml() {
        return ResponseEntity.ok(JsonResult.success("reloadxml", eslService.reloadXml()));
    }

    /** 触发 reloadacl（重载 ACL） */
    // http://127.0.0.1:9003/api/v1/freeswitch/esl/reloadacl
    @PostMapping("/reloadacl")
    public ResponseEntity<JsonResult<?>> reloadacl() {
        return ResponseEntity.ok(JsonResult.success("reloadacl", eslService.reloadAcl()));
    }

    /** 刷新 XML 缓存（配合 mod_xml_curl） */
    // http://127.0.0.1:9003/api/v1/freeswitch/esl/xml_flush_cache
    @PostMapping("/xml_flush_cache")
    public ResponseEntity<JsonResult<?>> xmlFlushCache() {
        return ResponseEntity.ok(JsonResult.success("xml_flush_cache", eslService.xmlFlushCache()));
    }

    /** show 系列查询（channels/calls/registrations 等） */
    @GetMapping("/show/{what}")
    public ResponseEntity<JsonResult<?>> show(@PathVariable String what) {
        return ResponseEntity.ok(JsonResult.success("show", eslService.show(what)));
    }

    // sofia
    /** 获取 sofia 总览状态 */
    // http://127.0.0.1:9003/api/v1/freeswitch/esl/sofia/status
    @GetMapping("/sofia/status")
    public ResponseEntity<JsonResult<?>> sofiaStatus() {
        return ResponseEntity.ok(JsonResult.success("sofia status", eslService.sofiaStatus()));
    }

    /** 对指定 profile 执行动作（rescan/restart/start/stop） */
    @PostMapping("/sofia/profile/{profile}/{action}")
    public ResponseEntity<JsonResult<?>> sofiaProfile(@PathVariable String profile, @PathVariable String action) {
        return ResponseEntity.ok(JsonResult.success("sofia profile", eslService.sofiaProfileAction(profile, action)));
    }

    // 呼叫控制
    /** 直接透传 originate 参数串 */
    @PostMapping("/originate")
    public ResponseEntity<JsonResult<?>> originate(@RequestBody @Valid OriginateRequest req) {
        return ResponseEntity.ok(JsonResult.success("originate", eslService.originateRaw(req.getArgs())));
    }

    /** 应答通道 */
    @PostMapping("/uuid/answer/{uuid}")
    public ResponseEntity<JsonResult<?>> uuidAnswer(@PathVariable String uuid) {
        return ResponseEntity.ok(JsonResult.success("uuid_answer", eslService.uuidAnswer(uuid)));
    }

    /** 挂断通道（可带 hangup cause） */
    @PostMapping("/uuid/kill/{uuid}")
    public ResponseEntity<JsonResult<?>> uuidKill(@PathVariable String uuid, @RequestParam(required = false) String cause) {
        return ResponseEntity.ok(JsonResult.success("uuid_kill", eslService.uuidKill(uuid, cause)));
    }

    /** 转接通道 */
    @PostMapping("/uuid/transfer")
    public ResponseEntity<JsonResult<?>> uuidTransfer(@RequestBody @Valid UuidTransferRequest req) {
        return ResponseEntity.ok(JsonResult.success("uuid_transfer",
                eslService.uuidTransfer(req.getUuid(), req.getDest(), req.getDialplan(), req.getContext(), req.getLeg())));
    }

    /** 桥接两个通道 */
    @PostMapping("/uuid/bridge")
    public ResponseEntity<JsonResult<?>> uuidBridge(@RequestBody @Valid UuidBridgeRequest req) {
        return ResponseEntity.ok(JsonResult.success("uuid_bridge", eslService.uuidBridge(req.getUuidA(), req.getUuidB())));
    }

    /** 向通道注入播放（广播） */
    @PostMapping("/uuid/broadcast")
    public ResponseEntity<JsonResult<?>> uuidBroadcast(@RequestBody @Valid UuidBroadcastRequest req) {
        return ResponseEntity.ok(JsonResult.success("uuid_broadcast", eslService.uuidBroadcast(req.getUuid(), req.getFile(), req.getLegs())));
    }

    /** 控制通道录音 */
    @PostMapping("/uuid/record")
    public ResponseEntity<JsonResult<?>> uuidRecord(@RequestBody @Valid UuidRecordRequest req) {
        return ResponseEntity.ok(JsonResult.success("uuid_record", eslService.uuidRecord(req.getUuid(), req.getAction(), req.getPath())));
    }

    /** 设置通道变量 */
    @PostMapping("/uuid/setvar")
    public ResponseEntity<JsonResult<?>> uuidSetVar(@RequestBody @Valid UuidVarRequest req) {
        return ResponseEntity.ok(JsonResult.success("uuid_setvar", eslService.uuidSetVar(req.getUuid(), req.getVar(), req.getValue())));
    }

    /** 获取通道变量 */
    @GetMapping("/uuid/getvar")
    public ResponseEntity<JsonResult<?>> uuidGetVar(@RequestParam String uuid, @RequestParam String var) {
        return ResponseEntity.ok(JsonResult.success("uuid_getvar", eslService.uuidGetVar(uuid, var)));
    }

    /** 发送 DTMF */
    @PostMapping("/uuid/dtmf")
    public ResponseEntity<JsonResult<?>> uuidSendDtmf(@RequestBody @Valid UuidDtmfRequest req) {
        return ResponseEntity.ok(JsonResult.success("uuid_send_dtmf", eslService.uuidSendDtmf(req.getUuid(), req.getDtmf())));
    }

    // 会议
    /** conference 子命令统一入口 */
    @PostMapping("/conference")
    public ResponseEntity<JsonResult<?>> conference(@RequestBody @Valid ConferenceRequest req) {
        return ResponseEntity.ok(JsonResult.success("conference", eslService.conference(req.getRoom(), req.getSubCommand(), req.getArgs())));
    }

    // 日志级别便捷接口（可选）
    @PostMapping("/log/level/{level}")
    public ResponseEntity<JsonResult<?>> setLogLevel(@PathVariable IModEslApi.LoggingLevel level) {
        return ResponseEntity.ok(JsonResult.success("set log level", eslService.setLogLevel(level)));
    }

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
