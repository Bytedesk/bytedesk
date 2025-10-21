package com.bytedesk.call.xmlcurl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 最小可用的 mod_xml_curl HTTP 控制器。
 *
 * FreeSWITCH 配置 xml_curl.conf.xml 后，会以 HTTP 请求获取 directory 与 dialplan 的 XML。
 * 本控制器通过查询参数进行路由：
 * - type=directory&user=1000&domain=default
 * - type=dialplan&context=default&dest=1000
 *
 * 注意：生产环境需加入鉴权、白名单、限流与审计；此处为演示用途。
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "bytedesk.call.freeswitch", name = "xmlcurl.enabled", havingValue = "true", matchIfMissing = false)
@RequestMapping("/freeswitch/xmlcurl")
public class XmlCurlController {

    private final XmlCurlService xmlCurlService;

    @GetMapping(value = "", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> fetch(
            @RequestParam String type,
            @RequestParam(required = false) String domain,
            @RequestParam(required = false) String user,
            @RequestParam(required = false) String context,
            @RequestParam(required = false, name = "dest") String destinationNumber,
            // directory 覆盖项
            @RequestParam(required = false, name = "pwd") String password,
            @RequestParam(required = false, name = "cid_name") String callerIdName,
            @RequestParam(required = false, name = "cid_num") String callerIdNumber,
            @RequestParam(required = false, name = "user_ctx") String userContext,
            // dialplan 选项
            @RequestParam(required = false, name = "bridge") String bridgeEndpoint,
            @RequestParam(required = false, name = "playback") String playbackFile,
            @RequestParam(required = false, name = "tts_engine") String ttsEngine,
            @RequestParam(required = false, name = "tts_text") String ttsText,
            @RequestParam(required = false, name = "sleep") Integer sleepMs,
            @RequestParam(required = false, name = "no_answer") Boolean noAnswer,
            @RequestParam(required = false, name = "ivr_menu") String ivrMenu,
            @RequestParam(required = false, name = "queue") String queueName,
            @RequestParam(required = false, name = "record") String recordFile,
            // phrases/config
            @RequestParam(required = false, name = "lang") String phrasesLang,
            @RequestParam(required = false, name = "conf_name") String confName,
            @RequestParam(required = false, name = "key_value") String confKeyValue,
            // callcenter.conf 选项（可选）
            @RequestParam(required = false, name = "cc_dsn") String ccDsn,
            @RequestParam(required = false, name = "cc_client") String ccClientAddress,
            @RequestParam(required = false, name = "cc_debug") String ccDebug,
            @RequestParam(required = false, name = "cc_cdr") String ccCdrLogDir,
            @RequestParam(required = false, name = "cc_create") String ccCreateTables,
            @RequestHeader HttpHeaders headers) {
        // 可在此处增加鉴权/签名/白名单校验
        log.debug("xml_curl request: type={}, domain={}, user={}, context={}, dest={}", type, domain, user, context,
                destinationNumber);

        // 兼容 FreeSWITCH configuration 请求常见的 key_value 作为配置名
        String finalConfName = (confName == null || confName.isBlank()) ? confKeyValue : confName;

        String xml = route(type, domain, user, context, destinationNumber,
                password, callerIdName, callerIdNumber, userContext,
                bridgeEndpoint, playbackFile, ttsEngine, ttsText, sleepMs, noAnswer,
                ivrMenu, queueName, recordFile,
                phrasesLang, finalConfName,
                ccDsn, ccClientAddress, ccDebug, ccCdrLogDir, ccCreateTables);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(xml);
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> fetchPost(
            @RequestParam MultiValueMap<String, String> form,
            @RequestHeader HttpHeaders headers) {
        // FreeSWITCH 常见 POST
        // 字段兼容：section/type、domain/user/context、dest/destination_number/Caller-Destination-Number
        String type = firstNonEmpty(form, "type", "section");
        String domain = firstNonEmpty(form, "domain", "Realm");
        String user = firstNonEmpty(form, "user", "User-Name");
        String context = firstNonEmpty(form, "context");
        String dest = firstNonEmpty(form, "dest", "destination_number", "Caller-Destination-Number");

        // directory 覆盖项
        String password = firstNonEmpty(form, "pwd", "Event-Calling-Function" /* placeholder fallback */);
        String callerIdName = firstNonEmpty(form, "cid_name", "Caller-Caller-ID-Name");
        String callerIdNumber = firstNonEmpty(form, "cid_num", "Caller-Caller-ID-Number");
        String userContext = firstNonEmpty(form, "user_ctx", "user_context");
        // dialplan 选项
        String bridgeEndpoint = firstNonEmpty(form, "bridge");
        String playbackFile = firstNonEmpty(form, "playback");
        String ttsEngine = firstNonEmpty(form, "tts_engine");
        String ttsText = firstNonEmpty(form, "tts_text");
        Integer sleepMs = parseInt(firstNonEmpty(form, "sleep"));
        Boolean noAnswer = parseBoolean(firstNonEmpty(form, "no_answer"));
        String ivrMenu = firstNonEmpty(form, "ivr_menu");
        String queueName = firstNonEmpty(form, "queue");
        String recordFile = firstNonEmpty(form, "record");
        // phrases/config
        String phrasesLang = firstNonEmpty(form, "lang");
        String confName = firstNonEmpty(form, "conf_name", "key_value");
        String ccDsn = firstNonEmpty(form, "cc_dsn");
        String ccClientAddress = firstNonEmpty(form, "cc_client");
        String ccDebug = firstNonEmpty(form, "cc_debug");
        String ccCdrLogDir = firstNonEmpty(form, "cc_cdr");
        String ccCreateTables = firstNonEmpty(form, "cc_create");

        log.debug("xml_curl POST: type={}, domain={}, user={}, context={}, dest={}", type, domain, user, context, dest);

        if (type == null || type.isBlank()) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_XML)
                    .body(xmlCurlService.buildError("bad_request", "missing type/section"));
        }

        String xml = route(type, domain, user, context, dest,
                password, callerIdName, callerIdNumber, userContext,
                bridgeEndpoint, playbackFile, ttsEngine, ttsText, sleepMs, noAnswer,
                ivrMenu, queueName, recordFile,
                phrasesLang, confName,
                ccDsn, ccClientAddress, ccDebug, ccCdrLogDir, ccCreateTables);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(xml);
    }

