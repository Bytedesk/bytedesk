/*
 * @Author: bytedesk.com
 * @Description: FreeSWITCH ESL REST 控制器
 */
package com.bytedesk.call.esl;

import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.call.esl.client.internal.IModEslApi;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/freeswitch/api/v1/esl")
@ConditionalOnProperty(prefix = "bytedesk.call.freeswitch", name = "enabled", havingValue = "true", matchIfMissing = false)
public class EslController {

    private final EslService eslService;

    public EslController(EslService eslService) {
        this.eslService = eslService;
    }

    // 健康与通用
    /** FreeSWITCH 运行状态 */
    // http://127.0.0.1:9003/freeswitch/api/v1/esl/status
    @GetMapping("/status")
    public ResponseEntity<JsonResult<?>> status() {
        Map<String, Object> res = eslService.status();
        return ResponseEntity.ok(JsonResult.success("ok", res));
    }

    /** 通用同步 API（等同 fs_cli 中的 api 命令） */
    @PostMapping("/api")
    public ResponseEntity<JsonResult<?>> api(@RequestBody @Valid ApiRequest req) {
        Map<String, Object> res = eslService.api(req.getCommand(), req.getArgs());
        return ResponseEntity.ok(JsonResult.success("ok", res));
    }

    /** 通用异步 BGAPI（返回 BACKGROUND_JOB 事件数据） */
    @PostMapping("/bgapi")
    public CompletableFuture<ResponseEntity<JsonResult<?>>> bgapi(@RequestBody @Valid ApiRequest req) {
        return eslService.bgapi(req.getCommand(), req.getArgs())
                .thenApply(data -> ResponseEntity.ok(JsonResult.success("accepted", data)));
    }

    // 配置相关
    /** 触发 reloadxml（重载 XML 配置与拨号计划） */
    // http://127.0.0.1:9003/freeswitch/api/v1/esl/reloadxml
    @PostMapping("/reloadxml")
    public ResponseEntity<JsonResult<?>> reloadxml() {
        return ResponseEntity.ok(JsonResult.success("reloadxml", eslService.reloadXml()));
    }

    /** 触发 reloadacl（重载 ACL） */
    // http://127.0.0.1:9003/freeswitch/api/v1/esl/reloadacl
    @PostMapping("/reloadacl")
    public ResponseEntity<JsonResult<?>> reloadacl() {
        return ResponseEntity.ok(JsonResult.success("reloadacl", eslService.reloadAcl()));
    }

    /** 刷新 XML 缓存（配合 mod_xml_curl） */
    // http://127.0.0.1:9003/freeswitch/api/v1/esl/xml_flush_cache
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
    // http://127.0.0.1:9003/freeswitch/api/v1/esl/sofia/status
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
