package com.bytedesk.call.esl.xmlcurl;

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
            @RequestHeader HttpHeaders headers
    ) {
        // 可在此处增加鉴权/签名/白名单校验
        log.debug("xml_curl request: type={}, domain={}, user={}, context={}, dest={}", type, domain, user, context, destinationNumber);

        String xml = route(type, domain, user, context, destinationNumber);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(xml);
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> fetchPost(
            @RequestParam MultiValueMap<String, String> form,
            @RequestHeader HttpHeaders headers
    ) {
        // FreeSWITCH 常见 POST 字段兼容：section/type、domain/user/context、dest/destination_number/Caller-Destination-Number
        String type = firstNonEmpty(form, "type", "section");
        String domain = firstNonEmpty(form, "domain", "Realm");
        String user = firstNonEmpty(form, "user", "User-Name");
        String context = firstNonEmpty(form, "context");
        String dest = firstNonEmpty(form, "dest", "destination_number", "Caller-Destination-Number");

        log.debug("xml_curl POST: type={}, domain={}, user={}, context={}, dest={}", type, domain, user, context, dest);

        if (type == null || type.isBlank()) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_XML)
                    .body(xmlCurlService.buildError("bad_request", "missing type/section"));
        }

        String xml = route(type, domain, user, context, dest);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(xml);
    }

    private String route(String type, String domain, String user, String context, String destinationNumber) {
        switch (type) {
            case "directory":
            case "directory_user":
                return xmlCurlService.buildDirectoryUser(domain, user);
            case "dialplan":
                return xmlCurlService.buildDialplan(context, destinationNumber);
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
}
