/*
 * @Author: bytedesk.com
 * @Description: FreeSWITCH ESL 常用命令 Service 实现
 *
 * 提供通用 API/BGAPI、配置热加载（reloadxml/reloadacl）、
 * Sofia 管理（profile rescan/restart 等）、呼叫控制（originate、uuid_*）、
 * 会议控制（conference）等常用封装，底层基于内嵌的 ESL Inbound 客户端。
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

    /**
     * 将 EslMessage 统一转换为 Map 结果
     * - ok: 是否以 +OK 开头
     * - replyText: Reply-Text 头信息
     * - contentType: Content-Type 头
     * - body: 按行拼接后的 Body 文本
     */
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

    /**
     * 将 CommandResponse 统一转换为 Map 结果（包含原始 command 与消息体）
     */
    private Map<String, Object> toResult(CommandResponse response) {
        Map<String, Object> res = new HashMap<>();
        res.put("command", response.getCommand());
        res.putAll(toResult(response.getResponse()));
        return res;
    }

    /**
     * 执行同步 API 命令
     * @param command FreeSWITCH API 命令（如 reloadxml、sofia、uuid_bridge 等）
     * @param args    命令参数，允许为空
     * @return 标准化结果 Map
     */
    public Map<String, Object> api(String command, String args) {
        EslMessage msg = eslClient.sendApiCommand(command, args == null ? "" : args);
        return toResult(msg);
    }

    /**
     * 执行异步 BGAPI 命令（不阻塞），返回 BACKGROUND_JOB 事件内容
     * @param command FreeSWITCH BGAPI 命令
     * @param args    命令参数
     */
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

    // ===================== 基础维护类 =====================

    /** 触发 reloadxml，重载主配置与拨号计划（对 SIP Profile 变更可能还需 sofia rescan/restart） */
    public Map<String, Object> reloadXml() {
        return api("reloadxml", null);
    }

    /** 触发 reloadacl，重载访问控制列表 */
    public Map<String, Object> reloadAcl() {
        return api("reloadacl", null);
    }

    /** FreeSWITCH 运行状态 */
    public Map<String, Object> status() {
        return api("status", null);
    }

    /**
     * show 系列查询（channels/calls/registrations 等）
     * @param what 例如：channels、calls、registrations
     */
    public Map<String, Object> show(String what) { // e.g. channels/calls/registrations
        return api("show", what);
    }

    /** 刷新 XML 缓存（常与 mod_xml_curl 配合） */
    public Map<String, Object> xmlFlushCache() {
        return api("fsctl", "xml_flush_cache");
    }

    // ===================== Sofia 相关 =====================

    /** 获取 sofia 总览状态 */
    public Map<String, Object> sofiaStatus() {
        return api("sofia", "status");
    }

    /**
     * 对指定 profile 执行动作
     * @param profile profile 名称，如 internal/external
     * @param action  动作：rescan/restart/start/stop 等
     */
    public Map<String, Object> sofiaProfileAction(String profile, String action) { // rescan/restart/start/stop
        return api("sofia", String.format("profile %s %s", profile, action));
    }

    // ===================== 日志级别 =====================

    /** 设置 FreeSWITCH 日志输出级别（同 console.conf 的级别枚举） */
    public Map<String, Object> setLogLevel(IModEslApi.LoggingLevel level) {
        CommandResponse resp = eslClient.setLoggingLevel(level);
        return toResult(resp);
    }

    /** 取消通过 setLogLevel 开启的日志输出 */
    public Map<String, Object> cancelLogging() {
        CommandResponse resp = eslClient.cancelLogging();
        return toResult(resp);
    }

    // ===================== 呼叫控制类 =====================

    /**
     * 直接透传 originate 参数串
     * 示例：{"args":"{ignore_early_media=true}sofia/gateway/gw/1001 &park"}
     */
    public Map<String, Object> originateRaw(String args) {
        // 直接透传 originate 的参数，如 "{ignore_early_media=true}sofia/gateway/gw/1001 &park"
        return api("originate", args);
    }

    /** 应答通道 */
    public Map<String, Object> uuidAnswer(String uuid) {
        return api("uuid_answer", uuid);
    }

    /**
     * 挂断通道（可选携带 cause）
     * @param uuid  通道 UUID
     * @param cause 可选，hangup cause
     */
    public Map<String, Object> uuidKill(String uuid, String cause) {
        String args = cause == null || cause.isBlank() ? uuid : (uuid + " " + cause);
        return api("uuid_kill", args);
    }

    /**
     * 转接通道
     * @param uuid     通道 UUID
     * @param dest     目的路由（拨号计划目标）
     * @param dialplan 如 XML
     * @param context  如 default
     * @param leg      选项 -bleg|-both
     */
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

    /** 桥接两个通道 */
    public Map<String, Object> uuidBridge(String uuidA, String uuidB) {
        return api("uuid_bridge", uuidA + " " + uuidB);
    }

    /**
     * 向通道注入播放（广播）
     * @param legs aleg|bleg|both，默认 aleg
     */
    public Map<String, Object> uuidBroadcast(String uuid, String file, String legs) { // legs: aleg|bleg|both
        String useLegs = (legs == null || legs.isBlank()) ? "aleg" : legs;
        return api("uuid_broadcast", uuid + " " + file + " " + useLegs);
    }

    /**
     * 控制通道录音
     * @param action start|stop
     * @param path   start 时可选指定保存路径
     */
    public Map<String, Object> uuidRecord(String uuid, String action, String path) { // action: start|stop
        String args = uuid + " " + action + (path == null || path.isBlank() ? "" : (" " + path));
        return api("uuid_record", args);
    }

    /** 设置通道变量 */
    public Map<String, Object> uuidSetVar(String uuid, String var, String value) {
        return api("uuid_setvar", uuid + " " + var + " " + (value == null ? "" : value));
    }

    /** 获取通道变量 */
    public Map<String, Object> uuidGetVar(String uuid, String var) {
        return api("uuid_getvar", uuid + " " + var);
    }

    /** 向通道发送 DTMF */
    public Map<String, Object> uuidSendDtmf(String uuid, String dtmf) {
        return api("uuid_send_dtmf", uuid + " " + dtmf);
    }

    // ===================== 会议控制 =====================

    /**
     * conference 子命令统一入口
     * 示例：room=9000, subCommand=list；或 subCommand=kick, args=<uuid>
     */
    public Map<String, Object> conference(String room, String subCommand, String args) {
        String full = (args == null || args.isBlank()) ?
                String.format("%s %s", room, subCommand) :
                String.format("%s %s %s", room, subCommand, args);
        return api("conference", full);
    }
}
