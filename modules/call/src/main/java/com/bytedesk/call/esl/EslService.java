/*
 * @Author: bytedesk.com
 * @Description: FreeSWITCH ESL 常用命令 Service 实现
 */
package com.bytedesk.call.esl;

import com.bytedesk.call.esl.client.inbound.Client;
import com.bytedesk.call.esl.client.internal.IModEslApi;
import com.bytedesk.call.esl.client.transport.CommandResponse;
import com.bytedesk.call.esl.client.transport.message.EslHeaders;
import com.bytedesk.call.esl.client.transport.message.EslMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "bytedesk.call.freeswitch", name = "enabled", havingValue = "true", matchIfMissing = false)
public class EslService {

    private final Client eslClient;

    // 通用工具：将 EslMessage 转为统一 Map 结果
    private Map<String, Object> toResult(EslMessage msg) {
        Map<String, Object> res = new HashMap<>();
        if (msg == null) {
            res.put("ok", false);
            res.put("replyText", "NULL");
            res.put("contentType", null);
            res.put("body", "");
            return res;
        }
        String reply = null;
        try { reply = msg.getHeaderValue(EslHeaders.Name.REPLY_TEXT); } catch (Throwable ignore) {}
        String contentType = null;
        try { contentType = msg.getContentType(); } catch (Throwable ignore) {}
        List<String> bodyLines = msg.getBodyLines();
        String body = String.join("\n", bodyLines);

        boolean ok = false;
        if (reply != null && reply.trim().startsWith("+OK")) {
            ok = true;
        } else if (!bodyLines.isEmpty() && bodyLines.get(0).trim().startsWith("+OK")) {
            ok = true;
        }

        res.put("ok", ok);
        res.put("replyText", reply);
        res.put("contentType", contentType);
        res.put("body", body);
        return res;
    }

    private Map<String, Object> toResult(CommandResponse response) {
        Map<String, Object> res = new HashMap<>();
        res.put("command", response.getCommand());
        res.putAll(toResult(response.getResponse()));
        return res;
    }

    // 通用 API 执行
    public Map<String, Object> api(String command, String args) {
        EslMessage msg = eslClient.sendApiCommand(command, args == null ? "" : args);
        return toResult(msg);
    }

    // 可选：后台任务（返回 future，不阻塞）
    public CompletableFuture<Map<String, Object>> bgapi(String command, String args) {
        return eslClient.sendBackgroundApiCommand(command, args == null ? "" : args)
                .thenApply(event -> {
                    Map<String, Object> res = new HashMap<>();
                    res.put("eventName", event.getEventName());
                    res.put("headers", event.getEventHeaders());
                    res.put("bodyLines", event.getEventBodyLines());
                    return res;
                });
    }

    // 基础维护类
    public Map<String, Object> reloadXml() {
        return api("reloadxml", null);
    }

    public Map<String, Object> reloadAcl() {
        return api("reloadacl", null);
    }

    public Map<String, Object> status() {
        return api("status", null);
    }

    public Map<String, Object> show(String what) { // e.g. channels/calls/registrations
        return api("show", what);
    }

    // Sofia 相关
    public Map<String, Object> sofiaStatus() {
        return api("sofia", "status");
    }

    public Map<String, Object> sofiaProfileAction(String profile, String action) { // rescan/restart/start/stop
        return api("sofia", String.format("profile %s %s", profile, action));
    }

    // 日志级别: 使用 ESL 的 log 命令封装
    public Map<String, Object> setLogLevel(IModEslApi.LoggingLevel level) {
        CommandResponse resp = eslClient.setLoggingLevel(level);
        return toResult(resp);
    }

    public Map<String, Object> cancelLogging() {
        CommandResponse resp = eslClient.cancelLogging();
        return toResult(resp);
    }

    // 呼叫控制类
    public Map<String, Object> originateRaw(String args) {
        // 直接透传 originate 的参数，如 "{ignore_early_media=true}sofia/gateway/gw/1001 &park"
        return api("originate", args);
    }

    public Map<String, Object> uuidAnswer(String uuid) {
        return api("uuid_answer", uuid);
    }

    public Map<String, Object> uuidKill(String uuid, String cause) {
        String args = cause == null || cause.isBlank() ? uuid : (uuid + " " + cause);
        return api("uuid_kill", args);
    }

    public Map<String, Object> uuidTransfer(String uuid, String dest, String dialplan, String context, String leg) {
        StringBuilder sb = new StringBuilder();
        if (leg != null && !leg.isBlank()) {
            sb.append(leg).append(' '); // -bleg | -both
        }
        sb.append(uuid).append(' ').append(dest);
        if (dialplan != null && !dialplan.isBlank()) sb.append(' ').append(dialplan);
        if (context != null && !context.isBlank()) sb.append(' ').append(context);
        return api("uuid_transfer", sb.toString());
    }

    public Map<String, Object> uuidBridge(String uuidA, String uuidB) {
        return api("uuid_bridge", uuidA + " " + uuidB);
    }

    public Map<String, Object> uuidBroadcast(String uuid, String file, String legs) { // legs: aleg|bleg|both
        String useLegs = (legs == null || legs.isBlank()) ? "aleg" : legs;
        return api("uuid_broadcast", uuid + " " + file + " " + useLegs);
    }

    public Map<String, Object> uuidRecord(String uuid, String action, String path) { // action: start|stop
        String args = uuid + " " + action + (path == null || path.isBlank() ? "" : (" " + path));
        return api("uuid_record", args);
    }

    public Map<String, Object> uuidSetVar(String uuid, String var, String value) {
        return api("uuid_setvar", uuid + " " + var + " " + (value == null ? "" : value));
    }

    public Map<String, Object> uuidGetVar(String uuid, String var) {
        return api("uuid_getvar", uuid + " " + var);
    }

    public Map<String, Object> uuidSendDtmf(String uuid, String dtmf) {
        return api("uuid_send_dtmf", uuid + " " + dtmf);
    }

    // 会议控制
    public Map<String, Object> conference(String room, String subCommand, String args) {
        String full = (args == null || args.isBlank()) ?
                String.format("%s %s", room, subCommand) :
                String.format("%s %s %s", room, subCommand, args);
        return api("conference", full);
    }
}
