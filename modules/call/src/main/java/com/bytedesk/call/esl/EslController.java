/*
 * @Author: bytedesk.com
 * @Description: FreeSWITCH ESL REST 控制器
 */
package com.bytedesk.call.esl;

import com.bytedesk.core.utils.JsonResult;
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
    @GetMapping("/status")
    public ResponseEntity<JsonResult<?>> status() {
        Map<String, Object> res = eslService.status();
        return ResponseEntity.ok(JsonResult.success("ok", res));
    }

    @PostMapping("/api")
    public ResponseEntity<JsonResult<?>> api(@RequestBody @Valid ApiRequest req) {
        Map<String, Object> res = eslService.api(req.getCommand(), req.getArgs());
        return ResponseEntity.ok(JsonResult.success("ok", res));
    }

    @PostMapping("/bgapi")
    public CompletableFuture<ResponseEntity<JsonResult<?>>> bgapi(@RequestBody @Valid ApiRequest req) {
        return eslService.bgapi(req.getCommand(), req.getArgs())
                .thenApply(data -> ResponseEntity.ok(JsonResult.success("accepted", data)));
    }

    // 配置相关
    @PostMapping("/reloadxml")
    public ResponseEntity<JsonResult<?>> reloadxml() {
        return ResponseEntity.ok(JsonResult.success("reloadxml", eslService.reloadXml()));
    }

    @PostMapping("/reloadacl")
    public ResponseEntity<JsonResult<?>> reloadacl() {
        return ResponseEntity.ok(JsonResult.success("reloadacl", eslService.reloadAcl()));
    }

    @GetMapping("/show/{what}")
    public ResponseEntity<JsonResult<?>> show(@PathVariable String what) {
        return ResponseEntity.ok(JsonResult.success("show", eslService.show(what)));
    }

    // sofia
    @GetMapping("/sofia/status")
    public ResponseEntity<JsonResult<?>> sofiaStatus() {
        return ResponseEntity.ok(JsonResult.success("sofia status", eslService.sofiaStatus()));
    }

    @PostMapping("/sofia/profile/{profile}/{action}")
    public ResponseEntity<JsonResult<?>> sofiaProfile(@PathVariable String profile, @PathVariable String action) {
        return ResponseEntity.ok(JsonResult.success("sofia profile", eslService.sofiaProfileAction(profile, action)));
    }

    // 呼叫控制
    @PostMapping("/originate")
    public ResponseEntity<JsonResult<?>> originate(@RequestBody @Valid OriginateRequest req) {
        return ResponseEntity.ok(JsonResult.success("originate", eslService.originateRaw(req.getArgs())));
    }

    @PostMapping("/uuid/answer/{uuid}")
    public ResponseEntity<JsonResult<?>> uuidAnswer(@PathVariable String uuid) {
        return ResponseEntity.ok(JsonResult.success("uuid_answer", eslService.uuidAnswer(uuid)));
    }

    @PostMapping("/uuid/kill/{uuid}")
    public ResponseEntity<JsonResult<?>> uuidKill(@PathVariable String uuid, @RequestParam(required = false) String cause) {
        return ResponseEntity.ok(JsonResult.success("uuid_kill", eslService.uuidKill(uuid, cause)));
    }

    @PostMapping("/uuid/transfer")
    public ResponseEntity<JsonResult<?>> uuidTransfer(@RequestBody @Valid UuidTransferRequest req) {
        return ResponseEntity.ok(JsonResult.success("uuid_transfer",
                eslService.uuidTransfer(req.getUuid(), req.getDest(), req.getDialplan(), req.getContext(), req.getLeg())));
    }

    @PostMapping("/uuid/bridge")
    public ResponseEntity<JsonResult<?>> uuidBridge(@RequestBody @Valid UuidBridgeRequest req) {
        return ResponseEntity.ok(JsonResult.success("uuid_bridge", eslService.uuidBridge(req.getUuidA(), req.getUuidB())));
    }

    @PostMapping("/uuid/broadcast")
    public ResponseEntity<JsonResult<?>> uuidBroadcast(@RequestBody @Valid UuidBroadcastRequest req) {
        return ResponseEntity.ok(JsonResult.success("uuid_broadcast", eslService.uuidBroadcast(req.getUuid(), req.getFile(), req.getLegs())));
    }

    @PostMapping("/uuid/record")
    public ResponseEntity<JsonResult<?>> uuidRecord(@RequestBody @Valid UuidRecordRequest req) {
        return ResponseEntity.ok(JsonResult.success("uuid_record", eslService.uuidRecord(req.getUuid(), req.getAction(), req.getPath())));
    }

    @PostMapping("/uuid/setvar")
    public ResponseEntity<JsonResult<?>> uuidSetVar(@RequestBody @Valid UuidVarRequest req) {
        return ResponseEntity.ok(JsonResult.success("uuid_setvar", eslService.uuidSetVar(req.getUuid(), req.getVar(), req.getValue())));
    }

    @GetMapping("/uuid/getvar")
    public ResponseEntity<JsonResult<?>> uuidGetVar(@RequestParam String uuid, @RequestParam String var) {
        return ResponseEntity.ok(JsonResult.success("uuid_getvar", eslService.uuidGetVar(uuid, var)));
    }

    @PostMapping("/uuid/dtmf")
    public ResponseEntity<JsonResult<?>> uuidSendDtmf(@RequestBody @Valid UuidDtmfRequest req) {
        return ResponseEntity.ok(JsonResult.success("uuid_send_dtmf", eslService.uuidSendDtmf(req.getUuid(), req.getDtmf())));
    }

    // 会议
    @PostMapping("/conference")
    public ResponseEntity<JsonResult<?>> conference(@RequestBody @Valid ConferenceRequest req) {
        return ResponseEntity.ok(JsonResult.success("conference", eslService.conference(req.getRoom(), req.getSubCommand(), req.getArgs())));
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
