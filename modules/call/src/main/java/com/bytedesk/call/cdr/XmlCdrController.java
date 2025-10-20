package com.bytedesk.call.cdr;

import com.bytedesk.core.utils.JsonResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 接收 FreeSWITCH mod_xml_cdr 推送的 XML，并写入现有 CDR 表。
 * 建议在生产环境加鉴权/白名单/签名验签与限流。
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "bytedesk.call.freeswitch", name = "enabled", havingValue = "true", matchIfMissing = false)
@RequestMapping("/freeswitch/cdr")
public class XmlCdrController {

    private final CallCdrService cdrService;
    private final XmlCdrParser parser = new XmlCdrParser();

    @PostMapping(value = "/xml", consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE})
    @Transactional
    public ResponseEntity<JsonResult<?>> receiveXml(@RequestBody String xml) {
        try {
            CallCdrEntity cdr = parser.parse(xml);
            if (cdr.getUid() == null) {
                log.warn("XML CDR 缺少 UUID，丢弃");
                return ResponseEntity.badRequest().body(JsonResult.error("missing uuid"));
            }

            // upsert：存在则更新，不存在则创建
            var existing = cdrService.findByUid(cdr.getUid());
            if (existing.isPresent()) {
                CallCdrEntity old = existing.get();
                old.setCallerIdName(cdr.getCallerIdName());
                old.setCallerIdNumber(cdr.getCallerIdNumber());
                old.setDestinationNumber(cdr.getDestinationNumber());
                old.setContext(cdr.getContext());
                old.setStartStamp(cdr.getStartStamp());
                old.setAnswerStamp(cdr.getAnswerStamp());
                old.setEndStamp(cdr.getEndStamp());
                old.setDuration(cdr.getDuration());
                old.setBillsec(cdr.getBillsec());
                old.setHangupCause(cdr.getHangupCause());
                old.setAccountcode(cdr.getAccountcode());
                old.setReadCodec(cdr.getReadCodec());
                old.setWriteCodec(cdr.getWriteCodec());
                old.setSipHangupDisposition(cdr.getSipHangupDisposition());
                old.setRecordFile(cdr.getRecordFile());
                old.setDirection(cdr.getDirection());
                cdrService.updateCdr(old);
            } else {
                cdrService.createCdr(cdr);
            }

            return ResponseEntity.ok(JsonResult.success("cdr accepted"));
        } catch (Exception e) {
            log.error("处理 XML CDR 失败", e);
            return ResponseEntity.badRequest().body(JsonResult.error("invalid xml"));
        }
    }
}