    private String route(String type, String domain, String user, String context, String destinationNumber,
            String password, String callerIdName, String callerIdNumber, String userContext,
            String bridgeEndpoint, String playbackFile, String ttsEngine, String ttsText, Integer sleepMs,
            Boolean noAnswer,
            String ivrMenu, String queueName, String recordFile,
            String phrasesLang, String confName,
            String ccDsn, String ccClientAddress, String ccDebug, String ccCdrLogDir, String ccCreateTables) {
        switch (type) {
            case "directory":
            case "directory_user":
                DirectoryOptions dOpt = DirectoryOptions.builder()
                        .password(password)
                        .callerIdName(callerIdName)
                        .callerIdNumber(callerIdNumber)
                        .userContext(userContext)
                        .build();
                return xmlCurlService.buildDirectoryUser(domain, user, dOpt);
            case "dialplan":
                DialplanOptions dpOpt = DialplanOptions.builder()
                        .bridgeEndpoint(bridgeEndpoint)
                        .playbackFile(playbackFile)
                        .ttsEngine(ttsEngine)
                        .ttsText(ttsText)
                        .sleepMs(sleepMs)
                        .noAnswer(noAnswer)
                        .ivrMenu(ivrMenu)
                        .queueName(queueName)
                        .recordFile(recordFile)
                        .build();
                return xmlCurlService.buildDialplan(context, destinationNumber, dpOpt);
            case "phrases":
                return xmlCurlService.buildPhrases(phrasesLang);
            case "configuration":
                if (confName != null
                        && ("callcenter".equalsIgnoreCase(confName) || "callcenter.conf".equalsIgnoreCase(confName))) {
                    CallcenterOptions cc = CallcenterOptions.builder()
                            .odbcDsn(ccDsn)
                            .clientAddress(ccClientAddress)
                            .debug(ccDebug)
                            .cdrLogDir(ccCdrLogDir)
                            .createTables(ccCreateTables)
                            .build();
                    return xmlCurlService.buildConfiguration(confName, cc);
                }
                return xmlCurlService.buildConfiguration(confName);
            default:
                return xmlCurlService.buildNotFound();
        }
    }

    private static String firstNonEmpty(MultiValueMap<String, String> form, String... keys) {
        for (String k : keys) {
            if (form.containsKey(k)) {
                for (String v : form.get(k)) {
                    if (v != null && !v.isBlank()) {
                        return v;
                    }
                }
            }
        }
        return null;
    }

    private static Integer parseInt(String v) {
        try {
            return v == null ? null : Integer.parseInt(v);
        } catch (Exception e) {
            return null;
        }
    }

    private static Boolean parseBoolean(String v) {
        if (v == null)
            return null;
        return "1".equals(v) || "true".equalsIgnoreCase(v) || "yes".equalsIgnoreCase(v);
    }
}
