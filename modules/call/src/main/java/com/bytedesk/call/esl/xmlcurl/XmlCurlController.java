package com.bytedesk.call.esl.xmlcurl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

        String xml;
        switch (type) {
            case "directory":
                xml = xmlCurlService.buildDirectoryUser(domain, user);
                break;
            case "dialplan":
                xml = xmlCurlService.buildDialplan(context, destinationNumber);
                break;
            default:
                xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><document type=\"freeswitch/xml\"></document>";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(xml);
    }
}
